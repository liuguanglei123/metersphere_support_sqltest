export enum TableDataType {
  // TODO：还有非常多的数据类型这里无法适配，需要后期支持
  BOOLEAN = 'BOOLEAN',
  NUMERIC = 'NUMERIC',
  STRING = 'STRING',
  DATETIME = 'DATETIME',
  // 暂时不适配
  BINARY = 'BINARY',
  CONTENT = 'CONTENT',
  STRUCT = 'STRUCT',
  DOCUMENT = 'DOCUMENT',
  ARRAY = 'ARRAY',
  OBJECT = 'OBJECT',
  REFERENCE = 'REFERENCE',
  ROWID = 'ROWID',
  ANY = 'ANY',
  UNKNOWN = 'UNKNOWN',
  CHAT2DB_ROW_NUMBER = 'CHAT2DB_ROW_NUMBER',
}

export enum StatusType {
  SUCCESS = 'success',
  FAIL = 'fail',
}

export type DataType =
  | 'BOOLEAN'
  | 'NUMERIC'
  | 'STRING'
  | 'DATETIME'
  | 'BINARY'
  | 'CONTENT'
  | 'STRUCT'
  | 'DOCUMENT'
  | 'ARRAY'
  | 'OBJECT'
  | 'REFERENCE'
  | 'ROWID'
  | 'ANY'
  | 'UNKNOWN'
  | 'CHAT2DB_ROW_NUMBER';
