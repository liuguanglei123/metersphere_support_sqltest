import { SqlScenarioStepItem } from '@/models/sqlTest/scenario';
import { SqlScenarioStepRefType, SqlScenarioStepType } from '@/enums/sqlEnum';

export default function getStepType(step: SqlScenarioStepItem) {
  const isCopySql = step.stepType === SqlScenarioStepType.SQL && step.refType === SqlScenarioStepRefType.COPY;
  const isQuoteSql = step.stepType === SqlScenarioStepType.SQL && step.refType === SqlScenarioStepRefType.REF;
  const isCopyScenario = step.stepType === SqlScenarioStepType.SQL_SCENARIO && step.refType === SqlScenarioStepRefType.COPY;
  const isQuoteScenario =
    step.stepType === SqlScenarioStepType.SQL_SCENARIO &&
    [SqlScenarioStepRefType.REF, SqlScenarioStepRefType.PARTIAL_REF].includes(step.refType);

  return {
    isCopySql,
    isQuoteSql,
    isCopyScenario,
    isQuoteScenario,
  };
}
