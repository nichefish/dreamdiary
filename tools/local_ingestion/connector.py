"""Read-only connector boundary and normalization of observable API evidence.

The host executes the emitted allowlisted requests. This module owns request
construction and parsing; agents must not invent completion/version/hierarchy.
Missing connector fields remain unknown, rather than being inferred from time.
"""
import json
from urllib.parse import parse_qs, urlparse


READ_TOOLS = {"searchconfluenceusingcql", "getconfluencepage"}


def request(tool, **arguments):
    """Allow only the two read operations used by the ingestion contract."""
    if tool not in READ_TOOLS:
        raise ValueError("remote mutation or unsupported read")
    return {"tool": tool, "arguments": arguments}


def unwrap(response):
    """Decode the full JSON text block; structured search summaries are lossy."""
    if response.get("isError"):
        raise ValueError("connector error")
    blocks = [x["text"] for x in response.get("content", []) if x.get("type") == "text"]
    if len(blocks) != 1:
        raise ValueError("ambiguous or incomplete response")
    payload = json.loads(blocks[0])
    if payload.get("error"):
        raise ValueError("API error")
    return payload


def listing_request(scope, content_type, cursor=None):
    """Discover each content type separately; no modified-time filtering."""
    if content_type not in {"page", "folder"}:
        raise ValueError("unsupported type")
    identifier = scope["id"]
    if not str(identifier).isdigit():
        raise ValueError("invalid scope identity")
    if scope["type"] == "space":
        key = scope.get("spaceKey")
        if not isinstance(key, str) or not key.strip():
            raise ValueError("space key unavailable")
        clause = "space = " + json.dumps(key)
    else:
        clause = f"ancestor = {identifier}"
    args = {"cloudId": scope["cloudId"], "cql": f"{clause} AND type = {content_type}",
            "expand": "content.ancestors,content.version", "limit": 250, "next": True}
    if cursor is not None:
        args["cursor"] = cursor
    return request("searchconfluenceusingcql", **args)


def listing(response, request_cursor=None, previous_ids=(), expected_total=None):
    """Bind listing size, next link and total to a cursor observation.

    An absent next link alone is insufficient: cumulative unique IDs must equal the
    stable reported total. Cursor pages may report start=0 independently.
    """
    payload = unwrap(response)
    rows = payload.get("results")
    if not isinstance(rows, list):
        raise ValueError("missing results")
    ids = [str(x["content"]["id"]) for x in rows]
    next_link = payload.get("_links", {}).get("next")
    next_cursor = parse_qs(urlparse(next_link).query).get("cursor", [None])[0] if next_link else None
    cumulative = list(previous_ids) + ids
    total = payload.get("totalSize")
    valid = (type(total) is int and total >= 0
             and type(payload.get("size")) is int and payload["size"] == len(rows)
             and len(set(cumulative)) == len(cumulative)
             and len(cumulative) <= total
             and (request_cursor is None or expected_total == total))
    observed_end = not next_link and valid and len(cumulative) == total
    evidence = {"success": True, "requestCursor": request_cursor, "endCursor": next_cursor,
                "hasNextPage": True if valid and next_cursor and len(cumulative) < total else False if observed_end else None, "ids": ids,
                "cumulativeUnique": len(set(cumulative)), "start": payload.get("start"), "size": payload.get("size"), "total": payload.get("totalSize")}
    return evidence, [x["content"] for x in rows]


def detail_request(scope, page_id):
    """Get the current Markdown body and metadata, without requesting comments."""
    return request("getconfluencepage", cloudId=scope["cloudId"], pageId=str(page_id), contentFormat="markdown")


def detail(response, page_id):
    """Use full page endpoint body; explicit truncation flags remain failures."""
    payload = unwrap(response)
    body = payload.get("body")
    if not isinstance(body, str):
        raise ValueError("body is not Markdown text")
    complete = not any(payload.get(k) is True for k in ("truncated", "isTruncated", "hasMore"))
    return payload, {"success": True, "requestedId": str(page_id), "responseId": str(payload.get("id")),
                     "version": payload.get("version", {}).get("number"), "format": "markdown",
                     "complete": complete, "markdown": body, "utf8Bytes": len(body.encode("utf-8"))}


def page_record(payload, scope, ancestors):
    """Accept only observed ancestry, scoped to root; unknown ancestry is fatal.

    The connector must supply root-to-parent nodes including folders. Existing
    paths and cached ancestors are not substitutes for current hierarchy.
    """
    if ancestors is None:
        raise ValueError("current ancestry unavailable")
    nodes = ancestors
    if scope["type"] != "space":
        matches = [n for n, node in enumerate(nodes) if str(node["id"]) == scope["id"]]
        if len(matches) != 1:
            raise ValueError("scope root not uniquely observed")
        nodes = nodes[matches[0] + 1:]
    return {"pageId": str(payload["id"]), "title": payload["title"], "status": payload["status"],
            "createdAt": payload["createdAt"], "version": payload["version"],
            "sourceUrl": scope["site"].rstrip("/") + "/pages/" + str(payload["id"]),
            "relativeAncestors": [node["title"] for node in nodes]}


def resolve_scopes(workspace):
    """Resolve the four contract aliases from their existing index identities."""
    base = workspace / "daily/text/raw/confluence"
    definitions = [("csr", "최희승", "CSR 현황"), ("les", "연구솔루션팀", "LIMS/LES 리메이크"),
                   ("meetings", "GxP 사업부/직책자회의", None), ("personal", "nichefish", None)]
    result = []
    for alias, directory, prefix in definitions:
        mirror = base / directory
        index = json.loads((mirror / "index.json").read_text(encoding="utf-8"))
        source = index["source"]
        if prefix:
            matches = [x for x in index["scope"]["folderSnapshots"] if x["relativePath"] == prefix]
            if len(matches) != 1:
                raise ValueError("ambiguous alias root")
            root_id, kind = matches[0]["folderId"], "folder"
        elif alias == "personal":
            root_id, kind = source["spaceId"], "space"
            prefix = ""
        else:
            root_id, kind = source["rootFolderId"], "folder"
            if index["scope"]["rootSnapshot"]["folderId"] != root_id:
                raise ValueError("root identity mismatch")
            prefix = ""
        result.append({"alias": alias, "mirror": str(mirror), "scope": {
            "id": str(root_id), "type": kind, "prefix": prefix,
            **({"spaceKey": source.get("spaceKey")} if kind == "space" else {}),
            "cloudId": source["site"].split("/wiki")[0], "site": source["site"].split("/wiki")[0] + "/wiki"}})
    return result
