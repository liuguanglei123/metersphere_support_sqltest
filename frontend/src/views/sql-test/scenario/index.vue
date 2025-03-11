<template>
  <MsCard no-content-padding simple>
    <MsSplitBox :size="300" :max="0.5">
      <template #first>
        <div class="flex h-full flex-col">
          <div class="flex-1 p-[16px]">
            <scenarioModuleTree> </scenarioModuleTree>
          </div>
        </div>
      </template>
      <template #second>
        <div class="flex items-center justify-between p-[8px_16px_8px_16px]">
          <MsEditableTab
            v-model:active-tab="activeScenarioTab"
            v-model:tabs="sqlScenarioTabs"
            class="flex-1 overflow-hidden"
            @add="() => newTab()"
          >
            <template #label="{ tab }">
              <a-tooltip :content="tab.name || tab.label" :mouse-enter-delay="500">
                <div class="one-line-text max-w-[144px]">
                  {{ tab.name || tab.label }}
                </div>
              </a-tooltip>
            </template>
          </MsEditableTab>
          <div v-show="activeScenarioTab.id !== 'all'" class="flex items-center gap-[8px]">
            <!--              <MsEnvironmentSelect :env="activeScenarioTab.environmentId" />-->
            <executeButton
              ref="executeButtonRef"
              v-permission="['PROJECT_API_SCENARIO:READ+EXECUTE']"
              :execute-loading="activeScenarioTab.executeLoading"
              @execute="handleExecute"
              @stop-debug="handleStopExecute"
            />
            <a-button
              v-if="
                activeScenarioTab.isNew
                  ? hasAnyPermission(['PROJECT_API_SCENARIO:READ+ADD'])
                  : hasAnyPermission(['PROJECT_API_SCENARIO:READ+UPDATE'])
              "
              type="primary"
              :loading="saveLoading"
              @click="saveScenario"
            >
              {{ t('common.save') }}
            </a-button>
          </div>
        </div>
        <a-divider class="!my-0" />
        <keep-alive :include="cacheStore.cacheViews">
          <MsCacheWrapper
              v-if="activeScenarioTab.id === 'all'"
              :key="CacheTabTypeEnum.SQL_SCENARIO_TABLE"
              class="pageWrap overflow-x-hidden"
              :cache-name="CacheTabTypeEnum.SQL_SCENARIO_TABLE"
          >
            <ScenarioTable
              ref="apiTableRef"
              :module-tree="moduleTree"
              :active-module="activeModule"
              :offspring-ids="offspringIds"
              @refresh-module-tree="refreshTree"
              @open-scenario="openSqlScenarioTab"
              @create-scenario="() => newTab()"
            />
          </MsCacheWrapper>
        </keep-alive>

        <div v-if="activeScenarioTab.isNew && activeScenarioTab.id !== 'all'" class="pageWrap">
          <create
              ref="createRef"
              v-model:scenario="activeScenarioTab"
              :module-tree="moduleTree"
              @batch-debug="realExecute($event, false)"
          ></create>
        </div>
        <div v-if="!activeScenarioTab.isNew && activeScenarioTab.id !== 'all'" class="pageWrap">
          <detail
              ref="detailRef"
              v-model:scenario="activeScenarioTab"
              :module-tree="moduleTree"
              @batch-debug="realExecute($event, false)"
          ></detail>
        </div>
      </template>
    </MsSplitBox>
  </MsCard>
</template>

