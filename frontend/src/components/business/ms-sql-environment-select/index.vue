<template>
  <div>
    <a-select
      v-model:model-value="currentEnv"
      :options="envOptions"
      :class="[`${props.size === 'mini' ? '!w-[113px]' : '!w-[200px]'} pl-0 pr-[8px]`]"
      :loading="envLoading"
      allow-search
      :size="props.size"
      @popup-visible-change="popupVisibleChange"
    >
      <template #prefix>
        <div :class="[`flex cursor-pointer ${props.size === 'mini' ? 'p-[4px]' : 'p-[8px]'}`]" @click.stop="goEnv">
          <svg-icon width="14px" height="14px" :name="'icon_env'" class="text-[var(--color-text-4)]" />
        </div>
      </template>
    </a-select>
  </div>
</template>

<script setup lang="ts">
  import { SelectOptionData } from '@arco-design/web-vue';

  import useOpenNewPage from '@/hooks/useOpenNewPage';
  import useAppStore from '@/store/modules/app';

  import { ProjectManagementRouteEnum } from '@/enums/routeEnum';

  const props = defineProps<{
    env?: string;
    size?: 'mini' | 'small' | 'medium' | 'large';
  }>();

  const appStore = useAppStore();
  const { openNewPage } = useOpenNewPage();

  const envLoading = ref(false);
  const envOptions = ref<SelectOptionData[]>([]);
  const currentEnv = computed({
    get: () => appStore.currentEnvConfig?.id || '',
    set: (val: string) => {
      appStore.setEnvConfig(val);
    },
  });

  watch(
    () => props.env,
    (val) => {
      if (val) {
        appStore.setEnvConfig(val);
      }
    },
    {
      immediate: true,
    }
  );

  async function initEnvList() {
    try {
      envLoading.value = true;
      // TODO：需要后端支持环境选择，另外最好限制每个人看到的环境
      // await appStore.initEnvList(props.env);
      // envOptions.value = appStore.envList.map((item) => ({
      //   label: item.name,
      //   value: item.id,
      // }));
      envOptions.value = [
        {label:"环境1 172.30.14.238",value:"172.30.14.238"},
        {label:"环境2 172.30.14.239",value:"172.30.14.239"},
        {label:"环境3 172.30.14.240",value:"172.30.14.240"},
        {label:"环境4 172.30.14.241",value:"172.30.14.241"},
        {label:"环境5 172.30.14.242",value:"172.30.14.242"},

      ];
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
    } finally {
      envLoading.value = false;
    }
  }

  async function popupVisibleChange(visible: boolean) {
    if (visible) {
      await initEnvList();
    }
  }

  function goEnv() {
    openNewPage(ProjectManagementRouteEnum.PROJECT_MANAGEMENT_ENVIRONMENT_MANAGEMENT);
  }

  onBeforeMount(() => {
    initEnvList();
  });
</script>

<style lang="less" scoped>
  .ms-input-group--prepend();
  :deep(.arco-select-view-prefix) {
    margin-right: 8px;
    padding-right: 0;
    border-right: 1px solid var(--color-text-input-border);
  }
</style>
