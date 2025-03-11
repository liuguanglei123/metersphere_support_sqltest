<template>
  <div :class="['p-[0_16px_8px_16px]', props.class]">
    <MsAdvanceFilter
      ref="msAdvanceFilterRef"
      v-model:keyword="keyword"
      :view-type="ViewTypeEnum.API_DEFINITION"
      :filter-config-list="filterConfigList"
      :search-placeholder="t('apiTestManagement.searchPlaceholder')"
      @refresh="loadSqlList(false)"
      @keyword-search="loadSqlList(false)"
    >
      <template #left>
        <div>filter is developmenting.</div>
      </template>
      <template #right>
        <ShareButton v-if="hasAnyPermission(['PROJECT_API_DEFINITION:READ+SHARE'])" ref="shareButtonRef" />
      </template>
    </MsAdvanceFilter>
    <ms-base-table
      ref="apiTableRef"
      v-bind="propsRes"
      :action-config="batchActions"
      :first-column-width="44"
      no-disable
      class="mt-[16px]"
      filter-icon-align-left
      v-on="propsEvent"
    >
      <!--      TODO:-->
      <!--      @selected-change="handleTableSelect"-->
      <!--      @batch-action="handleTableBatch"-->
      <!--      @drag-change="handleTableDragSort"-->
      <!--    >-->
      <!--      <template #[FilterSlotNameEnum.API_TEST_API_REQUEST_METHODS]="{ filterContent }">-->
      <!--        <apiMethodName :method="filterContent.value" />-->
      <!--      </template>-->
      <!--      <template #[FilterSlotNameEnum.API_TEST_API_REQUEST_API_STATUS]="{ filterContent }">-->
      <!--        <apiStatus :status="filterContent.value" />-->
      <!--      </template>-->
      <!--      <template #num="{ record }">-->
      <!--        <MsButton type="text" @click="openApiTab(record)">{{ record.num }}</MsButton>-->
      <!--      </template>-->
      <!--&lt;!&ndash;      TODO:协议和，sqlMethod的支持 &ndash;&gt;-->
      <!--&lt;!&ndash;      <template #protocol="{ record }">&ndash;&gt;-->
      <!--&lt;!&ndash;        <apiMethodName :method="record.protocol" />&ndash;&gt;-->
      <!--&lt;!&ndash;      </template>&ndash;&gt;-->
      <!--&lt;!&ndash;      <template #method="{ record }">&ndash;&gt;-->
      <!--&lt;!&ndash;        <apiMethodName :method="record.method" is-tag />&ndash;&gt;-->
      <!--&lt;!&ndash;      </template>&ndash;&gt;-->
      <!--      <template #caseTotal="{ record }">-->
      <!--        {{ record.caseTotal }}-->
      <!--      </template>-->
      <!--      <template #createUserName="{ record }">-->
      <!--        <a-tooltip :content="`${record.createUserName}`" position="tl">-->
      <!--          <div class="one-line-text">{{ characterLimit(record.createUserName) }}</div>-->
      <!--        </a-tooltip>-->
      <!--      </template>-->
      <!--      <template #status="{ record }">-->
      <!--        <a-select-->
      <!--            v-if="hasAnyPermission(['PROJECT_API_DEFINITION:READ+UPDATE'])"-->
      <!--            v-model:model-value="record.status"-->
      <!--            class="param-input w-full"-->
      <!--            size="mini"-->
      <!--            @change="() => handleStatusChange(record)"-->
      <!--        >-->
      <!--          <template #label>-->
      <!--            <apiStatus :status="record.status" />-->
      <!--          </template>-->
      <!--          <a-option v-for="item of Object.values(RequestDefinitionStatus)" :key="item" :value="item">-->
      <!--            <apiStatus :status="item" />-->
      <!--          </a-option>-->
      <!--        </a-select>-->
      <!--        <apiStatus v-else :status="record.status" />-->
      <!--      </template>-->
      <!--      <template #action="{ record }">-->
      <!--        <MsButton-->
      <!--            v-permission="['PROJECT_API_DEFINITION:READ+UPDATE']"-->
      <!--            type="text"-->
      <!--            class="!mr-0"-->
      <!--            @click="editDefinition(record)"-->
      <!--        >-->
      <!--          {{ t('common.edit') }}-->
      <!--        </MsButton>-->
      <!--        <a-divider v-permission="['PROJECT_API_DEFINITION:READ+UPDATE']" direction="vertical" :margin="8"></a-divider>-->
      <!--        <MsButton-->
      <!--            v-permission="['PROJECT_API_DEFINITION:READ+EXECUTE']"-->
      <!--            type="text"-->
      <!--            class="!mr-0"-->
      <!--            @click="executeDefinition(record)"-->
      <!--        >-->
      <!--          {{ t('apiTestManagement.execute') }}-->
      <!--        </MsButton>-->
      <!--        <a-divider v-permission="['PROJECT_API_DEFINITION:READ+EXECUTE']" direction="vertical" :margin="8"></a-divider>-->
      <!--        <MsButton-->
      <!--            v-permission="['PROJECT_API_DEFINITION:READ+ADD']"-->
      <!--            type="text"-->
      <!--            class="!mr-0"-->
      <!--            @click="copyDefinition(record)"-->
      <!--        >-->
      <!--          {{ t('common.copy') }}-->
      <!--        </MsButton>-->
      <!--        <a-divider v-permission="['PROJECT_API_DEFINITION:READ+ADD']" direction="vertical" :margin="8"></a-divider>-->
      <!--        <MsTableMoreAction :list="tableMoreActionList" @select="handleTableMoreActionSelect($event, record)" />-->
      <!--      </template>-->

      <!--      <template v-if="hasAnyPermission(['PROJECT_API_DEFINITION:READ+ADD', 'FUNCTIONAL_CASE:READ+IMPORT'])" #empty>-->
      <!--        <div class="flex w-full items-center justify-center p-[8px] text-[var(&#45;&#45;color-text-4)]">-->
      <!--          {{ t('apiTestManagement.tableNoDataAndPlease') }}-->
      <!--          <MsButton class="ml-[8px]" @click="emit('addApiTab')">-->
      <!--            {{ t('apiTestManagement.newApi') }}-->
      <!--          </MsButton>-->
      <!--          {{ t('apiTestManagement.or') }}-->
      <!--          <MsButton class="ml-[8px]" @click="emit('import')">-->
      <!--            {{ t('common.import') }}-->
      <!--          </MsButton>-->
      <!--        </div>-->
      <!--      </template>-->
    </ms-base-table>
  </div>
