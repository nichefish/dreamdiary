from __future__ import annotations

import json
import io
import sys
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch


SCRIPTS = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(SCRIPTS))

import jira_audit_pipeline as pipeline  # noqa: E402
import reconcile_jira_audit as reconciler  # noqa: E402
import verify_jira_audit as verifier  # noqa: E402


def write_json(path: Path, value: object) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def markdown(key: str, comment_ids: list[str], status_rows: list[tuple[str, str, str, str]], description: str = "설명") -> str:
    comments = "\n\n".join(f"### 2026-01-01T00:00:00+09:00 · Alice · id {item}\n\n본문" for item in comment_ids) or "(없음)"
    rows = "\n".join(f"| {created} | {author} | {before} | {after} |" for created, author, before, after in status_rows)
    return (
        f"# {key} 제목\n\n- updated: 2026-01-01T00:00:00+09:00\n\n"
        f"## 설명\n\n{description}\n\n## 댓글\n\n{comments}\n\n"
        "## 상태 이력\n\n| 시각 | 작성자 | 이전 | 이후 |\n| --- | --- | --- | --- |\n"
        f"{rows}\n"
    )


def issue(key: str, updated: str, comment_ids: list[str], status_rows: list[tuple[str, str, str, str]], description: str = "설명") -> dict:
    comments = [
        {"id": item, "created": "2026-01-01T00:00:00+09:00", "author": {"displayName": "Alice"}, "body": "본문"}
        for item in comment_ids
    ]
    histories = [
        {
            "created": created,
            "author": {"displayName": author},
            "items": [{"field": "status", "fromString": before, "toString": after}],
        }
        for created, author, before, after in status_rows
    ]
    return {
        "key": key,
        "id": key.split("-")[1],
        "webUrl": f"https://example.atlassian.net/browse/{key}",
        "fields": {
            "summary": "제목",
            "description": description,
            "updated": updated,
            "created": "2026-01-01T00:00:00+09:00",
            "comment": {"total": len(comments), "comments": comments},
            "status": {"name": "Open"},
            "issuetype": {"name": "Task"},
            "priority": {"name": "Medium"},
            "labels": [],
        },
        "changelog": {"total": len(histories), "histories": histories},
    }


