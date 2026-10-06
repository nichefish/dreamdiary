import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { ActivityIndicator, Modal, Pressable, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { getMonthlyJournalDays } from "../../api/dreamDiaryApi";
import { isUnauthorizedError } from "../../api/client";
import { dayHasEntries } from "../../types/journalDay";
import { colors } from "../../theme/colors";
import { getWeekStartDateStr, normalizeDateStr, toDateStr } from "../../utils/date";

/** 피커 선택 단위 — 일(DAILY)·주(WEEKLY)·월(MONTHLY) 뷰에 대응 */
export type JournalDatePickerMode = "day" | "week" | "month";

export type JournalDatePickerModalProps = {
  visible: boolean;
  mode: JournalDatePickerMode;
  /**
   * 현재 값.
   * - day: 선택 일자 YYYY-MM-DD
   * - week: 주 시작일(월요일) 또는 그 주의 아무 날 YYYY-MM-DD
   * - month: "YYYY-MM" (YYYY-MM-DD도 허용)
   */
  value: string;
  /**
   * 선택 콜백. 호출 직후 피커가 onClose를 부른다.
   * - day: YYYY-MM-DD
   * - week: 선택한 날이 속한 주의 월요일 YYYY-MM-DD (useJournalWeek weekStartDt 기준)
   * - month: "YYYY-MM"
   */
  onSelect: (value: string) => void;
  onClose: () => void;
};

type Panel = "days" | "months" | "years";

const WEEKDAYS_FROM_SUN = ["일", "월", "화", "수", "목", "금", "토"];
const YEARS_PER_PAGE = 12;
const MIN_YEAR = 1900;
const MIN_YM = `${MIN_YEAR}-01`;
const SUNDAY_COLOR = "#B5523B";
const SATURDAY_COLOR = "#3F6A94";

function ymOf(year: number, month: number): string {
  return `${year}-${String(month).padStart(2, "0")}`;
}

function parseYm(ym: string): { year: number; month: number } {
  const [y, m] = ym.split("-").map(Number);
  return { year: y, month: m };
}

function shiftYm(ym: string, delta: number): string {
  const { year, month } = parseYm(ym);
  const d = new Date(year, month - 1 + delta, 1);
  return ymOf(d.getFullYear(), d.getMonth() + 1);
}

function currentYm(): string {
  const now = new Date();
  return ymOf(now.getFullYear(), now.getMonth() + 1);
}

/** YYYY-MM / YYYY-MM-DD → "YYYY-MM" (미래·비정상이면 이번 달) */
function toYm(value: string): string {
  const match = /^(\d{4})-(\d{2})/.exec(value);
  const cur = currentYm();
  if (!match) return cur;
  const month = Number(match[2]);
  if (month < 1 || month > 12) return cur;
  const ym = ymOf(Number(match[1]), month);
  if (ym > cur) return cur;
  return ym < MIN_YM ? MIN_YM : ym;
}

/** 연도 페이지 시작 연도 — 마지막 페이지가 올해로 끝나도록 12년 단위 정렬 */
function yearPageStart(year: number): number {
  const curYear = new Date().getFullYear();
  const page = Math.floor((curYear - year) / YEARS_PER_PAGE);
  return curYear - (YEARS_PER_PAGE - 1) - page * YEARS_PER_PAGE;
}

/** 6주(42칸) 고정 달력 셀. 앞뒤 달 날짜 포함. */
function buildMonthCells(ym: string, weekStartsOn: 0 | 1): string[] {
  const { year, month } = parseYm(ym);
  const first = new Date(year, month - 1, 1);
  const lead = (first.getDay() - weekStartsOn + 7) % 7;
  const cells: string[] = [];
  for (let i = 0; i < 42; i += 1) {
    cells.push(toDateStr(new Date(year, month - 1, 1 - lead + i)));
  }
  return cells;
}

function weekdayColor(dow: number): string | undefined {
  if (dow === 0) return SUNDAY_COLOR;
  if (dow === 6) return SATURDAY_COLOR;
  return undefined;
}

/**
 * 저널 날짜 점프 피커 (바텀시트 모달). 외부 라이브러리 없이 구현.
 * - 일: 달력에서 하루 선택 (일~토 그리드)
 * - 주: 아무 날이나 누르면 그 날이 속한 월요일 시작 주로 이동 (월~일 그리드, 선택 주 행 하이라이트)
 * - 월: 연·월 그리드에서 바로 선택
 * 달력 제목을 누르면 연·월 → 연도(12년 단위) 패널로 올라가 먼 날짜로 빠르게 이동한다.
 * 기록 있는 날은 월 단위 API(getMonthlyJournalDays)로 점 표시하며, 불러온 달은 컴포넌트 state에 캐시한다.
 */
export function JournalDatePickerModal({ visible, mode, value, onSelect, onClose }: JournalDatePickerModalProps) {
  const insets = useSafeAreaInsets();
  const weekStartsOn: 0 | 1 = mode === "week" ? 1 : 0;

  const [panel, setPanel] = useState<Panel>(mode === "month" ? "months" : "days");
  const [viewYm, setViewYm] = useState<string>(() => toYm(value));
  const [pickerYear, setPickerYear] = useState<number>(() => parseYm(toYm(value)).year);
  const [yearPage, setYearPage] = useState<number>(() => yearPageStart(parseYm(toYm(value)).year));

  // 월별 기록 일자 캐시 ("YYYY-MM" → 기록 있는 YYYY-MM-DD 목록)
  const [entryCache, setEntryCache] = useState<Record<string, string[]>>({});
  const [loadingYms, setLoadingYms] = useState<Record<string, boolean>>({});
  const [errorYms, setErrorYms] = useState<Record<string, boolean>>({});
  const loadedRef = useRef<Set<string>>(new Set());
  const inflightRef = useRef<Set<string>>(new Set());
  const mountedRef = useRef(true);

  useEffect(() => {
    mountedRef.current = true;
    return () => {
      mountedRef.current = false;
    };
  }, []);

  const loadMonth = useCallback(async (ym: string, force = false) => {
    if (inflightRef.current.has(ym)) return;
    if (!force && loadedRef.current.has(ym)) return;
    inflightRef.current.add(ym);
    setLoadingYms((prev) => ({ ...prev, [ym]: true }));
    setErrorYms((prev) => ({ ...prev, [ym]: false }));
    try {
      const res = await getMonthlyJournalDays(ym);
      const dates = (res.rsltList ?? [])
        .filter(dayHasEntries)
        .map((day) => normalizeDateStr(String(day.stdrdDt).slice(0, 10)))
        .filter((d): d is string => d != null);
      loadedRef.current.add(ym);
      if (mountedRef.current) setEntryCache((prev) => ({ ...prev, [ym]: dates }));
    } catch (e) {
      if (!isUnauthorizedError(e)) {
        console.error("[JournalDatePickerModal] monthly entry markers load failed", { ym }, e);
      }
      if (mountedRef.current) setErrorYms((prev) => ({ ...prev, [ym]: true }));
    } finally {
      inflightRef.current.delete(ym);
      if (mountedRef.current) setLoadingYms((prev) => ({ ...prev, [ym]: false }));
    }
  }, []);

  // 열릴 때마다 현재 값 기준으로 위치 초기화 + 해당 달 기록 표시는 최신으로 다시 받는다
  useEffect(() => {
    if (!visible) return;
    const ym = toYm(value);
    const { year } = parseYm(ym);
    setViewYm(ym);
    setPickerYear(year);
    setYearPage(yearPageStart(year));
    setPanel(mode === "month" ? "months" : "days");
    if (mode !== "month") void loadMonth(ym, true);
    // value/mode는 열린 동안 바뀌지 않는다 — 열림 시점에만 초기화
  }, [visible]);

  // 표시 중인 달 기록 표시 로드 (캐시에 있으면 생략)
  useEffect(() => {
    if (!visible || panel !== "days") return;
    void loadMonth(viewYm);
  }, [visible, panel, viewYm, loadMonth]);

  const entryDates = useMemo(() => new Set(Object.values(entryCache).flat()), [entryCache]);

  const today = toDateStr(new Date());
  const thisYm = currentYm();
  const thisYear = new Date().getFullYear();
  const valueYm = toYm(value);
  const selectedWeekStart = mode === "week" ? getWeekStartDateStr(value) : null;

  function select(next: string) {
    onSelect(next);
    onClose();
  }

  function handlePickDate(dateStr: string) {
    if (dateStr > today) return;
    if (mode === "week") select(getWeekStartDateStr(dateStr));
    else select(dateStr);
  }

  function handleToday() {
    if (mode === "week") select(getWeekStartDateStr(today));
    else if (mode === "month") select(thisYm);
    else select(today);
  }

  function handlePickMonth(month: number) {
    const ym = ymOf(pickerYear, month);
    if (ym > thisYm) return;
    if (mode === "month") {
      select(ym);
      return;
    }
    setViewYm(ym);
    setPanel("days");
  }

  function handlePickYear(year: number) {
    if (year > thisYear) return;
    setPickerYear(year);
    setPanel("months");
  }

  function openMonthsPanel() {
    setPickerYear(parseYm(viewYm).year);
    setPanel("months");
  }

  function openYearsPanel() {
    setYearPage(yearPageStart(pickerYear));
    setPanel("years");
  }

  const titleByMode = mode === "week" ? "주 선택" : mode === "month" ? "월 선택" : "날짜 선택";
  const todayLabel = mode === "week" ? "이번 주" : mode === "month" ? "이번 달" : "오늘";

  // ── 네비게이션 행 (패널별) ──
  let navTitle: string;
  let navTitleAction: (() => void) | null;
  let canPrev: boolean;
  let canNext: boolean;
  let onPrev: () => void;
  let onNext: () => void;
  if (panel === "days") {
    const { year, month } = parseYm(viewYm);
    navTitle = `${year}년 ${month}월`;
    navTitleAction = openMonthsPanel;
    canPrev = viewYm > MIN_YM;
    canNext = viewYm < thisYm;
    onPrev = () => setViewYm((prev) => shiftYm(prev, -1));
    onNext = () => setViewYm((prev) => shiftYm(prev, 1));
  } else if (panel === "months") {
    navTitle = `${pickerYear}년`;
    navTitleAction = openYearsPanel;
    canPrev = pickerYear > MIN_YEAR;
    canNext = pickerYear < thisYear;
    onPrev = () => setPickerYear((prev) => prev - 1);
    onNext = () => setPickerYear((prev) => prev + 1);
  } else {
    navTitle = `${yearPage} – ${yearPage + YEARS_PER_PAGE - 1}`;
    navTitleAction = () => setPanel("months");
    canPrev = yearPage > MIN_YEAR;
    canNext = yearPage + YEARS_PER_PAGE - 1 < thisYear;
    onPrev = () => setYearPage((prev) => prev - YEARS_PER_PAGE);
    onNext = () => setYearPage((prev) => prev + YEARS_PER_PAGE);
  }

  function renderDays() {
    const cells = buildMonthCells(viewYm, weekStartsOn);
    const rows: string[][] = [];
    for (let i = 0; i < cells.length; i += 7) rows.push(cells.slice(i, i + 7));
    const headers = Array.from({ length: 7 }, (_, col) => (col + weekStartsOn) % 7);

    return (
      <View>
        <View style={styles.weekdayRow}>
          {headers.map((dow) => (
            <Text key={dow} style={[styles.weekdayText, { color: weekdayColor(dow) ?? colors.secondaryText }]}>
              {WEEKDAYS_FROM_SUN[dow]}
            </Text>
          ))}
        </View>
        {rows.map((row) => {
          const weekSelected = selectedWeekStart != null && row[0] === selectedWeekStart;
          return (
            <View key={row[0]} style={[styles.dayRow, weekSelected && styles.dayRowSelected]}>
              {row.map((dateStr, col) => {
                const dow = (col + weekStartsOn) % 7;
                const inMonth = dateStr.slice(0, 7) === viewYm;
                const isFuture = dateStr > today;
                const isTodayCell = dateStr === today;
                const daySelected = mode === "day" && dateStr === value;
                const onDark = daySelected || weekSelected;
                const hasEntry = entryDates.has(dateStr);
                const dayNum = Number(dateStr.slice(8, 10));
                const baseColor = onDark ? colors.onAccent : weekdayColor(dow) ?? colors.text;
                return (
                  <Pressable
                    key={dateStr}
                    accessibilityRole="button"
                    accessibilityLabel={`${Number(dateStr.slice(0, 4))}년 ${Number(dateStr.slice(5, 7))}월 ${dayNum}일${hasEntry ? ", 기록 있음" : ""}`}
                    accessibilityState={{ selected: daySelected || weekSelected, disabled: isFuture }}
                    disabled={isFuture}
                    onPress={() => handlePickDate(dateStr)}
                    style={styles.dayCell}
                  >
                    <View
                      style={[
                        styles.dayCircle,
                        daySelected && styles.dayCircleSelected,
                        isTodayCell && !daySelected && (onDark ? styles.dayCircleTodayOnDark : styles.dayCircleToday)
                      ]}
                    >
                      <Text
                        style={[
                          styles.dayText,
                          { color: baseColor },
                          (daySelected || isTodayCell) && styles.dayTextStrong,
                          !inMonth && styles.dayTextOutside,
                          isFuture && styles.dayTextFuture
                        ]}
                      >
                        {dayNum}
                      </Text>
                      {hasEntry && (
                        <View
                          style={[
                            styles.entryDot,
                            onDark && styles.entryDotOnDark,
                            !inMonth && styles.dayTextOutside
                          ]}
                        />
                      )}
                    </View>
                  </Pressable>
                );
              })}
            </View>
          );
        })}
      </View>
    );
  }

  function renderMonths() {
    const highlightYm = mode === "month" ? valueYm : viewYm;
    return (
      <View style={styles.bigGrid}>
        {Array.from({ length: 12 }, (_, i) => i + 1).map((month) => {
          const ym = ymOf(pickerYear, month);
          const isFuture = ym > thisYm;
          const selected = ym === highlightYm;
          const isCurrent = ym === thisYm;
          const hasEntry = (entryCache[ym]?.length ?? 0) > 0;
          return (
            <Pressable
              key={month}
              accessibilityRole="button"
              accessibilityLabel={`${pickerYear}년 ${month}월`}
              accessibilityState={{ selected, disabled: isFuture }}
              disabled={isFuture}
              onPress={() => handlePickMonth(month)}
              style={styles.bigCell}
            >
              <View
                style={[
                  styles.bigCellInner,
                  selected && styles.bigCellSelected,
                  isCurrent && !selected && styles.bigCellCurrent
                ]}
              >
                <Text
                  style={[
                    styles.bigCellText,
                    selected && styles.bigCellTextSelected,
                    isFuture && styles.dayTextFuture
                  ]}
                >
                  {month}월
                </Text>
                {hasEntry && <View style={[styles.entryDot, selected && styles.entryDotOnDark]} />}
              </View>
            </Pressable>
          );
        })}
      </View>
    );
  }

  function renderYears() {
    return (
      <View style={styles.bigGrid}>
        {Array.from({ length: YEARS_PER_PAGE }, (_, i) => yearPage + i).map((year) => {
          const disabled = year > thisYear || year < MIN_YEAR;
          const selected = year === pickerYear;
          const isCurrent = year === thisYear;
          return (
            <Pressable
              key={year}
              accessibilityRole="button"
              accessibilityLabel={`${year}년`}
              accessibilityState={{ selected, disabled }}
              disabled={disabled}
              onPress={() => handlePickYear(year)}
              style={styles.bigCell}
            >
              <View
                style={[
                  styles.bigCellInner,
                  selected && styles.bigCellSelected,
                  isCurrent && !selected && styles.bigCellCurrent
                ]}
              >
                <Text
                  style={[
                    styles.bigCellText,
                    selected && styles.bigCellTextSelected,
                    disabled && styles.dayTextFuture
                  ]}
                >
                  {year}
                </Text>
              </View>
            </Pressable>
          );
        })}
      </View>
    );
  }

  function renderFooter() {
    if (panel !== "days") {
      return (
        <Text style={styles.footerText}>
          {panel === "months" ? "연도를 누르면 연도 목록으로 이동합니다" : "연도를 고르면 월 선택으로 이동합니다"}
        </Text>
      );
    }
    if (loadingYms[viewYm]) {
      return (
        <View style={styles.footerRow}>
          <ActivityIndicator size="small" color={colors.accent} />
          <Text style={styles.footerText}>기록 표시를 불러오는 중…</Text>
        </View>
      );
    }
    if (errorYms[viewYm]) {
      return (
        <Pressable accessibilityRole="button" onPress={() => { void loadMonth(viewYm, true); }} style={styles.footerRow}>
          <Text style={styles.footerError}>기록 표시를 불러오지 못했습니다 · </Text>
          <Text style={styles.footerRetry}>다시 시도</Text>
        </Pressable>
      );
    }
    return (
      <View style={styles.footerRow}>
        <View style={styles.entryDotLegend} />
        <Text style={styles.footerText}>기록이 있는 날</Text>
      </View>
    );
  }

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      statusBarTranslucent
      navigationBarTranslucent
      onRequestClose={onClose}
    >
      <View style={styles.overlay}>
        <Pressable accessibilityLabel="닫기" style={StyleSheet.absoluteFill} onPress={onClose}>
          <View style={styles.backdrop} />
        </Pressable>

        <View style={[styles.sheet, { paddingBottom: Math.max(insets.bottom, 12) + 8 }]}>
          <View style={styles.handle} />

          <View style={styles.header}>
            <Text style={styles.headerTitle}>{titleByMode}</Text>
            <View style={styles.headerActions}>
              <Pressable accessibilityRole="button" onPress={handleToday} style={styles.todayButton}>
                <Text style={styles.todayButtonText}>{todayLabel}</Text>
              </Pressable>
              <Pressable accessibilityRole="button" accessibilityLabel="닫기" onPress={onClose} hitSlop={8} style={styles.closeButton}>
                <Text style={styles.closeButtonText}>닫기</Text>
              </Pressable>
            </View>
          </View>

          <View style={styles.nav}>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel={panel === "days" ? "이전 달" : panel === "months" ? "이전 해" : "이전 연도 목록"}
              disabled={!canPrev}
              onPress={onPrev}
              style={[styles.navButton, !canPrev && styles.navButtonDisabled]}
            >
              <Text style={[styles.navArrow, !canPrev && styles.navArrowDisabled]}>‹</Text>
            </Pressable>

            <Pressable
              accessibilityRole="button"
              accessibilityHint={panel === "days" ? "연·월 선택" : panel === "months" ? "연도 선택" : undefined}
              disabled={navTitleAction == null}
              onPress={navTitleAction ?? undefined}
              style={styles.navTitleWrap}
            >
              <Text style={styles.navTitle}>{navTitle}</Text>
              {navTitleAction != null && panel !== "years" && <Text style={styles.navTitleCaret}>▾</Text>}
            </Pressable>

            <Pressable
              accessibilityRole="button"
              accessibilityLabel={panel === "days" ? "다음 달" : panel === "months" ? "다음 해" : "다음 연도 목록"}
              disabled={!canNext}
              onPress={onNext}
              style={[styles.navButton, !canNext && styles.navButtonDisabled]}
            >
              <Text style={[styles.navArrow, !canNext && styles.navArrowDisabled]}>›</Text>
            </Pressable>
          </View>

          <View style={styles.body}>
            {panel === "days" ? renderDays() : panel === "months" ? renderMonths() : renderYears()}
          </View>

          <View style={styles.footer}>{renderFooter()}</View>
        </View>
      </View>
    </Modal>
  );
}

