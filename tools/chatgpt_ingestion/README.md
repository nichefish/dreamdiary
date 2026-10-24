# ChatGPT local ingestion executor

This directory contains the source-controlled executor for the ChatGPT local
ingestion contract.

`read_thread_ingest.js` is evaluated inside `functions.exec`, where the
`mcp__codex_app__read_thread` tool exists. It always runs a pagination canary
before collecting a target conversation. `store_snapshot.py` independently
rechecks the canary evidence and the existing local snapshot before it performs
an atomic write.

The canary must be an accessible conversation whose currently selected flow is
known to contain more than 10 turns. The preflight passes only when the first
page returns 10 turns with `hasMore=true` and a non-empty `nextCursor`, and the
second page returns distinct, strictly older turns.

Load the committed adapter rather than rewriting the collection loop:

```javascript
const source = await tools.exec_command({
  cmd: "git show HEAD:tools/chatgpt_ingestion/read_thread_ingest.js",
  workdir: "C:\\Dev\\projects\\dreamdiary\\workspace",
  max_output_tokens: 20000,
});
const run = eval(source.output);
await run(
  { tools, text },
  {
    action: "probe",
    workspaceRoot: "C:\\Dev\\projects\\dreamdiary\\workspace",
    canaryThreadId: "<known-more-than-10-turn-conversation-id>",
  },
);
```

Use `action: "ingest"` and add `threadId` only after the probe succeeds. The
adapter invokes `store_snapshot.py` through a PTY after the writer disables
input echo. It does not create a raw conversation staging file or echo
conversation bytes to a terminal.

Run the deterministic writer tests with:

```powershell
python -m unittest discover -s tools/chatgpt_ingestion -p "test_*.py"
```
