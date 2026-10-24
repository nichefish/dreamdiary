import { StackActions } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import type { RootStackParamList } from "./AppNavigator";

/** 스레드 칩·기간 스레드 섹션 항목에서 넘기는 최소 정보 */
export type ThreadResultTarget = {
  threadId: number | string | null | undefined;
  title?: string | null;
};

/**
 * 스레드 글 목록 화면(ThreadResult)을 연다. 웹의 스레드 칩·기간 스레드 요약 클릭(스레드 상세)에 해당한다.
 * push 액션이라 같은 스레드를 다시 눌러도 새 화면이 쌓이고, 탭 화면 안에서 호출하면 루트 스택까지 전달된다.
 * ID 가 숫자가 아니면 아무 것도 하지 않는다. 제목은 화면이 스레드 상세를 받기 전 임시 표시에 쓴다.
 */
export function openThreadResult(
  navigation: NativeStackNavigationProp<RootStackParamList>,
  target: ThreadResultTarget
): boolean {
  const threadId = Number(target.threadId);
  if (target.threadId == null || target.threadId === "" || !Number.isFinite(threadId)) return false;
  const params: RootStackParamList["ThreadResult"] = { threadId, title: target.title?.trim() ?? "" };
  navigation.dispatch(StackActions.push("ThreadResult", params));
  return true;
}