const CELL_HEIGHT = 44;
const BODY_HEIGHT = 24 + CELL_HEIGHT * 6;

const styles = StyleSheet.create({
  overlay: { flex: 1, justifyContent: "flex-end" },
  backdrop: { flex: 1, backgroundColor: "rgba(32, 27, 22, 0.45)" },
  sheet: {
    width: "100%",
    maxWidth: 520,
    alignSelf: "center",
    backgroundColor: colors.background,
    borderTopLeftRadius: 18,
    borderTopRightRadius: 18,
    paddingHorizontal: 16,
    paddingTop: 8
  },
  handle: {
    alignSelf: "center",
    width: 40,
    height: 4,
    borderRadius: 2,
    backgroundColor: colors.border,
    marginBottom: 8
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 4,
    paddingBottom: 4
  },
  headerTitle: { color: colors.secondaryText, fontSize: 13, fontWeight: "700" },
  headerActions: { flexDirection: "row", alignItems: "center", gap: 8 },
  todayButton: {
    borderRadius: 14,
    borderWidth: 1,
    borderColor: colors.accent,
    paddingHorizontal: 12,
    paddingVertical: 5
  },
  todayButtonText: { color: colors.accent, fontSize: 13, fontWeight: "700" },
  closeButton: { paddingHorizontal: 6, paddingVertical: 5 },
  closeButtonText: { color: colors.secondaryText, fontSize: 13, fontWeight: "600" },
  nav: { flexDirection: "row", alignItems: "center", paddingVertical: 4 },
  navButton: { paddingHorizontal: 14, paddingVertical: 4 },
  navButtonDisabled: { opacity: 0.3 },
  navArrow: { fontSize: 28, lineHeight: 32, color: colors.accent, fontWeight: "300" },
  navArrowDisabled: { color: colors.muted },
  navTitleWrap: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 4,
    paddingVertical: 6
  },
  navTitle: { color: colors.text, fontSize: 17, fontWeight: "700" },
  navTitleCaret: { color: colors.accent, fontSize: 12, fontWeight: "700" },
  body: { height: BODY_HEIGHT },
  weekdayRow: { flexDirection: "row", height: 24, alignItems: "center" },
  weekdayText: { flex: 1, textAlign: "center", fontSize: 12, fontWeight: "700" },
  dayRow: { flexDirection: "row", height: CELL_HEIGHT, borderRadius: CELL_HEIGHT / 2 },
  dayRowSelected: { backgroundColor: colors.accent },
  dayCell: { flex: 1, alignItems: "center", justifyContent: "center" },
  dayCircle: {
    width: 38,
    height: 38,
    borderRadius: 19,
    alignItems: "center",
    justifyContent: "center"
  },
  dayCircleSelected: { backgroundColor: colors.accent },
  dayCircleToday: { borderWidth: 1.5, borderColor: colors.accent },
  dayCircleTodayOnDark: { borderWidth: 1.5, borderColor: colors.onAccent },
  dayText: { fontSize: 15, fontWeight: "500" },
  dayTextStrong: { fontWeight: "800" },
  dayTextOutside: { opacity: 0.35 },
  dayTextFuture: { opacity: 0.25 },
  entryDot: {
    position: "absolute",
    bottom: 4,
    width: 5,
    height: 5,
    borderRadius: 2.5,
    backgroundColor: colors.accent
  },
  entryDotOnDark: { backgroundColor: colors.onAccent },
  bigGrid: { flex: 1, flexDirection: "row", flexWrap: "wrap", alignContent: "center", paddingVertical: 8 },
  bigCell: { width: "25%", height: 72, alignItems: "center", justifyContent: "center" },
  bigCellInner: {
    minWidth: 64,
    height: 48,
    borderRadius: 24,
    paddingHorizontal: 10,
    alignItems: "center",
    justifyContent: "center"
  },
  bigCellSelected: { backgroundColor: colors.accent },
  bigCellCurrent: { borderWidth: 1.5, borderColor: colors.accent },
  bigCellText: { color: colors.text, fontSize: 15, fontWeight: "600" },
  bigCellTextSelected: { color: colors.onAccent, fontWeight: "800" },
  footer: { height: 32, justifyContent: "center", alignItems: "center" },
  footerRow: { flexDirection: "row", alignItems: "center", gap: 6 },
  footerText: { color: colors.muted, fontSize: 12, fontWeight: "500" },
  footerError: { color: "#C0392B", fontSize: 12 },
  footerRetry: { color: colors.accent, fontSize: 12, fontWeight: "700" },
  entryDotLegend: { width: 5, height: 5, borderRadius: 2.5, backgroundColor: colors.accent }
});