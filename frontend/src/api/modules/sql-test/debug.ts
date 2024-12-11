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
export function sqlgetDebugModules() {
  return MSR.get<ModuleTreeNode[]>({ url: SqlGetDebugModulesUrl });
}

// 删除模块
export function sqldeleteDebugModule(deleteId: string) {
  return MSR.get({ url: SqlDeleteDebugModuleUrl, params: deleteId });
}

// 添加模块
export function sqladdDebugModule(data: SqlAddDebugModuleParams) {
  return MSR.post({ url: SqlAddDebugModuleUrl, data });
}

// 移动模块
export function sqlmoveDebugModule(data: MoveModules) {
  return MSR.post({ url: SqlMoveDebugModuleUrl, data });
}

// 更新模块
export function sqlupdateDebugModule(data: SqlUpdateDebugModule) {
  return MSR.post({ url: SqlUpdateDebugModuleUrl, data });
}

// 模块数量统计
export function sqlgetDebugModuleCount(data: { keyword: string }) {
  return MSR.post({ url: SqlGetDebugModuleCountUrl, data });
}

// 拖拽调试节点
export function sqldragDebug(data: DragSortParams) {
  return MSR.post({ url: SqlDragDebugUrl, data });
}

// 执行调试
export function sqlexecuteDebug(data: SqlExecuteRequestParams) {
  return MSR.post<SqlExecuteRequestParams>({ url: SqlExecuteApiDebugUrl, data });
}

// 新增调试
export function sqladdDebug(data: SqlSaveDebugParams) {
  return MSR.post({ url: SqlAddApiDebugUrl, data });
}

// 更新调试
export function sqlupdateDebug(data: SqlUpdateDebugParams) {
  return MSR.post({ url: SqlUpdateApiDebugUrl, data });
}

// 获取接口调试详情
export function sqlgetDebugDetail(id: string) {
  return MSR.get<SqlDebugDetail>({ url: SqlGetApiDebugDetailUrl, params: id });
}

// 删除接口调试
export function sqldeleteDebug(id: string) {
  return MSR.get({ url: SqlDeleteDebugUrl, params: id });
}

// 测试mock
export function sqltestMock(data: string) {
  return MSR.post({ url: SqlTestMockUrl, data });
}

// 上传文件
export function sqluploadTempFile(file: File) {
  return MSR.uploadFile({ url: SqlUploadTempFileUrl }, { fileList: [file] }, 'file');
}

// 文件转存
export function sqltransferFile(data: TransferFileParams) {
  return MSR.post({ url: SqlTransferFileUrl, data });
}

// 文件转存目录
export function sqlgetTransferOptions(projectId: string) {
  return MSR.get<ModuleTreeNode[]>({ url: SqlTransferOptionsUrl, params: projectId });
}
