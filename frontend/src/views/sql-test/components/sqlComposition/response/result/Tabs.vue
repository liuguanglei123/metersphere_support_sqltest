<template>
  <div :class="['tabBox', className]">
    <!-- 标签导航栏 -->
    <div v-if="!concealTabHeader" ref="tabsNavRef" class="tabsNav">
      <!-- 标签列表 -->
      <div v-if="internalTabs?.length" ref="tabListBoxRef" class="tabList">
        <a-popover
          v-for="t in internalTabs"
          :key="t.key"
          mouse-enter-delay="0.8"
          :content="t.popover"
          :class="['tabItem', { activeTab: t.key === internalActiveTab }]"
          @dblclick="onDoubleClick(t)"
        >
          <div
            style="t.styles"
            :class="['tabItem',t.key === internalActiveTab ? 'supportBaseTableBoxHidden':'']"
          >
            <div
              :key="t.key"
              :class="['textBox']"
              @click="changeTab(t.key)"
            >
              <div :class="'text'">{{t.label}}</div>
            </div>
          </div>
        </a-popover>
      </div>
    </div>
    <div v-if="true">
      <div :class="['tabsContent']">
        <div
          v-for="t in internalTabs"
          :key=t.key
          :class="['tabsContentItem', { tabsContentItemActive: t.key === internalActiveTab }]"
        >
          <VNodeRenderer :vnode="t.children" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import {CSSProperties, ref, VNode, watch} from 'vue';

  import { isValid } from '@/utils/check';

  import { Dropdown } from 'ant-design-vue';

  // 定义一个component组件
  const VNodeRenderer = defineComponent({
    props: ['vnode'],
    render() {
      return this.vnode;
    }
  })

  export interface ITabItem {
    prefixIcon?: string | VNode;
    label: VNode;
    key: number | string;
    popover?: string | VNode;
    children?: VNode | any;
    editableName?: boolean;
    canClosed?: boolean;
    styles?: CSSProperties;
  }

  const props = defineProps<{
    className?: string;
    items?: ITabItem[] | any;
    activeKey?: number | string | null;
    onChange?: (key: string | number | null) => void;
    onEdit?: (action: 'add' | 'remove', data?: ITabItem[], list?: ITabItem[]) => void;
    hideAdd?: boolean;
    editableNameOnBlur? : (option: ITabItem) => void;
    concealTabHeader? : boolean;
    // 最后一个tab不能关闭
    lastTabCannotClosed? : boolean;
    destroyInactiveTabPane? : boolean;
  }>();

  const internalTabs = ref<ITabItem[]>();
  const internalActiveTab = ref()
  const editingTab = ref();
  const showMoreTabs = ref<boolean>();
  const tabListBoxRef = ref<HTMLElement | null>(null);
  const tabsNavRef = ref<HTMLElement | null>(null);
  const isNumberKey = ref(false);

  watch(
    () => props.activeKey,
    (newVal) => {
      if (isValid(newVal)) {
        internalActiveTab.value = newVal;
      }
      isNumberKey.value = typeof newVal === 'number';
    },
    { immediate: true }
  );

  watch(() => props.items, (newVal) => {
    internalTabs.value = newVal;
    if(newVal?.length && !isValid(internalActiveTab)){
      internalActiveTab.value = newVal[0]?.key;
    }

  }, { immediate: true });

  watch(
    () => internalTabs, // 监听 internalTabs
    () => {
      // 当 internalTabs 发生变化时执行逻辑
      if(tabListBoxRef.value){
        const tabsNavWidth = tabsNavRef.value?.getBoundingClientRect().width || 0;
        const tabListBoxWidth = tabListBoxRef.value?.getBoundingClientRect().width || 0;
        showMoreTabs.value = (tabsNavWidth < tabListBoxWidth);
      }
    },
    { immediate: true } // 立即执行一次监听回调
  );

  onMounted(() => {

    watch(
      () => props.items, // 监听 props.items
      (newItems) => {
        // 当 items 发生变化时执行逻辑
        // 你可以在这里更新 internalTabs 或其他状态
        internalTabs.value = newItems || [];
        if (newItems?.length && !isValid(internalActiveTab.value)) {
          internalActiveTab.value = newItems[0]?.key;
        }
      },
      { immediate: true } // 立即执行一次监听回调
    );

    watch(
      () => internalActiveTab, // 监听 internalActiveTab
      () => {
        if(props.onChange){
          props.onChange(internalActiveTab.value);
        }

        // 聚焦的时候，聚焦的tab要在第一个
        // 确保DOM更新完成
        nextTick(() => {
          if (!tabListBoxRef.value) {
            return;
          }
          const activeTab = tabListBoxRef.value.querySelector('.active-tab');
          activeTab?.scrollIntoView({ block: 'nearest' })
        });
      },
      { immediate: true } // 立即执行一次监听回调
    );
  });

  const onDoubleClick = (t: ITabItem) => {
    if (t.editableName) {
      editingTab.value = t.key;
    }
  };

  const changeTab = (key: string | number | null) => {
    internalActiveTab.value = key;
  };
</script>

<style lang="less" scoped>
  .tabsContent {
    flex: 1;
    height: 0;

    .tabsContentItem {
      height: 100%;
      width: 100%;
      display: none;
      position: relative;
    }

    .tabsContentItemActive {
      display: block;
    }
  }

  .tabList {
    display: flex;
  }

  .tabItem {
    position: relative;
    display: flex;
    align-items: center;
    padding-left: 10px;
    line-height: 32px;
    height: 32px;
    cursor: pointer;
    user-select: none;
    border-right: 1px solid var(--color-border);

    &:last-child {
      border-right: 0;
    }

    .textBox {
      flex: 1;
      display: flex;
      align-items: center;
    }

    .text {
      flex: 1;
      width: fit-content;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }

    .icon {
      flex-shrink: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 20px;
      height: 20px;
      margin: 0px 4px;
      border-radius: 4px;
      cursor: pointer;
      color: var(--color-text-secondary);
      opacity: 0;

      i {
        font-size: 12px;
      }

      &:hover {
        color: var(--color-primary);
        background-color: var(--color-hover-bg);
      }
    }

    &:hover {
      // .tab-focus();
      color: var(--color-primary);

      .icon {
        opacity: 1;
      }
    }
  }

  .activeTab {
    background-color: var(--color-neutral-4);
    // 添加内阴影
    box-shadow: inset 0px -1px 0px rgb(var(--arcoblue-6));

    .icon {
      opacity: 1;
    }
  }
</style>
