/** Keep the source-controlled Python RPC process alive on the Node tool host. */
const { spawn } = require('node:child_process');
const path = require('node:path');
module.exports = function start(workspace, scope = 'all') {
  const child = spawn('py', ['-3', '-B', path.join(__dirname, 'run_confluence.py'),
                           workspace, '--scope', scope, '--apply'],
                      { cwd: workspace, env: { ...process.env, PYTHONIOENCODING: 'utf-8' },
                        windowsHide: true, stdio: ['pipe', 'pipe', 'pipe'] });
  let buffer = '', errors = '', ended = false, exitCode;
  const queue = [];
  child.stdout.setEncoding('utf8');
  child.stderr.setEncoding('utf8');
  child.stdout.on('data', chunk => {
    buffer += chunk;
    let boundary;
    while ((boundary = buffer.indexOf('\n')) >= 0) {
      const line = buffer.slice(0, boundary).trim();
      buffer = buffer.slice(boundary + 1);
      if (line) queue.push(JSON.parse(line));
    }
  });
  child.stderr.on('data', chunk => { errors += chunk; });
  child.on('close', code => { ended = true; exitCode = code; });
  child.on('error', error => { errors += String(error); ended = true; exitCode = 1; });
  return {
    async next() {
      while (!queue.length && !ended) await new Promise(resolve => setTimeout(resolve, 25));
      return queue.shift() || { done: true, exitCode, errors };
    },
    reply(response) { child.stdin.write(JSON.stringify(response) + '\n', 'utf8'); },
    stop() { child.kill(); }
  };
};
