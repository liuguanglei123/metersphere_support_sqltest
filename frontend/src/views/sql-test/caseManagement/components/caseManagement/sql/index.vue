<template>
  <div class="flex flex-1 flex-col overflow-hidden">
    <keep-alive :include="cacheStore.cacheViews">
      <sql-table
        v-if="activeSqlTab.id === 'all' && currentTab === 'sql'"
        :offspring-ids="props.offspringIds"
        :active-module="props.activeModule"
        :module-tree-data="props.moduleTree"
      >
      </sql-table>
    </keep-alive>
    <div v-if="activeSqlTab.id !== 'all'" class="flex-1 overflow-hidden">
      <div class="mt-[8px] flex items-center justify-between px-[16px]">
        <MsTab
            v-model:activeKey="activeSqlTab.definitionActiveKey"
            :content-tab-list="contentTabList"
            mode="button"
            class="ms-api-tab-nav"
            button-size="small"
            @change="changeDefinitionActiveKey"
        />
        <!-- TODO:在预览页面的时候，tab的右边有几个按钮，后面需要补充下 -->
<!--        <div v-if="activeApiTab.definitionActiveKey === 'preview'" class="flex gap-[12px]">-->
<!--          <a-button-->
<!--              v-permission="['PROJECT_API_DEFINITION:READ+DELETE']"-->
<!--              type="outline"-->
<!--              class="arco-btn-outline&#45;&#45;secondary"-->
<!--              size="small"-->
<!--              @click="handleDelete"-->
<!--          >-->
<!--            {{ t('common.delete') }}-->
<!--          </a-button>-->
<!--          <a-button-->
<!--              v-permission="['PROJECT_API_DEFINITION:READ+UPDATE']"-->
<!--              type="outline"-->
<!--              size="small"-->
<!--              @click="toEditDefinition"-->
<!--          >-->
<!--            {{ t('common.edit') }}-->
<!--          </a-button>-->
<!--          <executeButton-->
<!--              v-permission="['PROJECT_API_DEFINITION:READ+EXECUTE']"-->
<!--              size="small"-->
<!--              @execute="toExecuteDefinition"-->
<!--          />-->
<!--        </div>-->
      </div>
      <div class="h-[calc(100%-32px)]">
          <preview
            v-if="activeSqlTab.definitionActiveKey === 'preview'"
          />
          <sqlComposition
            v-if="activeSqlTab.definitionActiveKey === 'definition'"
            v-model:request="activeSqlTab"
            :execute-api="sqlDebugDefinition"
            is-definition
            :permission-map="{
              execute: 'PROJECT_API_DEFINITION:READ+EXECUTE',
              update: 'PROJECT_API_DEFINITION:READ+UPDATE',
              create: 'PROJECT_API_DEFINITION:READ+ADD',
            }"
            :create-api="addDefinition"
          />
        </div>
    </div>
  </div>
