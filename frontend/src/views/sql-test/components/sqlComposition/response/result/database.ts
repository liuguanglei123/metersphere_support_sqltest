import { DataType } from "@/models/sqlTest/constants/table";

export interface ITableHeaderItem {
  dataType: DataType;
  name: string;
  autoIncrement: boolean | null; // 是否自增
  columnSize: number | null; // 字段长度
  comment: string | null; // 字段注释
  decimalDigits: number | null; // 小数位
  defaultValue: string | null; // 默认值
  nullable: boolean | null; // 是否为空
  primaryKey: boolean | null; // 是否为主键
}

export interface IManageResultData {
  dataList: string[][];
  headerList: ITableHeaderItem[];
  description: string;
  message: string;
  sql: string;
  originalSql: string;
  success: boolean;
  uuid?: string;
  duration: number;
  fuzzyTotal: string;
  hasNextPage: boolean;
  sqlType: 'SELECT' | 'UNKNOWN';
  updateCount?: number; // 如果是修改的话。后端会返回修改的条数
  canEdit?: boolean; // 返回的数据是否可以编辑
  tableName?: string; // 如果可以编辑的话。后端会返回表名称。修改需要给后端传递表名
}
