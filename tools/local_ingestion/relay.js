// Generic host transport. Policy and response normalization live in Python.
// Load this source verbatim into the tool host and call relay(tools, session).
async function relay(tools, session, pending, maxRequests = 12) {
  let output = pending;
  const results = [];
  for (let n = 0; n < maxRequests; n++) {
    const lines = output.output.split(/\r?\n/).filter(x => x.trim());
    let next;
    for (const line of lines) {
      const item = JSON.parse(line);
      if (item.request) next = item.request;
      else results.push(item);
    }
    if (output.exit_code !== undefined) return { session, output, results, done: true };
    if (!next) throw new Error("No source-generated request; refusing to invent one");
    if (!["searchconfluenceusingcql", "getconfluencepage"].includes(next.tool))
      throw new Error("Connector operation not allowed");
    const name = "mcp__codex_apps__atlassian_rovo__legacy__atlassian_rovo_legacy_" + next.tool;
    const response = await tools[name](next.arguments);
    output = await tools.write_stdin({ session_id: session, chars: JSON.stringify(response) + "\n",
                                     yield_time_ms: 1000, max_output_tokens: 2000 });
  }
  return { session, output, results, done: false };
}

// Desktop host keeps stdin open through host.cjs rather than shell pipe lifetime.
async function relayNode(tools, pending, maxRequests = 10) {
  const results = [];
  for (let n = 0; n < maxRequests; n++) {
    if (pending.done) return { pending, results, done: true };
    if (pending.result) {
      results.push({ result: pending.result, state: pending.state,
                     counts: pending.audit?.counts, predicates: pending.audit?.predicates,
                     preservedMissingComments: pending.audit?.preservedMissingComments?.length,
                     missingRequiredFiles: pending.audit?.missingRequiredFiles, reason: pending.reason });
      const r = await tools.mcp__node_repl__js({
        code: 'nodeRepl.write(JSON.stringify(await cfHost.next()));', title: '적재 실행기 결과 확인' });
      pending = JSON.parse(r.content.find(x => x.type === 'text').text);
      continue;
    }
    const request = pending.request;
    if (!request || !['searchconfluenceusingcql', 'getconfluencepage'].includes(request.tool))
      throw new Error('Missing source-generated read request');
    const name = 'mcp__codex_apps__atlassian_rovo__legacy__atlassian_rovo_legacy_' + request.tool;
    const response = await tools[name](request.arguments);
    // The desktop JS tool limits code length. Buffer JSON as data in memory;
    // chunk boundaries do not alter the response and no raw temp files exist.
    const serialized = JSON.stringify(response);
    await tools.mcp__node_repl__js({ code: 'var cfPayload = "";', title: '응답 전달 버퍼 준비' });
    for (let at = 0; at < serialized.length; at += 6000) {
      const part = serialized.slice(at, at + 6000);
      const append = await tools.mcp__node_repl__js({
        code: 'cfPayload += ' + JSON.stringify(part) + ';', title: '응답 바이트 전달' });
      if (append.isError) throw new Error('Host payload transfer failed');
    }
    const r = await tools.mcp__node_repl__js({
      code: 'cfHost.reply(JSON.parse(cfPayload)); var cfPending = await cfHost.next(); nodeRepl.write(JSON.stringify(cfPending));',
      title: '원격 응답을 소스 실행기에 전달' });
    if (r.isError) throw new Error('Host request exchange failed');
    pending = JSON.parse(r.content.find(x => x.type === 'text').text);
  }
  return { pending, results, done: false };
}