</template>
<script setup lang="ts">
  import {cloneDeep} from "lodash-es";

  import { SQLTabItem } from "@/components/pure/ms-editable-tab/types";
  import MsTab from "@/components/pure/ms-tab/index.vue";
  import SqlTable from "@/views/sql-test/caseManagement/components/caseManagement/sql/sqlTable.vue";
  import { SqlRequestParam } from "@/views/sql-test/components/sqlComposition/index.vue";

  import { addDefinition } from "@/api/modules/sql-test/caseManagement";
  import { sqlDebugDefinition } from "@/api/modules/sql-test/caseManagement";
  import { useI18n } from '@/hooks/useI18n';
  import useCacheStore from '@/store/modules/cache/cache';
  import { hasAnyPermission } from "@/utils/permission";

  import { ModuleTreeNode } from "@/models/common";
  import { SqlDefinitionDetail } from "@/models/sqlTest/caseManagement";
  import {RequestComposition, RequestDefinitionStatus} from "@/enums/apiEnum";
  import {SQLRequestMethods} from "@/enums/sqlEnum";

  import {defaultSqlResponse, defaultSqlResponseItem} from "@/views/sql-test/components/config";

  const preview = defineAsyncComponent(() => import('./preview/index.vue'));

  // 懒加载requestComposition组件
  const sqlComposition = defineAsyncComponent(
      () => import('@/views/sql-test/components/sqlComposition/index.vue')
  );

  const requestCompositionRef = ref<InstanceType<typeof sqlComposition>>();

  const activeSqlTab = defineModel<SqlRequestParam>('activeSqlTab', {
    required: true,
  });

  const cacheStore = useCacheStore();

  const sqlTabs = defineModel<SqlRequestParam[]>('sqlTabs', {
    required: true,
  });

  const { t } = useI18n();

  const contentTabList = computed(() => {
    // 原版ms的api接口测试中，这里有四个tab：预览，定义（编辑），用例，mock
    // 对于sql测试来说，这里只有预览+定义
    const { isNew, protocol } = activeSqlTab.value;
    const tabs = [
      { condition: !isNew, value: 'preview', label: t('apiTestManagement.preview') },
      {
        condition: hasAnyPermission(['PROJECT_API_DEFINITION:READ+UPDATE', 'PROJECT_API_DEFINITION:READ+ADD']),
        value: 'definition',
        label: t('apiTestManagement.definition'),
      }
    ];
    return tabs.filter((tab) => tab.condition).map((tab) => ({ value: tab.value, label: tab.label }));
  });

  function changeDefinitionActiveKey(val: string | number) {
    // TODO：1切换到定义（编辑）页面的时候，需要刷新数据重新加载，当然这个功能还没做
    // TODO：2与此功能对应，在定义页面跳转到其他页面的时候，也需要提示是否保存，这部分功能需要增加
    if (val === 'definition') {
      // caseTableRef.value?.loadCaseList();
    }
  }

  // TODO:从目录树打开新的case，原方法内容太长了，稍后完成
  async function openSqlTab(options: {
    sqlInfo: ModuleTreeNode | SqlDefinitionDetail | string;
    isCopy?: boolean;
    isExecute?: boolean;
    isEdit?: boolean;
    isDebugMock?: boolean;
  }) {
    console.log("openSqlTab");
  }

  const props = defineProps<{
    activeModule: string;
    offspringIds: string[];
    moduleTree: ModuleTreeNode[]; // 模块树
    currentTab: string;
  }>();

  const initDefaultId = `definition-${Date.now()}`;

  const defaultDefinitionParams: SqlRequestParam = {
    type: 'sql',
    definitionActiveKey: 'definition',
    id: initDefaultId,
    moduleId: props.activeModule === 'all' ? 'root' : props.activeModule,
    tags: [],
    status: RequestDefinitionStatus.PROCESSING,
    description: '',
    // TODO：activeTab在接口测试中的用途是，在打开一个新的api定义时，默认停留的tab为header部分，这个在sql的部分暂时没有设计到，因为目前只有一个sql内容的填写，这里先行记录
    // activeTab: RequestComposition.HEADER,
    label: t('sqlTestDebug.newSql'),
    closable: true,
    method: SQLRequestMethods.DDL,
    unSaved: false,
    body: {
      sqlContent:'select * from t1 limit 10;',
    },
    // TODO:不清楚这个字段的作用
    polymorphicName: '',
    name: '',
    // TODO:不清楚这个字段的作用
    path: '',
    projectId: '',
    // TODO：未来可以增加如下配置，比如超市时间等
    // otherConfig: {
    //   connectTimeout: 60000,
    //   responseTimeout: 60000,
    //   certificateAlias: '',
    //   followRedirects: true,
    //   autoRedirects: false,
    // },
    // TODO：作用同activeTab
    // responseActiveTab: ResponseComposition.BODY,
    response: cloneDeep(defaultSqlResponse),
    responseDefinition: [cloneDeep(defaultSqlResponseItem)],
    isNew: true,
    mode: 'definition',
    executeLoading: false,
    // TODO：前置依赖和后置依赖需要完成
    // preDependency: [], // 前置依赖
    // postDependency: [], // 后置依赖
    errorMessageInfo: {},
  };

  function addSqlTab(defaultProps?: Partial<SQLTabItem>) {
    const id = `definition-${Date.now()}`;
    sqlTabs.value.push({
      ...cloneDeep(defaultDefinitionParams),
      moduleId: props.activeModule === 'all' ? 'root' : props.activeModule,
      label: t('sqlTestDebug.newSql'),
      id,
      isNew: !defaultProps?.id, // 新开的tab标记为前端新增的调试，因为此时都已经有id了；但是如果是查看打开的会有携带id
      definitionActiveKey: !defaultProps ? 'definition' : 'preview',
      ...defaultProps,
    });
    activeSqlTab.value = sqlTabs.value[sqlTabs.value.length - 1];
    console.log("activeSqlTab");
    console.log(activeSqlTab);
  }

  defineExpose({
    addSqlTab,
    openSqlTab,
  });
</script>