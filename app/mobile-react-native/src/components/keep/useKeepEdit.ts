import { expireFromDateText } from "../../utils/keepQuickAdd";
import type { KeepEditSubmit, KeepEditDraft } from "../../utils/keepEdit";
import { useEffect, useRef, useState } from "react";
import { getKeepDetail, getKeepPrefixOptions, getKeepProjects, getKeepTagCategoryMap, modifyKeep, toggleKeepState } from "../../api/dreamDiaryApi";
import { isUnauthorizedError } from "../../api/client";
import { notifyBrief } from "../journal/JournalCopyShareBar";
import { keepHasState, type KeepItem, type KeepPrefixOption, type KeepProject } from "../../types/prospectiveKeep";
import { keepChangedSince, keepEditFailureMessage, keepEditDraft, copyKeepEditDraft, keepEditDraftChanged, keepEditPrefixOptions, keepEditProjectOptions } from "../../utils/keepEdit";
import type { KeepQuickAddResult } from "../../utils/keepQuickAdd";
import { keepDetailTags } from "../../utils/keepTags";

/**
 * Keep 읽기·편집 통합 화면의 임시 값·저장 흐름. 목록 항목과 목록 ⋯ 수정은 같은 화면을 연다.
 * - 열기: 상세 응답(목록에서는 ID로 먼저 `GET /api/prospective/keep/{id}`)을 기준으로 삼고, 현재 태그를 정확히 읽을 수 없으면 열지 않는다.
 *   Project·말머리·태그 후보를 새로 받는다(실패하면 현재 값만).
 * - 저장: ① 상세 재조회로 동시 수정 확인(updatedAt) → ② 수정 POST 한 번(보존 필드는 `toKeepEditFormData`) → ③ 중요가 바뀌었으면 IMPRTC 토글 →
 *   저장 뒤 `onSaved`(화면 재조회)·안내. 서버가 응답한 실패·보존 불가는 임시 값을 유지하고 메시지. 응답 없는 전송 오류는 임시 값을 해제하고 `onSaved`로 확인.
 */
