export interface BreadCrumb { name: string; code: string; hidden?: boolean }
export interface colConfig { prop?: string; title: string; align?: string; showOverflowTooltip?: boolean; customSlot?: string; width?: number; minWidth?: number; formatter?: any; fixed?: string }
export interface Pagination { currentPage: number; pageSize: number; total: number }
export interface TableConfig { tableData: Array<any>; colConfigs: Array<colConfig>; seqType: string; pagination?: Pagination; loading?: boolean }

export const BreadCrumbList: Array<BreadCrumb> = [{ name: '项目管理', code: 'project-management' }]

export const colConfigs: colConfig[] = [
  { prop: 'name', title: '项目名', minWidth: 140, showOverflowTooltip: true },
  { prop: 'workspace', title: '工作空间', minWidth: 160, showOverflowTooltip: true },
  { prop: 'assetsDir', title: '资产目录', minWidth: 120, showOverflowTooltip: true },
  { prop: 'designAppName', title: '项目设计', minWidth: 140, showOverflowTooltip: true },
  { prop: 'planAppName', title: '项目计划', minWidth: 140, showOverflowTooltip: true },
  { prop: 'developAppName', title: '项目开发', minWidth: 140, showOverflowTooltip: true },
  { prop: 'createByUsername', title: '创建人', minWidth: 120, showOverflowTooltip: true },
  { prop: 'createDateTime', title: '创建时间', minWidth: 140, showOverflowTooltip: true },
  { prop: 'remark', title: '备注', minWidth: 140, showOverflowTooltip: true },
  { title: '操作', align: 'center', customSlot: 'options', width: 90, fixed: 'right' }
]

export const TableConfig: TableConfig = {
  tableData: [],
  colConfigs,
  pagination: { currentPage: 1, pageSize: 10, total: 0 },
  seqType: 'seq',
  loading: false
}
