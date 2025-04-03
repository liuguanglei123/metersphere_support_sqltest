<template>
  <div class="h-full w-full overflow-hidden">
    <div class="px-[24px] pt-[16px]">
      <MsDetailCard :title="`【${scenario.num}】${scenario.name}`" :description="description" class="!py-[8px]">
        <template #titlePrefix>
          <sqlStatus :status="scenario.status" size="small" />
          <caseLevel :case-level="scenario.priority as CaseLevel" />
        </template>
        <template #titleAppend>
          <a-tooltip :content="t(scenario.follow ? 'common.forked' : 'common.notForked')">
            <MsIcon
              :loading="followLoading"
              :type="scenario.follow ? 'icon-icon_collect_filled' : 'icon-icon_collection_outlined'"
              :class="`${scenario.follow ? 'text-[rgb(var(--warning-6))]' : 'text-[var(--color-text-4)]'}`"
              class="cursor-pointer"
              :size="16"
              @click="toggleFollowReview"
            />
          </a-tooltip>
          <a-tooltip :content="t('report.detail.api.copyLink')">
            <MsIcon
              type="icon-icon_link-copy_outlined"
              class="cursor-pointer text-[var(--color-text-4)]"
              :size="16"
              @click="share"
            />
          </a-tooltip>
        </template>
      </MsDetailCard>
    </div>
    <div class="h-[calc(100%-104px)]">
      <a-tabs v-model:active-key="activeKey" class="h-full" animation lazy-load>
        <a-tab-pane
          :key="SqlScenarioDetailComposition.BASE_INFO"
          :title="t('apiScenario.baseInfo')"
          class="scenario-detail-tab-pane base-info-pane"
        >
          <baseInfo
            ref="baseInfoRef"
            is-edit
            :scenario="scenario as SqlScenarioDetail"
            :module-tree="props.moduleTree"
            class="w-[30%]"
            @change="scenario.unSaved = true"
          />
        </a-tab-pane>
        <a-tab-pane
          :key="SqlScenarioDetailComposition.STEP"
          :title="t('apiScenario.step')"
          class="scenario-detail-tab-pane"
        >
          <step
            v-if="activeKey === SqlScenarioDetailComposition.STEP"
            v-model:scenario="scenario"
            @batch-debug="emit('batchDebug', $event)"
          />
        </a-tab-pane>
        <a-tab-pane
          :key="SqlScenarioDetailComposition.EXECUTE_HISTORY"
          :title="t('apiScenario.executeHistory')"
          class="scenario-detail-tab-pane"
        >
          <executeHistory v-if="activeKey === SqlScenarioDetailComposition.EXECUTE_HISTORY" :scenario-id="scenario.id" />
        </a-tab-pane>
        <a-tab-pane
          :key="SqlScenarioDetailComposition.CHANGE_HISTORY"
          :title="t('apiScenario.changeHistory')"
          class="scenario-detail-tab-pane"
        >
          <changeHistory v-if="activeKey === SqlScenarioDetailComposition.CHANGE_HISTORY" :source-id="scenario.id" />
        </a-tab-pane>
        <!-- <a-tab-pane
          :key="SqlScenarioDetailComposition.DEPENDENCY"
          :title="t('apiScenario.dependency')"
          class="scenario-detail-tab-pane"
        >
          <dependency v-if="activeKey === SqlScenarioDetailComposition.DEPENDENCY" />
        </a-tab-pane>
        <a-tab-pane :key="SqlScenarioDetailComposition.QUOTE" :title="t('apiScenario.quote')" class="scenario-detail-tab-pane">
          <quote v-if="activeKey === SqlScenarioDetailComposition.QUOTE" />
        </a-tab-pane> -->
      </a-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n';
  import { useClipboard } from '@vueuse/core';
  import { Message } from '@arco-design/web-vue';
  import { cloneDeep, debounce } from 'lodash-es';

  import MsDetailCard from '@/components/pure/ms-detail-card/index.vue';
  import MsIcon from '@/components/pure/ms-icon-font/index.vue';
  import caseLevel from '@/components/business/ms-case-associate/caseLevel.vue';
  import type { CaseLevel } from '@/components/business/ms-case-associate/types';
  import baseInfo from '../components/baseInfo.vue';
  import step from '../components/step/index.vue';
  import { TabErrorMessage } from '@/views/sql-test/components/requestComposition/index.vue';
  import sqlStatus from '@/views/sql-test/components/sqlStatus.vue';

  import { ModuleTreeNode } from '@/models/common';
  // import { followScenario } from '@/api/modules/sql-test/scenario';
  import { SqlScenario, SqlScenarioDebugRequest, SqlScenarioDetail } from '@/models/sqlTest/scenario';
  import { SqlScenarioDetailComposition } from '@/enums/sqlEnum';

  const props = defineProps<{
    moduleTree: ModuleTreeNode[]; // 模块树
  }>();
  const emit = defineEmits<{
    (e: 'batchDebug', data: Pick<SqlScenarioDebugRequest, 'steps' | 'stepDetails' | 'reportId'>): void;
    (e: 'updateFollow'): void;
  }>();

  const { copy, isSupported } = useClipboard({ legacy: true });
  const { t } = useI18n();

  const scenario = defineModel<SqlScenario>('scenario', {
    required: true,
  });

  const description = computed(() => [
    {
      key: 'tag',
      locale: 'common.tag',
      value: scenario.value.tags,
    },
    {
      key: 'description',
      locale: 'common.desc',
      value: scenario.value.description,
    },
  ]);

  const followLoading = ref(false);

  async function toggleFollowReview() {
    try {
      followLoading.value = true;
      // await followScenario(scenario.value.id || '');
      scenario.value.follow = !scenario.value.follow;
      Message.success(scenario.value.follow ? t('common.followSuccess') : t('common.unFollowSuccess'));
      emit('updateFollow');
    } catch (error) {
      // eslint-disable-next-line no-console
      console.error(error);
    } finally {
      followLoading.value = false;
    }
  }

  function share() {
    if (isSupported) {
      const url = window.location.href;
      const dIdParam = `&id=${scenario.value.id}`;
      const copyUrl = url.includes('id') ? url.split('&id')[0] : url;
      copy(`${copyUrl}${dIdParam}`);
      Message.success(t('common.copySuccess'));
    } else {
      Message.error(t('common.copyNotSupport'));
    }
  }

  const activeKey = ref<SqlScenarioDetailComposition>(SqlScenarioDetailComposition.STEP);

  // 前置和后置在一个tab里，isChangePre用于判断当前修改的form是前置还是后置
  const isChangePre = ref(true);

  function changePrePost(changePre: boolean) {
    isChangePre.value = changePre;
    scenario.value.unSaved = true;
  }

  // TODO: 优化，拆出来
  function initErrorMessageInfoItem(key: string) {
    if (scenario.value.errorMessageInfo && !scenario.value.errorMessageInfo[key]) {
      scenario.value.errorMessageInfo[key] = {};
    }
  }

  function setChildErrorMessage(listItem: TabErrorMessage) {
    // TODO：
    // if (!scenario.value.errorMessageInfo) return;
    // scenario.value.errorMessageInfo[activeKey.value][key] = cloneDeep(listItem);
  }

  function changeTabErrorMessageList(tabKey: string, formErrorMessageList: string[]) {
    if (!scenario.value.errorMessageInfo) return;
    initErrorMessageInfoItem(tabKey);
  }

  const setErrorMessageList = debounce((list: string[]) => {
    changeTabErrorMessageList(activeKey.value, list);
  }, 300);
  provide('setErrorMessageList', setErrorMessageList);

  // 需要最终提示的信息
  function getFlattenedMessages() {
    if (!scenario.value.errorMessageInfo) return;
    const flattenedMessages: { label: string; messageList: string[] }[] = [];
    const { errorMessageInfo } = scenario.value;
    Object.entries(errorMessageInfo).forEach(([key, item]) => {
      const label = item.label || Object.values(item)[0]?.label;
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

  function validScenarioForm(cb: () => Promise<void>) {
    // 检查全部的校验信息
    if (getFlattenedMessages()?.length) {
      showMessage();
      return;
    }
    if (!baseInfoRef.value) {
      cb();
      return;
    }
    baseInfoRef.value?.createFormRef?.validate(async (errors) => {
      if (errors) {
        activeKey.value = SqlScenarioDetailComposition.BASE_INFO;
      } else {
        cb();
      }
    });
  }

  defineExpose({
    validScenarioForm,
  });
</script>

<style lang="less" scoped>
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

    .scenario-detail-tab-pane {
      padding: 8px 16px;
    }

    .base-info-pane {
      @apply h-full overflow-auto;
      .ms-scroll-bar();
    }
  }
</style>
