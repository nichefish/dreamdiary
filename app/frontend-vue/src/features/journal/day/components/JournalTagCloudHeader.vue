<template>
  <div id="journal_tag_header" class="mb-6 ms-4 w-100">
    <template v-for="row in rows" :key="row.id">
      <div :id="row.id" class="mb-4 ms-8">
        <TagCloud
          :tags="row.tags"
          :label="row.label"
          :loading="store.tagCloudLoading"
          :error-message="store.tagCloudSectionError[row.section] ?? null"
          :tag-title="row.tooltip"
          :key-prefix="`${row.id}-`"
          @tag-click="(tag, event) => openTagContextMenu(event, tag as TagCloudItem, row.contentType)"
        />
      </div>
      <div v-if="row.hasSeparator" class="separator"></div>
    </template>
    <JournalPeriodThreadSummary />
  </div>
</template>

<script setup lang="ts">
import { computed, watch } from "vue";
import JournalPeriodThreadSummary from "./JournalPeriodThreadSummary.vue";
import TagCloud from "@/shared/ui/tag/TagCloud.vue";
import { useJournalStore } from "@/features/journal/stores/journal";
import { useJournalTagMenu } from "@/features/journal/shared/composables/useJournalTagMenu";
import type { TagCloudItem } from "@/features/journal/stores/journal";
import { useLocaleStore } from "@/shared/i18n/stores/locale";

const store = useJournalStore();
const { openJournalTagMenu } = useJournalTagMenu();
const { t } = useLocaleStore();

const rows = computed(() => [
  {
    id: "journal_day_tag_header",
    section: "day" as const,
    label: t("journal.tag-cloud.day"),
    tags: store.tagCloud.dayTagList,
    tooltip: t("journal.tag-cloud.menu.tooltip"),
    hasSeparator: true,
    contentType: "JOURNAL_DAY",
  },
  {
    id: "journal_diary_tag_header",
    section: "diary" as const,
    label: t("journal.tag-cloud.diary"),
    tags: store.tagCloud.diaryTagList,
    tooltip: t("journal.tag-cloud.menu.tooltip"),
    hasSeparator: true,
    contentType: "JOURNAL_DIARY",
  },
  {
    id: "journal_dream_tag_header",
    section: "dream" as const,
    label: t("journal.tag-cloud.dream"),
    tags: store.tagCloud.dreamTagList,
    tooltip: t("journal.tag-cloud.menu.tooltip"),
    hasSeparator: false,
    contentType: "JOURNAL_DREAM",
  },
]);

watch([() => store.yy, () => store.mnth, () => store.weekStartDt, () => store.dailyStdrdDt, () => store.viewType], () => {
  void store.fetchTagCloud();
});

function openTagContextMenu(event: MouseEvent, tag: TagCloudItem, contentType: string) {
  openJournalTagMenu(event, {
    tagId: tag.id,
    name: tag.name,
    ctgr: tag.ctgr ?? "",
    contentType,
  });
}
</script>
