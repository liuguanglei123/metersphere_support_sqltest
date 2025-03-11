<template>
  <div>
    <div>1</div>
    <a-dropdown
      v-model:popup-visible="visible"
      :position="props.position || 'bottom'"
      :popup-translate="props.popupTranslate"
      class="scenario-action-dropdown"
      @select="(val) => handleCreateActionSelect(val as SqlScenarioAddStepActionType)"
    >
      <slot></slot>
      <template #content>
        <a-dgroup :title="t('apiScenario.requestScenario')">
          <a-doption
            v-permission="['PROJECT_API_SCENARIO:READ+IMPORT']"
            :value="SqlScenarioAddStepActionType.IMPORT_SYSTEM_SQL"
          >
            {{ t('apiScenario.importSystemApi') }}
          </a-doption>
        </a-dgroup>
<!--        <a-dgroup :title="t('apiScenario.other')">-->
<!--          <a-doption :value="SqlScenarioAddStepActionType.SCRIPT_OPERATION">-->
<!--            {{ t('apiScenario.scriptOperation') }}-->
<!--          </a-doption>-->
<!--          <a-doption :value="SqlScenarioAddStepActionType.WAIT_TIME">{{ t('apiScenario.waitTime') }}</a-doption>-->
<!--        </a-dgroup>-->
      </template>
    </a-dropdown>
  </div>
</template>

<script setup lang="ts">
  import { TriggerPopupTranslate } from '@arco-design/web-vue';
  import { cloneDeep } from 'lodash-es';

  import MsButton from '@/components/pure/ms-button/index.vue';

  import { useI18n } from '@/hooks/useI18n';
  import useAppStore from '@/store/modules/app';
  import { findNodeByKey } from '@/utils';

  import {CreateStepAction, ScenarioStepItem} from '@/models/apiTest/scenario';
  import {SqlScenarioStepItem} from "@/models/sqlTest/scenario";
  import {SqlScenarioAddStepActionType} from "@/enums/sqlEnum";

  import useCreateActions from './useCreateActions';
  import { DropdownPosition } from '@arco-design/web-vue/es/dropdown/interface';

  const props = defineProps<{
    position?: DropdownPosition;
    popupTranslate?: TriggerPopupTranslate;
    createStepAction?: CreateStepAction;
  }>();
  const emit = defineEmits<{
    (e: 'close'): void;
    (
      e: 'otherCreate',
      type:
        | SqlScenarioAddStepActionType.IMPORT_SYSTEM_SQL,
        // | SqlScenarioAddStepActionType.CUSTOM_API
        // | SqlScenarioAddStepActionType.SCRIPT_OPERATION,
      step?: SqlScenarioStepItem
    ): void;
    (e: 'addDone', newStep: SqlScenarioStepItem): void;
  }>();

  const appStore = useAppStore();
  const { t } = useI18n();

  const visible = defineModel<boolean>('visible', {
    default: false,
  });
  const steps = defineModel<SqlScenarioStepItem[]>('steps', {
    required: true,
  });
  const selectedKeys = defineModel<(string | number)[]>('selectedKeys', {
    required: true,
  });
  const step = defineModel<SqlScenarioStepItem>('step', {
    default: undefined,
  });

  const { handleCreateStep, buildInsertStepInfos } = useCreateActions();

  /**
   * 处理创建步骤操作
   * @param val 创建步骤类型
   */
  function handleCreateActionSelect(val: SqlScenarioAddStepActionType) {
    switch (val) {
      case SqlScenarioAddStepActionType.IMPORT_SYSTEM_SQL:
        if (step.value) {
          const realStep = findNodeByKey<SqlScenarioStepItem>(steps.value, step.value.uniqueId, 'uniqueId');
          if (realStep) {
            emit('otherCreate', val, realStep as SqlScenarioStepItem);
          }
        } else {
          emit('otherCreate', val);
        }
        break;
      default:
        break;
    }
    if (step.value) {
      emit('close');
    }
  }

  function openTutorial() {
    window.open('https://kb.fit2cloud.com/?p=247', '_blank');
  }
</script>

<style lang="less" scoped></style>
