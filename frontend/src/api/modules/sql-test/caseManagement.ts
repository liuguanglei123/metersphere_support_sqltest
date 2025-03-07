import MSR from '@/api/http/index';
import {
    AddModuleUrl,
    AddSqlCaseDefinitionUrl, GetModuleOnlyTreeUrl, GetModuleTreeUrl,
    SqlDebugDefinitionUrl, SqlDefinitionPageUrl,
} from '@/api/requrls/sql-test/caseManagement';

import {AddModuleParams, CommonList, ModuleTreeNode} from "@/models/common";
import {
    SqlDefinitionCreateParams, SqlDefinitionDetail,
    SqlDefinitionGetModuleParams,
    SqlDefinitionPageParams
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