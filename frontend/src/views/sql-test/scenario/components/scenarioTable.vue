<template>
  <div :class="['p-[16px]', props.class]">
<!-- TODO   <MsAdvanceFilter-->
    <ms-base-table
        class="mt-[16px]"
        v-bind="propsRes"
        :first-column-width="44"
        no-disable
        filter-icon-align-left
        :not-show-table-filter="isAdvancedSearchMode"
        v-on="propsEvent"
        @selected-change="handleTableSelect"
        @batch-action="handleTableBatch"
        @drag-change="changeHandler"
    >
    </ms-base-table>
  </div>
</template>
<script setup lang="ts">
  import {computed, ref} from "vue";
  import dayjs from "dayjs";

  import MsAdvanceFilter from "@/components/pure/ms-advance-filter/index.vue";
  import MsBaseTable from "@/components/pure/ms-table/base-table.vue";
  import type {BatchActionParams, BatchActionQueryParams, MsTableColumn} from "@/components/pure/ms-table/type";
  import useTable from "@/components/pure/ms-table/useTable";

  import {getScenarioPage} from "@/api/modules/api-test/scenario";
  import {operationWidth} from "@/utils";
  import {hasAnyPermission} from "@/utils/permission";

  import {DragSortParams, ModuleTreeNode} from "@/models/common";
  import {TableKeyEnum} from "@/enums/tableEnum";
  import {FilterRemoteMethodsEnum, FilterSlotNameEnum} from "@/enums/tableFilterEnum";

  import {casePriorityOptions} from "@/views/api-test/components/config";

  const props = defineProps<{
    class?: string;
    activeModule: string;
    offspringIds: string[];
    moduleTree: ModuleTreeNode[]; // 模块树
    readOnly?: boolean; // 是否是只读模式
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


  const columns: MsTableColumn = [
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
  
  const { propsRes, propsEvent, viewId, advanceFilter, setAdvanceFilter, loadList, setLoadListParams, resetSelector } =
    useTable(
      getScenarioPage,
        {
            columns: props.readOnly ? columns : [],
            scroll: { x: '100%' },
            tableKey: TableKeyEnum.API_SCENARIO,
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

  /**
   * 处理表格选中后批量操作
   * @param event 批量操作事件对象
   */
  function handleTableBatch(event: BatchActionParams, params: BatchActionQueryParams) {
    // TODO

  }

  // 拖拽排序
  function changeHandler(params: DragSortParams) {
    // TODO

  }
</script>
<style>

</style>
