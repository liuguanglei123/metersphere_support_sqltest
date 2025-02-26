<template>
  <div v-if="!!modalData">
    <!-- TODO 暂不清楚这个modal是什么作用，先占位，也许后面用不到可以删掉 -->
    <AntdModal
        :title="modalData.title"
        :open="open"
        :width="modalData.width"
        oncancel=""
        destroyOnClose=""
    >
      {{modalData.content}}
    </AntdModal>
  </div>

</template>
<script setup lang="ts">
  import {ref} from "vue";

  import { Modal as AntdModal } from 'ant-design-vue';

  export type IModalData =
  | {
    title?: string;
    width?: string;
    onOk?: () => void;
    footer?: VNode | false;
    content: VNode | false;
  }
  | null
  | false;

  const modalData = ref<IModalData>();
  const open = ref<boolean>(false);

</script>
<style>

</style>