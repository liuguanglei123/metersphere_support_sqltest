<template>
  <div>
    <MsDrawer
      v-model:visible="visible"
      :width="960"
      class="customSqlDrawer"
      no-content-padding
      :show-continue="true"
      :footer="requestVModel.isNew === true"
      :ok-disabled="requestVModel.executeLoading"
      show-full-screen
      unmount-on-close
      @close="handleClose"
    >
      <template #title>
        <div class="flex flex-1 items-center gap-[8px] overflow-hidden">
          <div
            v-if="props.step"
            class="flex h-[16px] min-w-[16px] items-center justify-center rounded-full bg-[var(--color-text-brand)] pr-[2px] !text-white"
          >
            {{ props.step.sort }}
          </div>
          <stepTypeVue
            v-if="props.step && [SqlScenarioStepType.SQL].includes(props.step?.stepType)"
            :step="props.step"
          />
          <div v-if="!isShowEditStepNameInput" class="flex flex-1 items-center gap-[4px] overflow-hidden">
            <a-tooltip :content="title" position="bottom">
              <div class="one-line-text">
                {{ title }}
              </div>
              <MsIcon
                v-if="!props.step || !props.step.isQuoteScenarioStep"
                type="icon-icon_edit_outlined"
                class="min-w-[16px] cursor-pointer hover:text-[rgb(var(--primary-5))]"
                @click="isShowEditStepNameInput = true"
              />
            </a-tooltip>
          </div>
          <a-input
            v-if="isShowEditStepNameInput"
            v-model:model-value="requestVModel.stepName"
            class="flex-1"
            :placeholder="t('apiScenario.pleaseInputStepName')"
            :max-length="255"
            show-word-limit
          />
        </div>
      </template>
      <div class="flex h-full flex-col">
        <div class="flex flex-wrap items-center justify-between gap-[12px] px-[18px] pt-[8px]">
          <div>
            <a-button
              v-if="!requestVModel.executeLoading"
              class="mr-[12px]"
              type="primary"
              @click="() => execute('serverExec')"
            >
              F:{{ t('apiTestDebug.serverExec') }}
            </a-button>
            <a-button v-else type="primary" class="mr-[12px]" @click="stopDebug">
              {{ t('common.stop') }}
            </a-button>
          </div>
        </div>
        <div class="request-tab-and-response flex-1">
          <div class="px-[18px]">
            <a-input
              v-if="props.step?.stepType && _stepType.isQuoteSql"
              v-model:model-value="requestVModel.name"
              :max-length="255"
              :placeholder="t('apiTestManagement.apiNamePlaceholder')"
              :disabled="!isEditableSql"
              allow-clear
              class="my-[8px]"
            />
          </div>
          <div :class="`request-content-and-response ${activeLayout}`">
            <a-spin class="request block h-full w-full" :loading="requestVModel.executeLoading || loading">
              <div class="request-tab-pane flex flex-col p-[16px]">
                <sqlBody v-model:params="requestVModel.body" :is-debug="false" :is-case="true"> </sqlBody>
              </div>
            </a-spin>
          </div>
        </div>
      </div>
    </MsDrawer>
  </div>
</template>

