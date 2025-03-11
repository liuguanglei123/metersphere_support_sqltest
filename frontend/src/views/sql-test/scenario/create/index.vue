<template>
  <MsSplitBox ref="splitBoxRef" :size="0.7" :max="0.9" :min="0.7" direction="horizontal" expand-direction="right">
    <template #first>
      <a-tabs v-model:active-key="activeKey" class="h-full" animation lazy-load>
        <a-tab-pane
            :key="SqlScenarioCreateComposition.STEP"
            :title="t('apiScenario.step')"
            class="scenario-create-tab-pane"
        >
          <step
              v-if="activeKey === SqlScenarioCreateComposition.STEP"
              v-model:scenario="scenario"
              @batch-debug="emit('batchDebug', $event)"
          />
        </a-tab-pane>
      </a-tabs>
    </template>
    <template #second>
      <div class="p-[16px]">
        <baseInfo
            ref="baseInfoRef"
            :scenario="scenario as SqlScenario"
            :module-tree="props.moduleTree"
            @change="scenario.unSaved = true"
        />
      </div>
    </template>
  </MsSplitBox>
</template>
<script setup lang="ts">
  import MsSplitBox from "@/components/pure/ms-split-box/index.vue";
  import baseInfo from '../components/baseInfo.vue';

  import { useI18n } from '@/hooks/useI18n';

  import { ModuleTreeNode } from "@/models/common";
  import {SqlScenario, SqlScenarioDebugRequest} from "@/models/sqlTest/scenario";
  import { SqlScenarioCreateComposition } from "@/enums/sqlEnum";

  const step = defineAsyncComponent(() => import('../components/step/index.vue'));

  const { t } = useI18n();

  const props = defineProps<{
    moduleTree: ModuleTreeNode[]; // 模块树
  }>();

  const emit = defineEmits<{
    (e: 'batchDebug', data: Pick<SqlScenarioDebugRequest, 'steps' | 'stepDetails' | 'reportId'>): void;
  }>();

  const activeKey = ref<SqlScenarioCreateComposition>(SqlScenarioCreateComposition.STEP);

  const scenario = defineModel<SqlScenario>('scenario', {
    required: true,
  });
</script>
<style>
  :deep(.arco-tabs-nav) {
    @apply border-b;
  }
  :deep(.arco-tabs-content) {
    @apply pt-0;

    height: calc(100% - 49px);
    .arco-tabs-content-list {
      @apply h-full;
      .arco-tabs-pane {
        @apply h-full;
      }
    }
    .scenario-create-tab-pane {
      padding: 8px 16px;
    }
  }
</style>