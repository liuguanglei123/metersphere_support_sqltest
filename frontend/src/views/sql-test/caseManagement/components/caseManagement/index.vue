<template>
  <div class="flex gap-[8px] px-[16px] pt-[16px]">
    <MsEditableTab
      v-model:active-tab="activeSqlTab"
      v-model:tabs="sqlTabs"
      class="flex-1 overflow-hidden"
      :show-add="currentTab === 'sql' && hasAnyPermission(['PROJECT_API_DEFINITION:READ+ADD'])"
      @add="newTab"
      @close="handleTabClose"
    >
      <!-- TODO：这里ms使用GET POST等字样在tab标题处展示接口的请求类型，对于SQL测试可以参考这一思路，使用DQL DDL等关键字表示测试用例的语句类型 -->
      <template #label="{ tab }">
        <sqlMethodName
          v-if="tab.id !== 'all' && tab.type === 'sql'"
          class="mr-[4px]"
        />
        <a-tooltip :content="tab.name || tab.label" :mouse-enter-delay="500">
          <div class="one-line-text max-w-[144px]">
            {{ tab.name || tab.label }}
          </div>
        </a-tooltip>
      </template>
    </MsEditableTab>
    <MsSqlEnvironmentSelect
        ref="environmentSelectRef"
        :env="activeSqlTab.environmentId"
        size="mini"
    />
  </div>
  <sql
    ref="sqlRef"
    v-model:active-sql-tab="activeSqlTab"
    v-model:sql-tabs="sqlTabs"
    :active-module="props.activeModule"
    :current-tab="currentTab"
    :module-tree="props.moduleTree"
    :offspring-ids="props.offspringIds"
  >
  </sql>
</template>
<script setup lang="ts">
  import MsEditableTab from "@/components/pure/ms-editable-tab/index.vue";
  import { TabItem } from "@/components/pure/ms-editable-tab/types";
  import MsSqlEnvironmentSelect from "@/components/business/ms-sql-environment-select/index.vue";
  import sql from './sql/index.vue';
  import { SqlRequestParam } from '@/views/sql-test/components/sqlComposition/index.vue';

  import { useI18n } from "@/hooks/useI18n";
  import { hasAnyPermission } from "@/utils/permission";

  import { ModuleTreeNode } from "@/models/common";

  const props = defineProps<{
    activeModule: string;
    offspringIds: string[];
    moduleTree: ModuleTreeNode[]; // 模块树
  }>();
  const currentTab = ref('sql');

  const { t } = useI18n();
  const sqlRef = ref<InstanceType<typeof sql>>();

  const setActiveSql: ((params: SqlRequestParam) => void) | undefined = inject('setActiveSql');

  const sqlTabs = ref<SqlRequestParam[]>([
    {
      id: 'all',
      label: t('apiTestManagement.allSql'),
      closable: false,
      moduleId: 'root',
    } as unknown as SqlRequestParam,
  ]);

  const activeSqlTab = ref<SqlRequestParam>(sqlTabs.value[0] as SqlRequestParam);

  function newTab(sqlInfo?: ModuleTreeNode | string, isCopy?: boolean, isExecute?: boolean) {
    if (sqlInfo) {
      // TODO：从目录树中打开新的case
      sqlRef.value?.openSqlTab({
        sqlInfo,
        isCopy,
        isExecute,
      });
    } else {
      sqlRef.value?.addSqlTab();
    }
  }

  function handleTabClose(item: TabItem) {
    const closingIndex = sqlTabs.value.findIndex((e) => e.id === item.id);
    if (closingIndex > -1) {
      sqlTabs.value.splice(closingIndex, 1);
    }
  }

  // 切换到第一个tab
  function changeActiveSqlTabToFirst() {
    activeSqlTab.value = sqlTabs.value[0] as SqlRequestParam;
  }

  defineExpose({
    newTab,
    changeActiveSqlTabToFirst,
  });
</script>
<style>

</style>