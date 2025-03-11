<template>
  <div class="h-full">
    <a-input
        v-model:model-value="moduleKeyword"
        :placeholder="t('apiScenario.tree.selectorPlaceholder')"
        class="mb-[8px]"
        allow-clear
    />
    <div class="folder" @click="setActiveFolder('all')">
      <div :class="allFolderClass">
        <MsIcon type="icon-icon_folder_filled1" class="folder-icon" />
        <div class="folder-name">{{ t('apiScenario.tree.folder.allScenario') }}</div>
        <div class="folder-count">({{ allScenarioCount }})</div>
      </div>
    </div>
    <a-spin class="w-full" :style="{ height: `calc(100vh - 273px)` }" :loading="loading">
      <MsTree
        :data="folderTree"
      >
      </MsTree>
    </a-spin>

    </div>
</template>
<script setup lang="ts">
  import {computed, ref} from "vue";

  import MsIcon from "@/components/pure/ms-icon-font/index.vue";
  import MsTree from '@/components/business/ms-tree/index.vue';

  import { useI18n } from '@/hooks/useI18n';

  import { ModuleTreeNode } from "@/models/common";

  const emit = defineEmits([
    'init',
    'countRecycleScenario',
    'newScenario',
    'import',
    'folderNodeSelect',
    'changeProtocol',
    'change',
  ]);

  const props = withDefaults(
      defineProps<{
        isExpandAll?: boolean; // 是否展开所有节点
        isShowScenario?: boolean; // 是否显示挂载的场景
        activeModule?: string | number; // 选中的节点 key
        readOnly?: boolean; // 是否是只读模式
        activeNodeId?: string | number; // 当前选中节点 id
      }>(),
      {
        activeModule: 'all',
        readOnly: false,
      }
  );

  const loading = ref(false);

  const moduleKeyword = ref('');
  const { t } = useI18n();
  const selectedKeys = ref<Array<string | number>>([props.activeModule]);

  function setActiveFolder(id: string) {
    selectedKeys.value = [id];
    emit('folderNodeSelect', selectedKeys.value, []);
  }

  const folderTree = ref<ModuleTreeNode[]>([]);

  const modulesCount = ref<Record<string, number>>({});
  const allScenarioCount = computed(() => modulesCount.value.all || 0);

  const allFolderClass = computed(() =>
      selectedKeys.value[0] === 'all' ? 'folder-text folder-text--active' : 'folder-text'
  );
</script>
<style lang="less" scoped>
.folder {
@apply flex cursor-pointer items-center justify-between;

  padding: 8px 4px;
  border-radius: var(--border-radius-small);
  &:hover {
    background-color: rgb(var(--primary-1));
  }
  .folder-text {
  @apply flex flex-1 cursor-pointer items-center;
    .folder-icon {
      margin-right: 4px;
      color: var(--color-text-4);
    }
    .folder-name {
      color: var(--color-text-1);
    }
    .folder-count {
      margin-left: 4px;
      color: var(--color-text-4);
    }
  }
  .folder-text--active {
    .folder-icon,
    .folder-name,
    .folder-count {
      color: rgb(var(--primary-5));
    }
  }
}
:deep(#root ~ .arco-tree-node-drag-icon) {
@apply hidden;
}
</style>