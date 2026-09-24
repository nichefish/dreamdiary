/**
 * expireDt 유틸 단위 테스트.
 *
 * 년도 생략 해석은 「가장 가까운 해」 규칙이라 기준일에 따라 답이 갈린다.
 * 기준일을 인자로 주입해 경계 케이스를 고정한다 -- 이 테스트가 규칙의 회귀 방지선이다.
 */
import { describe, expect, it } from "vitest";
import { daysUntilExpire, formatExpireDt, parseExpireDt, resolveNearestYear, scanLastDate } from "./expireDt";

/** 기준일 리터럴. 로컬 타임존 자정 기준 Date 를 만든다. */
function on(iso: string): Date {
  const [y, m, d] = iso.split("-").map(Number) as [number, number, number];
  return new Date(y, m - 1, d);
}

describe("parseExpireDt - 년도 생략", () => {
  it("같은 해 안의 미래 날짜는 당해로 해석한다", () => {
    expect(parseExpireDt("~12/31", on("2026-09-19"))).toEqual({
      kind: "ok", date: "2026-12-31", yearWasOmitted: true,
    });
  });

  it("연말에 적은 1월 날짜는 익년으로 해석한다", () => {
    expect(parseExpireDt("~1/3", on("2026-12-28"))).toEqual({
      kind: "ok", date: "2027-01-03", yearWasOmitted: true,
    });
  });

  it("연초에 적은 12월 날짜는 전년으로 해석한다", () => {
    expect(parseExpireDt("~12/31", on("2026-01-05"))).toEqual({
      kind: "ok", date: "2025-12-31", yearWasOmitted: true,
    });
  });

  it("이미 지난 날짜를 뒤늦게 적으면 과거로 남는다 (놓친 기한 기록)", () => {
    expect(parseExpireDt("~9/15", on("2026-09-19"))).toEqual({
      kind: "ok", date: "2026-09-15", yearWasOmitted: true,
    });
  });
});

describe("parseExpireDt - 년도 명시", () => {
  it("4자리 년도를 그대로 쓴다", () => {
    expect(parseExpireDt("~2027/01/03", on("2026-09-19"))).toEqual({
      kind: "ok", date: "2027-01-03", yearWasOmitted: false,
    });
  });

  it("2자리 년도는 2000년대로 넓힌다", () => {
    expect(parseExpireDt("26/12/31", on("2026-09-19"))).toEqual({
      kind: "ok", date: "2026-12-31", yearWasOmitted: false,
    });
  });

  it("년도를 명시하면 가장 가까운 해 규칙보다 우선한다", () => {
    // 가장 가까운 해 규칙이라면 2026-12-31 이지만, 명시한 2024 를 그대로 쓴다.
    expect(parseExpireDt("2024/12/31", on("2026-01-05"))).toEqual({
      kind: "ok", date: "2024-12-31", yearWasOmitted: false,
    });
  });
});

describe("parseExpireDt - 입력 형태", () => {
  it.each(["~12/31", "12/31", "12-31", "12.31", " ~ 12/31 "])("%s 를 받는다", (raw) => {
    expect(parseExpireDt(raw, on("2026-09-19"))).toMatchObject({ kind: "ok", date: "2026-12-31" });
  });

  it.each(["", "   ", null, undefined])("빈 입력은 기한 해제다", (raw) => {
    expect(parseExpireDt(raw, on("2026-09-19"))).toEqual({ kind: "empty" });
  });

  it.each(["내일", "12/", "13/45", "12/31/2026/1", "2026/13/01"])("%s 는 해석 실패다", (raw) => {
    expect(parseExpireDt(raw, on("2026-09-19"))).toEqual({ kind: "invalid" });
  });
});

describe("parseExpireDt - 존재하지 않는 날", () => {
  it("평년만 후보인 2/29 는 보정하지 않고 실패로 둔다", () => {
    // 2025·2026·2027 모두 평년이라 2/29 가 없다.
    expect(parseExpireDt("~2/29", on("2026-09-19"))).toEqual({ kind: "invalid" });
  });

  it("후보에 윤년이 있으면 그 해로 해석한다", () => {
    expect(parseExpireDt("~2/29", on("2028-03-10"))).toEqual({
      kind: "ok", date: "2028-02-29", yearWasOmitted: true,
    });
  });

  it("4/31 은 5/1 로 넘기지 않는다", () => {
    expect(parseExpireDt("~4/31", on("2026-09-19"))).toEqual({ kind: "invalid" });
  });
});

describe("resolveNearestYear", () => {
  it("동률이면 미래를 택한다", () => {
    // 2027-08-31 기준 3/1 은 과거(2027-03-01)·미래(2028-03-01) 모두 183일.
    // 사이에 2028-02-29 가 끼어 1년이 366일이 되면서 성립하는 구간이다.
    expect(resolveNearestYear(3, 1, on("2027-08-31"))).toBe("2028-03-01");
  });

  it("동률이 아니면 가까운 쪽을 택한다 (하루 차이로 과거가 이긴다)", () => {
    // 2026-09-19 기준 3/21 은 과거 182일 / 미래 183일.
    expect(resolveNearestYear(3, 21, on("2026-09-19"))).toBe("2026-03-21");
  });
});