</template>

<script setup lang="ts">
  import { useRoute } from 'vue-router';
  import dayjs from 'dayjs';

  import MsAdvanceFilter from '@/components/pure/ms-advance-filter/index.vue';
  import { FilterFormItem } from '@/components/pure/ms-advance-filter/type';
  import MsButton from '@/components/pure/ms-button/index.vue';
  import MsBaseTable from '@/components/pure/ms-table/base-table.vue';
  import type { BatchActionQueryParams, MsTableColumn } from '@/components/pure/ms-table/type';
  import useTable from '@/components/pure/ms-table/useTable';
  import MsTableMoreAction from '@/components/pure/ms-table-more-action/index.vue';
  import ShareButton from '@/views/api-test/management/components/management/api/shareButton.vue';

  import {getSqlDefinitionPage} from "@/api/modules/sql-test/caseManagement";
  import { NAV_NAVIGATION } from '@/config/workbench';
  import { useI18n } from '@/hooks/useI18n';
  import useTableStore from '@/hooks/useTableStore';
  import useAppStore from '@/store/modules/app';
  import { characterLimit, operationWidth } from '@/utils';
  import { hasAnyPermission } from '@/utils/permission';

  import { ApiDefinitionDetail } from '@/models/apiTest/management';
  import { ModuleTreeNode } from '@/models/common';
  import { ViewTypeEnum } from '@/enums/advancedFilterEnum';
  import { RequestDefinitionStatus } from '@/enums/apiEnum';
  import {CacheTabTypeEnum} from "@/enums/cacheTabEnum";
  import { TableKeyEnum } from '@/enums/tableEnum';
  import { FilterRemoteMethodsEnum, FilterSlotNameEnum } from '@/enums/tableFilterEnum';
  import { WorkNavValueEnum } from '@/enums/workbenchEnum';

  const tableStore = useTableStore();

  const appStore = useAppStore();

  const props = defineProps<{
    class?: string;
    activeModule: string;
    offspringIds: string[];
    selectedProtocols: string[]; // 查看的协议类型
    readOnly?: boolean; // 是否是只读模式
    refreshTimeStamp?: number;
    moduleTreeData?: ModuleTreeNode[];
  }>();
  const emit = defineEmits<{
    (e: 'openApiTab', record: ApiDefinitionDetail, isExecute?: boolean): void;
    (e: 'openCopyApiTab', record: ApiDefinitionDetail): void;
    (e: 'addApiTab'): void;
    (e: 'import'): void;
    (e: 'handleAdvSearch', isStartAdvance: boolean): void;
    (
      e: 'openEditApiTab',
      options: { apiInfo: ApiDefinitionDetail; isCopy: boolean; isExecute: boolean; isEdit: boolean }
    ): void;
  }>();

  const { t } = useI18n();

  const keyword = ref('');

  const filterConfigList = computed<FilterFormItem[]>(() => []);

  const apiTableRef = ref();

  let columns: MsTableColumn = [
    {
      title: 'ID',
      dataIndex: 'num',
      slotName: 'num',
      sortIndex: 1,
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      fixed: 'left',
      width: 100,
      columnSelectorDisabled: true,
    },
    {
      title: 'sqlTestManagement.sqlName',
      dataIndex: 'name',
      showTooltip: true,
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      width: 200,
      columnSelectorDisabled: true,
    },
    {
      title: 'sqlTestManagement.sqlProtocol',
      dataIndex: 'sqlProtocol',
      slotName: 'sqlProtocol',
      showTooltip: true,
      width: 80,
      showDrag: true,
    },
    {
      title: 'sqlTestManagement.sqlType',
      dataIndex: 'sqlMethod',
      slotName: 'sqlMethod',
      width: 100,
      showDrag: true,
      filterConfig: {
        options: [],
        filterSlotName: FilterSlotNameEnum.API_TEST_API_REQUEST_METHODS,
      },
    },
    // TODO:
    // {
    //   title: 'apiTestManagement.apiStatus',
    //   dataIndex: 'status',
    //   slotName: 'status',
    //   filterConfig: {
    //     options: requestApiStatus.value,
    //     filterSlotName: FilterSlotNameEnum.API_TEST_API_REQUEST_API_STATUS,
    //   },
    //   width: 130,
    //   showDrag: true,
    // },
    // {
    //   title: 'apiTestManagement.belongModule',
    //   dataIndex: 'moduleName',
    //   showTooltip: true,
    //   width: 200,
    //   showDrag: true,
    // },
    // {
    //   title: 'apiTestManagement.caseTotal',
    //   dataIndex: 'caseTotal',
    //   showTooltip: true,
    //   width: 100,
    //   showDrag: true,
    //   slotName: 'caseTotal',
    // },
    // {
    //   title: 'common.tag',
    //   dataIndex: 'tags',
    //   isTag: true,
    //   isStringTag: true,
    //   showDrag: true,
    // },
    {
      title: 'apiTestManagement.createTime',
      dataIndex: 'createTime',
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      width: 180,
      showDrag: true,
    },
    {
      title: 'apiTestManagement.updateTime',
      dataIndex: 'updateTime',
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      width: 180,
      showDrag: true,
    },
    {
      title: 'common.creator',
      slotName: 'createUserName',
      dataIndex: 'createUser',
      filterConfig: {
        mode: 'remote',
        loadOptionParams: {
          projectId: appStore.currentProjectId,
        },
        remoteMethod: FilterRemoteMethodsEnum.PROJECT_PERMISSION_MEMBER,
      },
      showInTable: true,
      width: 200,
      showDrag: true,
    },
    // {
    //   title: hasOperationPermission.value ? 'common.operation' : '',
    //   slotName: 'action',
    //   dataIndex: 'operation',
    //   fixed: 'right',
    //   width: operationWidth(215, hasOperationPermission.value ? 200 : 50),
    // },
  ];

  function initFilterColumn() {
    columns = columns.map((item) => {
      // TODO：
      if (item.dataIndex === 'method') {
        return {
          ...item,
          filterConfig: {
            ...item.filterConfig,
            // options: requestMethodsOptions.value,
          },
        };
      }
      return item;
    });
  }

  const { propsRes, propsEvent, viewId, advanceFilter, loadList, setLoadListParams, resetSelector } =
    useTable(
      getSqlDefinitionPage,
      {
        columns: props.readOnly ? columns : [],
        scroll: { x: '100%' },
        tableKey: props.readOnly ? undefined : TableKeyEnum.SQL_TEST,
        showSetting: !props.readOnly,
        selectable: hasAnyPermission([
          'PROJECT_API_DEFINITION:READ+DELETE',
          'PROJECT_API_DEFINITION:READ+EXECUTE',
          'PROJECT_API_DEFINITION:READ+UPDATE',
        ]),
        showSelectAll: !props.readOnly,
        draggable: hasAnyPermission(['PROJECT_API_DEFINITION:READ+UPDATE']) ? { type: 'handle', width: 32 } : undefined,
        heightUsed: 272,
        paginationSize: 'mini',
      },
      (item) => ({
        ...item,
        // TODO：先临时注释掉下面一行，不确定作用
        // fullPath: folderTreePathMap?.[item.moduleId],
        createTime: dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss'),
        updateTime: dayjs(item.updateTime).format('YYYY-MM-DD HH:mm:ss'),
      })
    );
  const batchActions = {
    baseAction: [
      {
        label: 'common.export',
        eventTag: 'export',
        permission: ['PROJECT_API_DEFINITION:READ+EXPORT'],
      },
      {
        label: 'common.edit',
        eventTag: 'edit',
        permission: ['PROJECT_API_DEFINITION:READ+UPDATE'],
      },
      {
        label: 'common.move',
        eventTag: 'move',
        permission: ['PROJECT_API_DEFINITION:READ+UPDATE'],
      },
    ],
    moreAction: [
      {
        label: 'common.delete',
        eventTag: 'delete',
        danger: true,
        permission: ['PROJECT_API_DEFINITION:READ+DELETE'],
      },
    ],
  };
  // TODO：
  function editDefinition(record: ApiDefinitionDetail) {
    // emit('openEditApiTab', { apiInfo: record, isCopy: false, isExecute: false, isEdit: true });
  }
  // TODO：暂不确定高级搜索的功能，先忽略所有高级搜索的代码，后面再探索
  // const isAdvancedSearchMode = computed(() => msAdvanceFilterRef.value?.isAdvancedSearchMode);

  async function getModuleIds() {
    let moduleIds: string[] = [];
    if (props.activeModule !== 'all') {
      moduleIds = [props.activeModule];
      const getAllChildren = await tableStore.getSubShow(TableKeyEnum.SQL_TEST);
      if (getAllChildren) {
        console.log(props.offspringIds);
        moduleIds = [props.activeModule, ...props.offspringIds];
      }
    }
    return moduleIds;
  }
  const route = useRoute();

  async function loadSqlList(hasRefreshTree: boolean) {
    const moduleIds = await getModuleIds();
    let filterParams = {
      ...propsRes.value.filter,
    };

    if (route.query.home) {
      filterParams = {
        ...propsRes.value.filter,
        ...NAV_NAVIGATION[route.query.home as WorkNavValueEnum],
      };
    }
    const params = {
      keyword: keyword.value,
      projectId: appStore.currentProjectId,
      moduleIds,
      filter: filterParams,
      viewId: viewId.value,
      combineSearch: advanceFilter,
    };
    console.log(params)
    // TODO：
    // if (!hasRefreshTree && typeof refreshModuleTreeCount === 'function' && !isAdvancedSearchMode.value) {
    //   refreshModuleTreeCount({
    //     keyword: keyword.value,
    //     filter: filterParams,
    //     moduleIds: [],
    //     protocols: props.selectedProtocols,
    //     projectId: appStore.currentProjectId,
    //   });
    // }

    setLoadListParams(params);
    loadList();
  }

  watch(
    () => [props.activeModule],
    () => {
      resetSelector();
      loadSqlList(true);
    }
  );

  function onMountedLoad() {
    loadSqlList(true);
  }

  onBeforeMount(() => {
    onMountedLoad();
// TODO:
//     cacheStore.clearCache();
//     if (!isActivated.value) {
//       onMountedLoad();
//       cacheStore.setCache(CacheTabTypeEnum.API_TEST_API_TABLE);
//     }
  });

  // TODO：
  // onActivated(() => {
  //   if (isActivated.value) {
  //     onMountedLoad();
  //   }
  // });

  /**
   * TODO：删除接口
   */
  function deleteApi(record?: ApiDefinitionDetail, isBatch?: boolean, params?: BatchActionQueryParams) {}
  function batchUpdate() {}

  await tableStore.initColumn(TableKeyEnum.SQL_TEST, columns, 'drawer', true);
</script>

<style></style>
