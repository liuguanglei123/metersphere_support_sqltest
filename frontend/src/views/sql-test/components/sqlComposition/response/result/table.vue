<template>
  <div :class="['searchResult', className]">
    <!-- TODO:可能需要增加一个loading的div，参考chat2db，后面再考虑 -->
<!--    <div v-if="tabsList.length">-->
<!--    <Tabs-->
<!--        :class="['tabs']"-->
<!--        :items=tabsList-->
<!--        :active-tab="activateTab"-->
<!--        :onChange="onChange"-->
<!--    />-->
    <MsSqlEditableTab
      :showAdd="false"
      v-model:active-tab="activeDebug"
      v-model:tabs="debugTabs"
      :readonly="!hasAnyPermission(['PROJECT_API_DEBUG:READ+ADD'])"
      at-least-one
    >
    </MsSqlEditableTab>
    <div v-if="activeResultDataList?.success" :class="'successResult'">
        <div :class="'successResultContent'">
          <div v-if="needTable">
            <TableBox
              :table-box-id="activeResultDataList?.uuid"
              :key="activeResultDataList?.uuid"
              :outer-query-result-data="activeResultDataList"
              :execute-sql-params="props.executeSqlParams"
              :conceal-tab-header="concealTabHeader"
            >
            </TableBox>
          </div>
          <div v-else :class="'updateCountBox'">
              <div :class="'updateCount'">
                {{ t('common.text.affectedRows') }} {{ activeResultDataList?.updateCount }}
              </div>
              <div className="'statusBar'">
                <span>{{ t('common.text.result') }} {{ activeResultDataList?.description }}.</span>
                <span>{{ t('common.text.timeConsuming') }} {{ activeResultDataList?.duration }} ms.</span>
<!--    TODO：            {!!dataLength && <span>{`【${i18n('common.text.searchRow')}】${dataLength} ${i18n('common.text.row')}.`}</span>}-->
              </div>
          </div>
        </div>
      </div>
    <div v-else>
      <div :class="'errorBox'">
        <div :class="'errorText'">{{ activeResultDataList?.message }}</div>
      </div>
    </div>
<!--    <div v-else>this is empty</div>-->
  </div>
</template>

