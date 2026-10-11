import { useEffect, useRef, useState } from "react";
import { getKeepDetail, getKeepPrefixOptions, getKeepProjects, getKeepTagCategoryMap, modifyKeep, toggleKeepState } from "../../api/dreamDiaryApi";
import { isUnauthorizedError } from "../../api/client";
import { notifyBrief } from "../journal/JournalCopyShareBar";
import { keepHasState, type KeepItem, type KeepPrefixOption, type KeepProject } from "../../types/prospectiveKeep";
import { keepChangedSince, keepEditFailureMessage, keepEditInitial, keepEditPrefixOptions, keepEditProjectOptions } from "../../utils/keepEdit";
import type { KeepQuickAddResult, KeepQuickAddSubmit } from "../../utils/keepQuickAdd";
import { keepDetailTags } from "../../utils/keepTags";
import type { KeepSheetInitial } from "./KeepQuickAddSheet";

/**
 * Keep 수정 시트 흐름 (상세 `수정`과 목록 ⋯ `수정`이 함께 쓴다).
 * - 열기: 상세 응답(목록에서는 ID로 먼저 `GET /api/prospective/keep/{id}`)을 기준으로 삼고, 현재 태그를 정확히 읽을 수 없으면 열지 않는다.
 *   Project·말머리·태그 후보를 새로 받는다(실패하면 현재 값만).
 * - 저장: ① 상세 재조회로 동시 수정 확인(updatedAt) → ② 수정 POST 한 번(보존 필드는 `toKeepEditFormData`) → ③ 중요가 바뀌었으면 IMPRTC 토글 →
 *   시트 닫고 `onSaved`(화면 재조회)·안내. 서버가 응답한 실패·보존 불가는 시트를 열어 둔 채 메시지. 응답 없는 전송 오류는 닫고 `onSaved`로 확인.
 */
