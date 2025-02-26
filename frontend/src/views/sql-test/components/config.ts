import { cloneDeep } from 'lodash-es';

import { EQUAL } from '@/components/pure/ms-advance-filter';
import { LanguageEnum } from '@/components/pure/ms-code-editor/types';
import { RequestParam } from '@/views/api-test/components/requestComposition/index.vue';
import { SqlRequestParam } from '@/views/sql-test/components/sqlComposition/index.vue';

import { useI18n } from '@/hooks/useI18n';

import {
  EnableKeyValueParam,
  ExecuteBody,
  ExecuteRequestCommonParam,
  ExecuteRequestFormBodyFormValue,
  KeyValueParam,
  RequestTaskResult,
  ResponseAssertionItem,
  ResponseDefinition,
} from '@/models/apiTest/common';
import type { MockParams } from '@/models/apiTest/mock';
import { IManageResultData, SqlExecuteBody } from '@/models/sqlTest/common';
import {
  FullResponseAssertionType,
  RequestAssertionCondition,
  RequestAuthType,
  RequestBodyFormat,
  RequestCaseStatus,
  RequestComposition,
  RequestContentTypeEnum,
  RequestDefinitionStatus,
  RequestExtractEnvType,
  RequestExtractExpressionEnum,
  RequestExtractExpressionRuleType,
  RequestExtractResultMatchingRule,
  RequestExtractScope,
  RequestMethods,
  RequestParamsType,
  ResponseBodyFormat,
  ResponseBodyXPathAssertionFormat,
  ResponseComposition,
} from '@/enums/apiEnum';
import { ReportStatus } from '@/enums/reportEnum';

import type { ExpressionConfig } from './fastExtraction/moreSetting.vue';

const { t } = useI18n();

// 请求 body 参数表格默认行的值
export const defaultBodyParamsItem: ExecuteRequestFormBodyFormValue = {
  key: '',
  value: '',
  paramType: RequestParamsType.STRING,
  description: '',
  required: false,
  maxLength: undefined,
  minLength: undefined,
  encode: false,
  enable: true,
  contentType: RequestContentTypeEnum.TEXT,
  files: [],
};

// 请求 header 参数表格默认行的值
export const defaultHeaderParamsItem: EnableKeyValueParam = {
  key: '',
  value: '',
  description: '',
  enable: true,
};

// 请求 query、rest 参数表格默认行的值
export const defaultRequestParamsItem: ExecuteRequestCommonParam = {
  key: '',
  value: '',
  paramType: RequestParamsType.STRING,
  description: '',
  required: false,
  maxLength: undefined,
  minLength: undefined,
  encode: false,
  enable: true,
};

// 请求的响应 response 默认的响应信息项
export const defaultResponseItem: ResponseDefinition = {
  id: new Date().getTime(),
  name: 'apiTestManagement.response',
  label: 'apiTestManagement.response',
  closable: false,
  statusCode: 200,
  defaultFlag: true,
  showPopConfirm: false,
  showRenamePopConfirm: false,
  responseActiveTab: ResponseComposition.BODY,
  headers: [],
  body: {
    bodyType: ResponseBodyFormat.JSON,
    jsonBody: {
      jsonValue: '',
      enableJsonSchema: true,
      enableTransition: false,
      jsonSchemaTableData: [],
      jsonSchemaTableSelectedRowKeys: [],
    },
    xmlBody: {
      value: '',
    },
    rawBody: {
      value: '',
    },
    binaryBody: {
      description: '',
      file: undefined,
      sendAsBody: false,
    },
  },
};

// 请求的默认 body 参数
export const defaultSqlBodyParams: SqlExecuteBody = {
  sqlContent: 'this is default content!',
};

// 默认的响应内容结构
export const defaultResponse: RequestTaskResult = {
  requestResults: [
    {
      body: '',
      headers: '',
      method: '',
      url: '',
      responseResult: {
        body: '',
        contentType: '',
        headers: '',
        dnsLookupTime: 0,
        downloadTime: 0,
        latency: 0,
        responseCode: 0,
        responseTime: 0,
        responseSize: 0,
        socketInitTime: 0,
        tcpHandshakeTime: 0,
        transferStartTime: 0,
        sslHandshakeTime: 0,
        vars: '',
        extractResults: [],
        assertions: [],
      },
    },
  ],
  console: '',
};