describe("scanLastDate - 일시 원문에서 기한 뽑기", () => {
  it("다회차 원문에서 마지막 회차를 고른다", () => {
    const result = scanLastDate("10/17,18 · 10/24,25 19:30", on("2026-09-19"));
    expect(result.found).toEqual(["2026-10-17", "2026-10-18", "2026-10-24", "2026-10-25"]);
    expect(result.last).toBe("2026-10-25");
  });

  it("시각은 날짜로 잡지 않는다", () => {
    // `:` 는 날짜 구분자가 아니라 19:30 이 19/30 으로 읽히지 않는다.
    expect(scanLastDate("19:30 시작", on("2026-09-19"))).toEqual({ found: [], last: null });
  });

  it("축약 하나만 있으면 그대로 기한이 된다", () => {
    expect(scanLastDate("~12/31", on("2026-09-19")).last).toBe("2026-12-31");
  });

  it("문장 속에 섞여 있어도 찾는다", () => {
    expect(scanLastDate("신청은 10/10 까지, 공연은 10/17", on("2026-09-19")).last).toBe("2026-10-17");
  });

  it("쉼표 연속 일자는 앞 토큰의 해·월을 따른다", () => {
    // 12/30,31 을 날마다 따로 해석하면 한 묶음이 다른 해로 갈라질 수 있다.
    const result = scanLastDate("12/30,31", on("2026-09-19"));
    expect(result.found).toEqual(["2026-12-30", "2026-12-31"]);
  });

  it("년도를 명시한 토큰과 생략한 토큰이 섞여도 각각 해석한다", () => {
    const result = scanLastDate("2027/01/03 마감, 접수 10/17 부터", on("2026-09-19"));
    expect(result.last).toBe("2027-01-03");
  });

  it("없는 날은 버리고 나머지로 판단한다", () => {
    // 4/31 은 5/1 로 보정하지 않고 버린다.
    const result = scanLastDate("4/31 · 4/20", on("2026-09-19"));
    expect(result.found).toEqual(["2026-04-20"]);
    expect(result.last).toBe("2026-04-20");
  });

  it("날짜가 없거나 원문이 비면 null 이다", () => {
    expect(scanLastDate("다음 주 중", on("2026-09-19"))).toEqual({ found: [], last: null });
    expect(scanLastDate("", on("2026-09-19"))).toEqual({ found: [], last: null });
    expect(scanLastDate(null, on("2026-09-19"))).toEqual({ found: [], last: null });
  });

  it("같은 날짜가 여러 번 나와도 한 번만 센다", () => {
    expect(scanLastDate("10/17 공연, 10/17 재공연", on("2026-09-19")).found).toEqual(["2026-10-17"]);
  });
});

describe("formatExpireDt - 목록 표기", () => {
  it("기준일과 같은 해면 년도를 생략한다", () => {
    expect(formatExpireDt("2026-10-25", on("2026-09-19"))).toBe("~10/25");
  });

  it("다른 해면 2자리 년도를 붙인다", () => {
    expect(formatExpireDt("2027-01-03", on("2026-09-19"))).toBe("~27/01/03");
  });

  it("값이 없거나 형식이 다르면 빈 문자열이다", () => {
    expect(formatExpireDt(null, on("2026-09-19"))).toBe("");
    expect(formatExpireDt("", on("2026-09-19"))).toBe("");
    expect(formatExpireDt("2026/10/25", on("2026-09-19"))).toBe("");
  });
});

describe("daysUntilExpire", () => {
  it("오늘이면 0 이다", () => {
    expect(daysUntilExpire("2026-09-19", on("2026-09-19"))).toBe(0);
  });

  it("미래는 양수, 과거는 음수다", () => {
    expect(daysUntilExpire("2026-09-22", on("2026-09-19"))).toBe(3);
    expect(daysUntilExpire("2026-09-15", on("2026-09-19"))).toBe(-4);
  });
});

describe("해석 결과는 절대 날짜로 확정된다", () => {
  it("같은 입력도 기준일이 다르면 다른 해로 확정된다 -- 그래서 원문이 아니라 결과를 저장한다", () => {
    const a = parseExpireDt("~1/3", on("2026-12-28"));
    const b = parseExpireDt("~1/3", on("2027-01-10"));
    expect(a).toMatchObject({ date: "2027-01-03" });
    expect(b).toMatchObject({ date: "2027-01-03" });

    const c = parseExpireDt("~12/31", on("2026-01-05"));
    const d = parseExpireDt("~12/31", on("2026-09-19"));
    expect(c).toMatchObject({ date: "2025-12-31" });
    expect(d).toMatchObject({ date: "2026-12-31" });
  });
});
