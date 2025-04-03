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
  import {Message} from "@arco-design/web-vue";

  import MsSplitBox from '@/components/pure/ms-split-box/index.vue';
  import baseInfo from '../components/baseInfo.vue';

  import { useI18n } from '@/hooks/useI18n';

  import { ModuleTreeNode } from '@/models/common';
  import { SqlScenario, SqlScenarioDebugRequest } from '@/models/sqlTest/scenario';
  import { SqlScenarioCreateComposition } from '@/enums/sqlEnum';

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

  // 需要最终提示的信息
  function getFlattenedMessages() {
    if (!scenario.value.errorMessageInfo) return;
    const flattenedMessages: { label: string; messageList: string[] }[] = [];
    const { errorMessageInfo } = scenario.value;
    Object.entries(errorMessageInfo).forEach(([key, item]) => {
      const label = item.label || Object.values(item)[0]?.label;
      // 处理前后置已删除的
      // if (key === ScenarioCreateComposition.PRE_POST) {
      //   // 前后置一共的id
      //   const processorIds = [
      //     ...scenario.value.scenarioConfig.preProcessorConfig.processors,
      //     ...scenario.value.scenarioConfig.postProcessorConfig.processors,
      //   ].map((processorItem) => String(processorItem.id));
      //
      //   Object.entries(item).forEach(([childKey, childItem]) => {
      //     if (!processorIds.includes(childKey)) {
      //       childItem.messageList = [];
      //     }
      //   });
      // }
      const messageList: string[] =
          item.messageList || [...new Set(Object.values(item).flatMap((child) => child.messageList))] || [];
      if (messageList.length) {
        flattenedMessages.push({ label, messageList: [...new Set(messageList)] });
      }
    });
    return flattenedMessages;
  }

  function showMessage() {
    getFlattenedMessages()?.forEach(({ label, messageList }) => {
      messageList?.forEach((message) => {
        Message.error(`${label}${message}`);
      });
    });
  }

  const baseInfoRef = ref<InstanceType<typeof baseInfo>>();
  const splitBoxRef = ref<InstanceType<typeof MsSplitBox>>();

  function validScenarioForm(cb: () => Promise<void>) {
    // 检查全部的校验信息
    if (getFlattenedMessages()?.length) {
      showMessage();
      return;
    }
    baseInfoRef.value?.createFormRef?.validate(async (errors) => {
      if (errors) {
        splitBoxRef.value?.expand();
      } else {
        cb();
      }
    });
  }

  defineExpose({
    validScenarioForm,
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
