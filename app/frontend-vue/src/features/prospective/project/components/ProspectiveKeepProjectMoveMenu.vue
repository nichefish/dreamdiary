<template>
  <div class="separator my-2"></div>
  <!-- Project는 단일 소속이므로 선택은 추가가 아니라 이동이다. -->
  <div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="left-start">
    <a href="#" class="menu-link px-3" @click.prevent>
      <span class="menu-title">{{ t("prospective.project.move") }}</span>
      <span class="menu-arrow"></span>
    </a>
    <div class="menu-sub menu-sub-dropdown w-200px py-4">
      <div class="menu-item px-3">
        <a href="#" class="menu-link px-3" :class="{ active: projectId == null }" @click.prevent="emit('move', null)">
          <i v-if="projectId == null" class="bi bi-check2 me-2"></i>{{ t("prospective.project.unassigned") }}
        </a>
      </div>
      <div v-for="project in projects" :key="project.id" class="menu-item px-3">
        <a
          href="#"
          class="menu-link px-3"
          :class="{ active: projectId === project.id, disabled: lifecycleKeyOf(project) !== 'OPEN' }"
          @click.prevent="moveTo(project)"
        >
          <i v-if="projectId === project.id" class="bi bi-check2 me-2"></i>{{ project.title }}
        </a>
      </div>
    </div>
  </div>
  <div class="separator my-2"></div>
</template>

<script setup lang="ts">
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import type { ProspectiveProject } from "@/features/prospective/project/stores/prospectiveProject";

defineProps<{
  projectId?: number | null;
  projects: ProspectiveProject[];
}>();
const emit = defineEmits<{ move: [projectId: number | null] }>();
const { t } = useLocaleStore();

/** 저장 행이 없는 기존 Project는 OPEN이며, 비활성 Project는 현재 소속 표시만 하고 이동 대상으로 쓰지 않는다. */
function lifecycleKeyOf(project: ProspectiveProject): string {
  return project.lifecycle?.lifecycleKey ?? "OPEN";
}

function moveTo(project: ProspectiveProject): void {
  if (lifecycleKeyOf(project) !== "OPEN") return;
  emit("move", project.id);
}
</script>
