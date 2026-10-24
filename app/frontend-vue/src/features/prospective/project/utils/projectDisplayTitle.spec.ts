import { describe, expect, it } from "vitest";
import { projectDisplayTitle, projectPrefixLabel } from "./projectDisplayTitle";

describe("projectDisplayTitle", () => {
  it("말머리를 공백 없이 이름 앞에 붙인다", () => {
    expect(projectDisplayTitle({ title: "왕국건설스토리", prefix: { name: "게임" } })).toBe("[게임]왕국건설스토리");
    expect(projectPrefixLabel({ title: "왕국건설스토리", prefix: { name: "게임" } })).toBe("[게임]");
  });

  it("말머리가 없거나 이름이 비면 Project 이름만 반환한다", () => {
    expect(projectDisplayTitle({ title: "가상 프로젝트" })).toBe("가상 프로젝트");
    expect(projectDisplayTitle({ title: "가상 프로젝트", prefix: null })).toBe("가상 프로젝트");
    expect(projectDisplayTitle({ title: "가상 프로젝트", prefix: { name: "  " } })).toBe("가상 프로젝트");
    expect(projectDisplayTitle(null)).toBe("");
  });

  it("비활성 말머리도 기존 선택이면 그대로 표시한다", () => {
    expect(projectDisplayTitle({ title: "가상", prefix: { name: "보관", activeYn: "N" } })).toBe("[보관]가상");
  });
});
