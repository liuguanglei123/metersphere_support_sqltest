import MSR from '@/api/http/index';
import {
  GetEnvListUrl,
  GetProtocolListUrl,
  LocalExecuteSqlDebugUrl,
} from '@/api/requrls/sql-test/common';

import {
  SqlEnvironmentItem,
  SqlExecuteRequestParams,
  SqlProtocolItem,
} from '@/models/sqlTest/common';

// 获取协议列表
export function getProtocolList(organizationId: string) {
  return MSR.get<SqlProtocolItem[]>({ url: GetProtocolListUrl, params: organizationId });
}

// 本地执行调试
export function localExecuteSqlDebug(host: string, data: SqlExecuteRequestParams) {
  return MSR.post<SqlExecuteRequestParams>({ url: `${host}${LocalExecuteSqlDebugUrl}`, data });
}

// 获取环境列表
export function getEnvList(projectId: string) {
  return MSR.get<SqlEnvironmentItem[]>({ url: GetEnvListUrl, params: projectId });
}