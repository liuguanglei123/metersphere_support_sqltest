import { traverseTree } from '@/utils';

import { SqlRequestResult } from '@/models/sqlTest/common';
import { type SqlScenario, type SqlScenarioStepDetails, SqlScenarioStepItem } from '@/models/sqlTest/scenario';
import { SqlScenarioExecuteStatus, SqlScenarioStepType } from '@/enums/sqlEnum';

import type { SqlRequestParam } from './common/customSqlDrawer.vue';

/**
 * 调试或执行结束后，调用本方法更新步骤的执行状态
 * @param steps 响应式的步骤列表
 * @param stepResponses 步骤的执行结果
 * @param singleStepId 单步骤执行时的步骤ID
 */
export default function updateStepStatus(
  steps: SqlScenarioStepItem[],
  stepResponses: Record<string | number, SqlRequestResult[]>,
  singleStepId?: string | number
) {
  for (let i = 0; i < steps.length; i++) {
    const node = steps[i];
    if (node.enable || singleStepId === node.uniqueId) {
      // 启用的步骤才计算/如果是单步骤执行，无视顶层步骤的启用状态
      if (node.executeStatus === SqlScenarioExecuteStatus.EXECUTING) {
        // 非逻辑控制器直接更改本身状态
        if (stepResponses[node.uniqueId] && stepResponses[node.uniqueId].length > 0) {
          // 存在多个请求结果说明是循环控制器下的步骤，需要判断其子步骤的执行结果
          if (
            stepResponses[node.uniqueId].some(
              (report) => !report.isSuccessful && report.status !== SqlScenarioExecuteStatus.FAKE_ERROR
            )
          ) {
            node.executeStatus = SqlScenarioExecuteStatus.FAILED;
          } else if (
            stepResponses[node.uniqueId].some((report) => report.status === SqlScenarioExecuteStatus.FAKE_ERROR)
          ) {
            node.executeStatus = SqlScenarioExecuteStatus.FAKE_ERROR;
          } else {
            node.executeStatus = SqlScenarioExecuteStatus.SUCCESS;
          }
        } else {
          node.executeStatus = SqlScenarioExecuteStatus.UN_EXECUTE;
        }
      }
    }
  }
}

/**
 * 获取步骤详情参数集合
 * @param details 传入指定的详情映射
 */
export function getStepDetails(steps: SqlScenarioStepItem[], details: Record<string, SqlScenarioStepDetails>) {
  const newStepDetails: Record<string, SqlScenarioStepDetails> = {};
  traverseTree(steps, (step) => {
    const currentDetail = details[step.id] as SqlRequestParam;
    if (
      currentDetail &&
      [SqlScenarioStepType.SQL].includes(step.stepType)
    ) {
      // 接口类型需要处理 json-schema 的循环引用
      newStepDetails[step.id] = {
        ...currentDetail,
        // body: {
        //   ...currentDetail.body,
        // },
      };
    } else {
      newStepDetails[step.id] = details[step.id];
    }
  });
  return newStepDetails;
}
