<template>
  <!--  <div v-show="props.requestResult?.responseResult.responseCode" class="h-full">-->
  <div class="h-full">
    <div class="flex items-center" :class="$slots.tabRight ? 'border-b border-[var(--color-text-n8)]' : ''">
      <a-tabs v-model:active-key="activeTab" class="no-content flex-1">
        <a-tab-pane v-for="item of sqlResponseCompositionTabList" :key="item.value" :title="item.label" />
      </a-tabs>
      <slot name="tabRight"></slot>
    </div>
    <div v-if="!props.loading && props.requestResult && props.requestResult?.length > 0" class="response-container">
      <Table
        v-if="activeTab === SqlResponseComposition.TABLE"
        ref="resBodyRef"
        :request-result="props.requestResult"
        @copy="copyScript"
        style="height:100%"
        class-name="serchdiv"
        conceal-tab-header
        execute-sql-params=""
        is-active
        sql=""
        view-table
      />
      <!--        :request-result="props.requestResult"-->

      <!--      :request-result="props.requestResult"-->

      <!-- TODO: -->
      <!--      <ResConsole v-else-if="activeTab === SqlResponseComposition.CONSOLE" :console="props.console?.trim()" />-->
      <!--      <ResValueScript-->
      <!--        v-else-if="activeTab === SqlResponseComposition.HEADER || activeTab === SqlResponseComposition.REAL_SQL"-->
      <!--        :active-tab="activeTab"-->
      <!--        :request-result="props.requestResult"-->
      <!--      />-->
    </div>
    <div v-else class="noData">
      <img :src="emptyImg" />
      <p>{{ t('common.text.noData') }}</p>
    </div>
  </div>
  <!-- TODO:这里的默认状态暂时隐藏了，后面需要放开，目前还不知道v-show的条件怎么写，所以先注释掉
  <a-empty
    v-if="props.showEmpty"
    v-show="!props.requestResult?.responseResult.responseCode"
    class="flex h-[150px] items-center gap-[16px] p-[16px]"
  >
    <template #image>
      <img :src="noDataSvg" class="!h-[60px] w-[78px]" />
    </template>
    <div class="flex items-center gap-[8px]">
      <div>{{ t('apiTestManagement.click') }}</div>
      <MsButton
        class="!mr-0"
        type="text"
        :disabled="props.isHttpProtocol && !props.requestUrl"
        @click="emit('execute')"
      >
        {{ props.isPriorityLocalExec ? t('apiTestDebug.localExec') : t('apiTestDebug.serverExec') }}
      </MsButton>
      <div>{{ t('apiTestManagement.getResponse') }}</div>
    </div>
  </a-empty> -->
</template>
<script setup lang="ts">
  import { useClipboard } from '@vueuse/core';
  import { Message } from '@arco-design/web-vue';

  import MsButton from '@/components/pure/ms-button/index.vue';
  import ResBody from './result/body.vue';
  import ResConsole from './result/console.vue';
  import ResValueScript from './result/resValueScript.vue';
  import Table from './result/table.vue';

  import { useI18n } from '@/hooks/useI18n';

  import { IManageResultData } from '@/models/sqlTest/common';
  import { SqlResponseComposition } from '@/enums/apiEnum';

  const emptyImg = `${import.meta.env.BASE_URL}images/empty.svg`;

  const props = withDefaults(
    defineProps<{
      requestResult?: IManageResultData[];
      console?: string;
      isPriorityLocalExec: boolean;
      requestUrl?: string;
      isHttpProtocol?: boolean;
      isDefinition?: boolean;
      showEmpty?: boolean;
      loading?: boolean;
    }>(),
    {
      showEmpty: true,
    }
  );

  const emit = defineEmits(['execute']);

  const { t } = useI18n();

  const noDataSvg = `${import.meta.env.BASE_URL}images/noResponse.svg`;
  const sqlResponseCompositionTabList = [
    {
      label: t('sqlTestDebug.responseTable'),
      value: SqlResponseComposition.TABLE,
    },
    {
      label: t('sqlTestDebug.responseHeader'),
      value: SqlResponseComposition.HEADER,
    },
    {
      label: t('sqlTestDebug.realSql'),
      value: SqlResponseComposition.REAL_SQL,
    },
    {
      label: t('sqlTestDebug.console'),
      value: SqlResponseComposition.CONSOLE,
    },
    ...(props.isDefinition
      ? [
          {
            label: t('apiTestDebug.extract'),
            value: SqlResponseComposition.EXTRACT,
          },
          {
            label: t('apiTestDebug.assertion'),
            value: SqlResponseComposition.ASSERTION,
          },
        ]
      : []),
  ];

  const activeTab = defineModel<SqlResponseComposition>('activeTab', {
    required: true,
    default: SqlResponseComposition.TABLE,
  });

  const { copy, isSupported } = useClipboard({ legacy: true });

  const resBodyRef = ref();

  function copyScript() {
    const encodingFormatValue = resBodyRef.value.responseEditorRef.getEncodingCode();
    if (isSupported) {
      copy(encodingFormatValue || '');
      Message.success(t('common.copySuccess'));
    } else {
      Message.warning(t('apiTestDebug.copyNotSupport'));
    }
  }
</script>

<style lang="less">
  .response-container {
    margin-top: 8px;
    height: calc(100% - 58px);
  }

  :deep(.arco-table-th) {
    background-color: var(--color-text-n9);
  }

  :deep(.arco-tabs-tab) {
    @apply leading-none;
  }

  .no-content :deep(.arco-tabs-content) {
    display: none;
  }

  .noData {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    font-size: 12px;
    overflow: hidden;
  }
</style>
