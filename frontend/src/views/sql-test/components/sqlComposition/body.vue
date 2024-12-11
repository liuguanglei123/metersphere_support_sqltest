<template>
  <a-spin :loading="bodyLoading" class="block h-[calc(100%-34px)]">
    <MsCodeEditor
      v-model:model-value="currentBodyCode"
      theme="vs"
      height="100%"
      :show-full-screen="false"
      :show-theme-change="false"
      is-adaptive
    >
    </MsCodeEditor>
  </a-spin>
</template>

<script setup lang="ts">
  import MsCodeEditor from '@/components/pure/ms-code-editor/index.vue';
  import paramTable, { type ParamTableColumn } from '@/views/api-test/components/paramTable.vue';

  import useVisit from '@/hooks/useVisit';

  import { ModuleTreeNode, TransferFileParams } from '@/models/common';

  const bodyLoading = ref(false);

  const props = defineProps<{
    disabledBodyType?: boolean; // 禁用body类型切换
    disabledParamValue?: boolean; // 参数值禁用
    disabledExceptParam?: boolean; // 除了可以修改参数值其他都禁用
    isDebug?: boolean; // 是否调试模式
    hideJsonSchema?: boolean; // 隐藏json schema
    isCase?: boolean; // 是否是 case
    uploadTempFileApi?: (file: File) => Promise<any>; // 上传临时文件接口
    fileSaveAsSourceId?: string | number; // 文件转存关联的资源id
    fileSaveAsApi?: (params: TransferFileParams) => Promise<string>; // 文件转存接口
    fileModuleOptionsApi?: (projectId: string) => Promise<ModuleTreeNode[]>; // 文件转存目录下拉框接口
  }>();
  const emit = defineEmits<{
    (e: 'update:params', value: any[]): void;
    (e: 'change'): void;
  }>();

  const visitedKey = 'apiTestAutoMakeJsonTip';
  const { addVisited, getIsVisited } = useVisit(visitedKey);

  // const innerParams = defineModel<ExecuteBody>('params', {
  //   required: true,
  // });

  // 当前显示的sql
  const currentBodyCode = computed({
    get() {
      console.log(1111);
      return 'select * from t1;';
      // return innerParams.value.sql || '';
    },
    set(val) {
      // innerParams.value.sql = val || '';
    },
  });
</script>

<style lang="less" scoped></style>
