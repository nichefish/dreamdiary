import { useCallback, useEffect, useRef, useState } from "react";
import { useFocusEffect } from "@react-navigation/native";
import { getDailyJournalDay } from "../api/dreamDiaryApi";
import { isUnauthorizedError } from "../api/client";
import type { JournalDay, JournalChapter, JournalEntry } from "../types/journalDay";
import { dayHasEntries, dreamEntriesFromDay } from "../types/journalDay";

export type UseJournalDayOptions = {
  /** 포커스 시 조용한 refresh (스피너 없음). 기본 true */
  refreshOnFocus?: boolean;
};

export type UseJournalDayResult = {
  day: JournalDay | null;
  loading: boolean;
  refreshing: boolean;
  error: string | null;
  chapters: JournalChapter[];
  topDreams: JournalEntry[];
  hasAny: boolean;
  /** 현재 선택 날짜의 조회 성공으로 일자 부재가 확인된 경우에만 true. */
  canRegister: boolean;
  load: (isRefresh?: boolean) => Promise<void>;
  onRefresh: () => void;
};

/**
 * 단일 일자(DAILY) 저널 데이터 로드·refresh 훅.
 * TodayScreen 공통 축.
 */
export function useJournalDay(
  dateStr: string,
  options: UseJournalDayOptions = {}
): UseJournalDayResult {
  const { refreshOnFocus = true } = options;
  const [day, setDay] = useState<JournalDay | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [queriedDate, setQueriedDate] = useState<string | null>(null);
  const [resolvedDate, setResolvedDate] = useState<string | null>(null);
  const requestSequence = useRef(0);
  const currentDate = useRef(dateStr);
  currentDate.current = dateStr;

  const load = useCallback(async (isRefresh = false) => {
    const sequence = ++requestSequence.current;
    const isCurrent = () => sequence === requestSequence.current && currentDate.current === dateStr;
    setResolvedDate(null);
    if (!isRefresh) setLoading(true);
    setError(null);
    try {
      const res = await getDailyJournalDay(dateStr);
      if (!isCurrent()) {
        console.info("[useJournalDay] stale response discarded", { dateStr, sequence });
        return;
      }
      if (res.rslt !== true) throw new Error(res.message || "일자를 불러오지 못했습니다.");
      setDay(res.rsltList?.[0] ?? null);
      setResolvedDate(dateStr);
      setQueriedDate(dateStr);
    } catch (e) {
      if (!isCurrent()) {
        console.info("[useJournalDay] stale error discarded", { dateStr, sequence });
        return;
      }
      if (!isUnauthorizedError(e)) {
        console.error("[useJournalDay] daily journal load failed", { dateStr }, e);
      }
      setQueriedDate(dateStr);
      setError(e instanceof Error ? e.message : "불러오는 중 오류가 발생했습니다.");
    } finally {
      if (isCurrent()) {
        setLoading(false);
        setRefreshing(false);
      }
    }
  }, [dateStr]);

  useEffect(() => {
    void load();
    return () => { requestSequence.current += 1; };
  }, [load]);

  useFocusEffect(
    useCallback(() => {
      if (refreshOnFocus) {
        void load(true);
      }
    }, [load, refreshOnFocus])
  );

  const onRefresh = useCallback(() => {
    setRefreshing(true);
    void load(true);
  }, [load]);

  const chapters = day?.journalChapterList ?? [];
  const topDreams = dreamEntriesFromDay(day);
  const hasAny = day != null && dayHasEntries(day);

  return {
    day: queriedDate === dateStr ? day : null,
    loading: loading || queriedDate !== dateStr,
    refreshing,
    error: queriedDate === dateStr ? error : null,
    chapters,
    topDreams,
    hasAny,
    canRegister: resolvedDate === dateStr && !loading && !refreshing && error == null && day == null,
    load,
    onRefresh
  };
}
