#!/usr/bin/env python3
"""Jira 감사 snapshot을 기존 로컬 미러와 결정론적으로 reconciliation한다."""

from __future__ import annotations

import argparse
import hashlib
import json
import re
from copy import deepcopy
from datetime import datetime
from pathlib import Path
from typing import Any


COMMENT_ID = re.compile(r"^### .* · id (\S+)\s*$", re.MULTILINE)
STATUS_ROW = re.compile(r"^\| (?!시각 \|)(?!---)(.*?) \| (.*?) \| (.*?) \| (.*?) \|$", re.MULTILINE)
INVALID_FILENAME = re.compile(r'[<>:"/\\|?*]')
LIMITED_STATUS = "PREVIOUS_LOCAL_PRESERVED_CONNECTOR_LIMIT"


def read_json(path: Path) -> Any:
    with path.open("r", encoding="utf-8-sig") as stream:
        return json.load(stream)


def write_json(path: Path, value: Any) -> None:
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def text(value: Any, default: str = "") -> str:
    return default if value is None else str(value)


def display_name(value: Any) -> str:
    return text(value.get("displayName")) if isinstance(value, dict) else ""


def named(value: Any) -> str:
    return text(value.get("name")) if isinstance(value, dict) else ""


def split_status_section(markdown: str) -> str:
    marker = "## 상태 이력\n"
    offset = markdown.find(marker)
    return markdown[offset:] if offset >= 0 else ""


def description_present(markdown: str) -> bool:
    if "## 설명\n" not in markdown or "## 댓글\n" not in markdown:
        return False
    value = markdown.split("## 설명\n", 1)[1].split("## 댓글\n", 1)[0].strip()
    return bool(value and value != "(없음)")


def local_status_identities(markdown: str) -> set[str]:
    return {"|".join(row) for row in STATUS_ROW.findall(split_status_section(markdown))}


def remote_status_identities(snapshot: dict[str, Any]) -> set[str]:
    return {
        "|".join((row["created"], row["author"], row["from"], row["to"]))
        for row in status_rows(snapshot)
    }


def status_rows(snapshot: dict[str, Any]) -> list[dict[str, str]]:
    rows: list[dict[str, str]] = []
    histories = snapshot.get("changelog", {}).get("histories", [])
    for history in histories if isinstance(histories, list) else []:
        for item in history.get("items", []):
            if item.get("field") != "status":
                continue
            rows.append(
                {
                    "created": text(history.get("created")),
                    "author": display_name(history.get("author")),
                    "from": text(item.get("fromString")),
                    "to": text(item.get("toString")),
                }
            )
    return sorted(rows, key=lambda row: row["created"])


