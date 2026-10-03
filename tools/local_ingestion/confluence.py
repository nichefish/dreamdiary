"""Deterministic Confluence mirror planning, guarded application and audit.

Remote reads are supplied by a connector transport. No network credentials,
remote writes, content rewriting or application/database access occur here.
"""
from __future__ import annotations

import argparse
import copy
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import re
import sys
import tempfile
import subprocess


TRUE, FALSE, UNKNOWN = "TRUE", "FALSE", "UNKNOWN"


def digest(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def source_hash() -> str:
    """Identify the exact executable source, independently of audit outcome."""
    files = sorted(p for p in Path(__file__).parent.iterdir() if p.suffix in {".py", ".js", ".cjs"})
    data = b"".join(p.name.encode("utf-8") + b"\0" + p.read_bytes() for p in files)
    return digest(data)


def state(predicates: dict) -> str:
    """Derive trust from every predicate; caller-supplied status is ignored."""
    values = set(predicates.values())
    return "FAILED" if FALSE in values else "INCOMPLETE" if UNKNOWN in values else "VERIFIED"


def safe_component(value: str) -> str:
    """Map Windows-invalid local name characters without changing remote titles."""
    result = re.sub(r'[<>:"/\\|?*\x00-\x1f]', "_", value).rstrip(" .")
    if not result or re.fullmatch(r"(?i)(CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(\..*)?", result):
        result = "_" + result
    return result


def confined(root: Path, relative: str) -> Path:
    """Reject absolute, traversal and symlink escapes before any file operation."""
    posix = PurePosixPath(relative.replace("\\", "/"))
    if posix.is_absolute() or any(x in {"..", ""} for x in posix.parts) or ":" in relative:
        raise ValueError("unsafe relative path")
    path = root.joinpath(*posix.parts).resolve()
    if path == root.resolve() or not path.is_relative_to(root.resolve()):
        raise ValueError("path escapes mirror")
    return path


def chain(pages: list | None) -> str:
    """Require explicit successful cursor continuity and terminal observation."""
    if not isinstance(pages, list) or not pages:
        return UNKNOWN
    cursor = None
    for n, page in enumerate(pages):
        if page.get("success") is not True or page.get("requestCursor") != cursor:
            return FALSE
        if not isinstance(page.get("ids"), list):
            return UNKNOWN
        end = page.get("hasNextPage")
        if not isinstance(end, bool):
            return UNKNOWN
        if n < len(pages) - 1 and end is not True:
            return FALSE
        cursor = page.get("endCursor")
        if end and (not isinstance(cursor, str) or not cursor):
            return UNKNOWN
    return TRUE if pages[-1]["hasNextPage"] is False else UNKNOWN


def empty_comment_reference(entry: dict) -> bool:
    """Recognize recorded zero-count comments, not remote comment emptiness."""
    counts = entry.get("comments", {})
    return all(type(counts.get(key)) is int and counts[key] == 0
               for key in ("footerTopLevel", "inlineTopLevel", "replies", "total"))


def baseline_verified(index: dict, scope: dict, root: Path) -> bool:
    """Recompute prior evidence and file hashes; timestamps/status never suffice."""
    for audit in reversed(index.get("ingestionAudits", [])):
        if audit.get("scope") != scope or state(audit.get("predicates", {"missing": UNKNOWN})) != "VERIFIED":
            continue
        if audit.get("sourceHash") != source_hash():
            return False
        hashes = audit.get("fileHashes", {})
        if not hashes:
            return False
        absent = {x["file"]: x["pageId"] for x in audit.get("preservedMissingComments", [])}
        for name, expected in hashes.items():
            path = confined(root, name)
            if expected is None:
                entries = [x for x in index.get("pages", []) if x.get("commentsFile") == name
                           and str(x.get("pageId")) == absent.get(name)]
                if path.exists() or len(entries) != 1 or not empty_comment_reference(entries[0]):
                    return False
            elif not path.is_file() or digest(path.read_bytes()) != expected:
                return False
        return True
    return False


def plan(root: Path, index: dict, snapshot: dict) -> dict:
    """Compute expected local state without writing; scope is identity plus prefix.

    snapshot contains separate folder/page cursor evidence, remote page metadata
    and optional detail responses. A detail response binds requested/response ID,
    version, format, declared byte count and explicit complete-body observation.
    """
    root = root.resolve()
    old = copy.deepcopy(index)
    expected_files = {}
    preserved_missing_comments = []
    missing_required_files = []
    for entry in old.get("pages", []):
        for field in ("file", "commentsFile"):
            if entry.get(field):
                path = confined(root, entry[field])
                expected_files[entry[field]] = digest(path.read_bytes()) if path.is_file() else None
                if not path.exists():
                    if field == "commentsFile" and empty_comment_reference(entry):
                        preserved_missing_comments.append({"pageId": str(entry["pageId"]), "file": entry[field]})
                    else:
                        missing_required_files.append(entry[field])
    scope = snapshot.get("scope", {})
    predicates = {
        "scopeResolved": TRUE if scope.get("id") and scope.get("type") in {"folder", "page", "space"}
        and isinstance(scope.get("prefix"), str) else UNKNOWN,
        "folderPaginationExhausted": chain(snapshot.get("folderPages")),
        "pagePaginationExhausted": chain(snapshot.get("pagePages")),
        "paginationFailureCountZero": TRUE,
        "duplicatePageIdCountZero": TRUE,
        "duplicateFolderIdCountZero": TRUE,
        "requiredPageFetchFailureCountZero": TRUE,
        "unresolvedTruncationCountZero": TRUE,
        "reconciliationUnknownCountZero": TRUE,
        "localInvariantFailureCountZero": TRUE,
        "commentsUnrequestedUntouched": TRUE,
    }
    if missing_required_files:
        predicates["localInvariantFailureCountZero"] = FALSE
    for kind in ("folderPages", "pagePages"):
        if any(x.get("success") is False for x in snapshot.get(kind, [])):
            predicates["paginationFailureCountZero"] = FALSE
    if snapshot.get("commentsRequested") or snapshot.get("commentRequests", 0):
        # Comment ingestion has its own pagination contract and is unsupported.
        predicates["commentsUnrequestedUntouched"] = FALSE
    records = snapshot.get("pages", [])
    ids = [str(x.get("pageId", "")) for x in records]
    listed = [str(n) for x in snapshot.get("pagePages", []) for n in x.get("ids", [])]
    folder_ids = [str(n) for x in snapshot.get("folderPages", []) for n in x.get("ids", [])]
    if len(folder_ids) != len(set(folder_ids)):
        predicates["duplicateFolderIdCountZero"] = FALSE
    if len(ids) != len(set(ids)) or len(listed) != len(set(listed)):
        predicates["duplicatePageIdCountZero"] = FALSE
    if ids != listed or any(not n for n in ids):
        predicates["reconciliationUnknownCountZero"] = UNKNOWN
    entries = old.get("pages", [])
    if len({x["pageId"] for x in entries}) != len(entries):
        predicates["localInvariantFailureCountZero"] = FALSE
    by_id = {str(x["pageId"]): x for x in entries}
    trusted = baseline_verified(index, scope, root)
    writes, removes, branches, required = {}, [], [], []
    branches.extend({"pageId": x["pageId"], "branch": "ZERO_COUNT_MISSING_COMMENT_PRESERVED", "file": x["file"]}
                    for x in preserved_missing_comments)
    prefix = scope.get("prefix", "").strip("/")
    details = snapshot.get("details", {})
    for remote in records:
        pid = str(remote.get("pageId", ""))
        previous = by_id.get(pid)
        try:
            rv = remote["version"]["number"]
            pv = previous["version"]["number"] if previous else 0
            if previous and prefix and not previous["file"].startswith(prefix + "/"):
                raise ValueError("existing page outside authorized scope")
            if type(rv) is not int or rv < 1 or type(pv) is not int:
                raise ValueError("invalid version")
            if rv < pv:
                predicates["reconciliationUnknownCountZero"] = FALSE
                branches.append({"pageId": pid, "branch": "LOWER_VERSION_PRESERVED"})
                continue
            ancestors = remote["relativeAncestors"]
            if not isinstance(ancestors, list) or not all(isinstance(x, str) for x in ancestors):
                raise ValueError("unknown hierarchy")
            parent = "/".join(safe_component(x) for x in ancestors)
            filename = PurePosixPath(previous["file"]).name if previous else pid + "__" + safe_component(remote["title"]) + ".md"
            target = "/".join(x for x in (prefix, parent, filename) if x)
            confined(root, target)
            needs_body = previous is None or rv > pv or not trusted
            required.extend([pid] if needs_body else [])
            if needs_body:
                body = details.get(pid)
                if not body or body.get("success") is not True:
                    predicates["requiredPageFetchFailureCountZero"] = FALSE
                    continue
                if body.get("requestedId") != pid or body.get("responseId") != pid or body.get("version") != rv:
                    predicates["localInvariantFailureCountZero"] = FALSE
                    continue
                text = body.get("markdown")
                if body.get("complete") is not True:
                    predicates["unresolvedTruncationCountZero"] = FALSE if body.get("complete") is False else UNKNOWN
                    continue
                if body.get("format") != "markdown" or not isinstance(text, str) or not text or text.startswith("\ufeff"):
                    predicates["localInvariantFailureCountZero"] = FALSE
                    continue
                data = text.encode("utf-8")
                if body.get("utf8Bytes") != len(data):
                    predicates["unresolvedTruncationCountZero"] = FALSE
                    continue
            else:
                data = confined(root, previous["file"]).read_bytes()
            data.decode("utf-8")
            if data.startswith(b"\xef\xbb\xbf"):
                raise ValueError("UTF8 BOM")
            if previous and previous["file"] != target:
                removes.append(previous["file"])
            if needs_body or previous["file"] != target:
                writes[target] = data
            updated = copy.deepcopy(previous or {})
            for name in ("title", "status", "sourceUrl", "createdAt", "version"):
                updated[name] = copy.deepcopy(remote[name])
            updated.update(pageId=pid, file=target, relativeAncestors=ancestors)
            updated.pop("remoteMissing", None)
            if previous and previous.get("commentsFile") and previous["file"] != target:
                original_comment = previous["commentsFile"]
                if confined(root, original_comment).is_file():
                    comment_target = str(PurePosixPath(target).parent / PurePosixPath(original_comment).name)
                    writes[comment_target] = confined(root, original_comment).read_bytes()
                    removes.append(original_comment)
                    updated["commentsFile"] = comment_target
            by_id[pid] = updated
            branches.append({"pageId": pid, "branch": "BODY_APPLIED" if needs_body else "UNCHANGED_BODY", "file": target})
        except (KeyError, OSError, UnicodeError, ValueError, TypeError):
            predicates["localInvariantFailureCountZero"] = FALSE
    if predicates["folderPaginationExhausted"] == predicates["pagePaginationExhausted"] == TRUE:
        for pid, entry in by_id.items():
            in_scope = not prefix or entry["file"].startswith(prefix + "/")
            if in_scope and pid not in ids:
                entry["remoteMissing"] = {"observedAt": snapshot.get("observedAt"), "scopeId": scope.get("id")}
                branches.append({"pageId": pid, "branch": "REMOTE_ABSENT_PRESERVED"})
    new = copy.deepcopy(old)
    new["pages"] = list(by_id.values())
    new.setdefault("scope", {})["pageCount"] = len(new["pages"])
    if predicates["folderPaginationExhausted"] == predicates["pagePaginationExhausted"] == TRUE:
        folder_count = len({str(n) for page in snapshot.get("folderPages", []) for n in page.get("ids", [])})
        for folder in new["scope"].get("folderSnapshots", []):
            if str(folder.get("folderId")) == scope.get("id"):
                folder.update(descendantPageCount=len(ids), nestedFolderCount=folder_count)
        if scope.get("type") == "space":
            new["scope"].setdefault("spaceSnapshot", {}).update(pageCount=len(ids), folderCount=folder_count)
        elif str(new["scope"].get("rootSnapshot", {}).get("folderId")) == scope.get("id"):
            new["scope"]["rootSnapshot"].update(descendantPageCount=len(ids), nestedFolderCount=folder_count)
    counts = {"discovered": len(ids), "requiredDetails": len(required), "branches": len(branches)}
    audit = {"scope": scope, "sourceHash": source_hash(), "predicates": predicates, "counts": counts,
             "folderPages": snapshot.get("folderPages"), "pagePages": snapshot.get("pagePages"),
             "branches": branches, "observedAt": snapshot.get("observedAt"), "fileHashes": {}}
    audit["preservedMissingComments"] = preserved_missing_comments
    audit["missingRequiredFiles"] = missing_required_files
    # Detect destination collisions and preserve files owned by other identities.
    targets = [x["file"].casefold() for x in new["pages"]]
    if len(targets) != len(set(targets)) or any(confined(root, f).exists() and f not in
            {x["file"] for x in entries} | {x.get("commentsFile") for x in entries} for f in writes):
        predicates["localInvariantFailureCountZero"] = FALSE
    return {"state": state(predicates), "index": new, "audit": audit, "writes": writes,
            "removes": removes, "expectedFiles": expected_files,
            "expectedIndexHash": digest(json.dumps(index, ensure_ascii=False, sort_keys=True).encode("utf-8"))}


def apply(root: Path, result: dict) -> dict:
    """Apply a fully validated plan with concurrent-change and reread guards.

    Failed/incomplete plans perform no writes. Staging files are private atomic
    replacement buffers, not alternate source exports. On I/O failure original
    bytes are restored and the error propagates; no successful audit is retained.
    """
    if state(result["audit"]["predicates"]) != "VERIFIED":
        return result["audit"]
    index_path = root / "index.json"
    current = json.loads(index_path.read_text(encoding="utf-8"))
    if digest(json.dumps(current, ensure_ascii=False, sort_keys=True).encode("utf-8")) != result["expectedIndexHash"]:
        raise RuntimeError("index changed since planning")
    for name, expected in result["expectedFiles"].items():
        path = confined(root, name)
        actual = digest(path.read_bytes()) if path.is_file() else None
        if actual != expected:
            raise RuntimeError("mirror changed since planning")
    originals = {p: p.read_bytes() if p.exists() else None for p in
                 [confined(root, f) for f in set(result["writes"]) | set(result["removes"])] + [index_path]}
    # Snapshot unchanged files too, so baseline integrity can be recalculated.
    audit = copy.deepcopy(result["audit"])
    for e in result["index"]["pages"]:
        for key in ("file", "commentsFile"):
            if e.get(key):
                name = e[key]
                path = confined(root, name)
                audit["fileHashes"][name] = (digest(result["writes"][name]) if name in result["writes"]
                                             else digest(path.read_bytes()) if path.is_file() else None)
    result["index"].setdefault("ingestionAudits", []).append(audit)
    index_bytes = (json.dumps(result["index"], ensure_ascii=False, indent=2) + "\n").encode("utf-8")
    replacements = {confined(root, f): data for f, data in result["writes"].items()}
    replacements[index_path] = index_bytes
    staged = []
    try:
        for path, data in replacements.items():
            path.parent.mkdir(parents=True, exist_ok=True)
            fd, name = tempfile.mkstemp(prefix=".ingestion-", dir=path.parent)
            staged.append((Path(name), path))
            with os.fdopen(fd, "wb") as stream:
                stream.write(data)
                stream.flush()
                os.fsync(stream.fileno())
            if Path(name).read_bytes() != data:
                raise OSError("staging reread mismatch")
        for temp, path in staged:
            os.replace(temp, path)
        for name in result["removes"]:
            if confined(root, name) not in replacements:
                confined(root, name).unlink()
        for path, data in replacements.items():
            if path.read_bytes() != data:
                raise OSError("final reread mismatch")
    except Exception:
        for path, data in originals.items():
            if data is None:
                path.unlink(missing_ok=True)
            else:
                path.write_bytes(data)
        raise
    finally:
        for temp, _ in staged:
            temp.unlink(missing_ok=True)
    return audit


def main() -> int:
    """Consume one connector snapshot on stdin; stdout is derived audit only."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("mirror", type=Path)
    parser.add_argument("--apply", action="store_true")
    args = parser.parse_args()
    gate = subprocess.run([sys.executable, "-B", "-m", "unittest", "discover", "-s",
                           str(Path(__file__).parent), "-p", "test_*.py"], capture_output=True)
    if gate.returncode:
        sys.stderr.buffer.write(gate.stderr)
        return 1
    root = args.mirror.resolve()
    index = json.loads((root / "index.json").read_text(encoding="utf-8"))
    result = plan(root, index, json.load(sys.stdin))
    audit = apply(root, result) if args.apply else result["audit"]
    reported = "PLAN_VALIDATED" if not args.apply and result["state"] == "VERIFIED" else result["state"]
    print(json.dumps({"state": reported, "audit": audit}, ensure_ascii=False, indent=2))
    return {"VERIFIED": 0, "FAILED": 1, "INCOMPLETE": 2}[result["state"]]


if __name__ == "__main__":
    sys.exit(main())
