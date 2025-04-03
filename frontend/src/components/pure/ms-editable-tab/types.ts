export interface TabItem {
  id: string | number;
  label?: string;
  closable?: boolean;
  unSaved?: boolean; // 未保存
  draggable?: boolean;
  [key: string]: any;
}

export interface SqlTabItem {
  id: string | number;
  label?: string;
  closable?: boolean;
  unSaved?: boolean; // 未保存
  draggable?: boolean;
  [key: string]: any;
}
