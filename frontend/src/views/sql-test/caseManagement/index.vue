<template>
  <a-tabs default-active-key="1">
    <a-tab-pane key="1" title="SQL语句">
      <MsCard :loading="loading" show-full-screen simple no-cntoent-padding>
        <MsSplitBox :size="300" :max="0.5">
          <template #first>
            <div class="flex flex-col">
              <div class="p-[0px]" style="margin-right: 20px;">
                <moduleTree
                    ref="moduleTreeRef"
                    @init="handleModuleInit"
                    @click-sql-node="handleSqlNodeClick"
                    @folder-node-select="handleNodeSelect"
                />
              </div>
            </div>
          </template>
          <template #second>
            <div class="relative flex h-full flex-col">
              <caseManagement
                ref="managementRef"
                :module-tree="folderTree"
                :active-module="activeModule"
                :offspring-ids="offspringIds"
              />
            </div>
          </template>
        </MsSplitBox>
      </MsCard>
    </a-tab-pane>
    <a-tab-pane key="2" title="事务"> </a-tab-pane>
    <a-tab-pane key="3" title="SDK？"> </a-tab-pane>
  </a-tabs>
</template>
<script setup lang="ts">
  import { useRoute } from 'vue-router';

  import MsCard from '@/components/pure/ms-card/index.vue';
  import MsSplitBox from "@/components/pure/ms-split-box/index.vue";
  import caseManagement from './components/caseManagement/index.vue';
  import moduleTree from './components/moduleTree.vue';
  import management from "@/views/api-test/management/components/management/index.vue";

  import {localExecuteApiDebug} from "@/api/modules/api-test/common";
  import {addDebug, getTransferOptions, transferFile, updateDebug, uploadTempFile} from "@/api/modules/api-test/debug";
  import {getTrashModuleCount} from "@/api/modules/api-test/management";
  import {sqlexecuteDebug} from "@/api/modules/sql-test/debug";
  import {hasAnyPermission} from "@/utils/permission";

  import { ModuleTreeNode } from "@/models/common";
  import { SQLRequestMethods } from "@/enums/sqlEnum";
  
  const route = useRoute();

  const loading = ref(false);

  const folderTree = ref<ModuleTreeNode[]>([]);
  const folderTreePathMap = ref<Record<string, any>>({});
  const docShareId = ref<string>(route.query.docShareId as string);
  const recycleModulesCount = ref(0);

  const activeModule = ref<string>('all');
  const offspringIds = ref<string[]>([]);

  const caseManagementRef = ref<InstanceType<typeof caseManagement>>();


  function handleModuleInit(tree: ModuleTreeNode[], pathMap: Record<string, any>) {
    folderTree.value = tree;
    folderTreePathMap.value = pathMap;
  }

  function handleSqlNodeClick(node: ModuleTreeNode) {
    caseManagementRef.value?.newTab(node);
  }

  function handleNodeSelect(keys: string[], _offspringIds: string[]) {
    console.log("keys");
    console.log(keys);
    console.log("_offspringIds");
    console.log(_offspringIds);
    [activeModule.value] = keys;
    offspringIds.value = _offspringIds;
    caseManagementRef.value?.changeActiveSqlTabToFirst();
  }
</script>
<style>
  .case {
    padding: 8px 4px;
    border-radius: var(--border-radius-small);
    @apply flex cursor-pointer items-center justify-between;
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
    @apply absolute bottom-0 pb-4;

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