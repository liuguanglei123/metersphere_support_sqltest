import type {MsTreeNodeData} from "@/components/business/ms-tree/types";
import {SqlRequestParam} from "@/views/sql-test/scenario/components/common/customSqlDrawer.vue";

import {SqlScenario, SqlScenarioStepDetails, SqlScenarioStepItem} from "@/models/sqlTest/scenario";

/**
 * 步骤执行逻辑
 */
export default function useStepExecute({
  scenario,
  steps,
  stepDetails,
  activeStep,
  isPriorityLocalExec,
  localExecuteUrl,
}: {
  scenario: Ref<SqlScenario>;
  steps: Ref<SqlScenarioStepItem[]>;
  stepDetails: Ref<Record<string, SqlScenarioStepDetails>>;
  activeStep: Ref<SqlScenarioStepItem | undefined>;
  isPriorityLocalExec: Ref<boolean> | undefined;
  localExecuteUrl: Ref<string> | undefined;
}) {
  // TODO:


  /**
   * 单个步骤执行调试
   */
  function executeStep(node: MsTreeNodeData) {
    // TODO：
  }

  /**
   * 处理 api 详情抽屉的执行动作
   * @param request 抽屉内的请求参数
   * @param executeType 执行类型
   */
  function handleApiExecute(request: SqlRequestParam, executeType?: 'localExec' | 'serverExec') {
    // TODO：
  }

  async function handleStopExecute(step?: SqlScenarioStepItem) {
    // TODO：
  }

  return {
    executeStep,
    handleApiExecute,
    handleStopExecute,
  };

}
