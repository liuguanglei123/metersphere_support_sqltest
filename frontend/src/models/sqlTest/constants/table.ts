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

// TODO:目前datatype肯定还需要再继续补充，比如multiset和array
//  展示的时候可以按照默认类型展示，但是比对的时候就比较麻烦了
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
