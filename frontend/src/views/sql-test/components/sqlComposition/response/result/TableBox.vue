<template>
  <div
      :class="[props.className, 'tableBox', { 'noDataTableBox': !tableData || !tableData.length }]"
  >
    <div v-if="!columns.length">
<!--      TODO：无数据时的展示内容return (-->
<!--      <>-->
<!--      <StateIndicator state="success" text={i18n('common.text.successfulExecution')} />-->
<!--      <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0 }}>{bottomStatus}</div>-->
<!--  </>-->
<!--  );-->
    </div>
    <div v-else>
      <div>
        <div :class="['toolBar']">
          <div :class="['toolBarItem']">
            <!--TODO:中间的导航栏，暂时不做，后面补充，这里包含了多个组件，比如自定义组件MyPagination翻页导航-->
            <!--还有刷新，编辑等五个按钮操作，还有导出功能，暂时先不做，或者按需做-->
          </div>
        </div>
        <div v-if="concealTabHeader">
          <!--TODO:中间这里缺少了一些内容，暂不确定是关于什么的，先跳过，后面补充 -->
        </div>
        <div v-if="isActive">
          <!-- TODO:原版这里有个RightClickMenu组件，不确定是干啥的，先跳过-->
          <div :class="['RightClickMenu']">
            <div
                ref={tableBoxRef}
                :class="['supportBaseTableBox',tableLoading ? 'supportBaseTableBoxHidden':'']"
            >
              <div v-if="allDataReady">
                <div>
                  <!-- TODO:加载状态的标签，先跳过-->
<!--                  <div v-if="allDataReady">-->
<!--                    <Spin className={styles.supportBaseTableSpin} />-->
<!--                  </div>-->
                  <!--                      components={{ EmptyContent: () => <h2>{i18n('common.text.noData')}</h2> }}-->

