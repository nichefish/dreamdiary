#!/usr/bin/env python3
"""Validate and atomically store a ChatGPT read_thread snapshot.

The Codex tool-bound collector supplies a validated JSON payload.  This writer
rechecks every destructive-write precondition so an incomplete remote response
cannot replace a longer local snapshot.
"""

from __future__ import annotations

import argparse
import base64
import ctypes
import hashlib
import json
import os
import re
import sys
import tempfile
from datetime import datetime, timedelta, timezone
from pathlib import Path
from typing import Any


SCHEMA_VERSION = 1
END_MARKER = "__CHATGPT_INGESTION_END__"
ALLOWED_ATTACHMENT_SUFFIXES = {".txt", ".png", ".jpg", ".jpeg", ".webp", ".gif"}
MESSAGE_TAG = re.compile(r"^<(user|assistant) ([^>]+)>$", re.MULTILINE)
INVALID_FILENAME = re.compile(r"[<>:\"/\\|?*\x00-\x1f]")


class IngestionError(RuntimeError):
    """Raised when a snapshot cannot be proven safe to store."""


def disable_windows_stdin_echo() -> tuple[int, int] | None:
    """Disable PTY input echo so encoded conversation bytes stay out of logs."""
    if os.name != "nt":
        return None
    import msvcrt

    handle = msvcrt.get_osfhandle(sys.stdin.fileno())
    mode = ctypes.c_uint()
    kernel32 = ctypes.windll.kernel32
    if kernel32.GetConsoleMode(handle, ctypes.byref(mode)) == 0:
        return None
    enable_echo_input = 0x0004
    if kernel32.SetConsoleMode(handle, mode.value & ~enable_echo_input) == 0:
        raise IngestionError("cannot disable PTY input echo")
    return handle, mode.value


def restore_windows_stdin_mode(state: tuple[int, int] | None) -> None:
    if state is not None:
        handle, mode = state
        ctypes.windll.kernel32.SetConsoleMode(handle, mode)


def read_payload(args: argparse.Namespace) -> dict[str, Any]:
    if args.input:
        raw = Path(args.input).read_bytes()
    else:
        stdin_state = disable_windows_stdin_echo()
        try:
            chunks: list[str] = []
            for line in sys.stdin:
                value = line.rstrip("\r\n")
                if value == END_MARKER:
                    break
                chunks.append(value)
            else:
                raise IngestionError(f"missing stream terminator: {END_MARKER}")
        finally:
            restore_windows_stdin_mode(stdin_state)
        try:
            raw = base64.b64decode("".join(chunks), validate=True)
        except ValueError as exc:
            raise IngestionError("invalid base64 payload") from exc

    try:
        decoded = raw.decode("utf-8", errors="strict")
        payload = json.loads(decoded)
    except (UnicodeDecodeError, json.JSONDecodeError) as exc:
        raise IngestionError("payload must be strict UTF-8 JSON") from exc
    if not isinstance(payload, dict):
        raise IngestionError("payload root must be an object")
    return payload


def message_blocks(text: str) -> list[tuple[str, str, str]]:
    matches = list(MESSAGE_TAG.finditer(text))
    blocks: list[tuple[str, str, str]] = []
    for index, match in enumerate(matches):
        body_start = match.end()
        if text[body_start : body_start + 2] == "\r\n":
            body_start += 2
        elif text[body_start : body_start + 1] == "\n":
            body_start += 1
        body_end = matches[index + 1].start() if index + 1 < len(matches) else len(text)
        body = text[body_start:body_end].rstrip("\r\n")
        blocks.append((match.group(1), match.group(2), body))
    return blocks


def is_contiguous_subsequence(old: list[tuple[str, str, str]], new: list[tuple[str, str, str]]) -> bool:
    if not old or len(old) > len(new):
        return False
    width = len(old)
    return any(new[start : start + width] == old for start in range(len(new) - width + 1))


def format_started_at(value: float) -> str:
    kst = timezone(timedelta(hours=9))
    return datetime.fromtimestamp(value, timezone.utc).astimezone(kst).strftime("%Y-%m-%d %H:%M")


