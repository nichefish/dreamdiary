#!/usr/bin/env python3
"""Jira 감사 ledger의 신뢰 상태를 원자 관측값에서 계산한다."""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import sys
from datetime import datetime, timezone
from pathlib import Path
from typing import Any


TRUE = "TRUE"
FALSE = "FALSE"
UNKNOWN = "UNKNOWN"
COMMENT_HEADER = re.compile(r"^### .* · id (\S+)\s*$", re.MULTILINE)
STATUS_ROW = re.compile(r"^\| (?!시각 \|)(?!---)(.*?) \| (.*?) \| (.*?) \| (.*?) \|$", re.MULTILINE)
LIMITED_STATUS = "PREVIOUS_LOCAL_PRESERVED_CONNECTOR_LIMIT"


def read_json(path: Path) -> Any:
    with path.open("r", encoding="utf-8-sig") as stream:
        return json.load(stream)


def sha256_path(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def truth(condition: bool) -> str:
    return TRUE if condition else FALSE


def timestamp_key(value: Any) -> datetime:
    if not isinstance(value, str) or not value:
        return datetime.min.replace(tzinfo=timezone.utc)
    try:
        return datetime.fromisoformat(value.replace("Z", "+00:00"))
    except ValueError:
        return datetime.min.replace(tzinfo=timezone.utc)


def timestamps_equal(left: Any, right: Any) -> bool:
    if not isinstance(left, str) or not left or not isinstance(right, str) or not right:
        return False
    try:
        return datetime.fromisoformat(left.replace("Z", "+00:00")) == datetime.fromisoformat(
            right.replace("Z", "+00:00")
        )
    except ValueError:
        return False


def remote_status_count(snapshot: dict[str, Any]) -> int:
    issue = snapshot.get("remoteSnapshot", {})
    histories = issue.get("changelog", {}).get("histories", [])
    return sum(
        1
        for history in histories if isinstance(history, dict)
        for item in history.get("items", []) if isinstance(item, dict) and item.get("field") == "status"
    )


def reusable_snapshot_valid(snapshot: dict[str, Any], key: str, remote_updated: Any) -> bool:
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
    accepted_limit = snapshot.get("statusHistoryDisposition") == LIMITED_STATUS
    return (
        snapshot.get("requestedKey") == key
        and snapshot.get("responseKey") == key
        and timestamps_equal(snapshot.get("remoteUpdated"), remote_updated)
        and comments_complete
        and (changelog_complete or accepted_limit)
        and snapshot.get("reconciliation")
        in {"REMOTE_APPLIED", "PREVIOUS_LOCAL_PRESERVED", "REMOTE_ABSENT_PRESERVED"}
    )


def evaluate(audit_dir: Path, mirror_dir: Path | None = None) -> dict[str, Any]:
    manifest_path = audit_dir / "manifest.json"
    if not manifest_path.is_file():
        return {
            "state": "INCOMPLETE",
            "predicates": {"manifestReadable": UNKNOWN},
            "reason": "manifest.json이 없습니다.",
        }

    try:
        manifest = read_json(manifest_path)
    except (OSError, UnicodeError, json.JSONDecodeError) as error:
        return {
            "state": "FAILED",
            "predicates": {"manifestReadable": FALSE},
            "reason": f"manifest.json을 읽을 수 없습니다: {error}",
        }

    predicates: dict[str, str] = {"manifestReadable": TRUE}
    project_key = manifest.get("projectKey")
    jql = manifest.get("jql")
    predicates["scopeResolved"] = truth(
        isinstance(project_key, str)
        and bool(project_key)
        and isinstance(jql, str)
        and f"project = {project_key}" in jql
    )

    pages = manifest.get("pages")
    if not isinstance(pages, list) or not pages:
        predicates["paginationExhausted"] = UNKNOWN
        page_keys: list[str] = []
    else:
        page_keys = []
        chain_valid = True
        for index, page in enumerate(pages):
            if not isinstance(page, dict) or not isinstance(page.get("keys"), list):
                chain_valid = False
                continue
            page_keys.extend(page["keys"])
            request_cursor = page.get("requestCursor")
            if index == 0:
                chain_valid = chain_valid and request_cursor is None
            else:
                chain_valid = chain_valid and request_cursor == pages[index - 1].get("endCursor")
            if index < len(pages) - 1:
                chain_valid = chain_valid and page.get("hasNextPage") is True
        terminal_observed = pages[-1].get("hasNextPage") is False
        predicates["paginationExhausted"] = truth(chain_valid and terminal_observed)

    discovered = manifest.get("discoveredIssues")
    if not isinstance(discovered, list):
        predicates["discoverySetReadable"] = UNKNOWN
        discovered_keys: list[str] = []
    else:
        discovered_keys = [item.get("key") for item in discovered if isinstance(item, dict)]
        predicates["discoverySetReadable"] = truth(
            len(discovered_keys) == len(discovered)
            and all(isinstance(key, str) and bool(key) for key in discovered_keys)
        )

    predicates["duplicateIdentityCountZero"] = truth(
        len(discovered_keys) == len(set(discovered_keys))
    )
    predicates["pageAndDiscoveryIdentitySetsMatch"] = (
        UNKNOWN
        if not page_keys or not discovered_keys
        else truth(page_keys == discovered_keys)
    )

    issues_dir = audit_dir / "issues"
    snapshot_by_key: dict[str, dict[str, Any]] = {}
    invalid_snapshot_count = 0
    if issues_dir.is_dir():
        for path in issues_dir.glob("*.json"):
            try:
                snapshot = read_json(path)
            except (OSError, UnicodeError, json.JSONDecodeError):
                invalid_snapshot_count += 1
                continue
            requested_key = snapshot.get("requestedKey")
            response_key = snapshot.get("responseKey")
            if (
                requested_key != response_key
                or path.stem != requested_key
                or not isinstance(snapshot.get("remoteUpdated"), str)
            ):
                invalid_snapshot_count += 1
                continue
            snapshot_by_key[requested_key] = snapshot

    missing_keys = sorted(set(discovered_keys) - set(snapshot_by_key))
    extra_keys = sorted(set(snapshot_by_key) - set(discovered_keys))
    predicates["invalidSnapshotCountZero"] = truth(invalid_snapshot_count == 0)
    predicates["requiredFetchComplete"] = UNKNOWN if missing_keys else TRUE
    predicates["extraSnapshotCountZero"] = truth(not extra_keys)

    unresolved_truncation_keys: list[str] = []
    accepted_status_history_limit_keys: list[str] = []
    truncation_unknown_keys: list[str] = []
    reconciliation_unknown_keys: list[str] = []
    local_invariant_failure_keys: list[str] = []
    local_invariant_unknown_keys: list[str] = []
    index_by_key: dict[str, dict[str, Any]] = {}
    index_issue_count_valid: bool | None = None
    absent_preservation_failures: list[str] = []
    reuse_failures: list[str | None] = []
    if mirror_dir is not None:
        try:
            index = read_json(mirror_dir / "index.json")
            index_issues = index.get("issues", [])
            index_by_key = {
                item["key"]: item
                for item in index_issues
                if isinstance(item, dict) and isinstance(item.get("key"), str)
            }
            index_issue_count_valid = index.get("scope", {}).get("issueCount") == len(index_issues)
        except (OSError, UnicodeError, json.JSONDecodeError):
            index_issue_count_valid = False

    if manifest.get("schemaVersion", 1) >= 2:
        plan_path = audit_dir / "plan.json"
        try:
            plan = read_json(plan_path)
            baseline = plan.get("baseline", {})
            predicates["regressionEvidenceReadable"] = truth(
                isinstance(baseline, dict)
                and plan.get("auditId") == manifest.get("auditId")
                and isinstance(plan.get("sourceIndexSha256"), str)
            )
        except (OSError, UnicodeError, json.JSONDecodeError):
            plan = {}
            baseline = {}
            predicates["regressionEvidenceReadable"] = FALSE
        if isinstance(baseline, dict):
            remote_by_key = {
                item["key"]: item.get("updated")
                for item in discovered
                if isinstance(item, dict) and isinstance(item.get("key"), str)
            } if isinstance(discovered, list) else {}
            local_before = {
                key: value.get("entry", {})
                for key, value in baseline.items()
                if isinstance(value, dict)
            }
            expected_new = set(remote_by_key) - set(local_before)
            expected_changed = {
                key
                for key in set(remote_by_key) & set(local_before)
                if not timestamps_equal(remote_by_key[key], local_before[key].get("updatedAt"))
            }
            expected_absent = set(local_before) - set(remote_by_key)
            expected_updated_regressions = {
                key
                for key in set(remote_by_key) & set(local_before)
                if timestamp_key(remote_by_key[key]) < timestamp_key(local_before[key].get("updatedAt"))
            }
            predicates["remoteUpdatedRegressionCountZero"] = truth(
                not expected_updated_regressions
            )
            predicates["localKeyLossCountZero"] = truth(set(baseline) <= set(index_by_key))
            for key in plan.get("remoteAbsentKeys", []):
                before = baseline.get(key, {})
                entry = index_by_key.get(key)
                path = mirror_dir / entry.get("file", "") if mirror_dir is not None and entry else None
                if (
                    entry is None
                    or entry != before.get("entry")
                    or path is None
                    or not path.is_file()
                    or sha256_path(path) != before.get("fileHash")
                ):
                    absent_preservation_failures.append(key)
            predicates["remoteAbsentPreservationSatisfied"] = truth(not absent_preservation_failures)
            reusable = plan.get("reusableSnapshots", [])
            valid_reusable_keys: set[str] = set()
            for item in reusable if isinstance(reusable, list) else []:
                key = item.get("key") if isinstance(item, dict) else None
                path = issues_dir / f"{key}.json" if key else None
                try:
                    snapshot = read_json(path) if path is not None and path.is_file() else {}
                except (OSError, UnicodeError, json.JSONDecodeError):
                    snapshot = {}
                if (
                    key not in remote_by_key
                    or path is None
                    or not path.is_file()
                    or sha256_path(path) != item.get("sourceSha256")
                    or not reusable_snapshot_valid(snapshot, key, remote_by_key.get(key))
                ):
                    reuse_failures.append(key)
                else:
                    valid_reusable_keys.add(key)
            expected_required = set(remote_by_key) - valid_reusable_keys
            predicates["candidateSetMatches"] = truth(
                set(plan.get("newKeys", [])) == expected_new
                and set(plan.get("changedKeys", [])) == expected_changed
                and set(plan.get("remoteAbsentKeys", [])) == expected_absent
                and set(plan.get("requiredFetchKeys", [])) == expected_required
                and set(plan.get("remoteUpdatedRegressionKeys", [])) == expected_updated_regressions
            )
            reuse_observed = manifest.get("snapshotReuse", {})
            if not reusable and not reuse_observed:
                predicates["reusedSnapshotIntegritySatisfied"] = TRUE
            else:
                predicates["reusedSnapshotIntegritySatisfied"] = truth(
                    not reuse_failures
                    and isinstance(reuse_observed, dict)
                    and reuse_observed.get("plannedCount") == len(reusable)
                    and reuse_observed.get("sha256VerifiedCount") == len(reusable)
                    and reuse_observed.get("hashFailureCount") == 0
                )
        else:
            predicates["candidateSetMatches"] = UNKNOWN
            predicates["remoteUpdatedRegressionCountZero"] = UNKNOWN
            predicates["localKeyLossCountZero"] = UNKNOWN
            predicates["remoteAbsentPreservationSatisfied"] = UNKNOWN
            predicates["reusedSnapshotIntegritySatisfied"] = UNKNOWN
    for key, snapshot in snapshot_by_key.items():
        comment = snapshot.get("comment")
        changelog = snapshot.get("changelog")
        if not isinstance(comment, dict) or comment.get("total") is None or comment.get("returned") is None:
            truncation_unknown_keys.append(key)
        elif comment.get("total") != comment.get("returned"):
            unresolved_truncation_keys.append(key)
        if not isinstance(changelog, dict) or changelog.get("total") is None or changelog.get("returned") is None:
            truncation_unknown_keys.append(key)
        elif changelog.get("total") != changelog.get("returned"):
            if (
                snapshot.get("statusHistoryDisposition")
                == "PREVIOUS_LOCAL_PRESERVED_CONNECTOR_LIMIT"
                and isinstance(comment, dict)
                and comment.get("total") == comment.get("returned")
            ):
                accepted_status_history_limit_keys.append(key)
            else:
                unresolved_truncation_keys.append(key)
        reconciliation = snapshot.get("reconciliation")
        if reconciliation not in {"REMOTE_APPLIED", "PREVIOUS_LOCAL_PRESERVED", "REMOTE_ABSENT_PRESERVED"}:
            reconciliation_unknown_keys.append(key)
        if mirror_dir is None:
            local_invariant_unknown_keys.append(key)
        else:
            entry = index_by_key.get(key)
            path = mirror_dir / entry.get("file", "") if entry else None
            try:
                markdown = path.read_text(encoding="utf-8-sig") if path and path.is_file() else ""
            except (OSError, UnicodeError):
                markdown = ""
            comment_section = markdown.split("## 댓글\n", 1)[1].split("## 상태 이력\n", 1)[0] if "## 댓글\n" in markdown and "## 상태 이력\n" in markdown else ""
            status_section = markdown.split("## 상태 이력\n", 1)[1] if "## 상태 이력\n" in markdown else ""
            comment_count = len(COMMENT_HEADER.findall(comment_section))
            status_count = len(STATUS_ROW.findall(status_section))
            expected_comments = snapshot.get("comment", {}).get("returned")
            failures = not markdown or entry is None
            if reconciliation == "PREVIOUS_LOCAL_PRESERVED":
                previous = snapshot.get("previousLocal", {})
                failures = (
                    failures
                    or not isinstance(previous, dict)
                    or path is None
                    or sha256_path(path) != previous.get("sha256")
                )
            elif snapshot.get("statusHistoryDisposition") == LIMITED_STATUS:
                failures = failures or entry.get("commentCount") != comment_count or expected_comments != comment_count
                failures = failures or entry.get("statusTransitionCount") != status_count
            else:
                failures = failures or entry.get("commentCount") != comment_count or expected_comments != comment_count
                failures = failures or entry.get("statusTransitionCount") != status_count
                failures = failures or remote_status_count(snapshot) != status_count
            if failures:
                local_invariant_failure_keys.append(key)

    predicates["unresolvedTruncationCountZero"] = (
        FALSE if unresolved_truncation_keys else UNKNOWN if truncation_unknown_keys else TRUE
    )
    predicates["reconciliationComplete"] = UNKNOWN if reconciliation_unknown_keys else TRUE
    predicates["localInvariantSatisfied"] = (
        FALSE if local_invariant_failure_keys else UNKNOWN if local_invariant_unknown_keys else TRUE
    )
    if index_issue_count_valid is False:
        predicates["localInvariantSatisfied"] = FALSE

    values = set(predicates.values())
    if FALSE in values:
        state = "FAILED"
    elif UNKNOWN in values:
        state = "INCOMPLETE"
    else:
        state = "VERIFIED"

    return {
        "state": state,
        "predicates": predicates,
        "counts": {
            "pages": len(pages) if isinstance(pages, list) else 0,
            "discovered": len(discovered_keys),
            "snapshots": len(snapshot_by_key),
            "missingSnapshots": len(missing_keys),
            "extraSnapshots": len(extra_keys),
            "invalidSnapshots": invalid_snapshot_count,
            "unresolvedTruncations": len(set(unresolved_truncation_keys)),
            "acceptedStatusHistoryLimits": len(set(accepted_status_history_limit_keys)),
            "truncationUnknown": len(set(truncation_unknown_keys)),
            "reconciliationUnknown": len(reconciliation_unknown_keys),
            "localInvariantFailures": len(local_invariant_failure_keys),
            "localInvariantUnknown": len(local_invariant_unknown_keys),
            "remoteAbsentPreservationFailures": len(absent_preservation_failures),
            "reusedSnapshotIntegrityFailures": len(reuse_failures),
        },
        "samples": {
            "missingSnapshots": missing_keys[:20],
            "extraSnapshots": extra_keys[:20],
            "unresolvedTruncations": sorted(set(unresolved_truncation_keys))[:20],
            "acceptedStatusHistoryLimits": sorted(set(accepted_status_history_limit_keys))[:20],
            "truncationUnknown": sorted(set(truncation_unknown_keys))[:20],
            "reconciliationUnknown": reconciliation_unknown_keys[:20],
            "localInvariantFailures": local_invariant_failure_keys[:20],
            "localInvariantUnknown": local_invariant_unknown_keys[:20],
            "remoteAbsentPreservationFailures": absent_preservation_failures[:20],
            "reusedSnapshotIntegrityFailures": reuse_failures[:20],
        },
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("audit_dir", type=Path)
    parser.add_argument("mirror_dir", type=Path, nargs="?")
    arguments = parser.parse_args()
    result = evaluate(
        arguments.audit_dir.resolve(),
        arguments.mirror_dir.resolve() if arguments.mirror_dir is not None else None,
    )
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return {"VERIFIED": 0, "FAILED": 1, "INCOMPLETE": 2}[result["state"]]


if __name__ == "__main__":
    sys.exit(main())
