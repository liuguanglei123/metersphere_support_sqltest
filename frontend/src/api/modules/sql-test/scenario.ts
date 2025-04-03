import {SqlRequestParam} from "@/views/sql-test/components/sqlComposition/index.vue";

import MSR from '@/api/http/index';
import {
  AddModuleUrl,
  AddScenarioUrl,
  BatchRunScenarioUrl,
  DebugScenarioUrl,
  DeleteModuleUrl,
  ExecuteScenarioUrl,
  GetModuleCountUrl,
  GetModuleTreeUrl,
  GetScenarioStepUrl,
  GetScenarioUrl,
  MoveModuleUrl,
  ScenarioAssociateExportUrl,
  ScenarioPageUrl,
  ScenarioTrashPageUrl,
  UpdateModuleUrl,
  UpdateScenarioUrl
} from '@/api/requrls/sql-test/scenario';

import { AddModuleParams, CommonList, ModuleTreeNode, MoveModules } from '@/models/common';
import {
  SqlImportSystemData,
  SqlScenario,
  SqlScenarioBatchRunParams,
  SqlScenarioDebugRequest,
  SqlScenarioDetail,
  SqlScenarioGetModuleParams,
  SqlScenarioModuleUpdateParams,
  SqlScenarioPageParams,
  SqlScenarioTableItem,
  SqlScenarioUpdateDTO,
} from '@/models/sqlTest/scenario';

// 获取接口场景列表
export function getScenarioPage(data: SqlScenarioPageParams) {
  return MSR.post<CommonList<SqlScenarioTableItem>>({ url: ScenarioPageUrl, data });
}

// 获取回收站的接口场景列表
export function getTrashScenarioPage(data: SqlScenarioPageParams) {
  return MSR.post<CommonList<SqlScenarioTableItem>>({ url: ScenarioTrashPageUrl, data });
}

// 获取模块树
export function getModuleTree(data: SqlScenarioGetModuleParams) {
  return MSR.post<ModuleTreeNode[]>({ url: GetModuleTreeUrl, data });
}

// 获取模块统计数量
export function getModuleCount(data: SqlScenarioGetModuleParams) {
  return MSR.post({ url: GetModuleCountUrl, data });
}

// 场景调试
export function debugScenario(data: SqlScenarioDebugRequest) {
  return MSR.post({ url: DebugScenarioUrl, data });
}

// 场景导出报告id集合
export function scenarioAssociateExport(data: SqlImportSystemData) {
  return MSR.post({ url: `${ScenarioAssociateExportUrl}`, data });
}

// 获取场景详情
export function getScenarioDetail(id: string | number) {
  return MSR.get<SqlScenarioDetail>({ url: GetScenarioUrl, params: id });
}

// 添加场景
export function addScenario(params: SqlScenario) {
  return MSR.post({ url: AddScenarioUrl, params });
}

// 更新接口场景
export function updateScenario(data: SqlScenarioUpdateDTO) {
  return MSR.post({ url: UpdateScenarioUrl, data });
}

// 添加模块
export function addModule(data: AddModuleParams) {
  return MSR.post({ url: AddModuleUrl, data });
}

// 删除模块
export function deleteModule(id: string) {
  return MSR.get({ url: DeleteModuleUrl, params: id });
}

// 移动模块
export function moveModule(data: MoveModules) {
  return MSR.post({ url: MoveModuleUrl, data });
}

// 更新模块
export function updateModule(data: SqlScenarioModuleUpdateParams) {
  return MSR.post({ url: UpdateModuleUrl, data });
}

// 批量执行场景
export function batchRunScenario(params: SqlScenarioBatchRunParams) {
  return MSR.post({ url: BatchRunScenarioUrl, params });
}

// 场景执行
export function executeScenario(data: SqlScenarioDebugRequest) {
  return MSR.post({ url: ExecuteScenarioUrl, data });
}

// 获取场景步骤详情
export function getScenarioStep(stepId: string | number) {
  return MSR.get<Partial<SqlRequestParam>>({
    url: GetScenarioStepUrl,
    params: stepId,
  });
}

// 复制步骤时复制文件
export function scenarioCopyStepFiles() {
  // TODO：
  return "";
}