<template>
  <a-tabs default-active-key="1">
    <a-tab-pane key="1" title="JDBC单SQL调试">
      <MsCard :loading="loading" show-full-screen simple no-cntoent-padding>
        <MsSplitBox :size="300" :max="0.5">
          <template #first>
            <moduleTree
              ref="moduleTreeRef"
              :active-node-id="activeDebug.id"
              @init="(val) => (folderTree = val)"
              @new-api="addDebugTab"
              @click-sql-node="openSqlTab"
              @update-api-node="handleSqlUpdateFromModuleTree"
              @delete-finish="handleDeleteFinish"
            />
          </template>
          <template #second>
            <div class="flex h-full flex-col">
              <div class="border-b border-[var(--color-text-n8)] p-[12px_18px]">
                <MsSqlEditableTab
                  v-model:active-tab="activeDebug"
                  v-model:tabs="debugTabs"
                  :readonly="!hasAnyPermission(['PROJECT_API_DEBUG:READ+ADD'])"
                  at-least-one
                  @add="addDebugTab"
                  @close="handleDebugTabClose"
                >
                  <template #label="{ tab }">
                    <a-tooltip :content="tab.name || tab.label" :mouse-enter-delay="500">
                      <div class="one-line-text max-w-[144px]">
                        {{ tab.name || tab.label }}
                      </div>
                    </a-tooltip>
                  </template>
                </MsSqlEditableTab>
              </div>
              <div class="flex-1 overflow-hidden">
                <sqlComposition
                  v-model:detail-loading="loading"
                  v-model:request="activeDebug"
                  :module-tree="folderTree"
                  :create-api="sqlAddDebug"
                  :update-api="sqlUpdateDebug"
                  :execute-api="sqlExecuteDebug"
                  :local-execute-api="localExecuteApiDebug"
                  :file-save-as-source-id="activeDebug.id"
                  hide-json-schema
                  :permission-map="{
                    execute: 'PROJECT_API_DEBUG:READ+EXECUTE',
                    update: 'PROJECT_API_DEBUG:READ+UPDATE',
                    create: 'PROJECT_API_DEBUG:READ+ADD',
                    saveASApi: 'PROJECT_API_DEFINITION:READ+ADD',
                  }"
                  @import="importDialogVisible = true"
                  @add-done="handleDebugAddDone"
                />
              </div>
            </div>
          </template>
        </MsSplitBox>
      </MsCard>
    </a-tab-pane>
    <a-tab-pane key="2" title="命令行调试">
      <terminal>
      </terminal>
    </a-tab-pane>
  </a-tabs>
</template>