<script setup lang="ts">
  import { cloneDeep } from 'lodash-es';

  import MsCacheWrapper from "@/components/pure/ms-cache-wrapper/index.vue";
  import MsCard from '@/components/pure/ms-card/index.vue';
  import { SQLTabItem } from '@/components/pure/ms-editable-tab/types';
  import MsSplitBox from '@/components/pure/ms-split-box/index.vue';
  import MsEditableTab from '@/components/pure/ms-sql-editable-tab/index.vue';
  import scenarioModuleTree from './components/scenarioModuleTree.vue';
  import ScenarioTable from '@/views/sql-test/scenario/components/scenarioTable.vue';

  import { useI18n } from '@/hooks/useI18n';
  import useCacheStore from "@/store/modules/cache/cache";
  import { getGenerateId } from '@/utils';
  import { hasAnyPermission } from '@/utils/permission';

  import {
    ApiScenarioDebugRequest,
    ApiScenarioGetModuleParams,
    ApiScenarioTableItem,
  } from '@/models/apiTest/scenario';
  import { ModuleTreeNode } from '@/models/common';
  import {SqlScenario, SqlScenarioDebugRequest} from '@/models/sqlTest/scenario';
  import {CacheTabTypeEnum} from "@/enums/cacheTabEnum";

  import useAppStore from '../../../store/modules/app';
  import { defaultSqlScenario } from '@/views/sql-test/scenario/components/config';

  // 异步导入
  const detail = defineAsyncComponent(() => import('./detail/index.vue'));
  const create = defineAsyncComponent(() => import('./create/index.vue'));

  const appStore = useAppStore();
  const cacheStore = useCacheStore();

  const moduleTree = ref<ModuleTreeNode[]>([]);
  const activeModule = ref<string>('all');
  const offspringIds = ref<string[]>([]);

  export type SqlScenarioParams = SqlScenario & SQLTabItem;

  const saveLoading = ref(false);

  const { t } = useI18n();

  const sqlScenarioTabs = ref<SqlScenarioParams[]>([
    {
      id: 'all',
      label: t('apiScenario.allScenario'),
      closable: false,
      environmentId: '',
    } as SqlScenarioParams,
  ]);

  const activeScenarioTab = ref<SqlScenarioParams>(sqlScenarioTabs.value[0] as SqlScenarioParams);

  function newTab(defaultScenarioInfo?: SqlScenario, action?: 'copy' | 'execute') {
    // TODO
    if (defaultScenarioInfo) {
      // TODO：
    } else {
      sqlScenarioTabs.value.push({
        ...cloneDeep(defaultSqlScenario),
        id: getGenerateId(),
        environmentId: appStore.getCurrentEnvId || '',
        label: `${t('apiScenario.createScenario')}${sqlScenarioTabs.value.length}`,
        moduleId: activeModule.value === 'all' ? 'root' : activeModule.value,
        projectId: appStore.currentProjectId,
        priority: 'P0',
      });
    }
    activeScenarioTab.value = sqlScenarioTabs.value[sqlScenarioTabs.value.length - 1] as SqlScenarioParams;
  }

  /**
   * 执行场景
   * @param executeType 执行类型
   * @param envId 环境ID
   */
  function handleExecute(envId?: string) {
    // TODO
    //  TODO：2： 是否要支持本地调试
  }

  function handleStopExecute() {
    // TODO
  }
  function saveScenario() {
    // TODO
  }
  function refreshTree(params?: ApiScenarioGetModuleParams) {
    // TODO
  }

  function openSqlScenarioTab(record: ApiScenarioTableItem | string, action?: 'copy' | 'execute') {
    // TODO
    // return;
  }

  /**
   * 实际执行函数
   * @param executeParams 执行参数
   * @param isExecute 是否执行，否则是调试
   * @param executeType 执行类型
   */
  function realExecute(
      executeParams: Pick<SqlScenarioDebugRequest, 'steps' | 'stepDetails' | 'reportId'>,
      isExecute?: boolean,
      executeType?: 'localExec' | 'serverExec',
      envId?: string
  ) {
    // TODO
  }
</script>

<style scoped lang="less">
  .pageWrap {
    height: calc(100% - 50px);
    border-radius: var(--border-radius-large);
    background-color: var(--color-text-fff);
  }
  .case {
    padding: 8px 4px;
    border-radius: var(--border-radius-small);
  @apply flex cursor-pointer  items-center justify-between;
    &:hover {
      background-color: rgb(var(--primary-1));
    }
    .folder-icon {
      margin-right: 4px;
      color: var(--color-text-4);
    }
    .folder-name {
      color: var(--color-text-1);
    }
    .folder-count {
      margin-left: 4px;
      color: var(--color-text-4);
    }
    .case-active {
      .folder-icon,
      .folder-name,
      .folder-count {
        color: rgb(var(--primary-5));
      }
    }
    .back {
      margin-right: 8px;
      width: 20px;
      height: 20px;
      border: 1px solid #ffffff;
      background: linear-gradient(90deg, rgb(var(--primary-9)) 3.36%, #ffffff 100%);
      box-shadow: 0 0 7px rgb(15 0 78 / 9%);
      .arco-icon {
        color: rgb(var(--primary-5));
      }
    @apply flex cursor-pointer items-center rounded-full;
    }
  }
  .recycle {
  @apply absolute bottom-0  pb-4;

    background-color: var(--color-text-fff);
    :deep(.arco-divider-horizontal) {
      margin: 8px 0;
    }
    .recycle-bin {
    @apply bottom-0 flex items-center;

      background-color: var(--color-text-fff);
      .recycle-count {
        margin-left: 4px;
        color: var(--color-text-4);
      }
    }
  }
</style>
