/** 모바일 날짜 입력 해석. 웹 features/prospective/keep/utils/expireDt.ts와 같은 규칙을 사용한다. */
/**
 * Keep 항목 유효 기한(expire_dt) 파생 규칙.
 *
 * 이 파일이 해석 규칙의 정본이다. 판정 로직을 컴포넌트에 인라인으로 흩지 않는다.
 *
 * 유효 기한은 사용자가 따로 입력하는 값이 아니라 일시(`date_text`) 원문에서 뽑아낸 파생값이다.
 * {@link scanLastDate} 가 원문에서 날짜를 전부 찾아 가장 늦은 것을 고르고,
 * {@link parseExpireDt} 는 그 안에서 토큰 하나를 해석하는 단위로 쓰인다.
 * 파생 결과는 사용자가 달력으로 고칠 수 있으며, 원문은 기계가 되쓰지 않는다.
 *
 * 규칙:
 *  1. 입력은 `~` 로 시작할 수 있다(표시 관례). 있으면 떼고 해석한다.
 *  2. 년도를 명시하면 그 해를 그대로 쓴다. `2026/12/31`, `26/12/31` 모두 받는다.
 *  3. 년도를 생략하면 **가장 가까운 해**로 해석한다 -- 전년·당해·익년 세 후보 중
 *     오늘과의 일수 차가 최소인 해. 동률이면 미래를 택한다.
 *     연말에 적는 `1/3` 이 익년이 되고, 연초에 적는 `12/31` 이 전년이 되며,
 *     이미 놓친 마감을 뒤늦게 적어도 과거로 남는다.
 *  4. 구분자는 `/` `-` `.` 를 모두 받는다.
 *  5. 해석 결과는 절대 날짜(`yyyy-MM-dd`)로 확정해 저장한다. 저장된 값은 두 번 해석하지 않는다 --
 *     원문을 보관해 매번 재해석하면 같은 항목이 해가 바뀔 때 의미가 달라진다.
 *  6. 달에 없는 날(평년 `2/29`, `4/31` 등)은 해석 실패로 둔다. 가까운 날로 보정하지 않는다.
 */

/** 2자리 년도를 4자리로 넓히는 기준 세기. `26` -> `2026`. */
const CENTURY_BASE = 2000;

/** 해석 결과. 실패와 미입력을 구분한다 -- 미입력은 기한 해제이고, 실패는 사용자 교정이 필요하다. */
export type ExpireDtParseResult =
  /** 입력이 비었다. 기한 해제. */
  | { kind: "empty" }
  /** 해석 성공. `date` 는 `yyyy-MM-dd`. */
  | { kind: "ok"; date: string; yearWasOmitted: boolean }
  /** 해석 실패. 저장을 막고 년도 명시를 요구한다. */
  | { kind: "invalid" };

