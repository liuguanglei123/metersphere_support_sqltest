// 场景
import type { saveParams } from '@/components/business/ms-associate-case/types';
import { CaseLevel } from '@/components/business/ms-case-associate/types';

import {BatchSqlParams, TableQueryParams} from '@/models/common';
import { SqlRequestResult, SqlResponseDefinition } from '@/models/sqlTest/common';
import {
  SqlRequestDefinitionStatus,
  SqlScenarioExecuteStatus,
  SqlScenarioStatus,
  SqlScenarioStepRefType,
  SqlScenarioStepType,
} from '@/enums/sqlEnum';

import type { SqlRequestParam } from '@/views/sql-test/scenario/components/common/customSqlDrawer.vue';
import {CustomApiStepDetail} from "@/models/apiTest/scenario";

export type SqlCreateStepAction = 'inside' | 'before' | 'after';

// 场景配置
export interface SqlScenarioConfig {}

// 场景引用配置
export interface SqlScenarioStepConfig {
  useOriginScenarioParam: boolean; // 是否优先使用当前场景参数
  useOriginScenarioParamPreferential: boolean; // 是否当前场景参数和源场景参数都应用（勾选非空值时为 true）
  enableScenarioEnv: boolean; // 是否应用源场景环境
}

// 自定义请求
export interface CustomSqlStepDetail {
  customizeRequest: boolean; // 是否自定义请求
  customizeRequestEnvEnable: boolean; // 是否启用环境
}

// 场景步骤详情
export type SqlScenarioStepDetail = Partial<
    CustomSqlStepDetail &
    SqlScenarioStepConfig & {
      method?: string
  }
>;

// 场景步骤项
export interface SqlScenarioStepItem {
  id: string | number;
  sort: number;
  name: string;
  enable: boolean; // 是否启用
  copyFromStepId?: string; // 如果步骤是复制的，这个字段是复制的步骤id；如果复制的步骤也是复制的，并且没有加载过详情，则这个 id 是最原始的 被复制的步骤 id
  resourceId?: string; // 详情或者引用、复制的类型才有
  resourceNum?: string; // 详情或者引用、复制的类型才有
  originProjectId?: string; // 如果步骤是复制的，这个字段是复制的资源的所在的项目id
  stepType: SqlScenarioStepType;
  refType: SqlScenarioStepRefType;
  config: SqlScenarioStepDetail; // 存储步骤列表需要展示的信息
  csvIds?: string[];
  projectId?: string;
  versionId?: string;
  children?: SqlScenarioStepItem[];
  isNew: boolean; // 是否新建的步骤，引用复制类型以此区分调用步骤详情还是资源详情
  // 页面渲染以及交互需要字段
  checked?: boolean; // 是否选中
  expanded?: boolean; // 是否展开
  draggable?: boolean; // 是否可拖拽
  createActionsVisible?: boolean; // 是否展示创建步骤下拉
  responsePopoverVisible?: boolean; // 是否展示步骤响应 popover
  parent?: SqlScenarioStepItem; // 父级节点，第一层的父级节点为undefined
  resourceName?: string; // 引用复制接口、用例、场景时的源资源名称
  // method?: RequestMethods;
  executeStatus?: SqlScenarioExecuteStatus;
  isExecuting?: boolean; // 是否正在执行
  reportId?: string | number; // 步骤单个调试时的报告id
  uniqueId: string | number; // 获取报告时的步骤唯一标识（用来区分重复引用的步骤）
  isQuoteScenarioStep?: boolean; // 是否是引用场景下的步骤(不分是不是完全引用，只要是引用类型就是)，不可修改引用 api 的参数值
  isRefScenarioStep?: boolean; // 是否是完全引用的场景下的步骤，是的话不允许启用禁用
}

// 场景步骤详情
export type SqlScenarioStepDetails = Partial<SqlRequestParam>;

export interface SqlScenario {
  id?: string | number;
  num?: number;
  name: string;
  moduleId: string | number;
  priority: CaseLevel;
  status: SqlScenarioStatus;
  tags: string[];
  projectId: string;
  description: string;
  grouped?: boolean;
  environmentId?: string;
  scenarioConfig: SqlScenarioConfig;
  steps: SqlScenarioStepItem[];
  stepDetails: Record<string, SqlScenarioStepDetails>; // case、api、脚本操作抽屉的详情结构
  // stepFileParam: Record<string, SqlScenarioStepFileParams>; // TODO：
  // fileParam: ScenarioFileParams; // TODO：
  follow?: boolean;
  copyFromScenarioId?: string | number;
  uploadFileIds: string[];
  linkFileIds: string[];
  // 前端渲染字段
  label: string;
  closable: boolean;
  isNew: boolean;
  unSaved: boolean;
  executeLoading: boolean; // 执行loading
  executeTime?: string | number; // 执行时间
  executeSuccessCount: number; // 执行成功数量
  executeFailCount: number; // 执行失败数量
  executeFakeErrorCount: number; // 执行误报数量
  reportId: string | number; // 场景报告 id
  stepResponses: Record<string | number, Array<SqlRequestResult>>; // 步骤响应集合，key 为步骤 id，value 为步骤响应内容
  isExecute?: boolean; // 是否从列表执行进去场景详情
  isDebug?: boolean; // 是否调试，区分执行场景和批量调试步骤
  executeType?: 'localExec' | 'serverExec'; // 执行类型
  errorMessageInfo?: {
    [key: string]: Record<string, any>;
  };
}

