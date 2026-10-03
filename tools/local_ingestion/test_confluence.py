"""Synthetic contract regression tests; production exports are never fixtures."""
import copy
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

import confluence as c


FIXTURE_BODY = "# Example\n\n| A | B |\n|---|---|\n| 1 | 2 |\n\n- [ ] task\nEnd\n"
FIXTURE_OLD = "# Previous\nPreserved\n"


class ContractTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = Path(self.tmp.name)
        self.index = {"source": {"service": "Example"}, "scope": {"pageCount": 1}, "pages": [
            {"pageId": "101", "title": "Previous", "version": {"number": 1},
             "file": "Tree/101__Previous.md", "commentsFile": "Tree/101.comments.json"}]}
        (self.root / "Tree").mkdir()
        (self.root / "Tree/101__Previous.md").write_text(FIXTURE_OLD, encoding="utf-8")
        (self.root / "Tree/101.comments.json").write_bytes(b'{"comments": []}')
        self.write_index()
        self.remote = {"pageId": "101", "title": "Example", "status": "current",
                       "sourceUrl": "https://example.invalid/pages/101", "createdAt": "2020-01-01",
                       "version": {"number": 1}, "relativeAncestors": []}
        self.snapshot = {"scope": {"type": "folder", "id": "10", "prefix": "Tree"},
                         "observedAt": "2020-01-02", "folderPages": [self.cursor([])],
                         "pagePages": [self.cursor(["101"])], "pages": [self.remote],
                         "details": {"101": self.detail("101", 1)}}

    def cursor(self, ids):
        return {"success": True, "requestCursor": None, "endCursor": None,
                "hasNextPage": False, "ids": ids}

    def detail(self, pid, version):
        return {"success": True, "requestedId": pid, "responseId": pid, "version": version,
                "format": "markdown", "complete": True, "markdown": FIXTURE_BODY,
                "utf8Bytes": len(FIXTURE_BODY.encode("utf-8"))}

    def write_index(self):
        (self.root / "index.json").write_text(json.dumps(self.index), encoding="utf-8")

    def bytes(self):
        return {str(p.relative_to(self.root)): p.read_bytes() for p in self.root.rglob("*") if p.is_file()}

    def run_plan(self):
        return c.plan(self.root, self.index, self.snapshot)

    def assert_rejected(self, expected):
        before = self.bytes()
        result = self.run_plan()
        self.assertEqual(expected, result["state"])
        c.apply(self.root, result)
        self.assertEqual(before, self.bytes())

    def test_unknown_baseline_refetch_same_version(self):
        self.snapshot["details"] = {}
        self.assert_rejected("FAILED")

    def test_normal_body_metadata_and_hashes(self):
        result = self.run_plan()
        self.assertEqual("VERIFIED", result["state"])
        c.apply(self.root, result)
        self.assertEqual(FIXTURE_BODY, (self.root / "Tree/101__Previous.md").read_text())
        index = json.loads((self.root / "index.json").read_text())
        self.assertEqual("Example", index["pages"][0]["title"])
        self.assertEqual(1, index["scope"]["pageCount"])
        self.assertTrue(c.baseline_verified(index, self.snapshot["scope"], self.root))

    def test_verified_same_version_skips_detail(self):
        c.apply(self.root, self.run_plan())
        self.index = json.loads((self.root / "index.json").read_text())
        self.snapshot["details"] = {}
        self.assertEqual(0, self.run_plan()["audit"]["counts"]["requiredDetails"])

    def test_baseline_cannot_use_status_or_time(self):
        self.index["ingestionAudits"] = [{"status": "SUCCESS", "collectedAt": "2099", "scope": self.snapshot["scope"]}]
        self.snapshot["details"] = {}
        self.assert_rejected("FAILED")

    def test_new_page(self):
        r = copy.deepcopy(self.remote)
        r["pageId"] = "102"
        self.snapshot["pages"].append(r)
        self.snapshot["pagePages"][0]["ids"].append("102")
        self.snapshot["details"]["102"] = self.detail("102", 1)
        result = self.run_plan()
        c.apply(self.root, result)
        self.assertEqual(FIXTURE_BODY, (self.root / "Tree/102__Example.md").read_text())
        self.assertEqual(2, result["index"]["scope"]["pageCount"])

    def test_higher_version_updates(self):
        self.remote["version"]["number"] = 2
        self.snapshot["details"]["101"]["version"] = 2
        self.assertEqual(2, self.run_plan()["index"]["pages"][0]["version"]["number"])

    def test_lower_version_never_overwrites(self):
        self.index["pages"][0]["version"]["number"] = 2
        self.write_index()
        self.assert_rejected("FAILED")

    def test_title_keeps_filename(self):
        self.remote["title"] = "Renamed"
        self.assertEqual("Tree/101__Previous.md", self.run_plan()["index"]["pages"][0]["file"])

    def test_move_body_and_comment_bytes(self):
        comment = (self.root / "Tree/101.comments.json").read_bytes()
        self.remote["relativeAncestors"] = ["Nested"]
        result = self.run_plan()
        c.apply(self.root, result)
        self.assertFalse((self.root / "Tree/101__Previous.md").exists())
        self.assertFalse((self.root / "Tree/101.comments.json").exists())
        self.assertEqual(comment, (self.root / "Tree/Nested/101.comments.json").read_bytes())

    def test_remote_missing_preserves(self):
        self.snapshot["pages"] = []
        self.snapshot["pagePages"][0]["ids"] = []
        before = (self.root / "Tree/101__Previous.md").read_bytes()
        result = self.run_plan()
        c.apply(self.root, result)
        self.assertEqual(before, (self.root / "Tree/101__Previous.md").read_bytes())
        self.assertIn("remoteMissing", result["index"]["pages"][0])

    def test_unobserved_terminal_does_not_mark_absent(self):
        self.snapshot["pages"] = []
        self.snapshot["pagePages"][0]["ids"] = []
        self.snapshot["pagePages"][0].pop("hasNextPage")
        self.assert_rejected("INCOMPLETE")
        self.assertNotIn("remoteMissing", self.run_plan()["index"]["pages"][0])

    def test_cursor_gap(self):
        self.snapshot["pagePages"][0].update(hasNextPage=True, endCursor="next")
        last = self.cursor([])
        last["requestCursor"] = "wrong"
        self.snapshot["pagePages"].append(last)
        self.assert_rejected("FAILED")

    def test_successful_cursor_chain(self):
        self.snapshot["pagePages"][0].update(hasNextPage=True, endCursor="next")
        last = self.cursor([])
        last["requestCursor"] = "next"
        self.snapshot["pagePages"].append(last)
        self.assertEqual("VERIFIED", self.run_plan()["state"])

    def test_failed_listing(self):
        self.snapshot["folderPages"][0]["success"] = False
        self.assert_rejected("FAILED")

    def test_duplicate_page_ids(self):
        self.snapshot["pages"].append(copy.deepcopy(self.remote))
        self.snapshot["pagePages"][0]["ids"].append("101")
        self.assert_rejected("FAILED")

    def test_truncated_or_unknown_body(self):
        for value, expected in [(False, "FAILED"), (None, "INCOMPLETE")]:
            self.snapshot["details"]["101"]["complete"] = value
            self.assert_rejected(expected)

    def test_id_version_byte_count_mismatch(self):
        for field, value in [("responseId", "999"), ("version", 3), ("utf8Bytes", 1)]:
            with self.subTest(field=field):
                self.snapshot["details"]["101"] = self.detail("101", 1)
                self.snapshot["details"]["101"][field] = value
                self.assert_rejected("FAILED")

    def test_comments_not_requested_not_modified(self):
        before = (self.root / "Tree/101.comments.json").read_bytes()
        c.apply(self.root, self.run_plan())
        self.assertEqual(before, (self.root / "Tree/101.comments.json").read_bytes())
        self.snapshot["commentRequests"] = 1
        self.assert_rejected("FAILED")

    def test_bom_rejected(self):
        self.snapshot["details"]["101"]["markdown"] = "\ufeff" + FIXTURE_BODY
        self.assert_rejected("FAILED")

    def test_scope_outside_files_preserved(self):
        (self.root / "Other").mkdir()
        (self.root / "Other/untouched.md").write_bytes(b"unchanged")
        c.apply(self.root, self.run_plan())
        self.assertEqual(b"unchanged", (self.root / "Other/untouched.md").read_bytes())

    def test_path_traversal_rejected(self):
        self.index["pages"][0]["file"] = "../escape.md"
        with self.assertRaises(ValueError):
            self.run_plan()

    def test_io_failure_protects_originals(self):
        before = self.bytes()
        result = self.run_plan()
        with patch.object(c.os, "replace", side_effect=OSError("synthetic failure")):
            with self.assertRaises(OSError):
                c.apply(self.root, result)
        self.assertEqual(before, self.bytes())

    def test_concurrent_index_and_body_change(self):
        result = self.run_plan()
        (self.root / "Tree/101__Previous.md").write_bytes(b"other session")
        with self.assertRaises(RuntimeError):
            c.apply(self.root, result)
        self.assertEqual(b"other session", (self.root / "Tree/101__Previous.md").read_bytes())

    def test_concurrent_index_change(self):
        result = self.run_plan()
        self.index["notes"] = ["other session"]
        self.write_index()
        before = self.bytes()
        with self.assertRaises(RuntimeError):
            c.apply(self.root, result)
        self.assertEqual(before, self.bytes())

    def test_failure_after_first_replace_restores(self):
        before = self.bytes()
        result = self.run_plan()
        original_replace = c.os.replace
        count = 0
        def fail_second(src, dst):
            nonlocal count
            count += 1
            if count == 2:
                raise OSError("synthetic second replace failure")
            original_replace(src, dst)
        with patch.object(c.os, "replace", side_effect=fail_second):
            with self.assertRaises(OSError):
                c.apply(self.root, result)
        self.assertEqual(before, self.bytes())

    def test_other_scope_identity_cannot_be_moved(self):
        self.index["pages"][0]["file"] = "Other/101.md"
        self.write_index()
        self.assert_rejected("FAILED")

    def test_folder_snapshot_count_updated(self):
        self.index["scope"]["folderSnapshots"] = [{"folderId": "10", "descendantPageCount": 9}]
        self.write_index()
        result = self.run_plan()
        self.assertEqual(1, result["index"]["scope"]["folderSnapshots"][0]["descendantPageCount"])

    def test_source_change_invalidates_baseline(self):
        c.apply(self.root, self.run_plan())
        self.index = json.loads((self.root / "index.json").read_text())
        self.index["ingestionAudits"][-1]["sourceHash"] = "other-source"
        self.snapshot["details"] = {}
        self.assert_rejected("FAILED")

    def test_missing_existing_comment_file_blocks_apply(self):
        (self.root / "Tree/101.comments.json").unlink()
        self.assert_rejected("FAILED")

    def test_duplicate_folder_id(self):
        self.snapshot["folderPages"][0]["ids"] = ["11", "11"]
        self.assert_rejected("FAILED")

    def missing_comment(self, counts):
        (self.root / "Tree/101.comments.json").unlink()
        self.index["pages"][0]["comments"] = counts
        self.write_index()

    def test_zero_count_missing_comment_preserves_reference_and_absence(self):
        self.missing_comment(dict(footerTopLevel=0, inlineTopLevel=0, replies=0, total=0))
        result = self.run_plan()
        self.assertEqual("VERIFIED", result["state"])
        audit = c.apply(self.root, result)
        self.assertFalse((self.root / "Tree/101.comments.json").exists())
        self.assertEqual("Tree/101.comments.json", result["index"]["pages"][0]["commentsFile"])
        self.assertIsNone(audit["fileHashes"]["Tree/101.comments.json"])
        self.assertEqual("ZERO_COUNT_MISSING_COMMENT_PRESERVED", audit["branches"][0]["branch"])
        self.index = json.loads((self.root / "index.json").read_text())
        self.snapshot["details"] = {}
        self.assertEqual(0, self.run_plan()["audit"]["counts"]["requiredDetails"])

    def test_missing_comment_with_positive_unknown_or_conflicting_counts_blocks(self):
        self.missing_comment({})
        for counts in [dict(total=0), dict(footerTopLevel=1, inlineTopLevel=0, replies=0, total=1),
                       dict(footerTopLevel=1, inlineTopLevel=0, replies=0, total=0),
                       dict(footerTopLevel=False, inlineTopLevel=0, replies=0, total=0)]:
            with self.subTest(counts=counts):
                self.index["pages"][0]["comments"] = counts
                self.write_index()
                self.assert_rejected("FAILED")

    def test_move_with_absent_zero_count_comment_keeps_original_reference(self):
        self.missing_comment(dict(footerTopLevel=0, inlineTopLevel=0, replies=0, total=0))
        self.remote["relativeAncestors"] = ["Nested"]
        result = self.run_plan()
        c.apply(self.root, result)
        self.assertEqual("Tree/101.comments.json", result["index"]["pages"][0]["commentsFile"])
        self.assertFalse((self.root / "Tree/101.comments.json").exists())
        self.assertFalse((self.root / "Tree/Nested/101.comments.json").exists())

    def test_new_comment_file_after_plan_is_concurrent_change(self):
        self.missing_comment(dict(footerTopLevel=0, inlineTopLevel=0, replies=0, total=0))
        result = self.run_plan()
        (self.root / "Tree/101.comments.json").write_bytes(b"other session")
        with self.assertRaises(RuntimeError):
            c.apply(self.root, result)

    def test_body_missing_is_never_a_zero_comment_exception(self):
        self.missing_comment(dict(footerTopLevel=0, inlineTopLevel=0, replies=0, total=0))
        (self.root / "Tree/101__Previous.md").unlink()
        self.assert_rejected("FAILED")


if __name__ == "__main__":
    unittest.main()
