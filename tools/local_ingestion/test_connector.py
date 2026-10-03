"""Synthetic connector envelopes cover evidence extraction and mutation denial."""
import json
import unittest
import connector


FIXTURE_SCOPE = {"id": "10", "type": "folder", "cloudId": "example.invalid", "site": "https://example.invalid/wiki"}


def envelope(payload):
    return {"content": [{"type": "text", "text": json.dumps(payload)}]}


class ConnectorTests(unittest.TestCase):
    def test_remote_mutation_denied(self):
        with self.assertRaises(ValueError):
            connector.request("updateconfluencepage")

    def test_request_uses_complete_scope_not_timestamp(self):
        r = connector.listing_request(FIXTURE_SCOPE, "page")
        self.assertEqual("ancestor = 10 AND type = page", r["arguments"]["cql"])
        self.assertNotIn("cursor", r["arguments"])

    def test_terminal_requires_size_and_total(self):
        r = {"results": [], "start": 0, "size": 0, "totalSize": 0, "_links": {}}
        self.assertIs(connector.listing(envelope(r))[0]["hasNextPage"], False)
        r.pop("totalSize")
        self.assertIsNone(connector.listing(envelope(r))[0]["hasNextPage"])

    def test_cursor_from_next_link(self):
        r = {"results": [], "_links": {"next": "/api?cursor=next%2Btoken"}}
        e, _ = connector.listing(envelope(r))
        self.assertEqual("next+token", e["endCursor"])

    def test_personal_space_uses_index_key(self):
        scope = dict(FIXTURE_SCOPE, type="space", spaceKey="~fixture")
        self.assertEqual('space = "~fixture" AND type = page',
                         connector.listing_request(scope, "page")["arguments"]["cql"])
        scope.pop("spaceKey")
        with self.assertRaises(ValueError):
            connector.listing_request(scope, "page")

    def test_cursor_pages_with_zero_start_use_cumulative_ids(self):
        previous = [str(i) for i in range(200)]
        payload = {"results": [{"content": {"id": str(i)}} for i in range(200, 242)],
                   "start": 0, "size": 42, "totalSize": 242, "_links": {}}
        evidence, _ = connector.listing(envelope(payload), "cursor", previous, 242)
        self.assertIs(evidence["hasNextPage"], False)
        self.assertEqual(242, evidence["cumulativeUnique"])
        for prior, total in [(previous[:-1], 242), (previous + ["200"], 242), (previous, 243)]:
            evidence, _ = connector.listing(envelope(payload), "cursor", prior, total)
            self.assertIsNone(evidence["hasNextPage"])

    def test_errors_and_partial_json_rejected(self):
        for r in [{"isError": True}, {"content": [{"type": "text", "text": "{"}]}]:
            with self.assertRaises(ValueError):
                connector.unwrap(r)

    def test_lossy_search_summary_not_used(self):
        with self.assertRaises(ValueError):
            connector.listing({"structuredContent": {"nodes": []}})

    def test_body_bytes_and_id_come_from_response(self):
        r = {"id": "101", "version": {"number": 2}, "body": "# Example\n"}
        _, d = connector.detail(envelope(r), "101")
        self.assertEqual(10, d["utf8Bytes"])
        self.assertEqual(2, d["version"])
        r["truncated"] = True
        self.assertIs(connector.detail(envelope(r), "101")[1]["complete"], False)

    def test_missing_ancestry_is_not_guessed(self):
        with self.assertRaises(ValueError):
            connector.page_record({}, FIXTURE_SCOPE, None)

    def test_observed_scope_ancestry(self):
        p = {"id": "101", "title": "Example", "status": "current", "createdAt": "2020", "version": {"number": 1}}
        record = connector.page_record(p, FIXTURE_SCOPE, [{"id": "10", "title": "Tree"}, {"id": "11", "title": "Nested"}])
        self.assertEqual(["Nested"], record["relativeAncestors"])


if __name__ == "__main__":
    unittest.main()
