<template>
  <MsTag :self-style="status.style" :size="props.size"> {{ status.text }}</MsTag>
</template>

<script setup lang="ts">
  import MsTag, { Size } from '@/components/pure/ms-tag/ms-tag.vue';

  import { useI18n } from '@/hooks/useI18n';

  import {SqlRequestCaseStatus, SqlRequestDefinitionStatus, SqlScenarioStatus} from "@/enums/sqlEnum";

  const props = defineProps<{
    status: SqlRequestDefinitionStatus | SqlScenarioStatus | SqlRequestCaseStatus;
    size?: Size;
  }>();

  const { t } = useI18n();

  const statusMap = {
    [SqlRequestDefinitionStatus.DEPRECATED]: {
      bgColor: 'var(--color-text-n8)',
      color: 'var(--color-text-4)',
      text: 'apiTestManagement.deprecate',
    },
    [SqlRequestDefinitionStatus.PROCESSING]: {
      bgColor: 'rgb(var(--link-2))',
      color: 'rgb(var(--link-5))',
      text: 'apiTestManagement.processing',
    },
    [SqlScenarioStatus.UNDERWAY]: {
      bgColor: 'rgb(var(--link-2))',
      color: 'rgb(var(--link-5))',
      text: 'apiTestManagement.processing',
    },
    [SqlRequestDefinitionStatus.DEBUGGING]: {
      bgColor: 'rgb(var(--link-2))',
      color: 'rgb(var(--link-6))',
      text: 'apiTestManagement.debugging',
    },
    [SqlRequestDefinitionStatus.DONE]: {
      bgColor: 'rgb(var(--success-2))',
      color: 'rgb(var(--success-6))',
      text: 'apiTestManagement.done',
    },
    [SqlScenarioStatus.COMPLETED]: {
      bgColor: 'rgb(var(--success-2))',
      color: 'rgb(var(--success-6))',
      text: 'apiTestManagement.done',
    },
  };
  const status = computed(() => {
    const config = statusMap[props.status];
    return {
      style: {
        backgroundColor: config?.bgColor,
        color: config?.color,
      },
      text: t(config?.text),
    };
  });
</script>

<style lang="less" scoped></style>