export function useKeepEdit({ onSaved, onConflict, isBusy, logTag }: {
  /** 저장 뒤(또는 결과를 모를 때) 화면이 다시 읽을 것 */
  onSaved: (id: number) => void;
  /** 저장 직전 다른 곳에서 바뀐 것을 발견했을 때 최신 응답 */
  onConflict?: (fresh: KeepItem) => void;
  /** 화면의 다른 쓰기가 진행 중이면 true — 열기·저장을 막는다 */
  isBusy?: () => boolean;
  logTag: string;
}) {
  const [draft, setDraft] = useState<KeepEditDraft | null>(null);
  const [initial, setInitial] = useState<KeepEditDraft | null>(null);
  const [projects, setProjects] = useState<KeepProject[]>([]);
  const [prefixes, setPrefixes] = useState<KeepPrefixOption[]>([]);
  const [tagMap, setTagMap] = useState<Record<string, string[]>>({});
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [optionError, setOptionError] = useState<string | null>(null);
  const baseRef = useRef<KeepItem | null>(null);
  /** 이번 편집에서 직접 지정한 기한은 일시 자동 추출로 덮어쓰지 않는다. */
  const manualExpireRef = useRef(false);
  const savingRef = useRef(false);
  const loadingRef = useRef(false);
  const mountedRef = useRef(true);
  useEffect(() => () => { mountedRef.current = false; }, []);

  /** 서버 상세를 기준으로 편집값·선택지를 준비한다. 화면이 ID로 읽은 상세를 전달하며 원문·태그 확인 불가는 편집을 차단한다. */
  async function initialize(base: KeepItem): Promise<void> {
    if (loadingRef.current || savingRef.current || isBusy?.()) return;
    loadingRef.current = true;
    manualExpireRef.current = false;
    baseRef.current = null;
    setDraft(null); setInitial(null); baseRef.current = null; setError(null);
    setLoading(true);
    try {
      // 현재 태그를 정확히 채울 수 없으면 저장 때 태그가 지워지므로 편집을 시작하지 않는다.
      const currentTags = keepDetailTags(base);
      if (currentTags == null) {
        console.warn(`[${logTag}] edit blocked: tags not readable`, { id: base.id });
        setError("현재 태그를 확인하지 못해 수정할 수 없습니다. 새로 고친 뒤 다시 시도하거나 웹에서 수정하세요.");
        return;
      }
      const [projectRes, prefixRes, tagRes] = await Promise.allSettled([getKeepProjects(), getKeepPrefixOptions(), getKeepTagCategoryMap()]);
      if (!mountedRef.current) return;
      for (const [name, res] of [["project", projectRes], ["prefix", prefixRes], ["tag", tagRes]] as const) {
        if (res.status === "rejected" && !isUnauthorizedError(res.reason)) console.warn(`[${logTag}] edit ${name} options failed`, res.reason);
      }
      const failedOptions = [projectRes, prefixRes, tagRes].some((res) => res.status === "rejected" && !isUnauthorizedError(res.reason));
      setOptionError(failedOptions ? "일부 선택지를 불러오지 못했습니다. 저장된 값은 유지합니다. 새로 고친 뒤 다시 확인하세요." : null);
      baseRef.current = base;
      setProjects(keepEditProjectOptions(projectRes.status === "fulfilled" ? projectRes.value : [], base));
      setPrefixes(keepEditPrefixOptions(prefixRes.status === "fulfilled" ? prefixRes.value : [], base));
      setTagMap(tagRes.status === "fulfilled" ? tagRes.value : {});
      try {
        const next = keepEditDraft(base);
        setInitial(copyKeepEditDraft(next));
        setDraft(next);
        setError(null);
      } catch (e) {
        console.warn("[KeepEdit] initialization blocked", { id: base.id }, e);
        setDraft(null);
        setInitial(null);
        setError(e instanceof Error ? e.message : "편집할 원문을 확인하지 못했습니다.");
      }
    } finally {
      loadingRef.current = false;
      if (mountedRef.current) setLoading(false);
    }
  }

  async function submit(input: KeepEditSubmit): Promise<KeepQuickAddResult> {
    const base = baseRef.current;
    if (base == null) return { ok: false, message: "수정할 Keep을 확인하지 못했습니다. 다시 열어 주세요." };
    if (savingRef.current) return { ok: false, message: "저장 중입니다." };
    if (isBusy?.()) return { ok: false, message: "다른 요청을 처리하는 중입니다. 잠시 후 다시 저장하세요." };
    savingRef.current = true;
    setSaving(true);
    try {
      let fresh: KeepItem;
      try {
        fresh = await getKeepDetail(base.id);
      } catch (e) {
        if (isUnauthorizedError(e)) return { ok: false, message: null };
        console.warn(`[${logTag}] edit pre-check failed`, { id: base.id }, e);
        return { ok: false, message: "Keep을 다시 확인하지 못해 저장하지 않았습니다. 잠시 후 다시 시도하세요." };
      }
      if (!mountedRef.current) return { ok: false, message: null };
      if (keepChangedSince(base, fresh)) {
        console.warn(`[${logTag}] edit blocked: changed elsewhere`, { id: base.id });
        if (mountedRef.current) onConflict?.(fresh);
        return { ok: false, message: "그사이 다른 곳에서 수정된 Keep입니다. 덮어쓰지 않도록 저장하지 않았습니다. 취소한 뒤 새로 고쳐 다시 수정하세요." };
      }
      try {
        await modifyKeep(fresh, input);
      } catch (e) {
        if (isUnauthorizedError(e)) return { ok: false, message: null };
        console.error(`[${logTag}] keep modify failed`, { id: base.id }, e);
        const message = keepEditFailureMessage(e);
        const responded = typeof (e as { status?: unknown } | null)?.status === "number" && Number((e as { status: number }).status) > 0 || (e as { name?: unknown } | null)?.name === "KeepEditPreserveError";
        if (!responded && mountedRef.current) {
          setDraft(null); setInitial(null); baseRef.current = null;
          onSaved(base.id);
          notifyBrief(message);
          return { ok: false, message: null };
        }
        return { ok: false, message };
      }
      const notices: string[] = [];
      if (input.important !== keepHasState(fresh, "IMPRTC")) {
        try {
          await toggleKeepState(fresh.id, "IMPRTC");
        } catch (e) {
          if (!isUnauthorizedError(e)) {
            console.error(`[${logTag}] edit important toggle failed`, { id: fresh.id }, e);
            notices.push("중요 표시는 바꾸지 못했습니다. 목록의 ⋯에서 다시 설정하세요.");
          }
        }
      }
      if (mountedRef.current) {
        setDraft(null); setInitial(null); baseRef.current = null;
        onSaved(fresh.id);
        notifyBrief(notices.length ? `Keep을 수정했습니다. ${notices.join(" ")}` : "Keep을 수정했습니다.");
      }
      return { ok: true, id: fresh.id, notice: notices.length ? notices.join(" ") : null };
    } finally {
      savingRef.current = false;
      if (mountedRef.current) setSaving(false);
    }
  }

  /** 임시 값만 바꾼다. 저장·초기화 중에는 입력을 받지 않는다. */
  function patch(value: Partial<KeepEditDraft>) {
    if (savingRef.current || loadingRef.current) return;
    const next = { ...value };
    if (Object.prototype.hasOwnProperty.call(value, "expireDt")) {
      manualExpireRef.current = true;
      console.info("[KeepEdit] due date set directly");
    } else if (typeof value.dateText === "string") {
      const extracted = expireFromDateText(value.dateText);
      if (!manualExpireRef.current && extracted) next.expireDt = extracted;
      console.info("[KeepEdit] date text processed", { manualDue: manualExpireRef.current, extracted: Boolean(extracted) });
    }
    setDraft((previous) => previous ? { ...previous, ...next } : previous);
    setError(null);
  }

  /** 취소는 기준값으로 복원한다. 서버 저장은 수행하지 않는다. */
  function reset() {
    if (savingRef.current || !initial) return;
    manualExpireRef.current = false;
    setDraft(copyKeepEditDraft(initial));
    setError(null);
  }

  /** 입력 검사는 화면이 수행하고 결과 메시지는 같은 화면에 남긴다. */
  async function save(input: KeepEditSubmit) {
    const result = await submit(input);
    if (mountedRef.current && !result.ok) setError(result.message);
    return result;
  }

  return {
    initialize, patch, reset, save, draft, initial, projects, prefixes, tagMap, error, optionError,
    loading, loadingRef, saving, savingRef, dirty: initial != null && draft != null && keepEditDraftChanged(initial, draft)
  };
}