def render_markdown(issue: dict[str, Any], preserved_status: str = "") -> tuple[str, int, int]:
    fields = issue.get("fields", {})
    source_url = text(issue.get("webUrl"), "https://polaqube.atlassian.net/browse/" + issue["key"])
    comments = fields.get("comment", {}).get("comments", [])
    comments = sorted(comments if isinstance(comments, list) else [], key=lambda item: text(item.get("created")))
    parent = fields.get("parent")
    parent_text = ""
    if isinstance(parent, dict):
        parent_text = " ".join(filter(None, [text(parent.get("key")), text(parent.get("fields", {}).get("summary"))]))
    labels = fields.get("labels")
    label_text = ", ".join(map(str, labels)) if isinstance(labels, list) else ""
    lines = [
        f"# {issue['key']} {text(fields.get('summary'))}",
        "",
        f"- sourceUrl: {source_url}",
        f"- id: {text(issue.get('id'))}",
        f"- issuetype: {named(fields.get('issuetype'))}",
        f"- status: {named(fields.get('status'))}",
        f"- priority: {named(fields.get('priority'))}",
        f"- assignee: {display_name(fields.get('assignee'))}",
        f"- reporter: {display_name(fields.get('reporter'))}",
        f"- created: {text(fields.get('created'))}",
        f"- updated: {text(fields.get('updated'))}",
        f"- resolution: {named(fields.get('resolution'))}",
        f"- parent: {parent_text}",
        f"- labels: {label_text}",
        "",
        "## 설명",
        "",
        text(fields.get("description"), "(없음)") or "(없음)",
        "",
        "## 댓글",
        "",
    ]
    if comments:
        for index, comment in enumerate(comments):
            if index:
                lines.append("")
            lines.extend(
                [
                    f"### {text(comment.get('created'))} · {display_name(comment.get('author'))} · id {text(comment.get('id'))}",
                    "",
                    text(comment.get("body"), "(없음)") or "(없음)",
                ]
            )
    else:
        lines.append("(없음)")
    lines.append("")
    rows = status_rows(issue)
    if preserved_status:
        lines.extend(preserved_status.rstrip("\n").splitlines())
        status_count = -1
    else:
        lines.extend(["## 상태 이력", "", "| 시각 | 작성자 | 이전 | 이후 |", "| --- | --- | --- | --- |"])
        for row in rows:
            values = [row[name].replace("|", "\\|").replace("\n", " ") for name in ("created", "author", "from", "to")]
            lines.append(f"| {' | '.join(values)} |")
        status_count = len(rows)
    return "\n".join(lines).rstrip() + "\n", len(comments), status_count


def safe_filename(key: str, summary: str) -> str:
    safe = INVALID_FILENAME.sub("_", summary).rstrip(" .")[:160]
    return f"{key}__{safe or '제목 없음'}.md"


def sha256(value: bytes) -> str:
    return hashlib.sha256(value).hexdigest()


def parsed_timestamp(value: Any) -> datetime | None:
    try:
        return datetime.fromisoformat(text(value).replace("Z", "+00:00"))
    except ValueError:
        return None