class JiraAuditPipelineTest(unittest.TestCase):
    def setUp(self) -> None:
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        self.audit = self.root / "audit" / "A2"
        self.mirror = self.root / "mirror"
        self.source = self.root / "audit" / "A1"
        self.mirror.mkdir(parents=True)

    def tearDown(self) -> None:
        self.temp.cleanup()

    def seed_mirror(self) -> None:
        rows = [("2026-01-01T00:00:00+09:00", "Alice", "Open", "Done")]
        entries = []
        for number in (1, 2):
            key = f"TEST-{number}"
            filename = f"{key}__제목.md"
            (self.mirror / filename).write_text(markdown(key, [f"c{number}"], rows), encoding="utf-8")
            entries.append(
                {
                    "key": key,
                    "file": filename,
                    "updatedAt": f"2026-01-0{number}T00:00:00+09:00",
                    "commentCount": 1,
                    "statusTransitionCount": 1,
                }
            )
        write_json(self.mirror / "index.json", {"scope": {"issueCount": 2, "keyTo": 2}, "issues": entries})

    def seed_manifest(self) -> None:
        write_json(
            self.audit / "manifest.json",
            {
                "schemaVersion": 2,
                "auditId": "A2",
                "projectKey": "TEST",
                "jql": "project = TEST ORDER BY key ASC",
                "pages": [{"requestCursor": None, "endCursor": None, "hasNextPage": False, "keys": ["TEST-1", "TEST-2", "TEST-3"]}],
                "discoveredIssues": [
                    {"key": "TEST-1", "updated": "2026-01-01T00:00:00+09:00"},
                    {"key": "TEST-2", "updated": "2026-01-03T00:00:00+09:00"},
                    {"key": "TEST-3", "updated": "2026-01-03T00:00:00+09:00"},
                ],
                "observed": {"paginationFailureCount": 0, "duplicateIdentityCount": 0},
            },
        )

    def test_normalize_changes_only_datetime_offset(self) -> None:
        self.assertEqual("TEST-2283", pipeline.normalize("TEST-2283"))
        self.assertEqual("2026-01-01T00:00:00+09:00", pipeline.normalize("2026-01-01T00:00:00+0900"))

    def test_record_page_rejects_cursor_discontinuity(self) -> None:
        pipeline.init_audit(self.audit, "TEST", "project = TEST", "A2")
        payload = self.root / "page.json"
        write_json(payload, {"issues": {"nodes": [], "pageInfo": {"hasNextPage": False, "endCursor": None}}})
        with self.assertRaisesRegex(ValueError, "cursor 불연속"):
            pipeline.record_page(self.audit, payload, "wrong")

    def test_record_page_accepts_standard_input_without_payload_file(self) -> None:
        pipeline.init_audit(self.audit, "TEST", "project = TEST", "A2")
        payload = {"issues": {"nodes": [], "pageInfo": {"hasNextPage": False, "endCursor": None}}}
        with patch("sys.stdin", io.StringIO(json.dumps(payload))):
            page = pipeline.record_page(self.audit, Path("-"), None)
        self.assertFalse(page["hasNextPage"])

    def test_plan_derives_candidates_from_manifest_and_index(self) -> None:
        self.seed_mirror()
        self.seed_manifest()
        plan = pipeline.build_plan(self.audit, self.mirror)
        self.assertEqual(["TEST-3"], plan["newKeys"])
        self.assertEqual(["TEST-2"], plan["changedKeys"])
        self.assertEqual(["TEST-1"], plan["unchangedKeys"])
        self.assertEqual([], plan["remoteAbsentKeys"])
        self.assertEqual(["TEST-1", "TEST-2", "TEST-3"], plan["requiredFetchKeys"])

    def test_plan_detects_remote_updated_regression(self) -> None:
        self.seed_mirror()
        self.seed_manifest()
        manifest = pipeline.read_json(self.audit / "manifest.json")
        manifest["discoveredIssues"][1]["updated"] = "2026-01-01T00:00:00+09:00"
        write_json(self.audit / "manifest.json", manifest)
        plan = pipeline.build_plan(self.audit, self.mirror)
        self.assertEqual(["TEST-2"], plan["remoteUpdatedRegressionKeys"])

    def test_plan_treats_equivalent_timezone_offset_formats_as_unchanged(self) -> None:
        self.seed_mirror()
        self.seed_manifest()
        index = pipeline.read_json(self.mirror / "index.json")
        index["issues"][0]["updatedAt"] = "2026-01-01T00:00:00+0900"
        write_json(self.mirror / "index.json", index)
        plan = pipeline.build_plan(self.audit, self.mirror)
        self.assertNotIn("TEST-1", plan["changedKeys"])
        self.assertIn("TEST-1", plan["unchangedKeys"])

    def test_timestamp_comparison_rejects_invalid_values(self) -> None:
        self.assertFalse(pipeline.timestamps_equal("invalid", "invalid"))
        self.assertFalse(verifier.timestamps_equal("invalid", "invalid"))

    def test_reuse_rejects_source_modified_after_plan(self) -> None:
        self.seed_mirror()
        self.seed_manifest()
        source_snapshot = self.source / "issues" / "TEST-1.json"
        write_json(
            source_snapshot,
            {
                "requestedKey": "TEST-1", "responseKey": "TEST-1", "remoteUpdated": "2026-01-01T00:00:00+09:00",
                "comment": {"total": 1, "returned": 1}, "changelog": {"total": 1, "returned": 1},
                "reconciliation": "REMOTE_APPLIED",
            },
        )
        pipeline.build_plan(self.audit, self.mirror, self.source)
        source_snapshot.write_text("{}\n", encoding="utf-8")
        result = pipeline.reuse_snapshots(self.audit, self.source)
        self.assertEqual({"copied": 0, "hashFailures": 1}, result)

    def test_plan_requires_unchanged_issue_without_reusable_snapshot(self) -> None:
        self.seed_mirror()
        self.seed_manifest()
        plan = pipeline.build_plan(self.audit, self.mirror, self.source)
        self.assertIn("TEST-1", plan["requiredFetchKeys"])

    def test_verifier_rejects_semantically_invalid_reused_snapshot(self) -> None:
        self.seed_mirror()
        self.seed_manifest()
        source_snapshot = self.source / "issues" / "TEST-1.json"
        write_json(
            source_snapshot,
            {
                "requestedKey": "TEST-1",
                "responseKey": "TEST-1",
                "remoteUpdated": "2026-01-01T00:00:00+09:00",
                "comment": {"total": 1, "returned": 1},
                "changelog": {"total": 1, "returned": 1},
                "reconciliation": "REMOTE_APPLIED",
            },
        )
        pipeline.build_plan(self.audit, self.mirror, self.source)
        pipeline.reuse_snapshots(self.audit, self.source)
        copied = pipeline.read_json(self.audit / "issues" / "TEST-1.json")
        copied["comment"]["returned"] = 0
        write_json(self.audit / "issues" / "TEST-1.json", copied)
        result = verifier.evaluate(self.audit, self.mirror)
        self.assertEqual("FALSE", result["predicates"]["candidateSetMatches"])
        self.assertEqual("FALSE", result["predicates"]["reusedSnapshotIntegritySatisfied"])

    def test_accept_status_limit_requires_complete_comments_and_existing_status(self) -> None:
        self.seed_mirror()
        self.seed_manifest()
        pipeline.build_plan(self.audit, self.mirror)
        remote = issue("TEST-1", "2026-01-03T00:00:00+09:00", ["c1"], [])
        remote["changelog"] = {"total": 2, "histories": []}
        write_json(
            self.audit / "issues" / "TEST-1.json",
            {
                "requestedKey": "TEST-1",
                "responseKey": "TEST-1",
                "comment": {"total": 1, "returned": 1},
                "changelog": {"total": 2, "returned": 0},
                "remoteSnapshot": remote,
            },
        )
        result = pipeline.accept_status_limit(self.audit, "TEST-1")
        self.assertEqual("PREVIOUS_LOCAL_PRESERVED_CONNECTOR_LIMIT", result["statusHistoryDisposition"])

        snapshot = pipeline.read_json(self.audit / "issues" / "TEST-1.json")
        snapshot["comment"]["returned"] = 0
        write_json(self.audit / "issues" / "TEST-1.json", snapshot)
        with self.assertRaisesRegex(ValueError, "댓글 snapshot"):
            pipeline.accept_status_limit(self.audit, "TEST-1")

    def test_accept_status_limit_allows_existing_empty_status_section(self) -> None:
        self.seed_mirror()
        empty_status_path = self.mirror / "TEST-1__제목.md"
        empty_status_path.write_text(markdown("TEST-1", ["c1"], []), encoding="utf-8")
        self.seed_manifest()
        pipeline.build_plan(self.audit, self.mirror)
        remote = issue("TEST-1", "2026-01-03T00:00:00+09:00", ["c1"], [])
        write_json(
            self.audit / "issues" / "TEST-1.json",
            {
                "requestedKey": "TEST-1",
                "responseKey": "TEST-1",
                "comment": {"total": 1, "returned": 1},
                "changelog": {"total": 2, "returned": 0},
                "remoteSnapshot": remote,
            },
        )
        result = pipeline.accept_status_limit(self.audit, "TEST-1")
        self.assertEqual("PREVIOUS_LOCAL_PRESERVED_CONNECTOR_LIMIT", result["statusHistoryDisposition"])

    def test_missing_comment_preserves_previous_local_bytes(self) -> None:
        self.seed_mirror()
        ledger = issue("TEST-1", "2026-01-03T00:00:00+09:00", [], [("2026-01-01T00:00:00+09:00", "Alice", "Open", "Done")])
        write_json(
            self.audit / "issues" / "TEST-1.json",
            {
                "requestedKey": "TEST-1", "responseKey": "TEST-1", "comment": {"total": 0, "returned": 0},
                "changelog": {"total": 1, "returned": 1}, "reconciliation": "UNKNOWN", "remoteSnapshot": ledger,
            },
        )
        write_json(self.audit / "manifest.json", {"discoveredIssues": [{"key": "TEST-1"}]})
        before = (self.mirror / "TEST-1__제목.md").read_bytes()
        reconciler.reconcile(self.audit, self.mirror, True)
        after = (self.mirror / "TEST-1__제목.md").read_bytes()
        snapshot = pipeline.read_json(self.audit / "issues" / "TEST-1.json")
        self.assertEqual(before, after)
        self.assertEqual("PREVIOUS_LOCAL_PRESERVED", snapshot["reconciliation"])
        self.assertIn("PREVIOUS_COMMENT_ID_MISSING", snapshot["regressionReasons"])

    def test_missing_status_identity_preserves_previous_local_bytes(self) -> None:
        self.seed_mirror()
        remote = issue("TEST-2", "2026-01-03T00:00:00+09:00", ["c2"], [])
        write_json(
            self.audit / "issues" / "TEST-2.json",
            {
                "requestedKey": "TEST-2", "responseKey": "TEST-2", "comment": {"total": 1, "returned": 1},
                "changelog": {"total": 0, "returned": 0}, "reconciliation": "UNKNOWN", "remoteSnapshot": remote,
            },
        )
        write_json(self.audit / "manifest.json", {"discoveredIssues": [{"key": "TEST-2"}]})
        reconciler.reconcile(self.audit, self.mirror, True)
        snapshot = pipeline.read_json(self.audit / "issues" / "TEST-2.json")
        self.assertIn("PREVIOUS_STATUS_IDENTITY_MISSING", snapshot["regressionReasons"])

    def test_verifier_rejects_tampered_candidate_set_and_local_key_loss(self) -> None:
        self.seed_mirror()
        self.seed_manifest()
        plan = pipeline.build_plan(self.audit, self.mirror)
        plan["newKeys"] = []
        write_json(self.audit / "plan.json", plan)
        index = pipeline.read_json(self.mirror / "index.json")
        index["issues"] = index["issues"][:1]
        index["scope"]["issueCount"] = 1
        write_json(self.mirror / "index.json", index)
        result = verifier.evaluate(self.audit, self.mirror)
        self.assertEqual("FALSE", result["predicates"]["candidateSetMatches"])
        self.assertEqual("FALSE", result["predicates"]["localKeyLossCountZero"])

    def test_complete_schema_v2_audit_is_verified(self) -> None:
        self.seed_mirror()
        rows = [("2026-01-01T00:00:00+09:00", "Alice", "Open", "Done")]
        write_json(
            self.audit / "manifest.json",
            {
                "schemaVersion": 2,
                "auditId": "A2",
                "projectKey": "TEST",
                "jql": "project = TEST ORDER BY key ASC",
                "pages": [{"requestCursor": None, "endCursor": None, "hasNextPage": False, "keys": ["TEST-1", "TEST-2"]}],
                "discoveredIssues": [
                    {"key": "TEST-1", "updated": "2026-01-01T00:00:00+09:00"},
                    {"key": "TEST-2", "updated": "2026-01-02T00:00:00+09:00"},
                ],
                "observed": {"paginationFailureCount": 0, "duplicateIdentityCount": 0},
            },
        )
        for number in (1, 2):
            key = f"TEST-{number}"
            remote = issue(key, f"2026-01-0{number}T00:00:00+09:00", [f"c{number}"], rows)
            write_json(
                self.source / "issues" / f"{key}.json",
                {
                    "schemaVersion": 2,
                    "requestedKey": key,
                    "responseKey": key,
                    "remoteUpdated": remote["fields"]["updated"],
                    "comment": {"total": 1, "returned": 1},
                    "changelog": {"total": 1, "returned": 1},
                    "reconciliation": "REMOTE_APPLIED",
                    "localInvariantFailures": 0,
                    "remoteSnapshot": remote,
                },
            )
        pipeline.build_plan(self.audit, self.mirror, self.source)
        pipeline.reuse_snapshots(self.audit, self.source)
        result = verifier.evaluate(self.audit, self.mirror)
        self.assertEqual("VERIFIED", result["state"])

    def test_stage_verifies_before_live_mirror_changes(self) -> None:
        self.seed_mirror()
        original_index = (self.mirror / "index.json").read_bytes()
        original_markdown = (self.mirror / "TEST-1__제목.md").read_bytes()
        rows = [("2026-01-01T00:00:00+09:00", "Alice", "Open", "Done")]
        remote = issue("TEST-1", "2026-01-03T00:00:00+09:00", ["c1", "c-new"], rows)
        write_json(
            self.audit / "manifest.json",
            {
                "schemaVersion": 2,
                "auditId": "A2",
                "projectKey": "TEST",
                "jql": "project = TEST AND key = TEST-1",
                "pages": [{"requestCursor": None, "endCursor": None, "hasNextPage": False, "keys": ["TEST-1"]}],
                "discoveredIssues": [{"key": "TEST-1", "updated": "2026-01-03T00:00:00+09:00"}],
                "observed": {"paginationFailureCount": 0, "duplicateIdentityCount": 0},
            },
        )
        pipeline.build_plan(self.audit, self.mirror)
        write_json(
            self.audit / "issues" / "TEST-1.json",
            {
                "schemaVersion": 2,
                "requestedKey": "TEST-1",
                "responseKey": "TEST-1",
                "remoteUpdated": remote["fields"]["updated"],
                "comment": {"total": 2, "returned": 2},
                "changelog": {"total": 1, "returned": 1},
                "reconciliation": "UNKNOWN",
                "localInvariantFailures": None,
                "remoteSnapshot": remote,
            },
        )
        pipeline.stage_audit(self.audit, self.mirror)
        self.assertEqual(original_index, (self.mirror / "index.json").read_bytes())
        self.assertEqual(original_markdown, (self.mirror / "TEST-1__제목.md").read_bytes())
        self.assertEqual("VERIFIED", verifier.evaluate(self.audit, self.audit / "staged")["state"])
        staged_index = pipeline.read_json(self.audit / "staged" / "index.json")
        self.assertEqual(2, staged_index["scope"]["keyTo"])

    def test_promote_rejects_live_baseline_change(self) -> None:
        self.test_stage_verifies_before_live_mirror_changes()
        index = pipeline.read_json(self.mirror / "index.json")
        index["scope"]["issueCount"] = 99
        write_json(self.mirror / "index.json", index)
        with self.assertRaisesRegex(ValueError, "index.json 기준선"):
            pipeline.promote_audit(self.audit, self.mirror)

    def test_promote_applies_verified_staging(self) -> None:
        self.test_stage_verifies_before_live_mirror_changes()
        result = pipeline.promote_audit(self.audit, self.mirror)
        self.assertEqual("PROMOTED", result["state"])
        index = pipeline.read_json(self.mirror / "index.json")
        entry = next(item for item in index["issues"] if item["key"] == "TEST-1")
        self.assertEqual(2, entry["commentCount"])
        self.assertIn("id c-new", (self.mirror / entry["file"]).read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
