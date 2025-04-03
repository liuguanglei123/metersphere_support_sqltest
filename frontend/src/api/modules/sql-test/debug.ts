import MSR from '@/api/http/index';
import {
  SqlAddApiDebugUrl,
  SqlAddDebugModuleUrl,
  SqlDeleteDebugModuleUrl,
  SqlDeleteDebugUrl,
  SqlDragDebugUrl,
  SqlExecuteApiDebugUrl,
  SqlGetApiDebugDetailUrl,
  SqlGetDebugModuleCountUrl,
  SqlGetDebugModulesUrl,
  SqlMoveDebugModuleUrl,
  SqlTestMockUrl,
  SqlTransferFileUrl,
  SqlTransferOptionsUrl,
  SqlUpdateApiDebugUrl,
  SqlUpdateDebugModuleUrl,
  SqlUploadTempFileUrl,
} from '@/api/requrls/sql-test/debug';

import { DragSortParams, ModuleTreeNode, MoveModules, TransferFileParams } from '@/models/common';
import { SqlExecuteRequestParams } from '@/models/sqlTest/common';
import {
  SqlAddDebugModuleParams,
  SqlDebugDetail,
  SqlSaveDebugParams,
  SqlUpdateDebugModule,
  SqlUpdateDebugParams,
} from '@/models/sqlTest/debug';

// 获取模块树
export function sqlGetDebugModules() {
  return MSR.get<ModuleTreeNode[]>({ url: SqlGetDebugModulesUrl });
}

// 删除模块
export function sqlDeleteDebugModule(deleteId: string) {
  return MSR.get({ url: SqlDeleteDebugModuleUrl, params: deleteId });
}

// 添加模块
export function sqlAddDebugModule(data: SqlAddDebugModuleParams) {
  return MSR.post({ url: SqlAddDebugModuleUrl, data });
}

// 移动模块
export function sqlMoveDebugModule(data: MoveModules) {
  return MSR.post({ url: SqlMoveDebugModuleUrl, data });
}

// 更新模块
export function sqlUpdateDebugModule(data: SqlUpdateDebugModule) {
  return MSR.post({ url: SqlUpdateDebugModuleUrl, data });
}

// 模块数量统计
export function sqlGetDebugModuleCount(data: { keyword: string }) {
  return MSR.post({ url: SqlGetDebugModuleCountUrl, data });
}

// 拖拽调试节点
export function sqlDragDebug(data: DragSortParams) {
  return MSR.post({ url: SqlDragDebugUrl, data });
}

// 执行调试
export function sqlExecuteDebug(data: SqlExecuteRequestParams) {
  return MSR.post<SqlExecuteRequestParams>({ url: SqlExecuteApiDebugUrl, data });
}

// 新增调试
export function sqlAddDebug(data: SqlSaveDebugParams) {
  return MSR.post({ url: SqlAddApiDebugUrl, data });
}

// 更新调试
export function sqlUpdateDebug(data: SqlUpdateDebugParams) {
  return MSR.post({ url: SqlUpdateApiDebugUrl, data });
}

// 获取接口调试详情
export function sqlGetDebugDetail(id: string) {
  return MSR.get<SqlDebugDetail>({ url: SqlGetApiDebugDetailUrl, params: id });
}

// 删除接口调试
export function sqlDeleteDebug(id: string) {
  return MSR.get({ url: SqlDeleteDebugUrl, params: id });
}