<template>
  <a-form ref="createFormRef" :model="scenario" layout="vertical">
  </a-form>
<!--  TODO -->
  <div>TODO：右侧基本信息</div>
</template>
<script setup lang="ts">
import {SqlScenario, SqlScenarioDetail} from "@/models/sqlTest/scenario";

const scenario = defineModel<SqlScenarioDetail | SqlScenario>('scenario', {
  required: true,
});
</script>
<style>

</style>