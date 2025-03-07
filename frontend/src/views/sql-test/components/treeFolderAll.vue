<!--// TODO:所有内容需要检查-->
<template>
  <MsFolderAll
    v-model:isExpandAll="isExpandAll"
    :active-folder="props.activeFolder"
    :folder-name="props.folderName"
    :all-count="props.allCount"
    @set-active-folder="(val: string) => emit('setActiveFolder', val)"
  >
    <template #expandLeft>
      <!-- 显示请求icon -->
      <a-tooltip :content="!isExpandSql ? t('apiTestManagement.expandApi') : t('apiTestManagement.collapseApi')">
        <MsButton
          v-show="!props.notShowOperation && showExpandSql"
          type="icon"
          status="secondary"
          class="!mr-[4px] p-[4px]"
          @click="changeApiExpand"
        >
          <MsIcon :type="`${!isExpandSql ? 'icon-icon_visible_outlined1' : 'icon-icon_preview_close_one'}`" />
        </MsButton>
      </a-tooltip>
    </template>
    <template #expandRight>
      <slot name="expandRight"></slot>
    </template>
  </MsFolderAll>
</template>

<script setup lang="ts">
  import MsButton from '@/components/pure/ms-button/index.vue';
  import MsFolderAll from '@/components/business/ms-folder-all/index.vue';

  import { useI18n } from '@/hooks/useI18n';
  import useAppStore from '@/store/modules/app';
  import { getLocalStorage, setLocalStorage } from '@/utils/local-storage';

  const props = defineProps<{
    activeFolder?: string; // 选中的节点
    folderName: string; // 名称
    allCount: number; // 总数
    showExpandSql?: boolean; // 展示 展开请求的开关
    notShowOperation?: boolean; // 是否展示操作按钮
  }>();
  const emit = defineEmits<{
    (e: 'setActiveFolder', val: string): void;
    (e: 'changeApiExpand'): void;
    (e: 'selectedProtocolsChange'): void;
  }>();

  const isExpandAll = defineModel<boolean | undefined>('isExpandAll', {
    required: false,
    default: undefined,
  });
  const isExpandSql = defineModel<boolean>('isExpandSql', {
    required: false,
    default: undefined,
  });
  const selectedProtocols = ref<string[]>([]);

  const { t } = useI18n();
  const appStore = useAppStore();

  const visible = ref(false);
  const protocolIsEmptyVisible = ref(false);
  const allProtocolList = ref<string[]>([]); // 全部
  const isCheckedAll = computed(() => {
    return selectedProtocols.value.length === allProtocolList.value.length;
  });
  const indeterminate = computed(() => {
    return selectedProtocols.value.length > 0 && selectedProtocols.value.length < allProtocolList.value.length;
  });
  const handleChangeAll = (value: boolean | (string | number | boolean)[]) => {
    if (value) {
      selectedProtocols.value = allProtocolList.value;
    } else {
      selectedProtocols.value = [];
    }
  };
  const handleGroupChange = (value: (string | number | boolean)[]) => {
    selectedProtocols.value = value as string[];
  };

  function changeApiExpand() {
    isExpandSql.value = !isExpandSql.value;
    nextTick(() => {
      setLocalStorage(`SQL_EXPAND_API`, isExpandSql.value);
      emit('changeApiExpand');
    });
  }

  watch(
    () => selectedProtocols.value,
    (val) => {
      // 存储取消的项
      const protocols = allProtocolList.value.filter((item) => !val.includes(item as string));
      emit('selectedProtocolsChange');
      if (props.notShowOperation) return;
      protocolIsEmptyVisible.value = !val.length;
    }
  );

  watch(
    () => props.notShowOperation,
    (val) => {
      if (val) {
        isExpandAll.value = undefined;
      } else {
        isExpandAll.value = false;
      }
    }
  );

  onBeforeMount(async () => {
    isExpandSql.value = getLocalStorage(`SQL_EXPAND_API`) === 'true';
  });

  defineExpose({
    selectedProtocols,
    allProtocolList,
  });
</script>

<style lang="less" scoped>
  .arco-dropdown {
    padding: 8px;
    .arco-dropdown-list .arco-dropdown-option {
      width: 107px;
    }
    .checkbox-all {
      border-bottom: 1px solid var(--color-text-n8);
    }
    .arco-checkbox {
      padding: 6px 12px;
      line-height: 24px;
    }
    .arco-switch {
      margin-left: 8px;
    }
  }
  .api-expend {
    :deep(.arco-dropdown-option-content) {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
    }
  }
</style>
