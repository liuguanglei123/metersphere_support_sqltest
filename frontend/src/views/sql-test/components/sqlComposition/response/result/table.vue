<template>
  <div :class="['searchResult', className]">
    <!-- TODO:可能需要增加一个loading的div，参考chat2db，后面再考虑 -->
<!--    <div v-if="tabsList.length">-->
    <Tabs
        :class="['tabs']"
        :items=tabsList
        :onChange="onChange"
    />
    <div>
      <div>this is table component content</div>
    </div>
<!--    <div v-else>this is empty</div>-->
  </div>
</template>

<script setup lang="ts">
  import { VNode } from 'vue';

  import {SQLTabItem} from "@/components/pure/ms-editable-tab/types";
  import TableBox from './TableBox.vue';
  import Tabs from './Tabs.vue';
  import Iconfont from '@/components/Iconfont/index.vue';

  import { useI18n } from '@/hooks/useI18n';

  import { IManageResultData } from '@/models/sqlTest/common';

  import { tempvar2 } from './temp';
  import { v4 as uuidV4 } from 'uuid';

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
      resultDataList: IManageResultData[];
    }>(),
    {
      isExpanded: true,
      hideLayoutSwitch: false,
      showEmpty: true,
      resultDataList: tempvar2.map((item) => { return {...item,uuid: uuidV4()} }),
    }
  );

  const activeTabId = ref<string>('');
  const { t } = useI18n();

  const status = computed(() => {
    return 'success';
  });

  function renderSuccessResult(queryResultData: IManageResultData): VNode {
    const needTable = queryResultData?.headerList?.length > 1;

    return h('div', {}, "renderSuccessResult");
  }

  function renderResult(queryResultData: IManageResultData): VNode{
    const needTable = queryResultData?.headerList?.length > 1;

    // 定义要传递的 props
    const tempprops = {
      isActive: true,
      className: 'custom-class',
      // 其他 props
    };

    return h('div', { key: queryResultData.uuid },[
      queryResultData.success ?
          h('div', { class: 'successResult' },
            h(
              'div',
              { class: 'successResultContent' },
                // TODO：这里的参数不够，需要补充
                  needTable? h(TableBox, {isActive: true,outerQueryResultData: queryResultData} , ""):
                      h('div', {} ,"TODO: this is no data render!")
              )
          ) :
          // TODO: chat2db这里实现了StateIndicator，需要抄一下
          h('div', {},"this is failed!")
        ]
    );
  }

  const tabsList = computed(() => {
    const result= props.resultDataList?.map((queryResultData,index) => {
      return {
        prefixIcon: '123123',
        popover: queryResultData.originalSql,
        key: queryResultData.uuid!,
        children: renderResult(queryResultData),
        label: `执行结果${index+1}`,
      };
    });
    return result;
  });

  const onChange = (uuid: string) => {
    activeTabId.value = uuid;
  };

</script>

<style scoped>
  .searchResult {
    height: 100%;
    display: flex;
    flex-direction: column;
    position: relative;
  }
</style>
