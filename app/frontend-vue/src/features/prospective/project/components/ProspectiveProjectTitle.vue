<template>
  <!-- Project 표시 이름: [말머리]이름. 말머리는 색 의미 클래스로 칠하고 이름과 공백 없이 붙인다. -->
  <span class="prospective-project-title"><span
    v-if="prefixLabel"
    class="fw-bold"
    :class="project.prefix?.textClass || undefined"
  >{{ prefixLabel }}</span>{{ project.title }}</span>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { projectPrefixLabel, type ProjectTitleLike } from "@/features/prospective/project/utils/projectDisplayTitle";

const props = defineProps<{ project: ProjectTitleLike }>();
const prefixLabel = computed(() => projectPrefixLabel(props.project));
</script>