<script lang="ts" setup>
  import { cloneDeep } from 'lodash-es';

  import MsCard from '@/components/pure/ms-card/index.vue';
  import { SqlTabItem } from '@/components/pure/ms-editable-tab/types';
  import MsSplitBox from '@/components/pure/ms-split-box/index.vue';
  import MsSqlEditableTab from '@/components/pure/ms-sql-editable-tab/index.vue';
  import terminal from '../components/terminal/index.vue';
  import moduleTree from './components/moduleTree.vue';
  import sqlComposition, { SqlRequestParam } from '@/views/sql-test/components/sqlComposition/index.vue';

  import { localExecuteApiDebug } from '@/api/modules/api-test/common';
  import {sqlAddDebug, sqlExecuteDebug, sqlGetDebugDetail, sqlUpdateDebug} from "@/api/modules/sql-test/debug";
  import { useI18n } from '@/hooks/useI18n';
  import useRequestCompositionStore from '@/store/modules/api/requestComposition';
  import { hasAnyPermission } from '@/utils/permission';

  import { ModuleTreeNode } from '@/models/common';
  import { SqlRequestMethods, SqlResponseComposition } from '@/enums/sqlEnum';

  import {defaultResponse} from "@/views/api-test/components/config";
  import {defaultSqlBodyParams, defaultSqlResponse} from '@/views/sql-test/components/config';

  const requestCompositionStore = useRequestCompositionStore();

  const importDialogVisible = ref(false);

  const loading = ref(false);

  const initDefaultId = `debug-${Date.now()}`;

  const { t } = useI18n();

  const defaultSqlDebugParams: SqlRequestParam = {
    isNew: true,
    body: {
      ...cloneDeep(defaultSqlBodyParams),
    },
    id: initDefaultId,
    name: '',
    label: t('sqlTestDebug.newSQL'),
    closable: true,
    unSaved: false,
    executeLoading: false,
    responseActiveTab: SqlResponseComposition.TABLE,
    response: cloneDeep(defaultSqlResponse),
  };

  const moduleTreeRef = ref<InstanceType<typeof moduleTree>>();

  async function handleDebugAddDone() {
    await moduleTreeRef.value?.initModules();
    moduleTreeRef.value?.initModuleCount();
  }

  const debugTabs = ref<SqlRequestParam[]>([cloneDeep(defaultSqlDebugParams)]);
  const activeDebug = ref<SqlRequestParam>(debugTabs.value[0]);

  const folderTree = ref<ModuleTreeNode[]>([]);

  // TODO:
  function addDebugTab(defaultProps?: Partial<SqlTabItem>) {
    const id = `debug-${Date.now()}`;
    debugTabs.value.push({
      ...cloneDeep(defaultSqlDebugParams),
      id,
      isNew: !defaultProps?.id, // 新开的tab标记为前端新增的调试，因为此时都已经有id了；但是如果是查看打开的会有携带id
      ...defaultProps,
    });
    activeDebug.value = debugTabs.value[debugTabs.value.length - 1];
  }

  async function openSqlTab(sqlInfo: ModuleTreeNode | string) {
    const id = typeof sqlInfo === 'string' ? sqlInfo : sqlInfo.id;
    const isLoadedTabIndex = debugTabs.value.findIndex((e) => e.id === id);

    if (isLoadedTabIndex > -1) {
      // 如果点击的请求在tab中已经存在，则直接切换到该tab
      activeDebug.value = debugTabs.value[isLoadedTabIndex];
      return;
    }
    try {
      loading.value = true;
      // TODO:
      const res = await sqlGetDebugDetail(id);
      // const res = {
      //   sql: 'this is new sql content!',
      //   name: 'new sql',
      // };

      addDebugTab({
        ...res,
        response: cloneDeep(defaultSqlResponse),
        ...res.request,
        label: res.name,
        name: res.name, // request里面还有个name但是是null
        moduleId: res.moduleId, // request里面还有个moduleId但是是null
      });
      nextTick(() => {
        // 等待内容渲染出来再隐藏loading
        loading.value = false;
      });
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
      loading.value = false;
    }
  }

  /**
   * 同步模块树的接口信息更新操作
   */
  function handleSqlUpdateFromModuleTree(newInfo: {
    id: string;
    name: string;
    moduleId?: string;
    [key: string]: any;
  }) {
    debugTabs.value = debugTabs.value.map((item) => {
      if (item.id === newInfo.id) {
        item.label = newInfo.name;
        item.name = newInfo.name;
        if (newInfo.moduleId) {
          item.moduleId = newInfo.moduleId;
        }
      }
      return item;
    });
    if (activeDebug.value.id === newInfo.id) {
      activeDebug.value.label = newInfo.name;
      activeDebug.value.name = newInfo.name;
      if (newInfo.moduleId) {
        activeDebug.value.moduleId = newInfo.moduleId;
      }
    }
  }

  /**
   * TODO 同步模块树的接口信息删除操作
   * @param id 接口 id
   * @param isModule 是否是删除模块
   */
  function handleDeleteFinish(node: ModuleTreeNode) {}

  function handleDebugTabClose(item: SqlTabItem) {
    requestCompositionStore.removePluginFormMapItem(item.id);
    const closingIndex = debugTabs.value.findIndex((e) => e.id === item.id);
    if (closingIndex > -1) {
      debugTabs.value.splice(closingIndex, 1);
    }
  }
</script>

<style lang="less" scoped></style>
