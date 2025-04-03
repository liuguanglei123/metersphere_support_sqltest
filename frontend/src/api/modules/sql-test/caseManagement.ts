import MSR from '@/api/http/index';
import {
    AddModuleUrl,
    AddSqlCaseDefinitionUrl,
    DeleteDefinitionUrl,
    DeleteModuleUrl,
    GetDefinitionDetailUrl, GetModuleCountUrl,
    GetModuleOnlyTreeUrl,
    GetModuleTreeUrl, GetPoolId,
    SqlDebugDefinitionUrl,
    SqlDefinitionPageUrl,
    UpdateDefinitionUrl,
    UpdateModuleUrl,
} from '@/api/requrls/sql-test/caseManagement';

import {AddModuleParams, CommonList, ModuleTreeNode} from "@/models/common";
import {
    SqlDefinitionCreateParams, SqlDefinitionDetail,
    SqlDefinitionGetModuleParams,
    SqlDefinitionPageParams, SqlDefinitionUpdateModuleParams, SqlDefinitionUpdateParams
} from "@/models/sqlTest/caseManagement";
import { SqlExecuteRequestParams } from "@/models/sqlTest/common";

// 接口定义调试
export function sqlDebugDefinition(data: SqlExecuteRequestParams) {
  return MSR.post({ url: SqlDebugDefinitionUrl, data });
}

// 添加接口定义
export function addDefinition(data: SqlDefinitionCreateParams) {
  return MSR.post({ url: AddSqlCaseDefinitionUrl, data });
}

// 获取SQL定义模块树
export function getModuleTree(data: SqlDefinitionGetModuleParams) {
    return MSR.post<ModuleTreeNode[]>({ url: GetModuleTreeUrl, data });
}

// 获取模块树-只包含模块
export function getModuleTreeOnlyModules(data: SqlDefinitionGetModuleParams) {
    return MSR.post<ModuleTreeNode[]>({ url: GetModuleOnlyTreeUrl, data });
}

// 添加模块
export function addModule(data: AddModuleParams) {
    return MSR.post({ url: AddModuleUrl, data });
}

// 获取SQL定义列表
export function getSqlDefinitionPage(data: SqlDefinitionPageParams) {
    return MSR.post<CommonList<SqlDefinitionDetail>>({ url: SqlDefinitionPageUrl, data });
}

// 删除定义
export function deleteSqlDefinition(id: string) {
    return MSR.get({ url: DeleteDefinitionUrl, params: id });
}

// 删除模块
export function deleteModule(id: string) {
    return MSR.get({ url: DeleteModuleUrl, params: id });
}

// 更新模块
export function updateModule(data: SqlDefinitionUpdateModuleParams) {
    return MSR.post({ url: UpdateModuleUrl, data });
}

// 更新接口定义
export function updateDefinition(data: SqlDefinitionUpdateParams) {
    return MSR.post({ url: UpdateDefinitionUrl, data });
}

// 获取接口定义详情
export function getDefinitionDetail(id: string | number) {
    return MSR.get<SqlDefinitionDetail>({ url: GetDefinitionDetailUrl, params: id });
}

// 获取接口定义列表
export function getDefinitionPage(data: SqlDefinitionPageParams) {
    return MSR.post<CommonList<SqlDefinitionDetail>>({ url: SqlDefinitionPageUrl, data });
}

// 获取模块统计数量
export function getModuleCount(data: SqlDefinitionGetModuleParams) {
    return MSR.post({ url: GetModuleCountUrl, data });
}
