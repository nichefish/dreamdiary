"""Verify source-controlled orchestration with a fake read-only transport."""
import json
import unittest
from unittest.mock import patch

import run_confluence as runner


FIXTURE_SCOPE = {"id": "10", "type": "folder", "prefix": "Tree",
                 "cloudId": "example.invalid", "site": "https://example.invalid/wiki"}


def wrap(obj):
    return {"content": [{"type": "text", "text": json.dumps(obj)}]}


class RunnerTests(unittest.TestCase):
    def transport(self, req):
        self.calls.append(req)
        if req["tool"] == "getconfluencepage":
            obj = {"id": "101", "body": "# Example\n", "title": "Example", "status": "current",
                   "createdAt": "2020", "version": {"number": 1}}
            if self.ancestry:
                obj["ancestors"] = [{"id": "10", "title": "Tree"}]
            return wrap(obj)
        rows = [] if req["arguments"]["cql"].endswith("folder") else [{"content": {"id": "101"}}]
        return wrap({"results": rows, "start": 0, "size": len(rows), "totalSize": len(rows), "_links": {}})

    def test_orchestration_uses_only_source_reads(self):
        self.calls, self.ancestry = [], True
        snapshot = runner.collect({"scope": FIXTURE_SCOPE}, self.transport)
        self.assertEqual(3, len(self.calls))
        self.assertEqual(0, snapshot["commentRequests"])
        self.assertEqual(["101"], snapshot["pagePages"][0]["ids"])
        self.assertEqual([], snapshot["pages"][0]["relativeAncestors"])

    def test_missing_current_hierarchy_aborts_before_apply(self):
        self.calls, self.ancestry = [], False
        with patch.object(runner.confluence, "apply") as apply:
            with self.assertRaises(ValueError):
                runner.collect({"scope": FIXTURE_SCOPE}, self.transport)
            apply.assert_not_called()


if __name__ == "__main__":
    unittest.main()
