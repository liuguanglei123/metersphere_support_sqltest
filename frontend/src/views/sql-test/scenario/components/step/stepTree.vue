<template>
  <div class="flex h-full flex-col gap-[8px]">
    <a-spin class="max-h-[calc(100%-46px)] w-full" :loading="loading">
      <MsTree
        ref="treeRef"
        v-model:data="steps"
        :expand-all="props.expandAll"
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
        @select="(selectedKeys, node) => handleStepSelect(node as SqlScenarioStepItem)"
      >
        <template #title="step">
          <div class="flex w-full items-center gap-[8px]">
            <!-- 步骤序号 -->
            <div
              class="flex h-[16px] min-w-[16px] items-center justify-center rounded-full bg-[var(--color-text-brand)] px-[2px] !text-white"
            >
              {{ step.sort }}
            </div>
            <div class="step-node-content">
              <!-- 步骤展开折叠按钮 -->
              <a-tooltip
                v-if="step.children?.length > 0"
                :content="
                  t(step.expanded ? 'apiScenario.collapseStepTip' : 'apiScenario.expandStepTip', {
                    count: step.children.length,
                  })
                "
              >
                <div class="flex cursor-pointer items-center gap-[2px] text-[var(--color-text-1)]">
                  <MsIcon type="icon-icon_split_turn-down_arrow" :size="14" />
                  {{ step.children?.length || 0 }}
                </div>
              </a-tooltip>
              <div class="mr-[8px] flex items-center gap-[8px]">
                <!-- 步骤启用/禁用，完全引用的场景下的子孙步骤不可禁用 -->
                <a-switch
                  v-model:model-value="step.enable"
                  :disabled="step.isRefScenarioStep"
                  size="small"
                  @click.stop="handleStepToggleEnable(step)"
                ></a-switch>
                <!-- 步骤执行 -->
                <MsIcon
                  v-show="!step.isExecuting"
                  v-permission="['PROJECT_API_SCENARIO:READ+EXECUTE']"
                  type="icon-icon_play-round_filled"
                  :size="18"
                  class="cursor-pointer text-[rgb(var(--link-6))]"
                  @click.stop="executeStep(step)"
                />
                <MsIcon
                  v-show="step.isExecuting"
                  v-permission="['PROJECT_API_SCENARIO:READ+EXECUTE']"
                  type="icon-icon_stop"
                  :size="20"
                  class="cursor-pointer text-[rgb(var(--link-6))]"
                  @click.stop="handleStopExecute(step)"
                />
              </div>
              <!-- 步骤类型 -->
              <stepType :step="step" />
              <!-- 步骤整体内容 -->
              <div class="relative flex flex-1 items-center gap-[4px]">
                <!-- 步骤差异内容，按步骤类型展示不同组件 -->
                <component
                  :is="getStepContent(step)"
                  :data="
                    checkStepIsSql(step) || step.stepType === SqlScenarioStepType.SQL_SCENARIO ? step : step.config
                  "
                  :step-id="step.uniqueId"
                  :disabled="!!step.isQuoteScenarioStep"
                  @quick-input="setQuickInput(step, $event)"
                  @change="handleStepContentChange($event, step)"
                  @click.stop
                />
                <!-- TODO：               <csvTag-->
                <!--                  :step="step"-->
                <!--                  :csv-variables="scenario.scenarioConfig.variable.csvVariables"-->
                <!--                  :disabled="!!step.isQuoteScenarioStep"-->
                <!--                  @remove="(id) => removeCsv(step, id)"-->
                <!--                  @replace="(id) => replaceCsv(step, id)"-->
                <!--                  @remove-deleted="(ids) => removeDeletedCsv(step, ids)"-->
                <!--                />-->
                <!-- 自定义请求、SQL、场景步骤名称 -->
                <template v-if="checkStepIsSql(step)">
                  <sqlMethodName method="" />
                  <div
                    v-if="step.uniqueId === showStepNameEditInputStepId"
                    class="name-warp absolute left-0 top-[-1px] z-10 w-[450px]"
                    @click.stop
                  >
                    <a-input
                      v-model:model-value="tempStepName"
                      :placeholder="t('apiScenario.pleaseInputStepName')"
                      :max-length="255"
                      size="mini"
                      @press-enter="applyStepNameChange(step)"
                      @blur="applyStepNameChange(step)"
                    />
                  </div>
                  <a-tooltip v-else :content="step.name">
                    <div class="step-name-container">
                      <div class="step-name-text one-line-text font-medium">
                        {{ step.name }}
                      </div>
                      <MsIcon
                        v-if="!step.isQuoteScenarioStep"
                        type="icon-icon_edit_outlined"
                        class="edit-script-name-icon"
                        @click.stop="handleStepNameClick(step)"
                      />
                    </div>
                  </a-tooltip>
                </template>
                <!-- 其他步骤描述 -->
                <template v-else>
                  <div
                    v-if="step.uniqueId === showStepDescEditInputStepId"
                    class="desc-warp absolute left-0 top-[-1px] z-10 w-[450px]"
                  >
                    <a-input
                      v-model:model-value="tempStepDesc"
                      :default-value="step.name || t('apiScenario.pleaseInputStepDesc')"
                      :placeholder="t('apiScenario.pleaseInputStepDesc')"
                      :max-length="255"
                      size="mini"
                      @press-enter="applyStepDescChange(step)"
                      @blur="applyStepDescChange(step)"
                      @click.stop
                    >
                      <template #prefix>
                        <div class="text-[12px] leading-[20px]">{{ t('common.desc') }}</div>
                      </template>
                    </a-input>
                  </div>
                  <a-tooltip :content="step.name" :disabled="!step.name">
                    <div class="step-name-container">
                      <div class="step-name-text one-line-text font-normal">
                        {{ step.name || t('apiScenario.pleaseInputStepDesc') }}
                      </div>
                      <MsIcon
                        v-if="!step.isQuoteScenarioStep"
                        type="icon-icon_edit_outlined"
                        class="edit-script-name-icon"
                        @click.stop="handleStepDescClick(step)"
                      />
                    </div>
                  </a-tooltip>
                </template>
              </div>
            </div>
          </div>
        </template>
        <template #extra=""> </template>
        <template #extraEnd="step">
          <responsePopover
            :step="step"
            :step-responses="scenario.stepResponses"
            :final-execute-status="getExecuteStatus(step)"
            @visible-change="handleResponsePopoverVisibleChange"
          />
        </template>
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
    <importSqlDrawer
      v-if="importSqlDrawerVisible"
      v-model:visible="importSqlDrawerVisible"
      :scenario-id="scenario.id"
      @copy="handleImportSqlApply('copy', $event)"
      @quote="handleImportSqlApply('quote', $event)"
    />
    <customSqlDrawer
      v-model:visible="customSqlDrawerVisible"
      :request="currentStepDetail as unknown as SqlRequestParam"
      :step="activeStep"
      :scenario-id="scenario.id"
      :step-responses="scenario.stepResponses"
      :steps="steps"
      @delete-step="() => deleteStep(activeStep)"
      @apply-step="applySqlStep"
      @stop-debug="() => handleStopExecute(activeStep)"
      @execute="handleApiExecute"
      @replace="handleReplaceStep"
    />
  </div>
