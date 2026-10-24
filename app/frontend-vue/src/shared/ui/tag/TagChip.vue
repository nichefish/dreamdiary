<template>
  <!--begin::인라인 태그 한 개 (#[카테고리]태그명)-->
  <span class="text-muted pe-1" :class="{ 'cursor-pointer': clickable }" @click.stop="onClick">
    #<span class="border-bottom text-primary fw-lighter" :class="{ 'opacity-hover': clickable }">
      <span v-if="ctgr" class="fs-7 text-noti">[{{ ctgr }}]</span>{{ name }}
    </span>
  </span>
  <!--end::인라인 태그-->
</template>

<script setup lang="ts">
/**
 * 본문·목록에 붙는 인라인 태그 표시.
 * <pre>
 *  표기는 `#[카테고리]태그명` 하나로 고정한다. `#` 은 밑줄 밖, 카테고리와 이름은 밑줄 안이다.
 *  빈도별 크기(`ts-N`)와 건수를 갖는 태그 클라우드(`TagCloud`)와 달리 고정 크기이며 건수가 없다.
 *  글자 크기 등 문맥별 조정은 호출부가 이 컴포넌트에 class 를 넘겨 적용한다(root 로 병합된다).
 * </pre>
 */
const props = withDefaults(defineProps<{
  /** 태그명 */
  name: string;
  /** 태그 카테고리. 없으면 대괄호 부분을 그리지 않는다. */
  ctgr?: string | null;
  /** 클릭 가능 여부. true 면 커서·hover 표현을 붙이고 `click` 을 내보낸다. */
  clickable?: boolean;
}>(), {
  ctgr: null,
  clickable: false,
});

const emit = defineEmits<{
  click: [event: MouseEvent];
}>();

function onClick(event: MouseEvent): void {
  if (!props.clickable) return;
  emit("click", event);
}
</script>
