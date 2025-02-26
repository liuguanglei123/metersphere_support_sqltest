<template>
  <!-- TODO:全文 多语言支持 -->
  <div class="request-composition flex h-full flex-col">
    <div class="mb-[8px] px-[18px] pt-[8px]">
      <div class="flex flex-wrap items-baseline justify-between gap-[12px]">
        <div class="flex flex-1 flex-wrap items-center gap-[16px]">
          <a-checkbox v-model="isPreDataChecked" value="1">预置数据</a-checkbox>
          <a-button type="primary" :disabled="!isPreDataChecked">配置预置数据</a-button>
        </div>
        <div>
          <template v-if="true">
            <a-button class="mr-[12px]" type="primary">服务端执行</a-button>
          </template>

          <template v-if="true">
            <a-dropdown-button
              v-if="requestVModel.mode === 'debug'"
              type="outline"
              class="arco-btn-group-outline--secondary"
            >
              保存
              <template #icon>
                <icon-down />
              </template>
              <template #content>
                <a-doption value="saveAsCase"> 另存为新用例 </a-doption>
              </template>
            </a-dropdown-button>
            <!-- 接口定义-定义模式，直接保存接口定义 -->
            <a-button v-else type="primary"> 保存 </a-button>
          </template>
        </div>
        <!-- 接口定义-调试模式，可保存或保存为新用例 -->
      </div>
    </div>
    <div :class="`${!props.isCase ? 'request-tab-and-response' : ''} flex-1`">
      <div :class="`request-content-and-response ${activeLayout}`">
        <a-spin class="request" :loading="requestVModel.executeLoading">
          <sqlBody :is-debug="requestVModel.mode === 'debug'" :is-case="props.isCase"> </sqlBody>
        </a-spin>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  // TODO:代码拆分，结构优化

  import { TabItem } from '@/components/pure/ms-editable-tab/types';
  import { RequestParam } from '@/views/api-test/components/requestComposition/index.vue';

  import { hasAnyPermission } from '@/utils/permission';

  import { ResponseDefinition } from '@/models/apiTest/common';
  import { ExecuteSqlRequestFullParams, SqlRequestTaskResult } from '@/models/sqlTest/common';
  import { RequestComposition, ResponseComposition } from '@/enums/apiEnum';

  const isPreDataChecked = ref(false);
  const sqlBody = defineAsyncComponent(() => import('./body.vue'));
  const activeLayout = ref<'horizontal' | 'vertical'>('vertical');

  export interface ResponseItem extends TabItem, ResponseDefinition {
    showPopConfirm?: boolean; // 是否显示确认弹窗
    showRenamePopConfirm?: boolean; // 是否显示重命名确认弹窗
    responseActiveTab?: ResponseComposition; // 当前激活的tab
  }

  export interface RequestCustomAttr {
    type: 'api' | 'case' | 'mock' | 'doc'; // 展示的请求 tab 类型；api包含了接口调试和接口定义
    isNew: boolean;
    protocol: string;
    activeTab: RequestComposition;
    mode?: 'definition' | 'debug'; // 接口定义时，展示的定义模式/调试模式（显示的 tab 不同）
    executeLoading: boolean; // 执行中loading
    isCopy?: boolean; // 是否是复制
    isExecute?: boolean; // 是否是执行
    errorMessageInfo?: {
      [key: string]: Record<string, any>;
    };
  }

  export type SqlRequestParam = ExecuteSqlRequestFullParams & {
    responseDefinition?: ResponseItem[];
    response?: SqlRequestTaskResult;
  } & RequestCustomAttr &
    TabItem;

  const props = defineProps<{
    isCase?: boolean; // 是否是用例引用的组件,只显示请求参数和响应内容,响应内容默认为空且折叠
    request: RequestParam; // 请求sql
    request2: SqlRequestParam; // 请求sql
  }>();

  const pluginError = ref(false);

  const requestVModel = defineModel<SqlRequestParam>('request2', { required: true });
</script>

<style lang="less" scoped>
  .exec-btn {
    margin-right: 12px;
    :deep(.arco-btn) {
      color: white !important;
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
  .url-input-tip {
    @apply w-full;

    margin-top: -14px;
    padding-left: 226px;
    font-size: 12px;
    color: rgb(var(--danger-6));
    line-height: 16px;
  }
  .name-input-tip {
    @apply w-full;

    margin-top: 4px;
    font-size: 12px;
    color: rgb(var(--danger-6));
    line-height: 16px;
  }
  .request-tab-and-response {
    overflow-x: hidden;
    overflow-y: auto;
    .ms-scroll-bar();
  }
  .sticky-content {
    @apply sticky bg-white;

    z-index: 101; // .arco-scrollbar-track是100
  }
  .request-content-and-response {
    display: flex;
    &.vertical {
      flex-direction: column;
      .response :deep(.response-head) {
        @apply sticky bg-white;

        top: 46px; // 请求参数tab高度(不算border-bottom)
        z-index: 11;
      }
      .request-tab-pane {
        min-height: 400px;
      }
    }
    &.horizontal {
      flex-direction: row;
      min-height: calc(100% - 49px); // 49px:请求参数tab高度
      .request {
        flex: 1;
        overflow-x: auto;
        border-right: 1px solid var(--color-text-n8);
        .ms-scroll-bar();
        .request-tab-pane {
          min-width: 800px;
        }
      }
      .response {
        width: 500px;
      }
    }
  }
</style>