<script setup lang="ts">
  import { VNode } from 'vue';
  import {cloneDeep} from "lodash-es";

  import {SqlTabItem} from "@/components/pure/ms-editable-tab/types";
  import MsSqlEditableTab from "@/components/pure/ms-sql-editable-tab/index.vue";
  import TableBox from './TableBox.vue';
  import Tabs from './Tabs.vue';
  import Iconfont from '@/components/Iconfont/index.vue';
  import {SqlRequestParam} from "@/views/sql-test/components/sqlComposition/index.vue";

  import { useI18n } from '@/hooks/useI18n';
  import {hasAnyPermission} from "@/utils/permission";

  import { IManageResultData } from '@/models/sqlTest/common';

  import { v4 as uuidV4 } from 'uuid';

  const needTable = ref<boolean>(true);

  const props = withDefaults(
    defineProps<{
      isExpanded?: boolean;
      isPriorityLocalExec?: boolean;
      requestUrl?: string;
      isHttpProtocol?: boolean;
      requestResult?: IManageResultData[];
      console?: string;
      hideLayoutSwitch?: boolean; // 隐藏布局切换
      loading?: boolean;
      isEdit?: boolean; // 是否可编辑
      uploadTempFileApi?: (...args: any) => Promise<any>; // 上传临时文件接口
      isDefinition?: boolean;
      isResponseModel?: boolean;
      showEmpty?: boolean;
      showResponseResultButton?: boolean; // 展示执行结果按钮
      className: string;
      sql: string;
      executeSqlParams: any;
      concealTabHeader: boolean;
      viewTable: boolean;
      isActive: boolean;
    }>(),
    {
      isExpanded: true,
      hideLayoutSwitch: false,
      showEmpty: true,
    }
  );

  // TODO:是否需要一个默认值
  const defaultActiveResultDataList: IManageResultData = {
    dataList: [],
    description: "",
    duration: 0,
    fuzzyTotal: "",
    hasNextPage: false,
    headerList: [],
    message: undefined,
    originalSql: "",
    sql: undefined,
    sqlType: 'SELECT',
    success: false
  };

  const resultDataList = ref<IManageResultData[]>();
  const activeResultDataList = ref<IManageResultData>();

  const debugTabs = ref<SqlTabItem[]>([]);
  const activeDebug = ref<SqlTabItem>();

  watch( () => activeDebug.value,
    (newValue) => {
      activeResultDataList.value = resultDataList.value?.find( (item) => item.uuid === newValue?.id)

      needTable.value =
        activeResultDataList.value &&
        activeResultDataList.value.headerList &&
        activeResultDataList.value.headerList.length > 1 || false;
    }, { immediate: true });

  watch(
    () => props.requestResult,
    (newValue) => {
      resultDataList.value = newValue?.map((item,index) => {
        const tempUuid = uuidV4()
        debugTabs.value.push({
          id: tempUuid,
          popover: item.originalSql,
          key: tempUuid,
          label: `执行结果${index+1}`,
          name: `执行结果${index+1}`,
        })
        return { ...item, uuid: tempUuid };
      });

      activeDebug.value = debugTabs?.value[0];
      activeResultDataList.value = resultDataList.value ? resultDataList.value[0] : defaultActiveResultDataList;
  }, { immediate: true });

  const { t } = useI18n();

  const status = computed(() => {
    return 'success';
  });

  // function renderSuccessResult(tempQueryResultData: IManageResultData): VNode {
  //   const needTable = tempQueryResultData?.headerList?.length > 1;
  //
  //   return h('div', {}, "renderSuccessResult");
  // }

  // function renderResult(queryResultData: IManageResultData): VNode{
  //   const needTable = queryResultData?.headerList?.length > 1;
  //
  //   // 定义要传递的 props
  //   const tempprops = {
  //     isActive: true,
  //     className: 'custom-class',
  //     // 其他 props
  //   };
  //
  //   return h('div', { key: queryResultData.uuid },[
  //     queryResultData.success ?
  //         h('div', { class: 'successResult' },
  //           h(
  //             'div',
  //             { class: 'successResultContent' },
  //               // TODO：这里的参数不够，需要补充
  //                 needTable? h(TableBox, {isActive: true,outerQueryResultData: queryResultData} , ""):
  //                     h('div', {} ,"TODO: this is no data render!")
  //             )
  //         ) :
  //         // TODO: chat2db这里实现了StateIndicator，需要抄一下
  //         h('div', {},"this is failed!")
  //       ]
  //   );
  // }

  // const tabsList = computed(() => {
  //   const result= resultDataList.value?.map((queryResultData,index) => {
  //     return {
  //       prefixIcon: '123123',
  //       popover: queryResultData.originalSql,
  //       key: queryResultData.uuid!,
  //       children: renderResult(queryResultData),
  //       label: `执行结果${index+1}`,
  //     };
  //   });
  //   return result;
  // });

  const tabsList = computed(() => {
    const result= resultDataList.value?.map((e,index) => {
      return {
        prefixIcon: '123123', // TODO:这里需要修改为图标
        popover: e.originalSql,
        key: e.uuid!,
        label: `执行结果${index+1}`,
      };
    });
    return result;
  });

  const onChange = (uuid: string|number|null) => {
    // activeTabId.value = uuid;
    // console.log(uuid);
  };

  // TODO: 所有的样式迁移过来
</script>

<style scoped lang="less">
  @import '@/assets/style/var2.less';

  .searchResult {
    height: 100%;
    display: flex;
    flex-direction: column;
    position: relative;
    flex: 1; /* 自动占据剩余的80% */
  }

  .successResult {
    height: 100%;
    .successResultContent {
      height: 100%;
    }

    .updateCount {
      height: calc(100% - 26px);
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }

  .updateCountBox {
    height: 100%;
  }

  .statusBar {
    height: 26px;
    box-sizing: border-box;
    padding: 4px 8px;
    font-size: 12px;
    display: flex;
    justify-content: start;
    align-items: center;
    border-top: 1px solid var(--color-border-secondary);
    background-color: var(--color-bg-subtle);
    overflow: hidden;
    flex-shrink: 0;
    .f-single-line();
    & > span {
      margin-right: 16px;
    }
  }

  .errorBox {
    margin-top: 40px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }

  .successBox {
    .errorBox {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }
    .successText {
      font-size: 14px;
      color: var(--success-color);
      text-align: center;
      transform: translateY(-20px);
    }
  }

  .errorText {
    font-size: 14px;
    color: var(--color-error);
    text-align: center;
    transform: translateY(-20px);
    white-space: pre-wrap;
  }

</style>
