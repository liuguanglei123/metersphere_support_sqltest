import MSR from '@/api/http';
import * as reportUrl from '@/api/requrls/sql-test/report';

import type { TableQueryParams } from '@/models/common';
import {GetShareId, SqlReportDetail} from "@/models/sqlTest/report";
import { ReportEnum } from '@/enums/reportEnum';

// 报告列表
export function reportList(data: TableQueryParams) {
    if (data.moduleType === ReportEnum.SQL_SCENARIO_REPORT) {
        return MSR.post({ url: reportUrl.ScenarioReportListUrl, data });
    }
    return MSR.post({ url: reportUrl.ScenarioReportListUrl, data });
}

// 删除报告
export function reportDelete(moduleType: string, id: string) {

    return MSR.get({ url: `${reportUrl.ScenarioReportListUrl}/${id}` });
}

// 生成分享id
export function getShareInfo(data: GetShareId) {
    return MSR.post({ url: `${reportUrl.getShareIdUrl}`, data });
}

// 报告详情
export function reportScenarioDetail(reportId: string, shareId?: string | undefined) {
    if (shareId) {
        return MSR.get<SqlReportDetail>({ url: `${reportUrl.ScenarioReportShareDetailUrl}/${shareId}/${reportId}` });
    }
    return MSR.get<SqlReportDetail>({ url: `${reportUrl.ScenarioReportDetailUrl}/${reportId}` });
}