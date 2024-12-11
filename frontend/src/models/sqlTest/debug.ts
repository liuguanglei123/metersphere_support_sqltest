import { RequestMethods } from '@/enums/apiEnum';

import { SqlExecuteApiRequestFullParams, SqlExecutePluginRequestParams } from './common';

// 保存接口调试入参
export interface SqlSaveDebugParams {
  name: string;
  protocol: string;
  method: RequestMethods | string;
  path: string;
  projectId: string;
  moduleId: string;
  request: SqlExecuteApiRequestFullParams | SqlExecutePluginRequestParams;
  uploadFileIds: string[];
  linkFileIds: string[];
}
// 更新接口调试入参
export interface SqlUpdateDebugParams extends Partial<SqlSaveDebugParams> {
  id: string;
  deleteFileIds?: string[];
  unLinkFileIds?: string[];
}
// 更新模块入参
export interface SqlUpdateDebugModule {
  id: string;
  name: string;
}
// 添加模块入参
export interface SqlAddDebugModuleParams {
  projectId: string;
  name: string;
  parentId: string;
}
// 接口调试详情-请求参数
export interface SqlDebugDetailRequest {
  stepId: string;
  resourceId: string;
  projectId: string;
  name: string;
  enable: boolean;
  children: string[];
  parent: string;
  polymorphicName: string;
}
// 接口调试详情
export interface SqlDebugDetail {
  id: string;
  name: string;
  protocol: string;
  method: RequestMethods | string;
  path: string;
  projectId: string;
  moduleId: string;
  createTime: number;
  createUser: string;
  updateTime: number;
  updateUser: string;
  pos: number;
  request: SqlDebugDetailRequest & (SqlExecuteApiRequestFullParams | SqlExecutePluginRequestParams);
  response: string;
}