def render_snapshot(turns: list[dict[str, Any]]) -> str:
    sections: list[str] = []
    seen_turn_ids: set[str] = set()
    previous_started_at: float | None = None
    for turn in turns:
        turn_id = turn.get("id")
        started_at = turn.get("startedAt")
        if not isinstance(turn_id, str) or not turn_id:
            raise IngestionError("every turn must have a non-empty string id")
        if turn_id in seen_turn_ids:
            raise IngestionError(f"duplicate turn id: {turn_id}")
        seen_turn_ids.add(turn_id)
        if not isinstance(started_at, (int, float)):
            raise IngestionError(f"turn {turn_id} has no numeric startedAt")
        if previous_started_at is not None and float(started_at) < previous_started_at:
            raise IngestionError("turns are not in chronological order")
        previous_started_at = float(started_at)
        timestamp = format_started_at(float(started_at))
        items = turn.get("items")
        if not isinstance(items, list):
            raise IngestionError(f"turn {turn_id} items must be an array")
        for item in items:
            item_type = item.get("type") if isinstance(item, dict) else None
            if item_type == "userMessage":
                content = item.get("content")
                if not isinstance(content, list):
                    raise IngestionError(f"turn {turn_id} userMessage content must be an array")
                pieces: list[str] = []
                for block in content:
                    if not isinstance(block, dict) or block.get("type") != "text" or not isinstance(block.get("text"), str):
                        raise IngestionError(f"turn {turn_id} contains an unsupported user content block")
                    pieces.append(block["text"])
                body = "".join(pieces)
                if len(body) == 20_000:
                    raise IngestionError(f"turn {turn_id} userMessage reached the 20,000 character limit")
                sections.append(f"<user {timestamp}>\n{body}")
            elif item_type == "agentMessage":
                body = item.get("text")
                if not isinstance(body, str):
                    raise IngestionError(f"turn {turn_id} agentMessage text must be a string")
                if len(body) == 20_000 or item.get("truncated") is True:
                    raise IngestionError(f"turn {turn_id} agentMessage may be truncated")
                sections.append(f"<assistant {timestamp}>\n{body}")
            else:
                raise IngestionError(f"turn {turn_id} contains unsupported item type: {item_type!r}")
    if not sections:
        raise IngestionError("snapshot contains no supported messages")
    rendered = "\n\n".join(sections) + "\n"
    if "\ufffd" in rendered:
        raise IngestionError("remote snapshot contains a replacement character")
    return rendered


def sanitize_title(title: str) -> str:
    cleaned = INVALID_FILENAME.sub("_", title).strip().rstrip(".")
    return cleaned or "untitled"


def find_existing(root: Path, conversation_id: str) -> Path | None:
    matches = [
        path
        for path in root.rglob(f"*__{conversation_id}.txt")
        if not path.name.endswith(".partial.txt")
    ]
    if len(matches) > 1:
        raise IngestionError(f"multiple existing snapshots found for {conversation_id}")
    return matches[0] if matches else None


def atomic_write(path: Path, data: bytes) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    descriptor, temporary = tempfile.mkstemp(prefix=f".{path.name}.", suffix=".tmp", dir=path.parent)
    try:
        with os.fdopen(descriptor, "wb") as stream:
            stream.write(data)
            stream.flush()
            os.fsync(stream.fileno())
        os.replace(temporary, path)
    except BaseException:
        try:
            os.unlink(temporary)
        except FileNotFoundError:
            pass
        raise


def load_index(index_path: Path) -> dict[str, Any]:
    if not index_path.exists():
        return {"schemaVersion": 1, "conversations": []}
    raw = index_path.read_bytes()
    if raw.startswith(b"\xef\xbb\xbf"):
        raise IngestionError("CHATGPT_INDEX.json must not contain a UTF-8 BOM")
    try:
        value = json.loads(raw.decode("utf-8", errors="strict"))
    except (UnicodeDecodeError, json.JSONDecodeError) as exc:
        raise IngestionError("CHATGPT_INDEX.json is not strict UTF-8 JSON") from exc
    if value.get("schemaVersion") != 1 or not isinstance(value.get("conversations"), list):
        raise IngestionError("unsupported CHATGPT_INDEX.json schema")
    return value


def copy_attachments(attachments: list[dict[str, Any]], snapshot_path: Path) -> list[dict[str, str | int]]:
    target_dir = snapshot_path.with_suffix("")
    target_dir = target_dir.with_name(target_dir.name + ".attachments")
    copied: list[dict[str, str | int]] = []
    seen_sources: set[tuple[str, str]] = set()
    for attachment in attachments:
        name = attachment.get("name")
        source_value = attachment.get("path")
        if not isinstance(name, str) or not isinstance(source_value, str):
            continue
        suffix = Path(name).suffix.lower()
        if suffix not in ALLOWED_ATTACHMENT_SUFFIXES:
            continue
        source = Path(source_value)
        key = (name, str(source))
        if key in seen_sources:
            continue
        seen_sources.add(key)
        if not source.is_file():
            raise IngestionError(f"attachment is not readable: {name}")
        ordinal = len(copied) + 1
        safe_name = sanitize_title(Path(name).stem) + suffix
        target_dir.mkdir(parents=True, exist_ok=True)
        target = target_dir / f"{ordinal:03d}_{safe_name}"
        source_bytes = source.read_bytes()
        if target.exists() and target.read_bytes() != source_bytes:
            digest_prefix = hashlib.sha256(source_bytes).hexdigest()[:8]
            target = target_dir / f"{ordinal:03d}_{sanitize_title(Path(name).stem)}_{digest_prefix}{suffix}"
        if not target.exists():
            atomic_write(target, source_bytes)
        copied.append({
            "file": str(target),
            "bytes": len(source_bytes),
            "sha256": hashlib.sha256(source_bytes).hexdigest(),
        })
    return copied