// 场景-执行请求参数
export interface SqlScenarioDebugRequest {
  id: string | number; // 场景 id
  grouped: boolean;
  environmentId: string;
  scenarioConfig: SqlScenarioConfig;
  stepDetails: Record<string, SqlScenarioStepDetail>;
  reportId: string | number;
  steps: SqlScenarioStepItem[];
  projectId: string;
  // stepFileParam: Record<string, SqlScenarioStepFileParams>;
  // fileParam: SqlScenarioFileParams;
  frontendDebug?: boolean;
}

// 场景详情
export interface SqlScenarioDetail extends SqlScenario {
  stepTotal: number;
  requestPassRate: string;
  lastReportStatus?: string;
  lastReportId?: string;
  deleted: boolean;
  versionId: string;
  refId: string;
  latest: boolean;
  modulePath: string;
  createUser: string;
  createUserName: string;
  createTime: number;
  updateTime: number;
  updateUser: string;
}

// 场景定时任务配置
export interface SqlScenarioScheduleConfig {
  scenarioId: string;
  enable: boolean;
  cron: string;
  config: {
    poolId: string;
    grouped: false;
    environmentId?: string;
  };
}

// 场景详情
export interface SqlScenarioTableItem {
  id: string;
  name: string;
  num: number;
  pos: number;
  projectId: string;
  moduleId: string;
  latest: boolean;
  versionId: string;
  refId: string;
  createTime: number;
  createUser: string;
  updateTime: number;
  updateUser: string;
  deleteUser: string;
  deleteTime: number;
  deleted: boolean;
  environmentId: string;
  environmentName: string;
  createUserName: string;
  updateUserName: string;
  deleteUserName: string;
  versionName: string;
  caseTotal: number;
  casePassRate: string;
  caseStatus: string;
  follow: boolean;
  tags: string[];
  response: SqlResponseDefinition;
  description: string;
  status: SqlRequestDefinitionStatus;
  scheduleConfig?: SqlScenarioScheduleConfig;
  lastReportId?: string;
}

export interface SqlScenarioAssociateCaseParams {
  scenarioId?: string | number;
  moduleMaps?: Record<string, saveParams>;
  selectAllModule: boolean; // 是否全选
  refType: 'COPY' | 'REF';
  projectId: string;
  associateType?: string;
}

// 多模块关联
export interface SqlImportSystemData {
  SQL: SqlScenarioAssociateCaseParams; // SQL用例
  SCENARIO: SqlScenarioAssociateCaseParams; // 场景
}

// 场景列表查询参数
export interface SqlScenarioPageParams extends TableQueryParams {
  id: string;
  name: string;
  projectId: string;
  versionId: string;
  refId: string;
  moduleIds: string[];
  deleted: boolean;
}

// 场景-获取模块树参数
export interface SqlScenarioGetModuleParams {
  keyword?: string;
  searchMode?: 'AND' | 'OR';
  filter?: Record<string, any>;
  combine?: Record<string, any>;
  moduleIds?: string[];
  projectId: string;
  versionId?: string;
  refId?: string;
}

// 场景修改参数
export interface SqlScenarioUpdateDTO extends Partial<SqlScenario> {
  id: string | number;
  name?: string;
  status?: SqlScenarioStatus;
  moduleId?: string | number;
  projectId?: string;
  description?: string;
  tags?: string[];
  grouped?: boolean;
  environmentId?: string ;
}

// 场景-更新模块参数
export interface SqlScenarioModuleUpdateParams {
  id: string;
  name: string;
}

export interface SqlRunModeRequest {
  runMode: string;
  integratedReport: boolean;
  integratedReportName: string;
  stopOnFailure: boolean;
  poolId: string;
  grouped: boolean;
  environmentId: string;
}

// 场景批量编辑参数
export interface SqlScenarioBatchParams extends BatchSqlParams {
  projectId?: string;
  moduleIds?: string[];
  apiScenarioId?: string;
  versionId?: string;
  refId?: string;
}

// 批量执行场景参数
export interface SqlScenarioBatchRunParams extends SqlScenarioBatchParams {
  // 运行模式配置
  runModeConfig?: SqlRunModeRequest;
}