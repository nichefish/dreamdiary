import { StackActions } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import type { RootStackParamList } from "./AppNavigator";

/** 태그 종류 — day: 일자 태그, diary: 일기 엔트리 태그, dream: 꿈 엔트리 태그 */
export type TagResultKind = "day" | "diary" | "dream";

/** 태그 칩·태그 클라우드 항목에서 넘기는 최소 정보 */
export type TagResultTarget = {
  tagId: number | string | null | undefined;
  name: string;
  ctgr?: string | null;
};

/**
 * 태그별 기록 화면(TagResult)을 연다. 웹 태그 메뉴의 "검색"에 해당한다.
 * push 액션이라 태그 화면에서 다른 태그를 눌러도 새 화면이 쌓이고, 탭 화면 안에서 호출하면 루트 스택까지 전달된다.
 * ID 가 숫자가 아니면 아무 것도 하지 않는다.
 */
export function openTagResult(
  navigation: NativeStackNavigationProp<RootStackParamList>,
  tag: TagResultTarget,
  kind: TagResultKind
): boolean {
  const tagId = Number(tag.tagId);
  if (tag.tagId == null || !Number.isFinite(tagId) || !tag.name?.trim()) return false;
  const params: RootStackParamList["TagResult"] = { tagId, name: tag.name.trim(), ctgr: tag.ctgr ?? null, kind };
  navigation.dispatch(StackActions.push("TagResult", params));
  return true;
}
