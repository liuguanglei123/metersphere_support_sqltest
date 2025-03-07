<template>
  <div class="mt-[8px] w-full">
    123
  </div>
</template>

<script setup lang="ts">
  import { SQLTabItem } from "@/components/pure/ms-editable-tab/types";

  import { SqlResponseDefinition } from "@/models/sqlTest/common";
  import { ResponseComposition } from "@/enums/apiEnum";

  export interface SqlResponseItem extends SQLTabItem, SqlResponseDefinition {
    showPopConfirm?: boolean; // 是否显示确认弹窗
    showRenamePopConfirm?: boolean; // 是否显示重命名确认弹窗
    responseActiveTab?: ResponseComposition; // 当前激活的tab
  }
</script>

<style lang="less" scoped>

</style>
