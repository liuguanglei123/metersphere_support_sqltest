<template>
  <div class="flex h-full flex-col gap-[8px]">
    <a-spin class="h-full w-full" :loading="loading">
      <div class="action-line">
        <div class="action-group">
          <a-checkbox
            v-show="scenario.steps.length > 0"
            v-model:model-value="checkedAll"
            :indeterminate="indeterminate"
          />
          <div class="flex items-center gap-[4px]">
            {{ t('apiScenario.sum') }}
            <div class="text-[rgb(var(--primary-5))]">{{ topLevelStepCount }}</div>
            {{ t('apiScenario.steps') }}
          </div>
        </div>
      </div>
      <div class="h-[calc(100%-30px)]">
        <stepTree
            ref="stepTreeRef"
            v-model:selected-keys="selectedKeys"
            v-model:steps="scenario.steps"
            v-model:checked-keys="checkedKeys"
            v-model:stepKeyword="keyword"
            v-model:scenario="scenario"
            :expand-all="isExpandAll"
        />
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
  import stepTree from './stepTree.vue';

  import { useI18n } from '@/hooks/useI18n';
  import useModal from '@/hooks/useModal';

  import { SqlScenario } from '@/models/sqlTest/scenario';

  const loading = ref(false);

  const { t } = useI18n();
  const { openModal } = useModal();

  const scenario = defineModel<SqlScenario>('scenario', {
    required: true,
  });
  
  const topLevelStepCount = computed(() => scenario.value.steps.length);

  const checkedAll = ref(false); // 是否全选
  const indeterminate = ref(false); // 是否半选
  const isExpandAll = ref(false); // 是否展开全部
  const checkedKeys = ref<(string | number)[]>([]); // 选中的key
  const selectedKeys = ref<(string | number)[]>([]); // 没啥用，现在用来展示选中样式
  const stepTreeRef = ref<InstanceType<typeof stepTree>>();
  const keyword = ref('');



</script>

<style lang="less">
.scenario-action-dropdown {
  .arco-dropdown-list-wrapper {
    width: 200px;
    max-height: 100%;
    .arco-dropdown-option-content {
    @apply flex w-full;
    }
  }
}
</style>

<style lang="less" scoped>
.action-line {
@apply flex items-center;

  margin-bottom: 8px;
  height: 32px;
  gap: 16px;
  .action-group {
  @apply flex items-center;

    gap: 8px;
    .expand-step-btn {
      padding: 4px;
      .arco-icon {
        color: var(--color-text-4);
      }
      &:hover {
        border-color: rgb(var(--primary-5)) !important;
        background-color: rgb(var(--primary-1)) !important;
        .arco-icon {
          color: rgb(var(--primary-5));
        }
      }
    }
  }
}
</style>
