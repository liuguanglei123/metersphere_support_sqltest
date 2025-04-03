import { useI18n } from '@/hooks/useI18n';

import { SqlScenario, SqlScenarioStepConfig } from '@/models/sqlTest/scenario';
import { SqlScenarioStatus } from '@/enums/sqlEnum';

const { t } = useI18n();

export const defaultSqlScenario: SqlScenario = {
  name: '',
  moduleId: '',
  priority: 'P0',
  status: SqlScenarioStatus.UNDERWAY,
  tags: [],
  projectId: '',
  description: '',
  grouped: false,
  environmentId: '',
  scenarioConfig: {
    // variable: {
    //     commonVariables: [],
    //     csvVariables: [],
    // },
    // preProcessorConfig: {
    //     enableGlobal: true,
    //     processors: [],
    // },
    // postProcessorConfig: {
    //     enableGlobal: true,
    //     processors: [],
    // },
    // assertionConfig: {
    //     assertions: [],
    // },
    // otherConfig: {
    //     enableGlobalCookie: true,
    //     enableCookieShare: false,
    //     enableStepWait: false,
    //     stepWaitTime: 1000,
    //     failureStrategy: ScenarioFailureStrategy.CONTINUE,
    // },
  },
  steps: [],
  stepDetails: {},
  executeTime: 0,
  executeSuccessCount: 0,
  executeFailCount: 0,
  executeFakeErrorCount: 0,
  uploadFileIds: [],
  linkFileIds: [],
  reportId: '',
  // 前端渲染字段
  label: '',
  closable: true,
  isNew: true,
  unSaved: false,
  executeLoading: false, // 执行loading
  isDebug: false,
  stepResponses: {},
  errorMessageInfo: {},
};

// 场景-常规参数默认值
export const defaultNormalParamItem = {
  key: '',
  paramType: 'CONSTANT',
  value: '',
  description: '',
  tags: [],
  enable: true,
};

// 场景配置
export const defaultScenarioStepConfig: SqlScenarioStepConfig = {
  enableScenarioEnv: false,
  useOriginScenarioParamPreferential: true,
  useOriginScenarioParam: false,
};

export const defaultStepItemCommon = {
  checked: false,
  expanded: false,
  enable: true,
  children: [],
  copyFromStepId: '', // 如果步骤是复制的，这个字段是复制的步骤id
  isNew: true, // 是否新建的步骤
  config: {
    id: '',
    name: '',
    enable: true,
  },
  createActionsVisible: false,
  responsePopoverVisible: false,
  isExecuting: false,
  executeStatus: undefined,
  isRefScenarioStep: false,
  isQuoteScenarioStep: false,
  csvIds: [],
};

// 场景状态选项
export const scenarioStatusOptions = [
  { label: t('apiTestManagement.processing'), value: SqlScenarioStatus.UNDERWAY },
  { label: t('apiTestManagement.deprecate'), value: SqlScenarioStatus.DEPRECATED },
  { label: t('apiTestManagement.done'), value: SqlScenarioStatus.COMPLETED },
];
