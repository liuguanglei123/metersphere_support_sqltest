import { Message } from '@arco-design/web-vue';
import { cloneDeep } from 'lodash-es';

import type { MsTreeExpandedData, MsTreeNodeData } from '@/components/business/ms-tree/types';

import { getScenarioStep, scenarioCopyStepFiles } from '@/api/modules/sql-test/scenario';
import { useI18n } from '@/hooks/useI18n';
import useModal from '@/hooks/useModal';
import useAppStore from '@/store/modules/app';
import { deleteNode, findNodeByKey, handleTreeDragDrop, mapTree } from '@/utils';

import {SqlScenario, SqlScenarioStepItem} from "@/models/sqlTest/scenario";
import { SqlScenarioStepRefType, SqlScenarioStepType } from '@/enums/sqlEnum';

import getStepType from '../common/stepType/utils';

/**
 * 处理步骤树交互
 */
export default function useStepOperation({
  scenario,
  steps,
  stepDetails,
  activeStep,
  selectedKeys,
  customSqlDrawerVisible,
  loading,
}: {
  scenario: Ref<SqlScenario>;
  steps: Ref<SqlScenarioStepItem[]>;
  stepDetails: Ref<Record<string, any>>;
  activeStep: Ref<SqlScenarioStepItem | undefined>;
  selectedKeys: Ref<Array<string | number>>;
  customSqlDrawerVisible: Ref<boolean>;
  loading: Ref<boolean>;
}) {
  const appStore = useAppStore();
  const { t } = useI18n();
  const { openModal } = useModal();

  /**
   * 处理步骤展开折叠
   */
  function handleStepExpand(data: MsTreeExpandedData) {
    const realStep = findNodeByKey<SqlScenarioStepItem>(steps.value, data.node?.uniqueId, 'uniqueId');
    if (realStep) {
      realStep.expanded = !realStep.expanded;
    }
  }

  async function getStepDetail(step: SqlScenarioStepItem) {
    try {
      appStore.showLoading();
      const res = await getScenarioStep(step.copyFromStepId || step.id);
      const parseRequestBodyResult: Record<string, any> = {
        uploadFileIds: [],
        linkFileIds: [],
        deleteFileIds: [], // 存储对比已保存的文件后，需要删除的文件 id 集合
        unLinkFileIds: [], // 存储对比已保存的文件后，需要取消关联的文件 id 集合
      };
      let newFileRes;
      // if (step.config.protocol === 'HTTP' && res.body) {
      //   if ((step.copyFromStepId || step.refType === ScenarioStepRefType.COPY) && step.isNew) {
      //     // 复制的步骤需要复制文件
      //     const fileIds = parseRequestBodyFiles((res as RequestParam).body, [], [], []).uploadFileIds;
      //     if (fileIds.length > 0) {
      //       newFileRes = await scenarioCopyStepFiles();
      //     }
      //     parseRequestBodyFiles(res.body, [], [], [], newFileRes);
      //   } else {
      //     parseRequestBodyResult = parseRequestBodyFiles(res.body, [], [], [], newFileRes); // 解析请求体中的文件，将详情中的文件 id 集合收集，更新时以判断文件是否删除以及是否新上传的文件
      //   }
      // }
      stepDetails.value[step.id] = {
        ...res,
        stepId: step.id,
        ...parseRequestBodyResult,
      };
      // if (!step.copyFromStepId && step.refType !== ScenarioStepRefType.COPY) {
      //   // 复制的步骤文件都是新的，不需要记录，等详情抽屉关闭时会处理
      //   scenario.value.stepFileParam[step.id] = {
      //     ...parseRequestBodyResult,
      //   };
      // }
    } catch (error) {
      // eslint-disable-next-line no-console
      console.error(error);
    } finally {
      appStore.hideLoading();
    }
  }

  /**
   * 处理步骤选中事件
   * @param step 点击的步骤节点
   */
  async function handleStepSelect(step: SqlScenarioStepItem) {
    console.log("handleStepSelect");

    activeStep.value = step;
    const _stepType = getStepType(step);
    const offspringIds: string[] = [];
    mapTree(step.children || [], (e) => {
      offspringIds.push(e.uniqueId);
      return e;
    });
    selectedKeys.value = [step.uniqueId, ...offspringIds];
    if (_stepType.isCopySql || _stepType.isQuoteSql) {
      // 复制 api、引用 api、自定义 api打开抽屉
      if (
        step.isQuoteScenarioStep ||
        (stepDetails.value[step.id] === undefined && step.copyFromStepId) ||
        (stepDetails.value[step.id] === undefined && !step.isNew)
      ) {
        // 引用的场景步骤资源每次加载最新数据
        // 查看步骤详情时，详情映射中没有对应数据，初始化步骤详情（复制的步骤没有加载详情前就被复制，打开复制后的步骤就初始化被复制步骤的详情）
        await getStepDetail(step);
      }
      customSqlDrawerVisible.value = true;
    }
  }

  /**
   * 删除
   */
  function deleteStep(step?: SqlScenarioStepItem) {
    if (step) {
      openModal({
        type: 'error',
        title: t('common.tip'),
        content: t('apiScenario.deleteStepConfirm', { name: step.name }),
        okText: t('common.confirmDelete'),
        cancelText: t('common.cancel'),
        okButtonProps: {
          status: 'danger',
        },
        maskClosable: false,
        onBeforeOk: async () => {
          customSqlDrawerVisible.value = false;
          deleteNode(steps.value, step.uniqueId, 'uniqueId');
          activeStep.value = undefined;
          scenario.value.unSaved = true;
          Message.success(t('common.deleteSuccess'));
        },
        hideCancel: false,
      });
    }
  }

  /**
   * 释放允许拖拽步骤到释放的节点内
   * @param dropNode 释放节点
   */
  function isAllowDropInside(dropNode: MsTreeNodeData) {
    return true;
    // (
    //   // 逻辑控制器内可以拖拽任意类型的步骤
    //   [
    //     SqlScenarioStepType.LOOP_CONTROLLER,
    //     SqlScenarioStepType.IF_CONTROLLER,
    //     SqlScenarioStepType.ONCE_ONLY_CONTROLLER,
    //   ].includes(dropNode.stepType) ||
    //   // 复制的场景内可以释放任意类型的步骤
    //   (dropNode.stepType === SqlScenarioStepType.API_SCENARIO && dropNode.refType === SqlScenarioStepRefType.COPY)
    // );
  }

  /**
   * 处理步骤节点拖拽事件
   * @param tree 树数据
   * @param dragNode 拖拽节点
   * @param dropNode 释放节点
   * @param dropPosition 释放位置（取值：-1，,0，,1。 -1：dropNodeId节点之前。 0:dropNodeId节点内。 1：dropNodeId节点后）
   */
  function handleDrop(
    tree: MsTreeNodeData[],
    dragNode: MsTreeNodeData,
    dropNode: MsTreeNodeData,
    dropPosition: number
  ) {
    try {
      if (dropPosition === 0 && !isAllowDropInside(dropNode)) {
        // Message.error(t('apiScenario.notAllowDropInside')); TODO:不允许释放提示
        return;
      }
      loading.value = true;
      const offspringIds: string[] = [];
      const realStep = findNodeByKey<SqlScenarioStepItem>(steps.value, dragNode.uniqueId, 'uniqueId');
      if (!realStep) return;
      mapTree(cloneDeep(realStep.children || []), (e) => {
        offspringIds.push(e.uniqueId);
        return e;
      });
      const stepIdAndOffspringIds = [realStep.uniqueId, ...offspringIds];
      if (dropPosition === 0) {
        // 拖拽到节点内
        if (selectedKeys.value.includes(dropNode.uniqueId)) {
          // 释放位置的节点已选中，则需要把拖动的节点及其子孙节点也需要选中（因为父级选中子级也会展示选中状态）
          selectedKeys.value = selectedKeys.value.concat(stepIdAndOffspringIds);
        }
      } else if (dropNode.parent && selectedKeys.value.includes(dropNode.parent.uniqueId)) {
        // 释放位置的节点的父节点已选中，则需要把拖动的节点及其子孙节点也需要选中（因为父级选中子级也会展示选中状态）
        selectedKeys.value = selectedKeys.value.concat(stepIdAndOffspringIds);
      } else if (realStep.parent && selectedKeys.value.includes(realStep.parent.uniqueId)) {
        // 如果被拖动的节点的父节点在选中的节点中，则需要把被拖动的节点及其子孙节点从选中的节点中移除
        selectedKeys.value = selectedKeys.value.filter((e) => {
          for (let i = 0; i < stepIdAndOffspringIds.length; i++) {
            const id = stepIdAndOffspringIds[i];
            if (e === id) {
              stepIdAndOffspringIds.splice(i, 1);
              return false;
            }
          }
          return true;
        });
      }
      const dragResult = handleTreeDragDrop(steps.value, realStep, dropNode, dropPosition, 'uniqueId');
      if (dragResult) {
        Message.success(t('common.moveSuccess'));
        scenario.value.unSaved = true;
      }
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
    } finally {
      nextTick(() => {
        loading.value = false;
      });
    }
  }

  return {
    getStepDetail,
    handleStepExpand,
    handleStepSelect,
    deleteStep,
    handleDrop,
  };
}
