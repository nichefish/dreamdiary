import json
import tempfile
import unittest
from pathlib import Path

from store_snapshot import IngestionError, apply_snapshot


FIXTURE_CONVERSATION_ID = "11111111-2222-3333-4444-555555555555"


def fixture_turn(turn_id: str, started_at: float, user: str, assistant: str) -> dict:
    return {
        "id": turn_id,
        "startedAt": started_at,
        "items": [
            {"type": "userMessage", "content": [{"type": "text", "text": user}]},
            {"type": "agentMessage", "text": assistant},
        ],
    }


def fixture_payload(turns: list[dict]) -> dict:
    return {
        "schemaVersion": 1,
        "conversationId": FIXTURE_CONVERSATION_ID,
        "title": "Alice and Bob",
        "remoteUpdatedAt": 1_800_000_000.0,
        "turns": turns,
        "attachments": [],
        "pagination": {
            "exhausted": True,
            "pages": 2,
            "failureCount": 0,
            "duplicateTurnIdCount": 0,
        },
        "regressionCheck": {
            "passed": True,
            "firstPageHasMore": True,
            "firstPageNextCursor": "cursor-1",
            "secondPageTurnCount": 2,
        },
    }


class StoreSnapshotTest(unittest.TestCase):
    def test_stores_and_extends_existing_snapshot(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            initial = [fixture_turn("turn-1", 1_800_000_000.0, "Hello", "Hi")]
            first = apply_snapshot(fixture_payload(initial), root)
            snapshot = Path(first["file"])
            self.assertTrue(snapshot.is_file())

            extended = initial + [fixture_turn("turn-2", 1_800_000_060.0, "Ready?", "Ready.")]
            second = apply_snapshot(fixture_payload(extended), root)
            self.assertEqual(second["turns"], 2)
            self.assertIn("Ready?", snapshot.read_text(encoding="utf-8"))

    def test_rejects_regression_preflight_failure(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            payload = fixture_payload([fixture_turn("turn-1", 1_800_000_000.0, "Hello", "Hi")])
            payload["regressionCheck"]["passed"] = False
            with self.assertRaisesRegex(IngestionError, "preflight"):
                apply_snapshot(payload, Path(directory))

    def test_rejects_destructive_shortening(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            turns = [
                fixture_turn("turn-1", 1_800_000_000.0, "Hello", "Hi"),
                fixture_turn("turn-2", 1_800_000_060.0, "Ready?", "Ready."),
            ]
            stored = apply_snapshot(fixture_payload(turns), root)
            snapshot = Path(stored["file"])
            before = snapshot.read_bytes()
            with self.assertRaisesRegex(IngestionError, "fewer message blocks"):
                apply_snapshot(fixture_payload(turns[1:]), root)
            self.assertEqual(snapshot.read_bytes(), before)

    def test_rejects_non_contiguous_replacement(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            turns = [
                fixture_turn("turn-1", 1_800_000_000.0, "Hello", "Hi"),
                fixture_turn("turn-2", 1_800_000_060.0, "Ready?", "Ready."),
            ]
            stored = apply_snapshot(fixture_payload(turns), root)
            snapshot = Path(stored["file"])
            before = snapshot.read_bytes()
            changed = [
                fixture_turn("turn-1", 1_800_000_000.0, "Changed", "Hi"),
                turns[1],
            ]
            with self.assertRaisesRegex(IngestionError, "not preserved"):
                apply_snapshot(fixture_payload(changed), root)
            self.assertEqual(snapshot.read_bytes(), before)


if __name__ == "__main__":
    unittest.main()