def apply_snapshot(payload: dict[str, Any], workspace_root: Path) -> dict[str, Any]:
    if payload.get("schemaVersion") != SCHEMA_VERSION:
        raise IngestionError("unsupported collector payload schema")
    regression = payload.get("regressionCheck")
    if not isinstance(regression, dict) or regression.get("passed") is not True:
        raise IngestionError("read_thread regression preflight did not pass")
    if regression.get("firstPageHasMore") is not True or not regression.get("firstPageNextCursor"):
        raise IngestionError("regression preflight did not prove a usable next page")
    if not isinstance(regression.get("secondPageTurnCount"), int) or regression["secondPageTurnCount"] < 1:
        raise IngestionError("regression preflight did not return older turns")

    conversation_id = payload.get("conversationId")
    title = payload.get("title")
    remote_updated_at = payload.get("remoteUpdatedAt")
    turns = payload.get("turns")
    attachments = payload.get("attachments", [])
    pagination = payload.get("pagination")
    if not isinstance(conversation_id, str) or not conversation_id:
        raise IngestionError("conversationId is required")
    if not isinstance(title, str):
        raise IngestionError("title must be a string")
    if not isinstance(remote_updated_at, (int, float)):
        raise IngestionError("remoteUpdatedAt must be numeric")
    if not isinstance(turns, list) or not turns:
        raise IngestionError("turns must be a non-empty array")
    if not isinstance(attachments, list):
        raise IngestionError("attachments must be an array")
    if not isinstance(pagination, dict) or pagination.get("exhausted") is not True:
        raise IngestionError("target pagination was not exhausted")
    if pagination.get("failureCount") != 0 or pagination.get("duplicateTurnIdCount") != 0:
        raise IngestionError("target pagination invariants failed")

    rendered = render_snapshot(turns)
    encoded = rendered.encode("utf-8")
    snapshot_root = workspace_root / "daily" / "text" / "daily"
    index_path = snapshot_root / "CHATGPT_INDEX.json"
    existing = find_existing(snapshot_root, conversation_id)
    if existing:
        old_raw = existing.read_bytes()
        if old_raw.startswith(b"\xef\xbb\xbf"):
            raise IngestionError(f"existing snapshot contains a UTF-8 BOM: {existing}")
        old_text = old_raw.decode("utf-8", errors="strict")
        old_blocks = message_blocks(old_text)
        new_blocks = message_blocks(rendered)
        if len(new_blocks) < len(old_blocks):
            raise IngestionError("new snapshot has fewer message blocks than the existing snapshot")
        if not is_contiguous_subsequence(old_blocks, new_blocks):
            raise IngestionError("existing snapshot is not preserved as a contiguous message sequence")
        snapshot_path = existing
    else:
        first_time = datetime.fromtimestamp(float(turns[0]["startedAt"]), timezone.utc).astimezone(
            timezone(timedelta(hours=9))
        )
        relative = Path(
            f"{first_time:%Y}/{first_time:%m}/{first_time:%d}/"
            f"{first_time:%y%m%d}_chatGPT_{sanitize_title(title)}__{conversation_id}.txt"
        )
        snapshot_path = snapshot_root / relative

    index = load_index(index_path)
    digest = hashlib.sha256(encoded).hexdigest()
    relative_file = snapshot_path.relative_to(workspace_root).as_posix()
    entry = {
        "conversationId": conversation_id,
        "remoteUpdatedAt": remote_updated_at,
        "ingestedAt": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        "contentSha256": digest,
        "file": relative_file,
    }
    entries = [item for item in index["conversations"] if item.get("conversationId") != conversation_id]
    entries.append(entry)
    entries.sort(key=lambda item: item.get("conversationId", ""))
    index["conversations"] = entries
    index_bytes = (json.dumps(index, ensure_ascii=False, indent=2) + "\n").encode("utf-8")

    atomic_write(snapshot_path, encoded)
    attachment_results = copy_attachments(attachments, snapshot_path)
    atomic_write(index_path, index_bytes)
    return {
        "conversationId": conversation_id,
        "file": str(snapshot_path),
        "messageBlocks": len(message_blocks(rendered)),
        "turns": len(turns),
        "pages": pagination.get("pages"),
        "contentSha256": digest,
        "attachments": attachment_results,
    }


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--workspace-root", required=True)
    parser.add_argument("--input")
    return parser.parse_args()


def main() -> int:
    try:
        args = parse_args()
        payload = read_payload(args)
        result = apply_snapshot(payload, Path(args.workspace_root).resolve())
        print(json.dumps(result, ensure_ascii=False))
        return 0
    except (IngestionError, OSError, ValueError) as exc:
        print(json.dumps({"error": str(exc)}, ensure_ascii=False), file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
