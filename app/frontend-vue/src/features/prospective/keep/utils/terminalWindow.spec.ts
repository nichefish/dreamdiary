/** terminalWindow 유틸 단위 테스트. 시간대 없는 서버 시각은 브라우저 시간대와 무관하게 Asia/Seoul 로 해석한다. */
import { describe, expect, it } from "vitest";
import { byTerminalRecentDesc, keepTerminalTime, parseServerTime, splitTerminalByWindow } from "./terminalWindow";

const NOW = Date.parse("2026-10-12T12:00:00Z");
const HOUR = 60 * 60 * 1000;

describe("parseServerTime", () => {
  it("시간대가 있는 값은 그대로 해석한다", () => {
    expect(parseServerTime("2026-10-12T03:00:00.000000Z")).toBe(Date.parse("2026-10-12T03:00:00Z"));
  });
  it("시간대 없는 값은 Asia/Seoul(+09:00)로 해석한다", () => {
    expect(parseServerTime("2026-10-12 12:00:00")).toBe(Date.parse("2026-10-12T03:00:00Z"));
    expect(parseServerTime("2026-10-12")).toBe(Date.parse("2026-10-11T15:00:00Z"));
  });
  it("비었거나 형식이 다르면 NaN", () => {
    expect(parseServerTime(null)).toBeNaN();
    expect(parseServerTime("어제")).toBeNaN();
  });
});

describe("keepTerminalTime", () => {
  it("changedAt → resolvedAt → updatedAt → createdAt 순으로 고른다", () => {
    const base = { id: 1, updatedAt: "2026-10-01 09:00:00", createdAt: "2026-09-01 09:00:00" };
    expect(keepTerminalTime({ ...base, lifecycle: { changedAt: "2026-10-10T00:00:00Z", resolvedAt: "2026-10-09T00:00:00Z" } }))
      .toBe(Date.parse("2026-10-10T00:00:00Z"));
    expect(keepTerminalTime({ ...base, lifecycle: { changedAt: null, resolvedAt: "2026-10-09T00:00:00Z" } }))
      .toBe(Date.parse("2026-10-09T00:00:00Z"));
    expect(keepTerminalTime({ ...base, lifecycle: null })).toBe(Date.parse("2026-10-01T00:00:00Z"));
    expect(keepTerminalTime({ id: 1, createdAt: "2026-09-01 09:00:00" })).toBe(Date.parse("2026-09-01T00:00:00Z"));
    expect(keepTerminalTime({ id: 1 })).toBeNaN();
  });
});

describe("splitTerminalByWindow", () => {
  const at = (id: number, iso: string | null) => ({ id, lifecycle: { changedAt: iso } });
  it("7×24시간 경계로 나누고 입력 순서를 유지한다", () => {
    const items = [
      at(1, new Date(NOW - HOUR).toISOString()),
      at(2, new Date(NOW - 7 * 24 * HOUR).toISOString()),
      at(3, new Date(NOW - 7 * 24 * HOUR - 1).toISOString()),
      at(4, null),
      at(5, new Date(NOW + HOUR).toISOString()),
    ];
    const { recent, older } = splitTerminalByWindow(items, NOW);
    expect(recent.map((item) => item.id)).toEqual([1, 2, 5]);
    expect(older.map((item) => item.id)).toEqual([3, 4]);
  });
});

describe("byTerminalRecentDesc", () => {
  it("최근 종결 순, 시각 없음은 맨 아래, 동시각은 id 내림차순", () => {
    const items = [
      { id: 1, lifecycle: { changedAt: "2026-10-01T00:00:00Z" } },
      { id: 2 },
      { id: 3, lifecycle: { changedAt: "2026-10-05T00:00:00Z" } },
      { id: 4, lifecycle: { changedAt: "2026-10-05T00:00:00Z" } },
    ];
    expect(items.slice().sort(byTerminalRecentDesc).map((item) => item.id)).toEqual([4, 3, 1, 2]);
  });
});
