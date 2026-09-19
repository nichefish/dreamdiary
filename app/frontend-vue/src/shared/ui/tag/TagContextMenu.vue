<template>
  <!--begin::태그 컨텍스트 메뉴 (항목 구성은 여는 화면이 정한다)-->
  <Teleport to="body">
    <div
      v-if="store.visible"
      ref="menuEl"
      class="ctx-menu"
      :style="{ left: store.x + 'px', top: store.y + 'px' }"
      @click.stop
    >
      <button
        v-for="(action, index) in store.actions"
        :key="`${index}-${action.label}`"
        type="button"
        class="ctx-menu-btn"
        :class="action.variant ? `ctx-menu-btn--${action.variant}` : undefined"
        @click="run(action)"
      >
        <i class="bi" :class="action.icon"></i>
        <span>{{ action.label }}</span>
      </button>
    </div>
  </Teleport>
  <!--end::태그 컨텍스트 메뉴-->
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import { useTagContextMenuStore, type TagContextMenuAction } from "@/shared/ui/tag/tagContextMenu";

/**
 * 태그 클릭 컨텍스트 메뉴.
 * <pre>
 *  좌표에 항목 목록을 띄우는 표시 전용 컴포넌트다. 어떤 항목이 있어야 하는지는 알지 않고,
 *  store 에 실려 온 목록을 그대로 그린다 -- 저널은 검색·설정, Keep·게시판은 필터 토글·설정.
 *  화면 어디든 하나만 마운트하면 되고, 바깥 클릭·ESC·스크롤·리사이즈에 닫힌다.
 * </pre>
 */
const store = useTagContextMenuStore();
const menuEl = ref<HTMLElement | null>(null);

/** 항목 실행 전 메뉴를 닫는다. 동작이 모달을 열거나 라우팅해도 메뉴가 남지 않게 한다. */
function run(action: TagContextMenuAction): void {
  store.close();
  action.run();
}

function onDocumentClick(evt: MouseEvent): void {
  if (menuEl.value && menuEl.value.contains(evt.target as Node)) return;
  store.close();
}

function onKeydown(evt: KeyboardEvent): void {
  if (evt.key === "Escape") store.close();
}

function onScrollOrResize(): void {
  store.close();
}

onMounted(() => {
  document.addEventListener("click", onDocumentClick);
  document.addEventListener("keydown", onKeydown);
  window.addEventListener("resize", onScrollOrResize);
  window.addEventListener("scroll", onScrollOrResize, true);
});

onUnmounted(() => {
  document.removeEventListener("click", onDocumentClick);
  document.removeEventListener("keydown", onKeydown);
  window.removeEventListener("resize", onScrollOrResize);
  window.removeEventListener("scroll", onScrollOrResize, true);
});
</script>
