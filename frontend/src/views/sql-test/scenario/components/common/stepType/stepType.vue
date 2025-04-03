<template>
  <div
    class="text-nowrap rounded-[0_999px_999px_0] border border-solid px-[8px] py-[2px] text-[12px] leading-[16px]"
    :style="{
      borderColor: type.color,
      color: type.color,
    }"
  >
    {{ t(type.label) }}
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from '@/hooks/useI18n';

  import { SqlScenarioStepItem } from "@/models/sqlTest/scenario";

  import getStepType from './utils';

  const props = defineProps<{
    step: SqlScenarioStepItem;
  }>();

  const { t } = useI18n();

  const type = computed(() => {
    const stepType = getStepType(props.step);
    let config;

    if (stepType.isQuoteSql) {
      config = { label: 'apiScenario.quoteApi', color: 'rgb(var(--link-7))' };
    } else if (stepType.isCopySql) {
      config = { label: 'apiScenario.copyApi', color: 'rgb(var(--link-7))' };
    } else if (stepType.isQuoteScenario) {
      config = { label: 'apiScenario.quoteScenario', color: 'rgb(var(--primary-7))' };
    } else if (stepType.isCopyScenario) {
      config = { label: 'apiScenario.copyScenario', color: 'rgb(var(--primary-7))' };
    }

    return {
      border: `1px solid ${config?.color}`,
      color: config?.color,
      label: config? t(config.label) : "",
    };
  });
</script>

<style lang="less" scoped></style>
