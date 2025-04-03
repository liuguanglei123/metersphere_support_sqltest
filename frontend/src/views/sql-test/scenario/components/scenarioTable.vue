<template>
  <div :class="['p-[16px]', props.class]">
    <!-- TODO   <MsAdvanceFilter-->
    <ms-base-table
      class="mt-[16px]"
      v-bind="propsRes"
      :action-config="batchActions"
      :first-column-width="44"
      no-disable
      filter-icon-align-left
      :not-show-table-filter="isAdvancedSearchMode"
      v-on="propsEvent"
      @selected-change="handleTableSelect"
      @batch-action="handleTableBatch"
      @drag-change="changeHandler"
    >
      <template #num="{ record }">
        <div class="flex items-center">
          <MsButton type="text" class="float-left" style="margin-right: 4px" @click="openScenarioTab(record)">
            {{ record.num }}
          </MsButton>
          <!--          <div v-if="record.scheduleConfig && record.scheduleConfig.enable" class="float-right">-->
          <!--            <a-tooltip position="top">-->
          <!--              <template #content>-->
          <!--                <span>-->
          <!--                  {{ t('apiScenario.schedule.table.tooltip.enable.one') }}-->
          <!--                </span>-->
          <!--                <br />-->
          <!--                <span>-->
          <!--                  {{-->
          <!--                    t('apiScenario.schedule.table.tooltip.enable.two', {-->
          <!--                      time: dayjs(record.nextTriggerTime).format('YYYY-MM-DD HH:mm:ss'),-->
          <!--                    })-->
          <!--                  }}-->
          <!--                </span>-->
          <!--              </template>-->
          <!--              <a-tag-->
          <!--                  style="border-color: rgb(var(&#45;&#45;success-6)); color: rgb(var(&#45;&#45;success-6)); background-color: transparent"-->
          <!--                  bordered-->
          <!--                  @click="openScheduleModal(record)"-->
          <!--              >{{ t('apiScenario.schedule.abbreviation') }}-->
          <!--              </a-tag>-->
          <!--            </a-tooltip>-->
          <!--          </div>-->
          <div v-if="record.scheduleConfig && !record.scheduleConfig.enable" class="float-right">
            <a-tooltip :content="t('apiScenario.schedule.table.tooltip.disable')" position="top">
              <a-tag
                style="border-color: var(--color-text-n8); color: var(--color-text-1); background-color: transparent"
                bordered
                @click="openScheduleModal(record)"
                >{{ t('apiScenario.schedule.abbreviation') }}
              </a-tag>
            </a-tooltip>
          </div>
        </div>
      </template>
      <!--TODO：中间还有其他的待添加的操作类型-->
      <template #operation="{ record }">
        <MsButton
          v-permission="['PROJECT_API_SCENARIO:READ+UPDATE']"
          type="text"
          class="!mr-0"
          @click="openScenarioTab(record)"
        >
          F:{{ t('common.edit') }}
        </MsButton>
        <a-divider v-permission="['PROJECT_API_SCENARIO:READ+UPDATE']" direction="vertical" :margin="8"></a-divider>
        <MsButton
          v-permission="['PROJECT_API_SCENARIO:READ+EXECUTE']"
          type="text"
          class="!mr-0"
          @click="openScenarioTab(record, 'execute')"
        >
          {{ t('apiScenario.execute') }}
        </MsButton>
        <a-divider v-permission="['PROJECT_API_SCENARIO:READ+EXECUTE']" direction="vertical" :margin="8"></a-divider>
        <MsButton
          v-permission="['PROJECT_API_SCENARIO:READ+ADD']"
          type="text"
          class="!mr-0"
          @click="openScenarioTab(record, 'copy')"
        >
          F:{{ t('common.copy') }}
        </MsButton>
        <a-divider v-permission="['PROJECT_API_SCENARIO:READ+ADD']" direction="vertical" :margin="8"></a-divider>
        <!--        <MsTableMoreAction-->
        <!--            v-permission="['PROJECT_API_SCENARIO:READ+EXECUTE', 'PROJECT_API_SCENARIO:READ+DELETE']"-->
        <!--            :list="getTableMoreActionList(record)"-->
        <!--            @select="handleTableMoreActionSelect($event, record)"-->
        <!--        />-->
      </template>
      <template v-if="hasAnyPermission(['PROJECT_API_SCENARIO:READ+ADD'])" #empty>
        <div class="flex w-full items-center justify-center p-[8px] text-[var(--color-text-4)]">
          {{ t('api_scenario.table.tableNoDataAndPlease') }}
          <MsButton
            v-permission="['PROJECT_API_SCENARIO:READ+ADD']"
            class="float-right ml-[8px]"
            @click="emit('createScenario')"
          >
            {{ t('apiScenario.createScenario') }}
          </MsButton>
        </div>
      </template>
    </ms-base-table>
  </div>
  <batch-run-modal
    v-model:visible="showBatchExecute"
    :batch-condition-params="batchOptionParams"
    :batch-params="batchParams"
    :table-selected="tableSelected"
    :batch-run-func="batchRunScenario"
    @finished="loadScenarioList"
  />
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { useRoute } from 'vue-router';
  import { cloneDeep } from 'lodash-es';
  import dayjs from 'dayjs';

  import MsAdvanceFilter from '@/components/pure/ms-advance-filter/index.vue';
  import MsButton from '@/components/pure/ms-button/index.vue';
  import MsBaseTable from '@/components/pure/ms-table/base-table.vue';
  import type { BatchActionParams, BatchActionQueryParams, MsTableColumn } from '@/components/pure/ms-table/type';
  import useTable from '@/components/pure/ms-table/useTable';
  import MsTableMoreAction from '@/components/pure/ms-table-more-action/index.vue';
  import BatchRunModal from '@/views/sql-test/scenario/components/batchRunModal.vue';

  import { batchRunScenario } from '@/api/modules/sql-test/scenario';
  import { getScenarioPage } from '@/api/modules/sql-test/scenario';
  import { NAV_NAVIGATION } from '@/config/workbench';
  import { useI18n } from '@/hooks/useI18n';
  import useTableStore from '@/hooks/useTableStore';
  import useCacheStore from '@/store/modules/cache/cache';
  import { operationWidth } from '@/utils';
  import { hasAnyPermission } from '@/utils/permission';

  import { ApiScenarioTableItem } from '@/models/apiTest/scenario';
  import { DragSortParams, ModuleTreeNode } from '@/models/common';
  import { SqlScenarioTableItem } from '@/models/sqlTest/scenario';
  import { CacheTabTypeEnum } from '@/enums/cacheTabEnum';
  import { TableKeyEnum } from '@/enums/tableEnum';
  import { FilterRemoteMethodsEnum, FilterSlotNameEnum } from '@/enums/tableFilterEnum';
  import { WorkNavValueEnum } from '@/enums/workbenchEnum';

  import useAppStore from '../../../../store/modules/app';
  import { casePriorityOptions } from '@/views/sql-test/components/config';

  const showBatchExecute = ref(false);

  const cacheStore = useCacheStore();
  const route = useRoute();
  const appStore = useAppStore();
  const { t } = useI18n();

  const props = defineProps<{
    class?: string;
    activeModule: string;
    offspringIds: string[];
    moduleTree: ModuleTreeNode[]; // 模块树
    readOnly?: boolean; // 是否是只读模式
  }>();

  const isBatch = ref(false);
  const showScheduleModal = ref(false);

  async function resetScheduleConfig(record: SqlScenarioTableItem) {
    // TODO
  }

  async function openScheduleModal(record: SqlScenarioTableItem) {
    isBatch.value = false;
    await resetScheduleConfig(record);
    showScheduleModal.value = true;
  }

  const emit = defineEmits<{
    (e: 'openScenario', record: SqlScenarioTableItem, action?: 'copy' | 'execute'): void;
    (e: 'refreshModuleTree', params: any): void;
    (e: 'createScenario'): void;
    (e: 'handleAdvSearch', isStartAdvance: boolean): void;
  }>();

  const tableSelected = ref<(string | number)[]>([]);

  /**
   * 处理表格选中
   */
  function handleTableSelect(arr: (string | number)[]) {
    tableSelected.value = arr;
  }

  const msAdvanceFilterRef = ref<InstanceType<typeof MsAdvanceFilter>>();
  const isAdvancedSearchMode = computed(() => msAdvanceFilterRef.value?.isAdvancedSearchMode);

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
      width: operationWidth(160, 140),
      showTooltip: false,
      columnSelectorDisabled: true,
    },
    {
      title: 'apiScenario.table.columns.name',
      dataIndex: 'name',
      slotName: 'name',
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      width: 134,
      showTooltip: true,
      columnSelectorDisabled: true,
    },
    {
      title: 'apiScenario.table.columns.level',
      dataIndex: 'priority',
      slotName: 'priority',
      showDrag: true,
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      filterConfig: {
        options: casePriorityOptions,
        filterSlotName: FilterSlotNameEnum.CASE_MANAGEMENT_CASE_LEVEL,
      },
      width: 140,
    },
    {
      title: 'apiScenario.table.columns.status',
      dataIndex: 'status',
      slotName: 'status',
      titleSlotName: 'statusFilter',
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      // TODO
      // filterConfig: {
      //   options: requestApiScenarioStatusOptions.value,
      //   filterSlotName: FilterSlotNameEnum.API_TEST_CASE_API_STATUS,
      //   disabledTooltip: true,
      // },
      showDrag: true,
      width: 140,
    },
    {
      title: 'apiScenario.table.columns.runResult',
      dataIndex: 'lastReportStatus',
      slotName: 'lastReportStatus',
      showTooltip: false,
      showDrag: true,
      // TODO：
      //  filterConfig: {
      //   options: statusList.value,
      //   filterSlotName: FilterSlotNameEnum.API_TEST_CASE_API_REPORT_STATUS,
      // },
      width: 200,
    },
    {
      title: 'apiScenario.table.columns.tags',
      dataIndex: 'tags',
      isTag: true,
      isStringTag: true,
      showDrag: true,
    },
    {
      title: 'apiScenario.table.columns.scenarioEnv',
      dataIndex: 'environmentName',
      showDrag: true,
      width: 159,
      showTooltip: true,
    },
    {
      title: 'apiScenario.table.columns.steps',
      dataIndex: 'stepTotal',
      slotName: 'stepTotal',
      showInTable: false,
      showDrag: true,
      width: 100,
    },
    {
      title: 'apiScenario.table.columns.passRate',
      dataIndex: 'execPassRate',
      slotName: 'execPassRate',
      titleSlotName: 'requestPassRateColumn',
      showDrag: true,
      showInTable: false,
      width: 100,
    },
    {
      title: 'apiScenario.table.columns.module',
      dataIndex: 'modulePath',
      showTooltip: true,
      showInTable: false,
      showDrag: true,
      width: 176,
    },
    {
      title: 'apiScenario.table.columns.createTime',
      dataIndex: 'createTime',
      showInTable: false,
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      width: 189,
      showDrag: true,
    },
    {
      title: 'apiScenario.table.columns.updateTime',
      dataIndex: 'updateTime',
      showInTable: false,
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      showDrag: true,
      width: 189,
    },
    {
      title: 'apiScenario.table.columns.createUser',
      dataIndex: 'createUserName',
      showTooltip: true,
      showDrag: true,
      width: 109,
      // TODO：
      //  filterConfig: {
      //   mode: 'remote',
      //   loadOptionParams: {
      //     projectId: appStore.currentProjectId,
      //   },
      //   remoteMethod: FilterRemoteMethodsEnum.PROJECT_PERMISSION_MEMBER,
      // },
    },
    {
      title: 'apiScenario.table.columns.updateUser',
      dataIndex: 'updateUserName',
      showTooltip: true,
      showDrag: true,
      width: 109,
      // TODO：
      //  filterConfig: {
      //   mode: 'remote',
      //   loadOptionParams: {
      //     projectId: appStore.currentProjectId,
      //   },
      //   remoteMethod: FilterRemoteMethodsEnum.PROJECT_PERMISSION_MEMBER,
      // },
    },
    {
      title: 'common.operation',
      slotName: 'operation',
      dataIndex: 'operation',
      fixed: 'right',
      width: operationWidth(215, 200),
    },
  ];

  const batchActions = {
    baseAction: [
      {
        label: 'apiScenario.execute',
        eventTag: 'execute',
        permission: ['PROJECT_API_SCENARIO:READ+EXECUTE'],
      },
    ],
    moreAction: [
      {
        label: 'common.delete',
        eventTag: 'delete',
        permission: ['PROJECT_API_SCENARIO:READ+DELETE'],
        danger: true,
      },
    ],
  };

  const { propsRes, propsEvent, viewId, advanceFilter, setAdvanceFilter, loadList, setLoadListParams, resetSelector } =
    useTable(
      getScenarioPage,
      {
        columns: props.readOnly ? columns : [],
        scroll: { x: '100%' },
        tableKey: TableKeyEnum.SQL_SCENARIO,
        showSetting: !props.readOnly,
        selectable: hasAnyPermission([
          'PROJECT_API_SCENARIO:READ+UPDATE',
          'PROJECT_API_SCENARIO:READ+EXECUTE',
          'PROJECT_API_SCENARIO:READ+DELETE',
        ]),
        showSelectAll: !props.readOnly,
        draggable: hasAnyPermission(['PROJECT_API_SCENARIO:READ+UPDATE']) ? { type: 'handle', width: 32 } : undefined,
        heightUsed: 282,
        paginationSize: 'mini',
      },
      (item) => ({
        ...item,
        execPassRate: item.execPassRate ? `${item.execPassRate}%` : '-',
        createTime: dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss'),
        updateTime: dayjs(item.updateTime).format('YYYY-MM-DD HH:mm:ss'),
      })
    );

  const batchParams = ref<BatchActionQueryParams>({ selectAll: false });
  const batchOptionParams = ref<any>();



  // 拖拽排序
  function changeHandler(params: DragSortParams) {
    // TODO
  }

  const viewName = ref('');
  const isActivated = computed(() => cacheStore.cacheViews.includes(CacheTabTypeEnum.SQL_SCENARIO_TABLE));
  const tableStore = useTableStore();
  const keyword = ref('');

  async function getModuleIds() {
    let moduleIds: string[] = [];
    if (props.activeModule !== 'all' && !isAdvancedSearchMode.value) {
      moduleIds = [props.activeModule];
      const getAllChildren = await tableStore.getSubShow(TableKeyEnum.SQL_SCENARIO);
      if (getAllChildren) {
        moduleIds = [props.activeModule, ...props.offspringIds];
      }
    }
    return moduleIds;
  }

  async function getBatchConditionParams() {
    const selectModules = await getModuleIds();
    return {
      condition: {
        keyword: keyword.value,
        filter: propsRes.value.filter,
        viewId: viewId.value,
        combineSearch: advanceFilter,
      },
      projectId: appStore.currentProjectId,
      moduleIds: selectModules,
    };
  }

  /**
   * 处理表格选中后批量操作
   * @param event 批量操作事件对象
   */
  async function handleTableBatch(event: BatchActionParams, params: BatchActionQueryParams) {
    tableSelected.value = params?.selectedIds || [];

    batchParams.value = { ...params };
    switch (event.eventTag) {
      case 'execute':
        batchOptionParams.value = await getBatchConditionParams();
        showBatchExecute.value = true;
        break;
      default:
        break;
    }
  }

  async function loadScenarioList(refreshTreeCount?: boolean) {
    const moduleIds = await getModuleIds();

    let filterParams = { ...propsRes.value.filter };
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
    };
    setLoadListParams({ ...params, viewId: viewId.value, combineSearch: advanceFilter });
    await loadList();
    if (refreshTreeCount && !isAdvancedSearchMode.value) {
      emit('refreshModuleTree', params);
    }
  }

  onBeforeMount(() => {
    cacheStore.clearCache();
    if (route.query.view) {
      setAdvanceFilter({ conditions: [], searchMode: 'AND' }, route.query.view as string);
      viewName.value = route.query.view as string;
    }
    if (!isActivated.value) {
      loadScenarioList();
      cacheStore.setCache(CacheTabTypeEnum.SQL_SCENARIO_TABLE);
    }
  });

  function openScenarioTab(record: SqlScenarioTableItem, action?: 'copy' | 'execute') {
    emit('openScenario', record, action);
  }

  defineExpose({
    loadScenarioList,
    isAdvancedSearchMode,
  });

  if (!props.readOnly) {
    await tableStore.initColumn(TableKeyEnum.SQL_SCENARIO, columns, 'drawer', true);
  } else {
    columns = columns.filter(
      (item) => !['version', 'createTime', 'updateTime', 'operation'].includes(item.dataIndex as string)
    );
  }
</script>

<style></style>
