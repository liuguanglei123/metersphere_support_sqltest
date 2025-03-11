<template>
  <div class="flex h-full flex-col gap-[8px]">
    <a-spin class="max-h-[calc(100%-46px)] w-full" :loading="loading">
      <MsTree
          ref="treeRef"
          :expand-all="props.expandAll"
          v-model:data="steps"
          :field-names="{ title: 'name', key: 'uniqueId', children: 'children' }"
          :virtual-list-props="{
          height: '100%',
          threshold: 200,
          fixedSize: true,
          buffer: 15, // 缓冲区默认 10 的时候，虚拟滚动的底部 padding 计算有问题
        }"
          title-class="step-tree-node-title"
          node-highlight-class="step-tree-node-focus"
          action-on-node-click="expand"
          :use-map-data="false"
          disabled-title-tooltip
          checkable
          block-node
          draggable
          hide-switcher
          handle-drop
      >
      </MsTree>
    </a-spin>
    <createStepActions
        v-model:selected-keys="selectedKeys"
        v-model:steps="steps"
        @add-done="handleAddStepDone"
        @other-create="handleOtherCreate"
    >
      <a-button type="dashed" class="add-step-btn" long>
        <div class="flex items-center gap-[8px]">
          <icon-plus />
          {{ t('apiScenario.addStep') }}
        </div>
      </a-button>
    </createStepActions>
  </div>
</template>
<script setup lang="ts">
  import MsTree from "@/components/business/ms-tree/index.vue";
  import createStepActions from './createAction/createStepActions.vue';

  import {useI18n} from "@/hooks/useI18n";

  import {CreateStepAction, ScenarioStepItem} from "@/models/apiTest/scenario";
  import {SqlScenario, SqlScenarioStepItem} from "@/models/sqlTest/scenario";
  import {ScenarioAddStepActionType} from "@/enums/apiEnum";
  import {SqlScenarioAddStepActionType} from "@/enums/sqlEnum";

  const selectedKeys = defineModel<(string | number)[]>('selectedKeys', {
    required: true,
  }); // 没啥用，目前用来展示选中样式

  const { t } = useI18n();

  const props = defineProps<{
    stepKeyword: string;
    expandAll?: boolean;
  }>();

  const steps = defineModel<SqlScenarioStepItem[]>('steps', {
    required: true,
  });

  const emit = defineEmits<{
    (e: 'updateResource', uploadFileIds: string[], linkFileIds: string[]): void;
    (e: 'stepAdd'): void;
  }>();

  const scenario = defineModel<SqlScenario>('scenario', {
    required: true,
  });

  const loading = ref(false);

  function handleAddStepDone(newStep: SqlScenarioStepItem) {
    selectedKeys.value = [newStep.uniqueId]; // 选中新添加的步骤
    emit('stepAdd');
    scenario.value.unSaved = true;
  }

  /**
   * 处理抽屉资源类型步骤创建动作
   */
  function handleOtherCreate(
      type:
          | SqlScenarioAddStepActionType.IMPORT_SYSTEM_SQL,
          // | ScenarioAddStepActionType.CUSTOM_API
          // | ScenarioAddStepActionType.SCRIPT_OPERATION,
      step?: SqlScenarioStepItem,
      _activeCreateAction?: CreateStepAction
  ) {
    // TODO：
    console.log(123123);
  }

</script>
<style lang="less">
.step-tree-active-action {
  color: rgb(var(--primary-5));
  background-color: rgb(var(--primary-1));
}
</style>

<style lang="less" scoped>
.add-step-btn {
  padding: 4px;
  border: 1px dashed rgb(var(--primary-3));
  color: rgb(var(--primary-5));
  background-color: var(--color-text-fff);
  &:hover,
  &:focus {
    border: 1px dashed rgb(var(--primary-5));
    color: rgb(var(--primary-5));
    background-color: rgb(var(--primary-1));
  }
}
// 循环生成树的左边距样式 TODO:transform性能更高以及保留步骤完整宽度，需要加横向滚动
.loop-levels(@index, @max) when (@index <= @max) {
  :deep(.arco-tree-node[data-level='@{index}']) {
    margin-left: @index * 32px;
  }
  .loop-levels(@index + 1, @max); // 下个层级
}
.loop-levels(0, 99); // 最大层级
:deep(.arco-tree-node) {
  padding: 0 8px;
  min-width: 1000px;
  border: 1px solid var(--color-text-n8);
  border-radius: var(--border-radius-medium) !important;
  &:not(:first-child) {
    margin-top: 4px;
  }
  &:hover {
    background-color: var(--color-text-n9) !important;
    .arco-tree-node-title {
      background-color: var(--color-text-n9) !important;
      .step-name-text {
        max-width: calc(100% - 244px) !important;
      }
    }
  }
  .arco-tree-node-title {
  @apply !cursor-pointer;

    padding: 8px 4px;
    background-color: var(--color-text-fff);
    &:hover {
      background-color: var(--color-text-n9) !important;
    }
    .step-node-content {
    @apply flex w-full flex-1 flex-nowrap items-center;

      gap: 8px;
    }
    .step-name-container {
    @apply flex flex-1 items-center overflow-hidden;

      margin-right: 16px;
      &:hover {
        .edit-script-name-icon {
        @apply visible;
        }
      }
      .step-name-text {
        margin-right: 4px;
        max-width: calc(100% - 170px);
        color: var(--color-text-1);
      }
      .edit-script-name-icon {
      @apply invisible cursor-pointer;

        color: rgb(var(--primary-5));
      }
    }
    .arco-tree-node-title-text {
    @apply flex-1;
    }
  }
  .arco-tree-node-indent {
  @apply hidden;
  }
  .arco-tree-node-switcher {
  @apply hidden;
  }
  .arco-tree-node-drag-icon {
  @apply hidden;
  }
  .ms-tree-node-extra {
    gap: 4px;
    background-color: var(--color-text-n9) !important;
  }
}
:deep(.arco-tree-node-selected) {
  .arco-tree-node-title {
    .step-tree-node-title {
      font-weight: 400;
      color: var(--color-text-1);
    }
  }
}
:deep(.step-tree-node-title) {
@apply w-full;
}
:deep(.step-tree-node-focus) {
  background-color: var(--color-text-n9) !important;
  .arco-tree-node-title {
    background-color: var(--color-text-n9) !important;
  }
  .ms-tree-node-extra {
  @apply !visible !w-auto;
  }
}
.ms-form {
  :deep(.arco-form-item-wrapper-col),
  :deep(.arco-form-item-content) {
    min-height: auto;
  }
}
:deep(.value-input) {
  .input-suffix-icon {
    width: 12px;
    height: 12px;
  @apply cursor-pointer;
  }
  .arco-input-suffix {
    opacity: 0;
  }
  &:hover {
    .arco-input-suffix {
      color: rgb(var(--primary-5));
      opacity: 1;
    }
  }
}
</style>