// 默认的响应内容结构
export const defaultSqlResponse: IManageResultData[] = [
  {
    sql: undefined,
    originalSql: 'select * from t1 limit 10',
    description: '执行成功',
    message: undefined,
    success: true,
    updateCount: undefined,
    headerList: [
      {
        dataType: 'CHAT2DB_ROW_NUMBER',
        name: '行号',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'ID',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'STRING',
        name: 'NAME',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'AGE',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'GMT',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'PRICE',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'AMOUNT',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'STRING',
        name: 'FEATURE',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'FEATURE_ID',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
    ],
    dataList: [
      [
        '1',
        '1',
        '1abcd',
        '2',
        '12',
        '1.111111044883728',
        '2.222222222222222',
        '[0.6542471, 0.57696617, 0.7122348, 0.047073293, 0.861135, 0.23653789, 0.45527896, 0.8690892]',
        '1',
      ],
      [
        '2',
        '2',
        '2abcd',
        '4',
        '24',
        '2.222222089767456',
        '4.444444444444444',
        '[0.6542471, 0.57696617, 0.7122348, 0.047073293, 0.861135, 0.23653789, 0.45527896, 0.8690892]',
        '2',
      ],
      [
        '3',
        '3',
        '3abcd',
        '6',
        '36',
        '3.3333332538604736',
        '6.666666666666666',
        '[0.6542471, 0.57696617, 0.7122348, 0.047073293, 0.861135, 0.23653789, 0.45527896, 0.8690892]',
        '3',
      ],
      [
        '4',
        '4',
        '4abcd',
        '8',
        '48',
        '4.444444179534912',
        '8.888888888888888',
        '[0.6542471, 0.57696617, 0.7122348, 0.047073293, 0.861135, 0.23653789, 0.45527896, 0.8690892]',
        '4',
      ],
      [
        '5',
        '5',
        '5abcd',
        '10',
        '60',
        '5.55555534362793',
        '11.111111111111109',
        '[0.6542471, 0.57696617, 0.7122348, 0.047073293, 0.861135, 0.23653789, 0.45527896, 0.8690892]',
        '5',
      ],
      [
        '6',
        '6',
        '6abcd',
        '12',
        '72',
        '6.666666507720947',
        '13.333333333333332',
        '[0.6542471, 0.57696617, 0.7122348, 0.047073293, 0.861135, 0.23653789, 0.45527896, 0.8690892]',
        '6',
      ],
      [
        '7',
        '7',
        '7abcd',
        '14',
        '84',
        '7.777777671813965',
        '15.555555555555554',
        '[0.6542471, 0.57696617, 0.7122348, 0.047073293, 0.861135, 0.23653789, 0.45527896, 0.8690892]',
        '7',
      ],
      [
        '8',
        '8',
        '8abcd',
        '16',
        '96',
        '8.888888359069824',
        '17.777777777777775',
        '[0.6542471, 0.57696617, 0.7122348, 0.047073293, 0.861135, 0.23653789, 0.45527896, 0.8690892]',
        '8',
      ],
      [
        '9',
        '9',
        '9abcd',
        '18',
        '108',
        '10.0',
        '19.999999999999996',
        '[0.6542471, 0.57696617, 0.7122348, 0.047073293, 0.861135, 0.23653789, 0.45527896, 0.8690892]',
        '9',
      ],
      [
        '10',
        '10',
        '10abcd',
        '20',
        '120',
        '11.11111068725586',
        '22.222222222222218',
        '[0.6542471, 0.57696617, 0.7122348, 0.047073293, 0.861135, 0.23653789, 0.45527896, 0.8690892]',
        '10',
      ],
    ],
    sqlType: 'SELECT',
    hasNextPage: false,
    pageNo: 1,
    pageSize: 200,
    fuzzyTotal: '10',
    duration: 145,
    canEdit: true,
    tableName: 't1',
  },
  {
    sql: undefined,
    originalSql: 'select * from t2 limit 20',
    description: '执行成功',
    message: undefined,
    success: true,
    updateCount: undefined,
    headerList: [
      {
        dataType: 'CHAT2DB_ROW_NUMBER',
        name: '行号',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'ID',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'STRING',
        name: 'NAME',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'AGE',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'GMT',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'PRICE',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'AMOUNT',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'STRING',
        name: 'FEATURE',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
      {
        dataType: 'NUMERIC',
        name: 'FEATURE_ID',
        primaryKey: null,
        comment: null,
        defaultValue: null,
        autoIncrement: null,
        nullable: null,
        columnSize: null,
        decimalDigits: null,
      },
    ],
    dataList: [
      [
        '1',
        '1',
        '1abcd',
        '2',
        '12',
        '1.111111044883728',
        '2.222222222222222',
        '[0.40725073, 0.7615715, 0.4432293, 0.28923556, 0.63410395, 0.43526706, 0.2887618, 0.6377667]',
        '1',
      ],
      [
        '2',
        '2',
        '2abcd',
        '4',
        '24',
        '2.222222089767456',
        '4.444444444444444',
        '[0.49257955, 0.7585382, 0.10008137, 0.0912083, 0.2147838, 0.58040327, 0.77037024, 0.2203935]',
        '2',
      ],
      [
        '3',
        '3',
        '3abcd',
        '6',
        '36',
        '3.3333332538604736',
        '6.666666666666666',
        '[0.40725073, 0.7615715, 0.4432293, 0.28923556, 0.63410395, 0.43526706, 0.2887618, 0.6377667]',
        '3',
      ],
      [
        '4',
        '4',
        '4abcd',
        '8',
        '48',
        '4.444444179534912',
        '8.888888888888888',
        '[0.49257955, 0.7585382, 0.10008137, 0.0912083, 0.2147838, 0.58040327, 0.77037024, 0.2203935]',
        '4',
      ],
      [
        '5',
        '5',
        '5abcd',
        '10',
        '60',
        '5.55555534362793',
        '11.111111111111109',
        '[0.40725073, 0.7615715, 0.4432293, 0.28923556, 0.63410395, 0.43526706, 0.2887618, 0.6377667]',
        '5',
      ],
      [
        '6',
        '6',
        '6abcd',
        '12',
        '72',
        '6.666666507720947',
        '13.333333333333332',
        '[0.49257955, 0.7585382, 0.10008137, 0.0912083, 0.2147838, 0.58040327, 0.77037024, 0.2203935]',
        '6',
      ],
      [
        '7',
        '7',
        '7abcd',
        '14',
        '84',
        '7.777777671813965',
        '15.555555555555554',
        '[0.40725073, 0.7615715, 0.4432293, 0.28923556, 0.63410395, 0.43526706, 0.2887618, 0.6377667]',
        '7',
      ],
      [
        '8',
        '8',
        '8abcd',
        '16',
        '96',
        '8.888888359069824',
        '17.777777777777775',
        '[0.49257955, 0.7585382, 0.10008137, 0.0912083, 0.2147838, 0.58040327, 0.77037024, 0.2203935]',
        '8',
      ],
      [
        '9',
        '9',
        '9abcd',
        '18',
        '108',
        '10.0',
        '19.999999999999996',
        '[0.29074618, 0.31934705, 0.94386715, 0.5579232, 0.17056802, 0.38839856, 0.60135365, 0.8315345]',
        '9',
      ],
      [
        '10',
        '10',
        '10abcd',
        '20',
        '120',
        '11.11111068725586',
        '22.222222222222218',
        '[0.40725073, 0.7615715, 0.4432293, 0.28923556, 0.63410395, 0.43526706, 0.2887618, 0.6377667]',
        '10',
      ],
      [
        '11',
        '11',
        '11abcd',
        '22',
        '132',
        '12.222222328186035',
        '24.44444444444444',
        '[0.49257955, 0.7585382, 0.10008137, 0.0912083, 0.2147838, 0.58040327, 0.77037024, 0.2203935]',
        '11',
      ],
      [
        '12',
        '12',
        '12abcd',
        '24',
        '144',
        '13.333333015441895',
        '26.666666666666664',
        '[0.40725073, 0.7615715, 0.4432293, 0.28923556, 0.63410395, 0.43526706, 0.2887618, 0.6377667]',
        '12',
      ],
      [
        '13',
        '13',
        '13abcd',
        '26',
        '156',
        '14.44444465637207',
        '28.888888888888886',
        '[0.49257955, 0.7585382, 0.10008137, 0.0912083, 0.2147838, 0.58040327, 0.77037024, 0.2203935]',
        '13',
      ],
      [
        '14',
        '14',
        '14abcd',
        '28',
        '168',
        '15.55555534362793',
        '31.111111111111107',
        '[0.40725073, 0.7615715, 0.4432293, 0.28923556, 0.63410395, 0.43526706, 0.2887618, 0.6377667]',
        '14',
      ],
      [
        '15',
        '15',
        '15abcd',
        '30',
        '180',
        '16.66666603088379',
        '33.33333333333333',
        '[0.49257955, 0.7585382, 0.10008137, 0.0912083, 0.2147838, 0.58040327, 0.77037024, 0.2203935]',
        '15',
      ],
      [
        '16',
        '16',
        '16abcd',
        '32',
        '192',
        '17.77777671813965',
        '35.55555555555555',
        '[0.49257955, 0.7585382, 0.10008137, 0.0912083, 0.2147838, 0.58040327, 0.77037024, 0.2203935]',
        '16',
      ],
      [
        '17',
        '17',
        '17abcd',
        '34',
        '204',
        '18.88888931274414',
        '37.77777777777777',
        '[0.29074618, 0.31934705, 0.94386715, 0.5579232, 0.17056802, 0.38839856, 0.60135365, 0.8315345]',
        '17',
      ],
      [
        '18',
        '18',
        '18abcd',
        '36',
        '216',
        '20.0',
        '39.99999999999999',
        '[0.40725073, 0.7615715, 0.4432293, 0.28923556, 0.63410395, 0.43526706, 0.2887618, 0.6377667]',
        '18',
      ],
      [
        '19',
        '19',
        '19abcd',
        '38',
        '228',
        '21.11111068725586',
        '42.222222222222214',
        '[0.49257955, 0.7585382, 0.10008137, 0.0912083, 0.2147838, 0.58040327, 0.77037024, 0.2203935]',
        '19',
      ],
      [
        '20',
        '20',
        '20abcd',
        '40',
        '240',
        '22.22222137451172',
        '44.444444444444436',
        '[0.40725073, 0.7615715, 0.4432293, 0.28923556, 0.63410395, 0.43526706, 0.2887618, 0.6377667]',
        '20',
      ],
    ],
    sqlType: 'SELECT',
    hasNextPage: false,
    pageNo: 1,
    pageSize: 200,
    fuzzyTotal: '20',
    duration: 118,
    canEdit: true,
    tableName: 't2',
  },
];

// 默认提取参数的 key-value 表格行的值
export const defaultKeyValueParamItem: KeyValueParam = {
  key: '',
  value: '',
};

// 请求的响应 response 的响应状态码集合
export const statusCodes = [200, 201, 202, 203, 204, 205, 400, 401, 402, 403, 404, 405, 500, 501, 502, 503, 504, 505];

// 用例等级选项
export const casePriorityOptions = [
  { label: 'P0', value: 'P0' },
  { label: 'P1', value: 'P1' },
  { label: 'P2', value: 'P2' },
  { label: 'P3', value: 'P3' },
];

export const apiStatusOptions = [
  {
    name: t('apiTestManagement.processing'),
    value: RequestDefinitionStatus.PROCESSING,
  },
  {
    name: t('apiTestManagement.done'),
    value: RequestDefinitionStatus.DONE,
  },
  {
    name: t('apiTestManagement.deprecate'),
    value: RequestDefinitionStatus.DEPRECATED,
  },
  {
    name: t('apiTestManagement.debugging'),
    value: RequestDefinitionStatus.DEBUGGING,
  },
];

// 用例状态选项
export const caseStatusOptions = [
  { label: 'apiTestManagement.processing', value: RequestCaseStatus.PROCESSING },
  { label: 'apiTestManagement.deprecate', value: RequestCaseStatus.DEPRECATED },
  { label: 'apiTestManagement.done', value: RequestCaseStatus.DONE },
];

// 断言xpath默认行的值
export const xpathAssertParamsItem: ResponseAssertionItem = {
  expression: '',
  expectedValue: '',
  enable: true,
  extractType: RequestExtractExpressionEnum.X_PATH,
  valid: true,
};
// @desc 断言的字段xpath和上边的defaultExtractParamItem不匹配所以添加此类型为了保存参数过滤正确
export const assertDefaultParamsItem: ResponseAssertionItem = {
  expression: '',
  condition: RequestAssertionCondition.EQUALS,
  expectedValue: '',
  enable: true,
};

// 断言 json默认值
export const jsonPathDefaultParamItem = {
  enable: true,
  expression: '',
  expectedValue: '',
  extractType: RequestExtractExpressionEnum.JSON_PATH,
  condition: EQUAL.value,
  valid: true,
};
// 断言 正则默认值
export const regexDefaultParamItem = {
  expression: '',
  enable: true,
  valid: true,
  variableType: RequestExtractEnvType.TEMPORARY,
  extractScope: RequestExtractScope.BODY,
  extractType: RequestExtractExpressionEnum.REGEX,
  expressionMatchingRule: RequestExtractExpressionRuleType.EXPRESSION,
  resultMatchingRule: RequestExtractResultMatchingRule.RANDOM,
  resultMatchingRuleNum: 1,
  responseFormat: ResponseBodyXPathAssertionFormat.XML,
  moreSettingPopoverVisible: false,
};
// 响应断言类型映射
export const responseAssertionTypeMap: Record<string, string> = {
  [FullResponseAssertionType.DOCUMENT]: 'apiTestManagement.document',
  [FullResponseAssertionType.RESPONSE_CODE]: 'apiTestManagement.responseCode',
  [FullResponseAssertionType.RESPONSE_HEADER]: 'apiTestManagement.responseHeader',
  [FullResponseAssertionType.RESPONSE_TIME]: 'apiTestManagement.responseTime',
  [FullResponseAssertionType.SCRIPT]: 'apiTestManagement.script',
  [FullResponseAssertionType.VARIABLE]: 'apiTestManagement.variable',
};
// 提取参数
export const defaultExtractParamItem: ExpressionConfig = {
  enable: true,
  variableName: '',
  variableType: RequestExtractEnvType.TEMPORARY,
  extractScope: RequestExtractScope.BODY,
  expression: '',
  extractType: RequestExtractExpressionEnum.JSON_PATH,
  expressionMatchingRule: RequestExtractExpressionRuleType.EXPRESSION,
  resultMatchingRule: RequestExtractResultMatchingRule.RANDOM,
  resultMatchingRuleNum: 1,
  responseFormat: ResponseBodyXPathAssertionFormat.XML,
  moreSettingPopoverVisible: false,
  description: '',
};
// 提取类型选项
export const extractTypeOptions = [
  // 全局参数，暂时不上
  // {
  //   label: t('apiTestDebug.globalParameter'),
  //   value: RequestExtractEnvType.GLOBAL,
  // },
  {
    label: 'apiTestDebug.envParameter',
    value: RequestExtractEnvType.ENVIRONMENT,
  },
  {
    label: 'apiTestDebug.tempParameter',
    value: RequestExtractEnvType.TEMPORARY,
  },
];
// mock 匹配规则默认项
export const defaultMatchRuleItem = {
  key: '',
  value: '',
  condition: 'EQUALS',
  description: '',
  paramType: RequestParamsType.STRING,
  files: [],
};
// mock 默认参数
export const mockDefaultParams: MockParams = {
  isNew: true,
  projectId: '',
  name: '',
  statusCode: 200,
  tags: [],
  mockMatchRule: {
    header: {
      matchRules: [],
      matchAll: true,
    },
    query: {
      matchRules: [],
      matchAll: true,
    },
    rest: {
      matchRules: [],
      matchAll: true,
    },
    body: {
      bodyType: RequestBodyFormat.NONE,
      formDataBody: {
        matchRules: [],
        matchAll: true,
      },
      wwwFormBody: {
        matchRules: [],
        matchAll: true,
      },
      jsonBody: {
        jsonValue: '',
        enableJsonSchema: true,
        jsonSchemaTableData: [],
        jsonSchemaTableSelectedRowKeys: [],
      },
      xmlBody: { value: '' },
      rawBody: { value: '' },
      binaryBody: {
        description: '',
        file: undefined,
        sendAsBody: false,
      },
    },
  },
  response: {
    statusCode: 200,
    headers: [],
    useApiResponse: false,
    apiResponseId: '',
    body: {
      bodyType: ResponseBodyFormat.JSON,
      jsonBody: {
        jsonValue: '',
        enableJsonSchema: true,
        enableTransition: false,
        jsonSchemaTableData: [],
        jsonSchemaTableSelectedRowKeys: [],
      },
      xmlBody: {
        value: '',
      },
      rawBody: {
        value: '',
      },
      binaryBody: {
        description: '',
        file: undefined,
        sendAsBody: false,
      },
    },
    delay: 0,
  },
  apiDefinitionId: '',
  uploadFileIds: [],
  linkFileIds: [],
};
export const makeMockDefaultParams = () => {
  const defaultParams = cloneDeep(mockDefaultParams);
  defaultParams.id = Date.now().toString();
  defaultParams.mockMatchRule.body.formDataBody.matchRules.push({
    ...defaultMatchRuleItem,
    id: Date.now().toString(),
  });
  defaultParams.mockMatchRule.body.wwwFormBody.matchRules.push({
    ...defaultMatchRuleItem,
    id: Date.now().toString(),
  });
  defaultParams.mockMatchRule.header.matchRules.push({ ...defaultMatchRuleItem, id: Date.now().toString() });
  defaultParams.mockMatchRule.query.matchRules.push({ ...defaultMatchRuleItem, id: Date.now().toString() });
  defaultParams.mockMatchRule.rest.matchRules.push({ ...defaultMatchRuleItem, id: Date.now().toString() });
  defaultParams.response.headers.push({ ...defaultMatchRuleItem, id: Date.now().toString() });
  return cloneDeep(defaultParams);
};
// mock 匹配规则选项
export const matchRuleOptions = [
  {
    label: 'mockManagement.equals',
    value: 'EQUALS',
  },
  {
    label: 'mockManagement.notEquals',
    value: 'NOT_EQUALS',
  },
  {
    label: 'mockManagement.lengthEquals',
    value: 'LENGTH_EQUALS',
  },
  {
    label: 'mockManagement.lengthNotEquals',
    value: 'LENGTH_NOT_EQUALS',
  },
  {
    label: 'mockManagement.lengthLarge',
    value: 'LENGTH_LARGE',
  },
  {
    label: 'mockManagement.lengthLess',
    value: 'LENGTH_SHOT',
  },
  {
    label: 'mockManagement.contain',
    value: 'CONTAINS',
  },
  {
    label: 'mockManagement.notContain',
    value: 'NOT_CONTAINS',
  },
  {
    label: 'mockManagement.empty',
    value: 'IS_EMPTY',
  },
  {
    label: 'mockManagement.notEmpty',
    value: 'IS_NOT_EMPTY',
  },
  {
    label: 'mockManagement.regular',
    value: 'REGULAR_MATCH',
  },
];
// mock 参数为文件类型的匹配规则选项
export const mockFileMatchRules = ['EQUALS', 'NOT_EQUALS', 'IS_EMPTY', 'IS_NOT_EMPTY'];

// 执行结果筛选下拉
export const lastReportStatusListOptions = computed(() => {
  return Object.keys(ReportStatus).map((key) => {
    return {
      value: key,
      label: t(ReportStatus[key].label),
    };
  });
});

// api下的创建用例弹窗也用到了defaultCaseParams
const initDefaultId = `case-${Date.now()}`;
export const defaultCaseParams: SqlRequestParam = {
  id: initDefaultId,
  type: 'case',
  moduleId: '',
  protocol: 'HTTP',
  tags: [],
  description: '',
  priority: 'P0',
  status: RequestDefinitionStatus.PROCESSING,
  url: '',
  activeTab: RequestComposition.HEADER,
  closable: true,
  method: RequestMethods.GET,
  headers: [],
  body: cloneDeep(defaultSqlBodyParams),
  query: [],
  rest: [],
  polymorphicName: '',
  name: '',
  path: '',
  projectId: '',
  uploadFileIds: [],
  linkFileIds: [],
  authConfig: {
    authType: RequestAuthType.NONE,
    basicAuth: {
      userName: '',
      password: '',
    },
    digestAuth: {
      userName: '',
      password: '',
    },
  },
  children: [
    {
      polymorphicName: 'MsCommonElement', // 协议多态名称，写死MsCommonElement
      assertionConfig: {
        enableGlobal: true,
        assertions: [],
      },
      postProcessorConfig: {
        enableGlobal: true,
        processors: [],
      },
      preProcessorConfig: {
        enableGlobal: true,
        processors: [],
      },
    },
  ],
  otherConfig: {
    connectTimeout: 60000,
    responseTimeout: 60000,
    certificateAlias: '',
    followRedirects: true,
    autoRedirects: false,
  },
  responseActiveTab: ResponseComposition.BODY,
  response: cloneDeep(defaultResponse),
  responseDefinition: [cloneDeep(defaultResponseItem)],
  isNew: true,
  unSaved: false,
  executeLoading: false,
  preDependency: [], // 前置依赖
  postDependency: [], // 后置依赖
  errorMessageInfo: {},
};

export const BeanShellScriptExampleMap = {
  preOperation: `import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.Header;

// 生成或获取 token
String token = "Bearer " + "MeterSphere 123456"; 

// 获取当前 HTTP 请求的 Header Manager
HeaderManager headerManager = sampler.getHeaderManager();

// 如果 Header Manager 不存在，创建一个新的
if (headerManager == null) {
    sampler.setHeaderManager(new HeaderManager());
}

// 设置 Authorization 头
headerManager.add(new Header("Authorization", token));

// 将 Header Manager 设置到 HTTP 请求
sampler.setHeaderManager(headerManager);`,
  postOperation: `// 获取 HTTP 请求的响应数据
String responseData = prev.getResponseDataAsString();

// 输出响应数据到控制台
log.info("Response Data: " + responseData);

// 你可以进一步处理响应数据
// 例如：保存响应数据到变量
vars.put("responseData", responseData);`,
  assertion: `// 获取响应状态码
String responseCode = prev.getResponseCode();
// 设置期望的状态码
String expectedCode = "200";
// 断言失败条件
if (!responseCode.equals(expectedCode)) {
    Failure = true;
    FailureMessage = "Expected response code: " + expectedCode + ", but got: " + responseCode;
}`,
  scenario: `import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

 // 创建 HttpClient 实例
HttpClient client = HttpClient.newHttpClient();

// 定义请求 URL
String urlString = "https://www.baidu.com";

// 创建 HttpRequest 对象
HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(urlString))
        .GET() // 使用 GET 方法
        .build();

// 发送请求并处理响应
client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenAccept(response -> {
            // 输出响应状态码和内容
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
        })
        .exceptionally(e -> {
            // 处理异常
            e.printStackTrace();
            return null;
        });`,
};

export const BeanShellJSR233ScriptExampleMap = {
  preOperation: BeanShellScriptExampleMap.preOperation,
  postOperation: BeanShellScriptExampleMap.postOperation,
  assertion: `// 获取响应状态码
String responseCode = prev.getResponseCode();
// 设置期望的状态码
String expectedCode = "200";
// 断言失败条件
if (!responseCode.equals(expectedCode)) {
    AssertionResult.setFailure(true);
    AssertionResult.setFailureMessage("Expected response code: " + expectedCode + ", but got: " + responseCode);
}`,
  scenario: BeanShellScriptExampleMap.scenario,
};

export interface ScriptExampleMap {
  [key: string]: {
    preOperation: string;
    postOperation: string;
    assertion: string;
    scenario: string;
  };
}
// 脚本示例
export const scriptExampleMap: ScriptExampleMap = {
  [LanguageEnum.JAVASCRIPT]: {
    preOperation: `// 生成或获取 token
var token = "Bearer MeterSphere 123456";

// 获取当前 HTTP 请求的 Header Manager
var headerManager = sampler.getHeaderManager();

// 如果 Header Manager 不存在，创建一个新的
if (headerManager == null) {
    headerManager = new org.apache.jmeter.protocol.http.control.HeaderManager();
    sampler.setHeaderManager(headerManager);
}

// 设置 Authorization 头
headerManager.add(new org.apache.jmeter.protocol.http.control.Header("Authorization", token));

// 将 Header Manager 设置到 HTTP 请求
sampler.setHeaderManager(headerManager);`,
    postOperation: `// 获取 HTTP 请求的响应数据
var responseData = prev.getResponseDataAsString();

// 输出响应数据到控制台
log.info("Response Data: " + responseData);
vars.put("variable_name", "variable_value");

// 你可以进一步处理响应数据
// 例如：保存响应数据到变量
vars.put("responseData", responseData);`,
    assertion: `// 获取响应状态码
var responseCode = prev.getResponseCode();
// 设置期望的状态码
var expectedCode = 200;
// 断言失败条件
if (responseCode != expectedCode) {
    AssertionResult.setFailure(true);
    AssertionResult.setFailureMessage(\`Expected response code: \${expectedCode}, but got: \${responseCode}\`)
}
`,
    scenario: `// 导入必要的 Java 类
var URL = java.net.URL;
var HttpURLConnection = java.net.HttpURLConnection;
var BufferedReader = java.io.BufferedReader;
var InputStreamReader = java.io.InputStreamReader;
var StringBuilder = java.lang.StringBuilder;

// 定义请求 URL
var urlString = "http://www.baidu.com";

// 创建 URL 对象
var url = new URL(urlString);

// 打开连接
var connection = url.openConnection();
connection.setRequestMethod("GET");

// 处理响应
var response = new StringBuilder();
var reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
var line;

// 读取响应内容
while ((line = reader.readLine()) != null) {
    response.append(line);
}

reader.close();

// 记录响应
log.info("Response: " + response.toString());

// 断开连接
connection.disconnect();`,
  },
  [LanguageEnum.PYTHON]: {
    preOperation: `import urllib.request

# 授权 token
token = "MeterSphere 123456"

# 设置请求头
headers = {
    "Authorization": f"Bearer {token}"
}

# 请求的 URL
url = "http://www.baidu.com/"

# 创建请求对象
request = urllib.request.Request(url, headers=headers)

# 发送请求并读取响应
with urllib.request.urlopen(request) as response:
    # 打印响应状态码
    status_code = response.getcode()
    log.info(f"Status Code: {status_code}")`,
    postOperation: `# 获取 HTTP 请求的响应数据
responseData = prev.getResponseDataAsString()

# 输出响应数据到控制台
log.info("Response Data: " + responseData)
vars.put("variable_name", "variable_value")

# 你可以进一步处理响应数据
# 例如：保存响应数据到变量
vars.put("responseData", responseData)`,
    assertion: `# 获取响应状态码
response_code = prev.getResponseCode()

# 设置期望的状态码
expected_code = '200'

# 断言失败条件
if response_code != expected_code:
    AssertionResult.setFailure(True)
    AssertionResult.setFailureMessage("Expected response code: {}, but got: {}".format(expected_code, response_code))`,
    scenario: `import urllib.request

# 授权 token
token = "MeterSphere 123456"

# 设置请求头
headers = {
    "Authorization": f"Bearer {token}"
}

# 请求的 URL
url = "http://www.baidu.com/"

# 创建请求对象
request = urllib.request.Request(url, headers=headers)

# 发送请求并读取响应
with urllib.request.urlopen(request) as response:
    # 打印响应状态码
    status_code = response.getcode()
    log.info(f"Status Code: {status_code}")`,
  },
  [LanguageEnum.BEANSHELL]: BeanShellScriptExampleMap,
  [LanguageEnum.BEANSHELL_JSR233]: BeanShellJSR233ScriptExampleMap,
  [LanguageEnum.GROOVY]: {
    preOperation: `// 生成或获取 token
def token = "Bearer " + "MeterSphere 123456"

// 获取 HTTP Header Manager 组件
def headerManager = ctx.getCurrentSampler().getHeaderManager()

// 添加或更新请求头
if (headerManager == null) {
    headerManager = new org.apache.jmeter.protocol.http.control.HeaderManager()
    ctx.getCurrentSampler().setHeaderManager(headerManager)
}
headerManager.add(new org.apache.jmeter.protocol.http.control.Header("Authorization", token))`,
    postOperation: `// 获取 HTTP 请求的响应数据
def responseData = prev.getResponseDataAsString();

// 输出响应数据到控制台
log.info("Response Data: " + responseData);
vars.put("variable_name", "variable_value");

// 你可以进一步处理响应数据
// 例如：保存响应数据到变量
vars.put("responseData", responseData);`,
    assertion: `// 获取响应状态码
def responseCode = prev.getResponseCode()
// 设置期望的状态码
def expectedCode = "200"
// 断言失败条件
if (responseCode != expectedCode) {
    AssertionResult.setFailure(true)
    AssertionResult.setFailureMessage("Expected response code: \${expectedCode}, but got: \${responseCode}")
}`,
    scenario: `import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.entity.StringEntity

// 创建 HTTP 客户端
CloseableHttpClient httpClient = HttpClients.createDefault()

// 创建 HTTP POST 请求
HttpPost postRequest = new HttpPost("http://www.baidu.com")

// 设置请求体
postRequest.setEntity(new StringEntity("your request body"))
CloseableHttpResponse response = httpClient.execute(postRequest)
def responseBody = response.getEntity().getContent().getText()
log.info("Response: " + responseBody)

// 关闭响应和客户端
response.close()
httpClient.close()

// 输出内容
log.info("response："+ responseBody)`,
  },
};