</template>

<script setup lang="ts">
  import MsIcon from '@/components/pure/ms-icon-font/index.vue';
  import MsTree from '@/components/business/ms-tree/index.vue';
  import importSqlDrawer, { ImportData } from '../common/importSqlDrawer/index.vue';
  import createStepActions from './createAction/createStepActions.vue';
  import sqlMethodName from '@/views/sql-test/components/sqlMethodName.vue';
  import CustomSqlDrawer, { SqlRequestParam } from '@/views/sql-test/scenario/components/common/customSqlDrawer.vue';

  import { useI18n } from '@/hooks/useI18n';
  import useAppStore from '@/store/modules/app';

  import { ScenarioStepDetails, ScenarioStepItem } from '@/models/apiTest/scenario';
  import {
    SqlCreateStepAction,
    SqlScenario,
    SqlScenarioStepDetails,
    SqlScenarioStepItem,
  } from '@/models/sqlTest/scenario';
  import { ScenarioExecuteStatus } from '@/enums/apiEnum';
  import {
    SqlScenarioAddStepActionType,
    SqlScenarioExecuteStatus,
    SqlScenarioStepRefType,
    SqlScenarioStepType,
  } from '@/enums/sqlEnum';

  import useCreateActions from './createAction/useCreateActions';
  import useStepExecute from './useStepExecute';
  import useStepNodeEdit from './useStepNodeEdit';
  import useStepOperation from './useStepOperation';

  const responsePopover = defineAsyncComponent(() => import('../common/responsePopover.vue'));

  const activeStepByCreate = ref<SqlScenarioStepItem | undefined>(); // 用于抽屉操作创建步骤时记录当前操作的步骤节点
  const activeCreateAction = ref<SqlCreateStepAction>(); // 用于抽屉操作创建步骤时记录当前插入类型
  const appStore = useAppStore();

  const selectedKeys = defineModel<(string | number)[]>('selectedKeys', {
    required: true,
  }); // 没啥用，目前用来展示选中样式

  const importSqlDrawerVisible = ref(false);

  const { t } = useI18n();

  const props = defineProps<{
    stepKeyword: string;
    expandAll?: boolean;
  }>();

  const steps = defineModel<SqlScenarioStepItem[]>('steps', {
    required: true,
  });

  const emit = defineEmits<{
    (e: 'stepAdd'): void;
  }>();

  const activeStep = ref<SqlScenarioStepItem>(); // 用于弹窗配置时记录当前操作的步骤节点
  // 步骤详情映射，存储部分抽屉展示详情的数据
  const stepDetails = defineModel<Record<string, SqlScenarioStepDetails>>('stepDetails', {
    required: true,
  });
  const currentStepDetail = computed<SqlScenarioStepDetails | undefined>(() => {
    if (activeStep.value) {
      return stepDetails.value[activeStep.value.id];
    }
    return undefined;
  });

  /**
   * API 详情抽屉关闭时应用更改
   */
  function applySqlStep(request: SqlRequestParam) {
    // TODO：
  }

  function handleReplaceStep(newStep: SqlScenarioStepItem) {
    // TODO：
  }

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
    type: SqlScenarioAddStepActionType.IMPORT_SYSTEM_SQL,
    step?: SqlScenarioStepItem,
    _activeCreateAction?: SqlCreateStepAction
  ) {
    activeStepByCreate.value = step;
    activeCreateAction.value = _activeCreateAction;
    switch (type) {
      case SqlScenarioAddStepActionType.IMPORT_SYSTEM_SQL:
        importSqlDrawerVisible.value = true;
        break;
      default:
        break;
    }
  }

  const { handleCreateStep, handleCreateSteps, buildInsertStepInfos } = useCreateActions();

  const isPriorityLocalExec = inject<Ref<boolean>>('isPriorityLocalExec');
  const localExecuteUrl = inject<Ref<string>>('localExecuteUrl');

  const { executeStep, handleApiExecute, handleStopExecute } = useStepExecute({
    scenario,
    steps,
    stepDetails,
    activeStep,
    isPriorityLocalExec,
    localExecuteUrl,
  });

  const customSqlDrawerVisible = ref(false);
  const scriptOperationDrawerVisible = ref(false);

  const { handleStepExpand, handleStepSelect, deleteStep, handleDrop, getStepDetail } = useStepOperation({
    scenario,
    steps,
    stepDetails,
    activeStep,
    selectedKeys,
    customSqlDrawerVisible,
    loading,
  });

  /**
   * 处理导入系统请求
   * @param type 导入类型
   * @param data 导入数据
   */
  function handleImportSqlApply(type: 'copy' | 'quote', data: ImportData) {
    let sort = steps.value.length + 1;
    if (activeStepByCreate.value && activeCreateAction.value) {
      switch (activeCreateAction.value) {
        case 'inside':
          sort = activeStepByCreate.value.children ? activeStepByCreate.value.children.length : 0;
          break;
        case 'before':
          sort = activeStepByCreate.value.sort;
          break;
        case 'after':
          sort = activeStepByCreate.value.sort + 1;
          break;
        default:
          break;
      }
    }
    const refType = type === 'copy' ? SqlScenarioStepRefType.COPY : SqlScenarioStepRefType.REF;

    const insertSqlSteps = buildInsertStepInfos(
      data.sql,
      SqlScenarioStepType.SQL,
      refType,
      sort,
      appStore.currentProjectId
    );

    const insertScenarioSteps = buildInsertStepInfos(
      data.scenario,
      SqlScenarioStepType.SQL_SCENARIO,
      refType,
      sort + insertSqlSteps.length,
      appStore.currentProjectId
    );

    const insertSteps = insertSqlSteps.concat(insertScenarioSteps);

    if (activeStepByCreate.value && activeCreateAction.value) {
      handleCreateSteps(
        activeStepByCreate.value,
        insertSteps,
        steps.value,
        activeCreateAction.value,
        selectedKeys.value
      );
    } else {
      steps.value = steps.value.concat(insertSteps);
    }
    emit('stepAdd');
    scenario.value.unSaved = true;
  }

  const quickInputDataKey = ref('');
  const quickInputParamValue = ref<any>('');
  const showQuickInput = ref(false);
  const treeRef = ref<InstanceType<typeof MsTree>>();
  const tempStepDesc = ref('');
  const showStepDescEditInputStepId = ref<string | number>('');
  const tempStepName = ref('');
  const showStepNameEditInputStepId = ref<string | number>('');
  const showScenarioConfig = ref(false);

  const {
    setQuickInput,
    clearQuickInput,
    applyQuickInput,
    handleStepDescClick,
    applyStepDescChange,
    handleStepContentChange,
    handleStepToggleEnable,
    handleStepNameClick,
    applyStepNameChange,
    saveScenarioConfig,
    cancelScenarioConfig,
    scenarioConfigForm,
  } = useStepNodeEdit({
    steps,
    scenario,
    activeStep,
    quickInputDataKey,
    quickInputParamValue,
    showQuickInput,
    treeRef,
    tempStepDesc,
    showStepDescEditInputStepId,
    tempStepName,
    showStepNameEditInputStepId,
    loading,
    selectedKeys,
    showScenarioConfig,
  });

  watch(
    () => steps.value,
    (val) => {}
  );

  /**
   * 根据步骤类型获取步骤内容组件
   */
  function getStepContent(step: SqlScenarioStepItem) {
    switch (step.stepType) {
      case SqlScenarioStepType.SQL:
      case SqlScenarioStepType.SQL_SCENARIO:
      default:
        return () => null;
    }
  }

  function checkStepIsSql(step: SqlScenarioStepItem) {
    return [SqlScenarioStepType.SQL].includes(step.stepType);
  }

  function getExecuteStatus(step: SqlScenarioStepItem) {
    if (scenario.value.stepResponses && scenario.value.stepResponses[step.uniqueId]) {
      if (
        scenario.value.stepResponses[step.uniqueId].some(
          (report) => report.status === SqlScenarioExecuteStatus.FAKE_ERROR
        )
      ) {
        return SqlScenarioExecuteStatus.FAKE_ERROR;
      }
      // 整个用例中有一个失败就是失败
      if (scenario.value.stepResponses[step.uniqueId].some((report) => !report.data.success === false)) {
        return SqlScenarioExecuteStatus.FAILED;
      }
      return SqlScenarioExecuteStatus.SUCCESS;
    }

    return step.executeStatus;
  }

  const focusStepKey = ref<string | number>(''); // 聚焦的key

  function setFocusNodeKey(id: string | number) {
    focusStepKey.value = id || '';
  }

  function handleResponsePopoverVisibleChange(visible: boolean, step: SqlScenarioStepItem) {
    if (visible) {
      setFocusNodeKey(step.uniqueId);
    } else {
      setFocusNodeKey('');
    }
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