<!--                  TODO:下面的class故意写错，不知道原版的这几个类绑定了什么样式和操作-->
                  <SupportBaseTable
                    :class="['supportBaseTable',props.className,'table']"
                    rowClassName="single-line-row"
                    isStickyHead
                    stickyTop={31}
                    :data="tableData"
                    :columns="columns"
                    :scrollbar="true"
                    :stripe="true"
                    :scroll="{x:'120%'}"
                    :pagination="false"
                    column-resizable
                  >
                      <template #body="props">
                        <a-tr :record="props.record" :index="props.index">
                          <a-td>{{ props.index + 1 }}</a-td> <!-- TODO：需要显示行号，不知道为啥现在不显示 -->
                          <a-td
                              v-for="(column, index) in columns.slice(1)"
                              :key="index"
                              :ellipsis="true"
                              :style="{ minWidth: column.minWidth, maxWidth: column.maxWidth }"
                          >
                            {{ props.record[column.dataIndex] }}
                          </a-td>
                        </a-tr>
                      </template>
                  </SupportBaseTable>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
  import { ref, VNode, watch } from 'vue';
  import { Table } from '@arco-design/web-vue';

  import ScreeningResult from "@/views/sql-test/components/sqlComposition/response/result/ScreeningResult.vue";

  import {isValid} from "@/utils/check";

  import {IManageResultData} from "@/models/sqlTest/common";

  import { styled } from '@vue-styled-components/core';

  // TODO：这里的很多css属性都是antd的，以后再修改吧
  const SupportBaseTable = styled(Table)`
    &.supportBaseTable {
      --bgcolor: var(--color-bg-base);
      --header-bgcolor: var(--color-bg-subtle);
      --hover-bgcolor: transparent;
      --header-hover-bgcolor: var(--color-bg-subtle);
      --highlight-bgcolor: transparent;
      --header-highlight-bgcolor: var(--color-bg-subtle);
      --color: var(--color-text);
      --header-color: var(--color-text);
      --lock-shadow: rgb(37 37 37 / 0.5) 0 0 6px 2px;
      --border-color: var(--color-border-secondary);
      --cell-padding: 0px;
      --row-height: 32px;
      --lock-shadow: 0px 1px 2px 0px var(--color-border);
      }
  `;

  const props = defineProps<{
    className?: string;
    outerQueryResultData: IManageResultData;
    executeSqlParams: any;
    tableBoxId: string;
    isActive?: boolean;
    concealTabHeader?: boolean; // concealTabHeader 是否隐藏tab头部, 目前来说隐藏头部都是单表查询。需要显示筛选
  }>();

  // tableData：带列标识的表数据 可以传给Table组件 进行渲染
  // 保存原始的表数据，用于撤销
  const tableData = ref<{ [key:string] : string | null }[]>();
  const tableLoading = ref<boolean>(false);
  // TODO:这里的allDataReady需要完善，目前只是默认定义为true
  const allDataReady = ref<boolean>(false);
  const queryResultData = ref<IManageResultData>(props.outerQueryResultData);

  const preCode = '$$chat2db_';

  const colNoCode = `${preCode}0No.`;

  const columns = computed<any[]>(() => {
    const result = (queryResultData.value.headerList || []).map((item, colIndex) => {
      const { dataType, name } = item;
      const isNumber = dataType === 'NUMERIC';
      const isNumericalOrder = dataType === 'CHAT2DB_ROW_NUMBER';
      const colId = `${preCode}${colIndex}${name}`;

      if(isNumericalOrder){
        return {
          dataIndex: colId,
          name: 'No.',
          slotName: 'index',
          title: '',
          width: 30,
          // TODO:这里chat2db定义了一些点击和右键方法，还绑定了一些classname，后续再做
        };
      }

      return {
        dataIndex: colId,
        name,
        title: name,
        cellClass: 'no-wrap-text',
        minWidth: '80px',
        maxWidth: '150px',
        // TODO:这里chat2db定义了一些点击和右键方法，还绑定了一些classname，后续再做
      };
    });
    console.log("columns result is");
    console.log(result);
    return result;
  });

  // TODO：最底下的状态栏内容
  // const bottomStatus:VNode = (
  //     `<div className={styles.statusBar}>
  //         <span>{`【${i18n('common.text.result')}】${queryResultData.description}.`}</span>
  //         <span>{`【${i18n('common.text.timeConsuming')}】${queryResultData.duration}ms.`}</span>
  //         <span>{`【${i18n('common.text.searchRow')}】${tableData.length} ${i18n('common.text.row')}.`}</span>
  //      </div>`
  //   );

  // 纯数据的dataList 转换为 tableData
  const dataListTransformTableData = (myDataList: string[][]) => {
    const newTableData = (myDataList || []).map((item) => {
      const rowData: any = {};
      // eslint-disable-next-line array-callback-return
      item.map((i: string | null, colIndex: number) => {
        const colId = `${preCode}${colIndex}${columns.value[colIndex].name}`;
        rowData[colId] = i;
      });
      return rowData;
    });
    return newTableData;
  };

  watch(
    () => queryResultData.value.dataList, // 监听 props.items
    (newItems, oldItems) => {
        if(!columns.value?.length){
          tableData.value = [];
        }else{
          const newTableData = dataListTransformTableData(queryResultData.value.dataList);
          tableData.value=newTableData;
          console.log("tableData.value");
          console.log(newTableData);
          // console.log(tableData.value);
          allDataReady.value=true;
        }
      },
      { immediate: true } // 立即执行一次监听回调
  );


</script>
<style>
  .tableBox {
    height: 100%;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    .allSelectBox{
      width: calc(100% + 8px);
      height: 100%;
      transform: translateX(-4px);
      &:hover{
        background-color: var(--color-hover-bg);
      }
    }
    :global {
      .table{
        overflow: hidden;
      }
      .art-table {
        table colgroup col:nth-of-type(1) {
          min-width: 60px;
        }
      }
    }
  }

  .single-line-row {
    height: 40px; /* 设置固定高度 */
    line-height: 40px; /* 确保文本垂直居中 */
    white-space: nowrap; /* 禁止换行 */
    overflow: hidden; /* 隐藏溢出的文本 */
    text-overflow: ellipsis; /* 使用省略号显示溢出的文本 */
  }

  .no-wrap-text {
    white-space: nowrap; /* 禁止换行 */
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .table-container {
    width: 100%;
    overflow-x: auto;
  }

  .a-table .a-td {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
</style>