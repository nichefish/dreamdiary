#!/usr/bin/env python3
"""Jira 감사 입력을 정규화하고 재현 가능한 수집 계획을 만든다.

이 모듈은 Atlassian 인증이나 네트워크 호출을 소유하지 않는다. 커넥터가 반환한
JSON을 원문 입력으로 받아 페이지 연속성, 변경 후보, 이전 snapshot 재사용과
회귀 기준선을 저장소 안의 결정론적 절차로 계산한다.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import os
import re
import shutil
import sys
from datetime import datetime, timezone
from pathlib import Path
from typing import Any


COMMENT_ID = re.compile(r"^### .* · id (\S+)\s*$", re.MULTILINE)
STATUS_ROW = re.compile(r"^\| (?!시각 \|)(?!---)(.*?) \| (.*?) \| (.*?) \| (.*?) \|$", re.MULTILINE)
DATE_WITH_COMPACT_OFFSET = re.compile(r"^(\d{4}-\d{2}-\d{2}T.*[+-]\d{2})(\d{2})$")


def read_json(path: Path) -> Any:
    with path.open("r", encoding="utf-8-sig") as stream:
        return json.load(stream)


def read_payload(path: Path) -> Any:
    if str(path) == "-":
        line = sys.stdin.readline()
        if not line:
            raise ValueError("표준입력 JSON이 없습니다.")
        return json.loads(line)
    return read_json(path)


def write_json(path: Path, value: Any) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def sha256_path(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def normalize(value: Any) -> Any:
    if isinstance(value, str):
        match = DATE_WITH_COMPACT_OFFSET.match(value)
        return f"{match.group(1)}:{match.group(2)}" if match else value
    if isinstance(value, list):
        return [normalize(item) for item in value]
    if isinstance(value, dict):
        return {key: normalize(item) for key, item in value.items()}
    return value


def issue_container(payload: dict[str, Any]) -> dict[str, Any]:
    structured = payload.get("structuredContent", payload)
    issues = structured.get("issues", structured)
    if not isinstance(issues, dict):
        raise ValueError("issues 컨테이너가 없습니다.")
    return issues


def issue_nodes(payload: dict[str, Any]) -> list[dict[str, Any]]:
    nodes = issue_container(payload).get("nodes")
    if not isinstance(nodes, list):
        raise ValueError("issues.nodes가 없습니다.")
    return [normalize(node) for node in nodes]


def init_audit(audit_dir: Path, project_key: str, jql: str, audit_id: str | None = None) -> dict[str, Any]:
    now = datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")
    resolved_id = audit_id or now.replace("-", "").replace(":", "").replace(".", "")
    manifest = {
        "schemaVersion": 2,
        "auditId": resolved_id,
        "projectKey": project_key,
        "jql": jql,
        "startedAt": now,
        "completedAt": None,
        "pages": [],
        "discoveredIssues": [],
        "observed": {"paginationFailureCount": 0, "duplicateIdentityCount": 0},
    }
    write_json(audit_dir / "manifest.json", manifest)
    return manifest


def record_page(audit_dir: Path, payload_path: Path, request_cursor: str | None) -> dict[str, Any]:
    manifest_path = audit_dir / "manifest.json"
    manifest = read_json(manifest_path)
    pages = manifest.get("pages", [])
    if pages and pages[-1].get("hasNextPage") is False:
        raise ValueError("종료 페이지 뒤에 페이지를 추가할 수 없습니다.")
    expected_cursor = None if not pages else pages[-1].get("endCursor")
    if request_cursor != expected_cursor:
        raise ValueError(f"cursor 불연속: expected={expected_cursor!r}, actual={request_cursor!r}")
    payload = read_payload(payload_path)
    container = issue_container(payload)
    nodes = issue_nodes(payload)
    page_info = container.get("pageInfo")
    if not isinstance(page_info, dict) or not isinstance(page_info.get("hasNextPage"), bool):
        raise ValueError("pageInfo 종료조건이 없습니다.")
    if page_info["hasNextPage"] and not page_info.get("endCursor"):
        raise ValueError("다음 페이지가 있지만 endCursor가 없습니다.")
    keys = [node.get("key") for node in nodes]
    if not all(isinstance(key, str) and key for key in keys):
        raise ValueError("페이지에 유효하지 않은 issue key가 있습니다.")
    pages.append(
        {
            "requestCursor": request_cursor,
            "endCursor": page_info.get("endCursor"),
            "hasNextPage": page_info["hasNextPage"],
            "keys": keys,
        }
    )
    discovered = manifest.setdefault("discoveredIssues", [])
    discovered.extend({"key": node["key"], "updated": node.get("fields", {}).get("updated")} for node in nodes)
    duplicate_count = len(discovered) - len({item["key"] for item in discovered})
    manifest["observed"]["duplicateIdentityCount"] = duplicate_count
    if page_info["hasNextPage"] is False:
        manifest["completedAt"] = datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")
    write_json(manifest_path, manifest)
    return pages[-1]


def markdown_facts(path: Path) -> dict[str, Any]:
    markdown = path.read_text(encoding="utf-8-sig")
    status_section_present = "## 상태 이력\n" in markdown
    status = markdown.split("## 상태 이력\n", 1)[1] if status_section_present else ""
    description = ""
    if "## 설명\n" in markdown and "## 댓글\n" in markdown:
        description = markdown.split("## 설명\n", 1)[1].split("## 댓글\n", 1)[0].strip()
    return {
        "fileHash": sha256_path(path),
        "commentIds": sorted(set(COMMENT_ID.findall(markdown))),
        "statusIdentities": sorted("|".join(row) for row in STATUS_ROW.findall(status)),
        "statusSectionPresent": status_section_present,
        "descriptionPresent": bool(description and description != "(없음)"),
    }


def build_plan(audit_dir: Path, mirror_dir: Path, source_audit_dir: Path | None = None) -> dict[str, Any]:
    manifest = read_json(audit_dir / "manifest.json")
    pages = manifest.get("pages", [])
    if not pages or pages[-1].get("hasNextPage") is not False:
        raise ValueError("pagination 종료조건이 관측되지 않았습니다.")
    discovered = manifest.get("discoveredIssues", [])
    remote_by_key = {item["key"]: item.get("updated") for item in discovered}
    if len(remote_by_key) != len(discovered):
        raise ValueError("중복 issue key가 있습니다.")
    if not all(isinstance(updated, str) and updated for updated in remote_by_key.values()):
        raise ValueError("discovery에 updated가 없는 issue가 있습니다.")
    index = read_json(mirror_dir / "index.json")
    index_items = index.get("issues", [])
    local_by_key = {item["key"]: item for item in index_items}
    baseline: dict[str, Any] = {}
    for key, entry in local_by_key.items():
        path = mirror_dir / entry.get("file", "")
        if not path.is_file():
            raise ValueError(f"인덱스 파일이 없습니다: {key}")
        baseline[key] = {"entry": entry, **markdown_facts(path)}
    new_keys = sorted(set(remote_by_key) - set(local_by_key), key=issue_number)
    changed_keys = sorted(
        (
            key
            for key in set(remote_by_key) & set(local_by_key)
            if not timestamps_equal(remote_by_key[key], local_by_key[key].get("updatedAt"))
        ),
        key=issue_number,
    )
    unchanged_keys = sorted(set(remote_by_key) & set(local_by_key) - set(changed_keys), key=issue_number)
    remote_absent_keys = sorted(set(local_by_key) - set(remote_by_key), key=issue_number)
    updated_regressions = sorted(
        (
            key
            for key in set(remote_by_key) & set(local_by_key)
            if timestamp_key(remote_by_key[key]) < timestamp_key(local_by_key[key].get("updatedAt"))
        ),
        key=issue_number,
    )
    reusable: list[dict[str, Any]] = []
    if source_audit_dir is not None:
        for key in unchanged_keys:
            source = source_audit_dir / "issues" / f"{key}.json"
            if not source.is_file():
                continue
            snapshot = read_json(source)
            if timestamps_equal(snapshot.get("remoteUpdated"), remote_by_key[key]) and snapshot_reusable(snapshot, key):
                reusable.append({"key": key, "sourceSha256": sha256_path(source)})
    reusable_keys = {item["key"] for item in reusable}
    plan = {
        "schemaVersion": 1,
        "auditId": manifest.get("auditId"),
        "sourceIndexSha256": sha256_path(mirror_dir / "index.json"),
        "sourceAuditId": source_audit_dir.name if source_audit_dir else None,
        "baseline": baseline,
        "newKeys": new_keys,
        "changedKeys": changed_keys,
        "unchangedKeys": unchanged_keys,
        "remoteAbsentKeys": remote_absent_keys,
        "remoteUpdatedRegressionKeys": updated_regressions,
        "requiredFetchKeys": sorted(set(remote_by_key) - reusable_keys, key=issue_number),
        "reusableSnapshots": reusable,
    }
    write_json(audit_dir / "plan.json", plan)
    return plan


def issue_number(key: str) -> int:
    return int(key.split("-", 1)[1])


def timestamp_key(value: Any) -> datetime:
    if not isinstance(value, str) or not value:
        return datetime.min.replace(tzinfo=timezone.utc)
    return datetime.fromisoformat(value.replace("Z", "+00:00"))


def timestamps_equal(left: Any, right: Any) -> bool:
    try:
        return timestamp_key(left) == timestamp_key(right)
    except (TypeError, ValueError):
        return False


def snapshot_reusable(snapshot: dict[str, Any], key: str) -> bool:
    comment = snapshot.get("comment", {})
    changelog = snapshot.get("changelog", {})
    comments_complete = (
        isinstance(comment, dict)
        and comment.get("total") is not None
        and comment.get("total") == comment.get("returned")
    )
    changelog_complete = (
        isinstance(changelog, dict)
        and changelog.get("total") is not None
        and changelog.get("total") == changelog.get("returned")
    )
    accepted_limit = snapshot.get("statusHistoryDisposition") == "PREVIOUS_LOCAL_PRESERVED_CONNECTOR_LIMIT"
    return (
        snapshot.get("requestedKey") == key
        and snapshot.get("responseKey") == key
        and comments_complete
        and (changelog_complete or accepted_limit)
        and snapshot.get("reconciliation") in {"REMOTE_APPLIED", "PREVIOUS_LOCAL_PRESERVED", "REMOTE_ABSENT_PRESERVED"}
    )


def reuse_snapshots(audit_dir: Path, source_audit_dir: Path) -> dict[str, int]:
    plan = read_json(audit_dir / "plan.json")
    if plan.get("sourceAuditId") != source_audit_dir.name:
        raise ValueError("plan의 sourceAuditId와 재사용 원천이 다릅니다.")
    copied = 0
    failures = 0
    target_dir = audit_dir / "issues"
    target_dir.mkdir(parents=True, exist_ok=True)
    for item in plan.get("reusableSnapshots", []):
        key = item["key"]
        source = source_audit_dir / "issues" / f"{key}.json"
        target = target_dir / f"{key}.json"
        expected = item.get("sourceSha256")
        if not source.is_file() or sha256_path(source) != expected:
            failures += 1
            continue
        shutil.copyfile(source, target)
        if sha256_path(target) != expected:
            failures += 1
            target.unlink(missing_ok=True)
            continue
        copied += 1
    manifest_path = audit_dir / "manifest.json"
    manifest = read_json(manifest_path)
    manifest["snapshotReuse"] = {
        "sourceAuditId": source_audit_dir.name,
        "plannedCount": len(plan.get("reusableSnapshots", [])),
        "sha256VerifiedCount": copied,
        "hashFailureCount": failures,
    }
    write_json(manifest_path, manifest)
    return {"copied": copied, "hashFailures": failures}


def record_issue(audit_dir: Path, requested_key: str, payload_path: Path) -> dict[str, Any]:
    nodes = issue_nodes(read_payload(payload_path))
    if len(nodes) != 1:
        raise ValueError(f"상세 응답 issue 수가 1이 아닙니다: {len(nodes)}")
    issue = nodes[0]
    if issue.get("key") != requested_key:
        raise ValueError(f"응답 key 불일치: {requested_key} != {issue.get('key')}")
    if not isinstance(issue.get("fields", {}).get("updated"), str):
        raise ValueError("상세 응답에 updated가 없습니다.")
    comments = issue.get("fields", {}).get("comment", {})
    changelog = issue.get("changelog", {})
    ledger = {
        "schemaVersion": 2,
        "requestedKey": requested_key,
        "responseKey": issue.get("key"),
        "fetchedAt": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        "remoteUpdated": issue.get("fields", {}).get("updated"),
        "comment": {
            "total": comments.get("total"),
            "returned": len(comments.get("comments", [])) if isinstance(comments.get("comments"), list) else None,
        },
        "changelog": {
            "total": changelog.get("total"),
            "returned": len(changelog.get("histories", [])) if isinstance(changelog.get("histories"), list) else None,
        },
        "reconciliation": "UNKNOWN",
        "localInvariantFailures": None,
        "remoteSnapshot": issue,
    }
    write_json(audit_dir / "issues" / f"{requested_key}.json", ledger)
    return ledger


def accept_status_limit(audit_dir: Path, issue_key: str) -> dict[str, Any]:
    plan = read_json(audit_dir / "plan.json")
    baseline = plan.get("baseline", {}).get(issue_key)
    if not isinstance(baseline, dict) or baseline.get("statusSectionPresent") is not True:
        raise ValueError("기존 로컬 상태 이력 섹션 기준선이 없습니다.")
    snapshot_path = audit_dir / "issues" / f"{issue_key}.json"
    snapshot = read_json(snapshot_path)
    if snapshot.get("requestedKey") != issue_key or snapshot.get("responseKey") != issue_key:
        raise ValueError("요청 key와 응답 key가 일치하지 않습니다.")
    comment = snapshot.get("comment", {})
    if comment.get("total") is None or comment.get("total") != comment.get("returned"):
        raise ValueError("댓글 snapshot이 완전하지 않습니다.")
    changelog = snapshot.get("changelog", {})
    total = changelog.get("total")
    returned = changelog.get("returned")
    if not isinstance(total, int) or not isinstance(returned, int) or total <= returned:
        raise ValueError("changelog 제한이 관측되지 않았습니다.")
    snapshot["statusHistoryDisposition"] = "PREVIOUS_LOCAL_PRESERVED_CONNECTOR_LIMIT"
    write_json(snapshot_path, snapshot)
    return {
        "key": issue_key,
        "statusHistoryDisposition": snapshot["statusHistoryDisposition"],
        "comment": comment,
        "changelog": changelog,
    }


def assert_live_baseline(plan: dict[str, Any], mirror_dir: Path) -> None:
    index_path = mirror_dir / "index.json"
    if not index_path.is_file() or sha256_path(index_path) != plan.get("sourceIndexSha256"):
        raise ValueError("실행 전 index.json 기준선이 변경되었습니다.")
    for key, before in plan.get("baseline", {}).items():
        entry = before.get("entry", {})
        path = mirror_dir / entry.get("file", "")
        if not path.is_file() or sha256_path(path) != before.get("fileHash"):
            raise ValueError(f"실행 전 Markdown 기준선이 변경되었습니다: {key}")


def stage_audit(audit_dir: Path, mirror_dir: Path) -> dict[str, Any]:
    from reconcile_jira_audit import reconcile

    plan = read_json(audit_dir / "plan.json")
    assert_live_baseline(plan, mirror_dir)
    staged_dir = audit_dir / "staged"
    if staged_dir.exists():
        raise ValueError("staged 디렉터리가 이미 존재합니다.")
    staged_dir.mkdir(parents=True)
    shutil.copy2(mirror_dir / "index.json", staged_dir / "index.json")
    for before in plan.get("baseline", {}).values():
        entry = before.get("entry", {})
        source = mirror_dir / entry.get("file", "")
        shutil.copy2(source, staged_dir / entry["file"])
    return reconcile(audit_dir, staged_dir, True)


def promote_audit(audit_dir: Path, mirror_dir: Path) -> dict[str, Any]:
    from verify_jira_audit import evaluate

    staged_dir = audit_dir / "staged"
    result = evaluate(audit_dir, staged_dir)
    if result.get("state") != "VERIFIED":
        raise ValueError(f"staged 감사가 VERIFIED가 아닙니다: {result.get('state')}")
    plan = read_json(audit_dir / "plan.json")
    assert_live_baseline(plan, mirror_dir)
    staged_index = read_json(staged_dir / "index.json")
    promoted = 0
    for entry in staged_index.get("issues", []):
        source = staged_dir / entry.get("file", "")
        target = mirror_dir / entry.get("file", "")
        if target.is_file() and sha256_path(target) == sha256_path(source):
            continue
        temporary = mirror_dir / f".{entry['key']}.promoting"
        shutil.copy2(source, temporary)
        os.replace(temporary, target)
        promoted += 1
    temporary_index = mirror_dir / ".index.json.promoting"
    shutil.copy2(staged_dir / "index.json", temporary_index)
    os.replace(temporary_index, mirror_dir / "index.json")
    return {"state": "PROMOTED", "markdownFiles": promoted, "issueCount": len(staged_index.get("issues", []))}


def parser() -> argparse.ArgumentParser:
    root = argparse.ArgumentParser(description=__doc__)
    commands = root.add_subparsers(dest="command", required=True)
    init = commands.add_parser("init")
    init.add_argument("audit_dir", type=Path)
    init.add_argument("project_key")
    init.add_argument("jql")
    init.add_argument("--audit-id")
    page = commands.add_parser("record-page")
    page.add_argument("audit_dir", type=Path)
    page.add_argument("payload", type=Path)
    page.add_argument("--request-cursor")
    plan = commands.add_parser("plan")
    plan.add_argument("audit_dir", type=Path)
    plan.add_argument("mirror_dir", type=Path)
    plan.add_argument("--source-audit", type=Path)
    reuse = commands.add_parser("reuse")
    reuse.add_argument("audit_dir", type=Path)
    reuse.add_argument("source_audit", type=Path)
    issue = commands.add_parser("record-issue")
    issue.add_argument("audit_dir", type=Path)
    issue.add_argument("requested_key")
    issue.add_argument("payload", type=Path)
    accept = commands.add_parser("accept-status-limit")
    accept.add_argument("audit_dir", type=Path)
    accept.add_argument("issue_key")
    stage = commands.add_parser("stage")
    stage.add_argument("audit_dir", type=Path)
    stage.add_argument("mirror_dir", type=Path)
    promote = commands.add_parser("promote")
    promote.add_argument("audit_dir", type=Path)
    promote.add_argument("mirror_dir", type=Path)
    return root


def main() -> int:
    arguments = parser().parse_args()
    if arguments.command == "init":
        result = init_audit(arguments.audit_dir, arguments.project_key, arguments.jql, arguments.audit_id)
    elif arguments.command == "record-page":
        result = record_page(arguments.audit_dir, arguments.payload, arguments.request_cursor)
    elif arguments.command == "plan":
        result = build_plan(arguments.audit_dir, arguments.mirror_dir, arguments.source_audit)
        result = {
            "auditId": result.get("auditId"),
            "new": len(result.get("newKeys", [])),
            "changed": len(result.get("changedKeys", [])),
            "unchanged": len(result.get("unchangedKeys", [])),
            "remoteAbsent": len(result.get("remoteAbsentKeys", [])),
            "requiredFetch": len(result.get("requiredFetchKeys", [])),
            "reusableSnapshots": len(result.get("reusableSnapshots", [])),
        }
    elif arguments.command == "reuse":
        result = reuse_snapshots(arguments.audit_dir, arguments.source_audit)
    elif arguments.command == "record-issue":
        result = record_issue(arguments.audit_dir, arguments.requested_key, arguments.payload)
        result = {
            "requestedKey": result.get("requestedKey"),
            "responseKey": result.get("responseKey"),
            "comment": result.get("comment"),
            "changelog": result.get("changelog"),
        }
    elif arguments.command == "accept-status-limit":
        result = accept_status_limit(arguments.audit_dir.resolve(), arguments.issue_key)
    elif arguments.command == "stage":
        result = stage_audit(arguments.audit_dir.resolve(), arguments.mirror_dir.resolve())
    else:
        result = promote_audit(arguments.audit_dir.resolve(), arguments.mirror_dir.resolve())
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