export function useKeepEditSheet({ onSaved, onConflict, isBusy, logTag }: {
  /** 저장 뒤(또는 결과를 모를 때) 화면이 다시 읽을 것 */
  onSaved: (id: number) => void;
  /** 저장 직전 다른 곳에서 바뀐 것을 발견했을 때 최신 응답 */
  onConflict?: (fresh: KeepItem) => void;
  /** 화면의 다른 쓰기가 진행 중이면 true — 열기·저장을 막는다 */
  isBusy?: () => boolean;
  logTag: string;
}) {
  const [open, setOpen] = useState(false);
  const [initial, setInitial] = useState<KeepSheetInitial | null>(null);
  const [projects, setProjects] = useState<KeepProject[]>([]);
  const [prefixes, setPrefixes] = useState<KeepPrefixOption[]>([]);
  const [tagMap, setTagMap] = useState<Record<string, string[]>>({});
  const [loading, setLoading] = useState(false);
  const baseRef = useRef<KeepItem | null>(null);
  const savingRef = useRef(false);
  const loadingRef = useRef(false);
  const mountedRef = useRef(true);
  useEffect(() => () => { mountedRef.current = false; }, []);

  /** 수정 시트를 연다. KeepItem 이면 그 상세 응답을, 숫자면 상세를 먼저 받는다. */
  async function openEdit(target: KeepItem | number): Promise<void> {
    if (loadingRef.current || savingRef.current || isBusy?.()) return;
    loadingRef.current = true;
    setLoading(true);
    try {
      let base: KeepItem;
      if (typeof target === "number") {
        try {
          base = await getKeepDetail(target);
        } catch (e) {
          if (!isUnauthorizedError(e)) {
            console.warn(`[${logTag}] edit detail load failed`, { id: target }, e);
            notifyBrief(e instanceof Error && e.message ? e.message : "Keep을 불러오지 못해 수정할 수 없습니다.");
          }
          return;
        }
      } else {
        base = target;
      }
      // 현재 태그를 정확히 채울 수 없으면 저장 때 태그가 지워지므로 시트를 열지 않는다.
      const currentTags = keepDetailTags(base);
      if (currentTags == null) {
        console.warn(`[${logTag}] edit blocked: tags not readable`, { id: base.id });
        notifyBrief("현재 태그를 확인하지 못해 수정할 수 없습니다. 새로 고친 뒤 다시 시도하거나 웹에서 수정하세요.");
        return;
      }
      const [projectRes, prefixRes, tagRes] = await Promise.allSettled([getKeepProjects(), getKeepPrefixOptions(), getKeepTagCategoryMap()]);
      if (!mountedRef.current) return;
      for (const [name, res] of [["project", projectRes], ["prefix", prefixRes], ["tag", tagRes]] as const) {
        if (res.status === "rejected" && !isUnauthorizedError(res.reason)) console.warn(`[${logTag}] edit ${name} options failed`, res.reason);
      }
      baseRef.current = base;
      setProjects(keepEditProjectOptions(projectRes.status === "fulfilled" ? projectRes.value : [], base));
      setPrefixes(keepEditPrefixOptions(prefixRes.status === "fulfilled" ? prefixRes.value : [], base));
      setTagMap(tagRes.status === "fulfilled" ? tagRes.value : {});
      setInitial({ ...keepEditInitial(base), tags: currentTags });
      setOpen(true);
    } finally {
      loadingRef.current = false;
      if (mountedRef.current) setLoading(false);
    }
  }

  async function submit(input: KeepQuickAddSubmit): Promise<KeepQuickAddResult> {
    const base = baseRef.current;
    if (base == null) return { ok: false, message: "수정할 Keep을 확인하지 못했습니다. 다시 열어 주세요." };
    if (savingRef.current) return { ok: false, message: "저장 중입니다." };
    if (isBusy?.()) return { ok: false, message: "다른 요청을 처리하는 중입니다. 잠시 후 다시 저장하세요." };
    savingRef.current = true;
    try {
      let fresh: KeepItem;
      try {
        fresh = await getKeepDetail(base.id);
      } catch (e) {
        if (isUnauthorizedError(e)) return { ok: false, message: null };
        console.warn(`[${logTag}] edit pre-check failed`, { id: base.id }, e);
        return { ok: false, message: "Keep을 다시 확인하지 못해 저장하지 않았습니다. 잠시 후 다시 시도하세요." };
      }
      if (keepChangedSince(base, fresh)) {
        console.warn(`[${logTag}] edit blocked: changed elsewhere`, { id: base.id });
        if (mountedRef.current) onConflict?.(fresh);
        return { ok: false, message: "그사이 다른 곳에서 수정된 Keep입니다. 덮어쓰지 않도록 저장하지 않았습니다. 시트를 닫고 다시 수정하세요." };
      }
      try {
        await modifyKeep(fresh, input);
      } catch (e) {
        if (isUnauthorizedError(e)) return { ok: false, message: null };
        console.error(`[${logTag}] keep modify failed`, { id: base.id }, e);
        const message = keepEditFailureMessage(e);
        const responded = typeof (e as { status?: unknown } | null)?.status === "number" || (e as { name?: unknown } | null)?.name === "KeepEditPreserveError";
        if (!responded && mountedRef.current) {
          setOpen(false);
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
        setOpen(false);
        onSaved(fresh.id);
        getKeepTagCategoryMap().then((map) => { if (mountedRef.current) setTagMap(map); }).catch(() => undefined);
        notifyBrief(notices.length ? `Keep을 수정했습니다. ${notices.join(" ")}` : "Keep을 수정했습니다.");
      }
      return { ok: true, id: fresh.id, notice: notices.length ? notices.join(" ") : null };
    } finally {
      savingRef.current = false;
    }
  }

  return {
    openEdit,
    loading,
    savingRef,
    sheetProps: {
      visible: open,
      projects,
      prefixes,
      defaultProjectId: initial?.projectId ?? null,
      initial,
      tagMap,
      onSubmit: submit,
      onClose: () => { if (!savingRef.current) setOpen(false); }
    }
  };
}