/** `yyyy-MM-dd` 로 포맷한다. */
function toIsoDate(year: number, month: number, day: number): string {
  return `${String(year).padStart(4, "0")}-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
}

/**
 * 해당 년/월/일이 실제로 존재하는 날인지 확인한다.
 * `Date` 는 존재하지 않는 날을 다음 달로 넘겨 버리므로, 되돌려 받은 값이 같은지로 판정한다.
 */
function isRealDate(year: number, month: number, day: number): boolean {
  const d = new Date(year, month - 1, day);
  return d.getFullYear() === year && d.getMonth() === month - 1 && d.getDate() === day;
}

/** 두 날짜의 일수 차 (절대값). */
function dayDistance(a: Date, b: Date): number {
  return Math.abs(Math.round((a.getTime() - b.getTime()) / 86400000));
}

/**
 * 년도가 생략된 월/일을 가장 가까운 해로 해석한다.
 *
 * @param month 1..12
 * @param day 1..31
 * @param today 기준일 (기본: 오늘)
 * @returns `yyyy-MM-dd`. 세 후보 모두 존재하지 않는 날이면 null (평년 2/29 등)
 */
export function resolveNearestYear(month: number, day: number, today: Date = new Date()): string | null {
  const base = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const candidates = [base.getFullYear() - 1, base.getFullYear(), base.getFullYear() + 1]
    .filter((year) => isRealDate(year, month, day))
    .map((year) => ({ year, distance: dayDistance(new Date(year, month - 1, day), base) }));
  if (candidates.length === 0) return null;

  // 거리 최소. 동률(정확히 반년 떨어진 경우)이면 미래 쪽 -- 기한은 앞을 보는 축이다.
  let best = candidates[0]!;
  for (const c of candidates.slice(1)) {
    if (c.distance < best.distance || (c.distance === best.distance && c.year > best.year)) best = c;
  }
  return toIsoDate(best.year, month, day);
}

/**
 * 유효 기한 입력을 절대 날짜로 해석한다.
 *
 * 받는 형태: `~12/31` `12/31` `12-31` `12.31` `2026/12/31` `26/12/31` `2026-12-31`
 *
 * @param raw 사용자 입력 원문
 * @param today 기준일 (기본: 오늘). 테스트가 경계 케이스를 고정하기 위해 주입한다.
 */
export function parseExpireDt(raw: string | null | undefined, today: Date = new Date()): ExpireDtParseResult {
  const text = (raw ?? "").trim().replace(/^~\s*/, "");
  if (!text) return { kind: "empty" };

  const parts = text.split(/[\/\-.]/).map((p) => p.trim());
  if (parts.some((p) => !/^\d{1,4}$/.test(p))) return { kind: "invalid" };

  if (parts.length === 2) {
    const [month, day] = parts.map(Number) as [number, number];
    const date = resolveNearestYear(month, day, today);
    return date === null ? { kind: "invalid" } : { kind: "ok", date, yearWasOmitted: true };
  }

  if (parts.length === 3) {
    const [rawYear, month, day] = parts.map(Number) as [number, number, number];
    const year = parts[0]!.length <= 2 ? CENTURY_BASE + rawYear : rawYear;
    if (!isRealDate(year, month, day)) return { kind: "invalid" };
    return { kind: "ok", date: toIsoDate(year, month, day), yearWasOmitted: false };
  }

  return { kind: "invalid" };
}

/**
 * 일시 원문에서 찾아낸 날짜들과 그중 가장 늦은 날짜.
 */
export interface ExpireDtScanResult {
  /** 찾은 날짜 전부 (오름차순, `yyyy-MM-dd`). 원문에 날짜가 없으면 빈 배열. */
  found: string[];
  /** 가장 늦은 날짜. 하나도 못 찾았으면 null. */
  last: string | null;
}

/**
 * 일시 원문 전체를 훑어 날짜를 모두 찾고 가장 늦은 것을 고른다.
 *
 * 유효 기한은 「이 항목이 유효한 마지막 날」이므로, 다회차 표기에서는 마지막 회차가 답이다.
 * `10/17,18 · 10/24,25 19:30` 이면 네 날짜를 모두 찾아 10/25 를 고른다.
 * 시각(`19:30`)은 `:` 가 날짜 구분자가 아니라 애초에 날짜로 잡히지 않는다.
 *
 * 쉼표로 이어 쓴 날(`10/17,18`)은 앞 토큰의 **같은 월·같은 해**에 붙인다.
 * 날마다 따로 해석하면 한 묶음이 서로 다른 해로 갈라질 수 있다.
 *
 * @param text 일시 원문
 * @param today 기준일 (기본: 오늘). 년도 생략 해석에 쓴다.
 */
export function scanLastDate(text: string | null | undefined, today: Date = new Date()): ExpireDtScanResult {
  const source = (text ?? "").trim();
  if (!source) return { found: [], last: null };

  // (년)?/월/일 + 뒤따르는 쉼표 연속 일자
  const TOKEN = /(\d{1,4})[\/.-](\d{1,2})(?:[\/.-](\d{1,2}))?((?:\s*,\s*\d{1,2})*)/g;
  const found = new Set<string>();

  for (const m of source.matchAll(TOKEN)) {
    const [, first, second, third, tail] = m;
    const hasYear = third !== undefined;
    const month = Number(hasYear ? second : first);
    const day = Number(hasYear ? third : second);

    let baseIso: string | null;
    if (hasYear) {
      const rawYear = Number(first);
      const year = first!.length <= 2 ? CENTURY_BASE + rawYear : rawYear;
      baseIso = isRealDate(year, month, day) ? toIsoDate(year, month, day) : null;
    } else {
      baseIso = resolveNearestYear(month, day, today);
    }
    if (baseIso === null) continue;
    found.add(baseIso);

    // 쉼표 연속 일자는 앞 토큰이 확정한 해·월을 그대로 따른다.
    if (!tail) continue;
    const baseYear = Number(baseIso.slice(0, 4));
    for (const extra of tail.split(",")) {
      const extraDay = Number(extra.trim());
      if (!extra.trim() || Number.isNaN(extraDay)) continue;
      if (isRealDate(baseYear, month, extraDay)) found.add(toIsoDate(baseYear, month, extraDay));
    }
  }

  const sorted = [...found].sort();
  return { found: sorted, last: sorted.length ? sorted[sorted.length - 1]! : null };
}

