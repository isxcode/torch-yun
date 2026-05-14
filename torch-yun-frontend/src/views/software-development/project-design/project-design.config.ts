export const BreadCrumbList = [{ name: '项目设计', code: 'project-assets' }]

export const TableConfig: any = {
  tableData: [],
  colConfigs: [
    { prop: 'name', title: '名称', minWidth: 180, customSlot: 'nameSlot', showOverflowTooltip: true },
    { prop: 'remark', title: '备注', minWidth: 220, showOverflowTooltip: true },
    { prop: 'createByUsername', title: '创建人', minWidth: 140, showOverflowTooltip: true },
    { prop: 'createDateTime', title: '创建时间', minWidth: 160, showOverflowTooltip: true },
    { title: '操作', align: 'center', customSlot: 'options', width: 90, fixed: 'right' }
  ],
  pagination: { currentPage: 1, pageSize: 10, total: 0 },
  seqType: 'seq',
  loading: false
}
