(async function runChatGptIngestion(context, config) {
  "use strict";

  const { tools, text } = context;
  const TURN_LIMIT = 10;
  const MAX_ITEM_CHARS = 20000;
  const END_MARKER = "__CHATGPT_INGESTION_END__";

  function resultText(result) {
    const block = result?.content?.find((item) => item.type === "text");
    if (!block?.text) throw new Error("read_thread returned no text result");
    return block.text;
  }

  function parseResult(result) {
    const raw = resultText(result);
    let parsed;
    try {
      parsed = JSON.parse(raw);
    } catch {
      throw new Error(`read_thread returned non-JSON text: ${raw.slice(0, 200)}`);
    }
    if (!parsed?.page || !Array.isArray(parsed.turns)) {
      throw new Error("read_thread response has no page or turns array");
    }
    return parsed;
  }

  async function readPage(threadId, cursor) {
    const args = {
      threadId,
      turnLimit: TURN_LIMIT,
      includeOutputs: false,
      maxOutputCharsPerItem: MAX_ITEM_CHARS,
    };
    if (cursor) args.cursor = cursor;
    return parseResult(await tools.mcp__codex_app__read_thread(args));
  }

  function turnIds(page) {
    return page.turns.map((turn) => turn?.id).filter((id) => typeof id === "string");
  }

  async function regressionCheck(canaryThreadId) {
    if (!canaryThreadId) throw new Error("a known conversation with more than 10 turns is required as canaryThreadId");
    const first = await readPage(canaryThreadId);
    const firstIds = new Set(turnIds(first));
    const nextCursor = first.page.nextCursor;
    if (
      first.turns.length !== TURN_LIMIT ||
      first.page.hasMore !== true ||
      typeof nextCursor !== "string" ||
      nextCursor.length === 0
    ) {
      throw new Error(
        `read_thread regression check failed: first page returned ${first.turns.length} turns, ` +
          `hasMore=${String(first.page.hasMore)}, nextCursor=${nextCursor ?? "null"}`
      );
    }
    const second = await readPage(canaryThreadId, nextCursor);
    if (second.turns.length < 1) throw new Error("read_thread regression check failed: second page is empty");
    const duplicate = turnIds(second).find((id) => firstIds.has(id));
    if (duplicate) throw new Error(`read_thread regression check failed: duplicate turn across pages: ${duplicate}`);
    const firstStarts = first.turns.map((turn) => turn.startedAt).filter(Number.isFinite);
    const secondStarts = second.turns.map((turn) => turn.startedAt).filter(Number.isFinite);
    if (
      firstStarts.length !== first.turns.length ||
      secondStarts.length !== second.turns.length ||
      Math.max(...secondStarts) >= Math.min(...firstStarts)
    ) {
      throw new Error("read_thread regression check failed: second page is not strictly older than the first page");
    }
    return {
      passed: true,
      canaryThreadId,
      firstPageTurnCount: first.turns.length,
      firstPageHasMore: true,
      firstPageNextCursor: nextCursor,
      secondPageTurnCount: second.turns.length,
    };
  }

  function validateItems(turn) {
    if (!turn || typeof turn.id !== "string" || !Number.isFinite(turn.startedAt) || !Array.isArray(turn.items)) {
      throw new Error("target contains a turn with missing id, startedAt, or items");
    }
    for (const item of turn.items) {
      if (item?.type === "agentMessage") {
        if (typeof item.text !== "string") throw new Error(`turn ${turn.id} has an invalid agentMessage`);
        if (item.text.length === MAX_ITEM_CHARS || item.truncated === true) {
          throw new Error(`turn ${turn.id} agentMessage may be truncated`);
        }
      } else if (item?.type === "userMessage") {
        if (!Array.isArray(item.content)) throw new Error(`turn ${turn.id} has invalid userMessage content`);
        const combined = item.content
          .map((block) => {
            if (block?.type !== "text" || typeof block.text !== "string") {
              throw new Error(`turn ${turn.id} has an unsupported user content block`);
            }
            return block.text;
          })
          .join("");
        if (combined.length === MAX_ITEM_CHARS) throw new Error(`turn ${turn.id} userMessage reached 20,000 characters`);
      } else {
        throw new Error(`turn ${turn.id} has unsupported item type: ${String(item?.type)}`);
      }
    }
  }

  async function collect(threadId) {
    let cursor;
    let thread;
    let pages = 0;
    const seenCursors = new Set();
    const seenTurns = new Set();
    const turnPages = [];
    const attachments = [];
    while (true) {
      const page = await readPage(threadId, cursor);
      thread ||= page.thread;
      if (page.thread?.id && page.thread.id !== threadId) throw new Error("target conversation identity changed");
      if (page.page.order !== "newest_first") throw new Error(`unsupported page order: ${String(page.page.order)}`);
      pages += 1;
      const pageTurns = [];
      for (const turn of page.turns) {
        validateItems(turn);
        if (seenTurns.has(turn.id)) throw new Error(`duplicate turn id: ${turn.id}`);
        seenTurns.add(turn.id);
        pageTurns.push(turn);
      }
      turnPages.push(pageTurns);
      for (const attachment of page.attachments || []) attachments.push(attachment);
      if (page.page.hasMore === false) break;
      const next = page.page.nextCursor;
      if (typeof next !== "string" || !next) throw new Error("hasMore=true without nextCursor");
      if (seenCursors.has(next)) throw new Error(`cursor loop detected: ${next}`);
      seenCursors.add(next);
      cursor = next;
    }
    if (!thread || thread.id !== threadId) throw new Error("target response has no matching thread metadata");
    const turns = turnPages
      .slice()
      .reverse()
      .flatMap((pageTurns) => pageTurns.slice().reverse());
    return {
      schemaVersion: 1,
      conversationId: threadId,
      title: thread.title || "untitled",
      remoteUpdatedAt: thread.updatedAt,
      turns,
      attachments,
      pagination: {
        exhausted: true,
        pages,
        failureCount: 0,
        duplicateTurnIdCount: 0,
      },
    };
  }

  function encodeBase64(value) {
    const bytes = [];
    for (let index = 0; index < value.length; index += 1) {
      let codePoint = value.charCodeAt(index);
      if (codePoint >= 0xd800 && codePoint <= 0xdbff && index + 1 < value.length) {
        const low = value.charCodeAt(index + 1);
        if (low >= 0xdc00 && low <= 0xdfff) {
          codePoint = 0x10000 + ((codePoint - 0xd800) << 10) + (low - 0xdc00);
          index += 1;
        }
      }
      if (codePoint <= 0x7f) {
        bytes.push(codePoint);
      } else if (codePoint <= 0x7ff) {
        bytes.push(0xc0 | (codePoint >> 6), 0x80 | (codePoint & 0x3f));
      } else if (codePoint <= 0xffff) {
        bytes.push(0xe0 | (codePoint >> 12), 0x80 | ((codePoint >> 6) & 0x3f), 0x80 | (codePoint & 0x3f));
      } else {
        bytes.push(
          0xf0 | (codePoint >> 18),
          0x80 | ((codePoint >> 12) & 0x3f),
          0x80 | ((codePoint >> 6) & 0x3f),
          0x80 | (codePoint & 0x3f)
        );
      }
    }
    const alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    let output = "";
    for (let index = 0; index < bytes.length; index += 3) {
      const a = bytes[index];
      const hasB = index + 1 < bytes.length;
      const hasC = index + 2 < bytes.length;
      const b = hasB ? bytes[index + 1] : 0;
      const c = hasC ? bytes[index + 2] : 0;
      output += alphabet[a >> 2];
      output += alphabet[((a & 3) << 4) | (b >> 4)];
      output += hasB ? alphabet[((b & 15) << 2) | (c >> 6)] : "=";
      output += hasC ? alphabet[c & 63] : "=";
    }
    return output;
  }

  async function persist(payload) {
    const workspaceRoot = config.workspaceRoot;
    if (typeof workspaceRoot !== "string" || !workspaceRoot) throw new Error("workspaceRoot is required");
    const command = "python tools/chatgpt_ingestion/store_snapshot.py --workspace-root .";
    const started = await tools.exec_command({
      cmd: command,
      workdir: workspaceRoot,
      tty: true,
      yield_time_ms: 1000,
      max_output_tokens: 3000,
    });
    if (!started.session_id) {
      throw new Error(`snapshot writer did not wait for input: ${started.output || "no output"}`);
    }
    const encoded = encodeBase64(JSON.stringify(payload));
    for (let offset = 0; offset < encoded.length; offset += 32768) {
      await tools.write_stdin({
        session_id: started.session_id,
        chars: `${encoded.slice(offset, offset + 32768)}\n`,
        yield_time_ms: 250,
        max_output_tokens: 100,
      });
    }
    const finished = await tools.write_stdin({
      session_id: started.session_id,
      chars: `${END_MARKER}\n`,
      yield_time_ms: 30000,
      max_output_tokens: 3000,
    });
    if (finished.session_id) throw new Error("snapshot writer did not finish after receiving the payload");
    const cleanOutput = finished.output
      .replace(/\x1b\][^\x07]*(?:\x07|\x1b\\)/g, "")
      .replace(/\x1b\[[0-?]*[ -/]*[@-~]/g, "");
    if (finished.exit_code !== 0) throw new Error(`snapshot writer failed: ${cleanOutput.trim()}`);
    const resultLine = cleanOutput
      .split(/\r?\n/)
      .map((line) => line.trim())
      .find((line) => line.startsWith('{"conversationId"'));
    if (!resultLine) throw new Error("snapshot writer returned no result object");
    return JSON.parse(resultLine);
  }

  const regression = await regressionCheck(config.canaryThreadId);
  if (config.action === "probe") {
    text(JSON.stringify(regression));
    return regression;
  }
  if (config.action !== "ingest") throw new Error(`unsupported action: ${String(config.action)}`);
  const payload = await collect(config.threadId);
  payload.regressionCheck = regression;
  const stored = await persist(payload);
  text(JSON.stringify(stored));
  return { payload, stored };
})