def reconcile(audit_dir: Path, mirror_dir: Path, apply: bool) -> dict[str, Any]:
    manifest = read_json(audit_dir / "manifest.json")
    index_path = mirror_dir / "index.json"
    index = read_json(index_path)
    issues = index.get("issues", [])
    by_key = {item["key"]: item for item in issues}
    before_index = deepcopy(index)
    counts = {"remoteApplied": 0, "previousLocalPreserved": 0, "new": 0, "limitedStatusPreserved": 0}
    changed_files = 0

    for snapshot_path in sorted((audit_dir / "issues").glob("*.json")):
        ledger = read_json(snapshot_path)
        issue = ledger["remoteSnapshot"]
        key = ledger["requestedKey"]
        entry = by_key.get(key)
        old_path = mirror_dir / entry["file"] if entry else None
        old_bytes = old_path.read_bytes() if old_path and old_path.is_file() else None
        old_markdown = old_bytes.decode("utf-8-sig").replace("\r\n", "\n") if old_bytes is not None else ""
        old_comment_ids = set(COMMENT_ID.findall(old_markdown))
        old_status_ids = local_status_identities(old_markdown)
        remote_comments = issue.get("fields", {}).get("comment", {}).get("comments", [])
        remote_comment_ids = {text(item.get("id")) for item in remote_comments if item.get("id") is not None}
        changelog = ledger.get("changelog", {})
        changelog_complete = (
            isinstance(changelog, dict)
            and changelog.get("total") is not None
            and changelog.get("total") == changelog.get("returned")
        )
        regression_reasons: list[str] = []
        if entry:
            remote_updated = parsed_timestamp(issue.get("fields", {}).get("updated"))
            previous_updated = parsed_timestamp(entry.get("updatedAt"))
            if remote_updated is None or previous_updated is None:
                regression_reasons.append("REMOTE_UPDATED_UNKNOWN")
            elif remote_updated < previous_updated:
                regression_reasons.append("REMOTE_UPDATED_REGRESSION")
        if old_comment_ids - remote_comment_ids:
            regression_reasons.append("PREVIOUS_COMMENT_ID_MISSING")
        if changelog_complete and old_status_ids - remote_status_identities(issue):
            regression_reasons.append("PREVIOUS_STATUS_IDENTITY_MISSING")
        if description_present(old_markdown) and not text(issue.get("fields", {}).get("description")).strip():
            regression_reasons.append("REMOTE_DESCRIPTION_EMPTY")

        if regression_reasons:
            ledger["reconciliation"] = "PREVIOUS_LOCAL_PRESERVED"
            ledger["regressionReasons"] = regression_reasons
            ledger["previousLocal"] = {
                "file": entry.get("file") if entry else None,
                "sha256": sha256(old_bytes) if old_bytes is not None else None,
                "commentIds": sorted(old_comment_ids),
                "statusIdentities": sorted(old_status_ids),
            }
            ledger["localInvariantFailures"] = (
                0 if old_bytes is not None and old_path is not None and sha256(old_path.read_bytes()) == sha256(old_bytes) else 1
            )
            counts["previousLocalPreserved"] += 1
            if apply:
                write_json(snapshot_path, ledger)
            continue

        limited = ledger.get("statusHistoryDisposition") == LIMITED_STATUS
        preserved_status = split_status_section(old_markdown) if limited else ""
        if limited and not preserved_status:
            ledger["reconciliation"] = "UNKNOWN"
            ledger["localInvariantFailures"] = 1
            if apply:
                write_json(snapshot_path, ledger)
            continue

        rendered, comment_count, status_count = render_markdown(issue, preserved_status)
        fields = issue.get("fields", {})
        if entry is None:
            filename = safe_filename(key, text(fields.get("summary")))
            entry = {"key": key, "file": filename}
            issues.append(entry)
            by_key[key] = entry
            counts["new"] += 1
        target = mirror_dir / entry["file"]
        if not limited:
            entry["statusTransitionCount"] = status_count
        else:
            counts["limitedStatusPreserved"] += 1
        entry.update(
            {
                "id": text(issue.get("id")),
                "title": text(fields.get("summary")),
                "status": named(fields.get("status")),
                "issuetype": named(fields.get("issuetype")),
                "sourceUrl": text(issue.get("webUrl"), f"https://polaqube.atlassian.net/browse/{key}"),
                "createdAt": text(fields.get("created")),
                "updatedAt": text(fields.get("updated")),
                "commentCount": comment_count,
            }
        )
        rendered_bytes = rendered.encode("utf-8")
        if old_bytes != rendered_bytes:
            changed_files += 1
        if apply:
            target.write_bytes(rendered_bytes)
        ledger["reconciliation"] = "REMOTE_APPLIED"
        ledger["localInvariantFailures"] = 0
        if apply:
            write_json(snapshot_path, ledger)
        counts["remoteApplied"] += 1

    issues.sort(key=lambda item: int(item["key"].split("-", 1)[1]))
    index["scope"]["issueCount"] = len(issues)
    discovered_keys = [item["key"] for item in manifest.get("discoveredIssues", [])]
    if discovered_keys:
        discovered_key_to = max(int(key.split("-", 1)[1]) for key in discovered_keys)
        index["scope"]["keyTo"] = max(int(index["scope"].get("keyTo", 0)), discovered_key_to)
    if apply:
        write_json(index_path, index)
    return {
        **counts,
        "changedMarkdownFiles": changed_files,
        "indexChanged": before_index != index,
        "issueCount": len(issues),
        "mode": "apply" if apply else "dry-run",
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("audit_dir", type=Path)
    parser.add_argument("mirror_dir", type=Path)
    parser.add_argument("--apply", action="store_true")
    arguments = parser.parse_args()
    result = reconcile(arguments.audit_dir.resolve(), arguments.mirror_dir.resolve(), arguments.apply)
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
