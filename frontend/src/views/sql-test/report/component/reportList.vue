<template>
  <div class="px-[16px]">
    <!-- 报告列表 -->
    <ms-base-table
      v-bind="propsRes"
      ref="tableRef"
      class="mt-[8px]"
      v-on="propsEvent"
      @batch-action="handleTableBatch"
      @filter-change="filterChange"
    >
      <template #name="{ record, rowIndex }">
        <div class="one-line-text text-[rgb(var(--primary-5))]" @click="showReportDetail(record.id, rowIndex)"
          >{{ record.name }}
        </div>
      </template>
    </ms-base-table>
    <ReportDetailDrawer
      v-model:visible="showDetailDrawer"
      :report-id="activeDetailId"
      :active-report-index="activeReportIndex"
      :table-data="propsRes.data"
      :page-change="propsEvent.pageChange"
      :pagination="propsRes.msPagination!"
      :show-type="showType"
      :share-time="shareTime"
    />
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import { Message } from '@arco-design/web-vue';
  import dayjs from 'dayjs';

  import MsBaseTable from '@/components/pure/ms-table/base-table.vue';
  import type { BatchActionParams, BatchActionQueryParams, MsTableColumn } from '@/components/pure/ms-table/type';
  import useTable from '@/components/pure/ms-table/useTable';
  import ReportDetailDrawer from '@/views/sql-test/report/component/reportDetailDrawer.vue';

  import { reportRename } from '@/api/modules/api-test/report';
  import { reportList } from '@/api/modules/sql-test/report';
  import { useI18n } from '@/hooks/useI18n';
  import { useTableStore } from '@/store';
  import { hasAnyPermission } from '@/utils/permission';

  import { ReportEnum, ReportStatus } from '@/enums/reportEnum';
  import { ColumnEditTypeEnum, TableKeyEnum } from '@/enums/tableEnum';
  import { FilterSlotNameEnum } from '@/enums/tableFilterEnum';

  import useAppStore from '../../../../store/modules/app';
  import { triggerModeOptions } from '@/views/api-test/report/utils';

  const { t } = useI18n();
  const tableStore = useTableStore();
  const shareTime = ref<string>('');

  const statusList = computed(() => {
    return Object.keys(ReportStatus).map((key) => {
      return {
        value: key,
        label: t(ReportStatus[key].label),
      };
    });
  });

  const columns: MsTableColumn = [
    {
      title: 'report.name',
      dataIndex: 'name',
      slotName: 'name',
      width: 300,
      showInTable: true,
      showTooltip: true,
      editType: hasAnyPermission(['PROJECT_API_REPORT:READ+UPDATE']) ? ColumnEditTypeEnum.INPUT : undefined,
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      showDrag: false,
      columnSelectorDisabled: true,
    },
    {
      title: 'report.type',
      slotName: 'integrated',
      dataIndex: 'integrated',
      width: 150,
      showDrag: true,
    },
    {
      title: 'report.result',
      dataIndex: 'status',
      slotName: 'status',
      filterConfig: {
        options: statusList.value,
        filterSlotName: FilterSlotNameEnum.API_TEST_CASE_API_REPORT_STATUS,
      },
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      showInTable: true,
      width: 150,
      showDrag: true,
    },
    {
      title: 'report.trigger.mode',
      dataIndex: 'triggerMode',
      slotName: 'triggerMode',
      showInTable: true,
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      filterConfig: {
        options: triggerModeOptions,
      },
      width: 150,
      showDrag: true,
    },
    {
      title: 'report.operator',
      slotName: 'createUserName',
      dataIndex: 'createUserName',
      showInTable: true,
      width: 300,
      showDrag: true,
      showTooltip: true,
    },
    {
      title: 'report.operating',
      dataIndex: 'startTime',
      slotName: 'startTime',
      width: 180,
      sortable: {
        sortDirections: ['ascend', 'descend'],
        sorter: true,
      },
      showDrag: true,
    },
    {
      slotName: 'operation',
      dataIndex: 'operation',
      fixed: 'right',
      title: hasAnyPermission(['PROJECT_API_REPORT:READ+DELETE']) ? 'common.operation' : '',
      width: hasAnyPermission(['PROJECT_API_REPORT:READ+DELETE']) ? 130 : 50,
    },
  ];

  await tableStore.initColumn(TableKeyEnum.SQL_TEST_REPORT, columns, 'drawer');

  const appStore = useAppStore();

  const props = defineProps<{
    moduleType: keyof typeof ReportEnum;
    name: string;
  }>();

  const rename = async (record: any) => {
    try {
      await reportRename(props.moduleType, record.id, record.name);
      Message.success(t('common.updateSuccess'));
      return true;
    } catch (error) {
      return false;
    }
  };

  const {
    propsRes,
    propsEvent,
    viewId,
    advanceFilter,
    setAdvanceFilter,
    loadList,
    setLoadListParams,
    setPagination,
    resetSelector,
    resetFilterParams,
  } = useTable(
    reportList,
    {
      tableKey: TableKeyEnum.SQL_TEST_REPORT,
      scroll: {
        x: '100%',
      },
      showSetting: true,
      selectable: hasAnyPermission(['PROJECT_API_REPORT:READ+DELETE']),
      heightUsed: 256,
      paginationSize: 'mini',
      showSelectorAll: true,
    },
    (item) => ({
      ...item,
      startTime: dayjs(item.startTime).format('YYYY-MM-DD HH:mm:ss'),
    }),
    rename
  );

  type ReportShowType = 'All' | 'INDEPENDENT' | 'INTEGRATED';
  const showType = ref<ReportShowType>('All');

  const typeFilter = computed(() => {
    if (showType.value === 'All') {
      return [];
    }
    return showType.value === 'INDEPENDENT' ? [false] : [true];
  });

  // 批量删除
  const handleTableBatch = async (event: BatchActionParams, params: BatchActionQueryParams) => {
    // TODO：
  };
  const keyword = ref<string>('');

  function initData(dataIndex?: string, value?: string[] | (string | number | boolean)[] | undefined) {
    const filterParams = {
      ...propsRes.value.filter,
    };
    if (dataIndex && value) {
      filterParams[dataIndex] = value;
    }
    setLoadListParams({
      keyword: keyword.value,
      projectId: appStore.currentProjectId,
      moduleType: props.moduleType,
      filter: {
        integrated: typeFilter.value,
        ...filterParams,
      },
      viewId: viewId.value,
      combineSearch: advanceFilter,
    });
    loadList();
  }

  function filterChange(dataIndex: string, value: string[] | (string | number | boolean)[] | undefined) {
    initData(dataIndex, value);
  }

  onBeforeMount(() => {
    initData();
  });

  /**
   * 报告详情 showReportDetail
   */
  const activeDetailId = ref<string>('');
  const activeReportIndex = ref<number>(0);
  const showDetailDrawer = ref<boolean>(false);

  function showReportDetail(id: string, rowIndex: number) {
    activeDetailId.value = id;
    activeReportIndex.value = rowIndex;
    console.log("props.moduleType");
    console.log(props.moduleType);
    if (props.moduleType === ReportEnum.SQL_SCENARIO_REPORT) {
      showDetailDrawer.value = true;
    }
  }
</script>

<style lang="less" scoped>
  .ms-table--special-small();
</style>