<script setup lang="ts">
  import { cloneDeep } from 'lodash-es';

  import MsDrawer from '@/components/pure/ms-drawer/index.vue';
  import MsIcon from '@/components/pure/ms-icon-font/index.vue';
  import stepTypeVue from './stepType/stepType.vue';

  import { useI18n } from '@/hooks/useI18n';

  import {
    SqlExecuteApiRequestFullParams,
    SqlExecutePluginRequestParams,
    SqlRequestResult,
    SqlRequestTaskResult,
  } from '@/models/sqlTest/common';
  import { SqlScenarioStepItem } from '@/models/sqlTest/scenario';
  import { SqlScenarioStepType } from '@/enums/sqlEnum';

  import getStepType from './stepType/utils';
  import { defaultBodyParams, defaultSqlResponse } from '@/views/sql-test/components/config';

  const sqlBody = defineAsyncComponent(() => import('@/views/sql-test/components/sqlComposition/body.vue'));

  const activeLayout = ref<'horizontal' | 'vertical'>('vertical');
  const loading = defineModel<boolean>('detailLoading', { default: false });

  const isShowEditStepNameInput = ref(false);
  const { t } = useI18n();

  export interface SqlRequestCustomAttr {
    type?: 'sql';
    label: string;
    name: string;
    stepId: string | number; // 所属步骤 id
    uniqueId: string | number; // 前端生成的唯一 id
    stepName: string; // 所属步骤名称
    resourceId: string | number; // 引用、复制的资源 id
    isNew: boolean;
    executeLoading: boolean; // 执行中loading
    isCopy?: boolean; // 是否是复制
    isExecute?: boolean; // 是否是执行
    unSaved: boolean;
    errorMessageInfo?: {
      [key: string]: Record<string, any>;
    };
  }

  export type SqlRequestParam = (SqlExecuteApiRequestFullParams | SqlExecutePluginRequestParams) & {
    response?: SqlRequestTaskResult;
    customizeRequest?: boolean;
    customizeRequestEnvEnable?: boolean;
  } & SqlRequestCustomAttr;

  const props = defineProps<{
    request?: SqlRequestParam; // 请求参数集合
    step?: SqlScenarioStepItem;
    steps: SqlScenarioStepItem[];
    detailLoading?: boolean; // 详情加载状态
    permissionMap?: {
      execute: string;
    };
    stepResponses?: Record<string | number, SqlRequestResult[]>;
  }>();

  const visible = defineModel<boolean>('visible', { required: true });

  const defaultSqlParams: SqlRequestParam = {
    label: '',
    name: '',
    type: 'sql',
    stepId: '',
    uniqueId: '',
    stepName: '',
    resourceId: '',
    customizeRequest: true,
    customizeRequestEnvEnable: true,
    unSaved: false,
    headers: [],
    body: cloneDeep(defaultBodyParams),
    query: [],
    rest: [],
    polymorphicName: '',
    path: '',
    uploadFileIds: [],
    linkFileIds: [],
    response: cloneDeep(defaultSqlResponse),
    isNew: true,
    executeLoading: false,
    errorMessageInfo: {},
  };

  const requestVModel = ref<SqlRequestParam>(defaultSqlParams);

  // 步骤类型判断
  const _stepType = computed(() => {
    if (props.step) {
      return getStepType(props.step);
    }
    return {
      isCopySql: false,
      isQuoteSql: false,
    };
  });

  // 抽屉标题
  const title = computed(() => {
    if (_stepType.value.isCopySql || _stepType.value.isQuoteSql) {
      return requestVModel.value.stepName || requestVModel.value.name || props.step?.name;
    }
    return requestVModel.value.stepName || requestVModel.value.name || t('apiScenario.customApi');
  });

  function handleClose() {
    // 关闭时若不是创建行为则是编辑行为，需要触发 applyStep
    if (!requestVModel.value.isNew) {
      // TODO：
      // emit('applyStep', cloneDeep(makeRequestParams()) as RequestParam);
    }
  }

  /**
   * 执行调试
   * @param val 执行类型
   */
  function execute(executeType?: 'localExec' | 'serverExec') {}

  function stopDebug() {
    requestVModel.value.executeLoading = false;
    // TODO：emit('stopDebug');
  }

  // 全可编辑接口
  const isEditableSql = computed(() => !props.step?.isQuoteScenarioStep && (_stepType.value.isCopySql || !props.step));

  async function initQuoteSqlDetail() {
    try {
      loading.value = true;
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
      loading.value = false;
    }
  }

  watch(
    () => visible.value,
    async (val) => {
      if (val) {
        if (props.request) {
          // 查看自定义请求、引用 api、复制 api
          requestVModel.value = cloneDeep({
            ...defaultSqlParams,
            ...props.request,
            name: props.step?.name || props.request.name,
            stepName: props.step?.name || props.request.name,
            stepId: props.step?.uniqueId || '',
            uniqueId: props.step?.uniqueId || '',
            customizeRequestEnvEnable:
              props.step?.refType === 'DIRECT' ? props.request.customizeRequestEnvEnable : true, // 自定义请求保留本身保存的是否引用环境，其他的请求固定是引用环境
            isNew: false,
          });
          if (_stepType.value.isQuoteSql) {
            // 引用接口时，每次都要获取源接口数据
            await initQuoteSqlDetail();
          }
        }
      }
    }
  );
</script>

<style lang="less">
  .hidden-second {
    :deep(.arco-split-trigger, .arco-split-pane-second) {
      @apply hidden;
    }
  }

  .show-second {
    :deep(.arco-split-trigger, .arco-split-pane-second) {
      @apply block;
    }
  }
</style>

<style lang="less" scoped>
  .exec-btn {
    margin-right: 12px;

    :deep(.arco-btn) {
      color: var(--color-text-fff) !important;
      background-color: rgb(var(--primary-5)) !important;
      .btn-base-primary-hover();
      .btn-base-primary-active();
      .btn-base-primary-disabled();
    }
  }

  :deep(.no-content) {
    .arco-tabs-content {
      display: none;
    }
  }

  :deep(.arco-tabs-tab:first-child) {
    margin-left: 0;
  }

  :deep(.arco-tabs-tab) {
    @apply leading-none;
  }

  .request-tab-and-response {
    overflow-x: hidden;
    overflow-y: auto;
    .ms-scroll-bar();
  }

  .sticky-content {
    @apply sticky;

    z-index: 101;
    background-color: var(--color-text-fff);
  }

  .request-content-and-response {
    display: flex;

    &.vertical {
      flex-direction: column;

      .response :deep(.response-head) {
        @apply sticky;

        top: 46px; // 请求参数tab高度(不算border-bottom)
        z-index: 11;
        background-color: var(--color-text-fff);
      }

      .request-tab-pane {
        min-height: 400px;
      }
    }
  }
</style>
