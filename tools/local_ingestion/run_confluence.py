"""Execute source-controlled reads through newline JSON RPC to the host.

Each stdout request must be dispatched verbatim to the named read connector.
The host returns its entire response as one stdin JSON line. Neither shell
interpolation nor temporary raw exports are needed. Unknown hierarchy aborts
the scope and leaves the mirror untouched. Each scope fails independently.
"""
import argparse
from datetime import datetime, timezone
import json
from pathlib import Path
import subprocess
import sys

import confluence
import connector


def rpc(request):
    """Expose only source-constructed allowlisted requests, bind one response."""
    print(json.dumps({"request": request}, ensure_ascii=False), flush=True)
    line = sys.stdin.readline()
    if not line:
        raise EOFError("host disconnected")
    return json.loads(line)


def collect(target, transport):
    """Build evidence from complete remote envelopes, never summary projections."""
    scope = target["scope"]
    snapshot = {"scope": scope, "observedAt": datetime.now(timezone.utc).isoformat(),
                "folderPages": [], "pagePages": [], "pages": [], "details": {}, "commentRequests": 0}
    discovered = []
    for kind, field in [("folder", "folderPages"), ("page", "pagePages")]:
        cursor, seen = None, set()
        identities, expected_total = [], None
        while True:
            evidence, rows = connector.listing(transport(connector.listing_request(scope, kind, cursor)),
                                               cursor, identities, expected_total)
            snapshot[field].append(evidence)
            identities.extend(evidence["ids"])
            expected_total = evidence["total"]
            if kind == "page":
                discovered.extend(rows)
            if evidence["hasNextPage"] is False:
                break
            cursor = evidence["endCursor"]
            if evidence["hasNextPage"] is not True or cursor in seen:
                raise ValueError("pagination termination unavailable")
            seen.add(cursor)
    # Current ancestry is a required observation before bodies can be applied.
    # CQL expansions can be omitted by the connector; cached paths are unsafe.
    for remote in discovered:
        pid = str(remote["id"])
        payload, body = connector.detail(transport(connector.detail_request(scope, pid)), pid)
        ancestors = payload.get("ancestors", remote.get("ancestors"))
        snapshot["pages"].append(connector.page_record(payload, scope, ancestors))
        snapshot["details"][pid] = body
    return snapshot


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("workspace", type=Path)
    parser.add_argument("--scope", choices=["all", "csr", "les", "meetings", "personal"], default="all")
    parser.add_argument("--apply", action="store_true")
    args = parser.parse_args()
    gate = subprocess.run([sys.executable, "-B", "-m", "unittest", "discover", "-s",
                           str(Path(__file__).parent), "-p", "test_*.py"], capture_output=True)
    if gate.returncode:
        sys.stderr.buffer.write(gate.stderr)
        return 1
    failed = False
    for target in connector.resolve_scopes(args.workspace.resolve()):
        if args.scope != "all" and target["alias"] != args.scope:
            continue
        try:
            snapshot = collect(target, rpc)
            root = Path(target["mirror"])
            index = json.loads((root / "index.json").read_text(encoding="utf-8"))
            result = confluence.plan(root, index, snapshot)
            if args.apply:
                confluence.apply(root, result)
            outcome = result["state"] if args.apply else "PLAN_VALIDATED" if result["state"] == "VERIFIED" else result["state"]
            print(json.dumps({"result": target["alias"], "state": outcome, "audit": result["audit"]}, ensure_ascii=False), flush=True)
            failed = failed or result["state"] != "VERIFIED"
        except (ValueError, KeyError, OSError, EOFError) as error:
            print(json.dumps({"result": target["alias"], "state": "INCOMPLETE",
                              "reason": str(error), "sourceHash": confluence.source_hash()}, ensure_ascii=False), flush=True)
            failed = True
    return 2 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
