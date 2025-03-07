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
              @click-api-node="openApiTab"
              @update-api-node="handleApiUpdateFromModuleTree"
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
                    <sqlMethodName :method="SQLRequestMethods.DDL" class="mr-[4px]" />
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
                  :create-api="addDebug"
                  :update-api="updateDebug"
                  :execute-api="sqlexecuteDebug"
                  :local-execute-api="localExecuteApiDebug"
                  :upload-temp-file-api="uploadTempFile"
                  :file-save-as-source-id="activeDebug.id"
                  :file-save-as-api="transferFile"
                  :file-module-options-api="getTransferOptions"
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
    <a-tab-pane key="2" title="SQL场景调试"> SQL场景模式调试 </a-tab-pane>
    <a-tab-pane key="3" title="命令行调试"> Linux命令行模式调试 </a-tab-pane>
  </a-tabs>
</template>

<script lang="ts" setup>
  import { cloneDeep } from 'lodash-es';

  import MsCard from '@/components/pure/ms-card/index.vue';
  import { SQLTabItem } from '@/components/pure/ms-editable-tab/types';
  import MsSplitBox from '@/components/pure/ms-split-box/index.vue';
  import MsSqlEditableTab from '@/components/pure/ms-sql-editable-tab/index.vue';
  import moduleTree from './components/moduleTree.vue';
  import sqlComposition, { SqlRequestParam } from '@/views/sql-test/components/sqlComposition/index.vue';

  import { localExecuteApiDebug } from '@/api/modules/api-test/common';
  import {
    addDebug,
    getTransferOptions,
    transferFile,
    updateDebug,
    uploadTempFile,
  } from '@/api/modules/api-test/debug';
  import { sqlexecuteDebug } from "@/api/modules/sql-test/debug";
  import { useI18n } from '@/hooks/useI18n';
  import useRequestCompositionStore from '@/store/modules/api/requestComposition';
  import { hasAnyPermission } from '@/utils/permission';

  import { ModuleTreeNode } from '@/models/common';
  import { SQLRequestMethods, SqlResponseComposition } from '@/enums/sqlEnum';

  import {defaultSqlBodyParams, defaultSqlResponse} from '@/views/sql-test/components/config';

  const requestCompositionStore = useRequestCompositionStore();

  const importDialogVisible = ref(false);

  const loading = ref(false);

  const initDefaultId = `debug-${Date.now()}`;

  const { t } = useI18n();

  const defaultSQLDebugParams: SqlRequestParam = {
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

  async function handleDebugAddDone() {
    // await moduleTreeRef.value?.initModules();
    // moduleTreeRef.value?.initModuleCount();
  }

  const debugTabs = ref<SqlRequestParam[]>([cloneDeep(defaultSQLDebugParams)]);
  const activeDebug = ref<SqlRequestParam>(debugTabs.value[0]);

  const folderTree = ref<ModuleTreeNode[]>([]);

  // TODO:
  function addDebugTab(defaultProps?: Partial<SQLTabItem>) {
    const id = `debug-${Date.now()}`;
    debugTabs.value.push({
      ...cloneDeep(defaultSQLDebugParams),
      id,
      isNew: !defaultProps?.id, // 新开的tab标记为前端新增的调试，因为此时都已经有id了；但是如果是查看打开的会有携带id
      ...defaultProps,
    });
    activeDebug.value = debugTabs.value[debugTabs.value.length - 1];
  }

  async function openApiTab(apiInfo: ModuleTreeNode | string) {
    const id = typeof apiInfo === 'string' ? apiInfo : apiInfo.id;
    const isLoadedTabIndex = debugTabs.value.findIndex((e) => e.id === id);
    if (isLoadedTabIndex > -1) {
      // 如果点击的请求在tab中已经存在，则直接切换到该tab
      activeDebug.value = debugTabs.value[isLoadedTabIndex];
      return;
    }
    try {
      loading.value = true;
      // TODO:
      // const res = await getDebugDetail(id);
      const res = {
        sql: 'this is new sql content!',
        name: 'new sql',
      };
      let parseRequestBodyResult;

      addDebugTab({
        name: res.name, // request里面还有个name但是是null
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
   * TODO 同步模块树的接口信息更新操作
   */
  function handleApiUpdateFromModuleTree(newInfo: {
    id: string;
    name: string;
    moduleId?: string;
    [key: string]: any;
  }) {}

  /**
   * TODO 同步模块树的接口信息删除操作
   * @param id 接口 id
   * @param isModule 是否是删除模块
   */
  function handleDeleteFinish(node: ModuleTreeNode) {}

  function handleDebugTabClose(item: SQLTabItem) {
    requestCompositionStore.removePluginFormMapItem(item.id);
    const closingIndex = debugTabs.value.findIndex((e) => e.id === item.id);
    if (closingIndex > -1) {
      debugTabs.value.splice(closingIndex, 1);
    }
  }
</script>

<style lang="less" scoped></style>
