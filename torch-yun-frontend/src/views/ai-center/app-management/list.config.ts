export interface BreadCrumb {
    name: string;
    code: string;
    hidden?: boolean;
}

export interface colConfig {
    prop?: string;
    title: string;
    align?: string;
    showOverflowTooltip?: boolean;
    customSlot?: string;
    width?: number;
    minWidth?: number;
    formatter?: any
    fixed?: string;
}

export interface Pagination {
    currentPage: number;
    pageSize: number;
    total: number;
}

export interface TableConfig {
    tableData: Array<any>;
    colConfigs: Array<colConfig>;
    seqType: string;
    pagination?: Pagination; // 分页数据
    loading?: boolean; // 表格loading
}

export const BreadCrumbList: Array<BreadCrumb> = [
    {
        name: '应用管理',
        code: 'app-management'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '名称',
        minWidth: 140,
        customSlot: 'nameSlot',
        showOverflowTooltip: true
    },
    {
        prop: 'logoId',
        title: '头像',
        minWidth: 100,
        customSlot: 'logo',
        showOverflowTooltip: true
    },
    {
        prop: 'aiName',
        title: '智能体',
        minWidth: 130,
        showOverflowTooltip: true
    },
    {
        prop: 'appType',
        title: '应用类型',
        minWidth: 130,
        showOverflowTooltip: true,
        formatter: (data: any): string => {
            const options = {
                TEXT_APP: '普通对话应用',
                PYTHON_APP: 'Python应用',
                BASH_APP: '脚本应用',
                NODE_JS_APP: 'NodeJs应用',
                REPORT_APP: '报表应用',
                VIEW_APP: '视图应用',
                LOG_APP: '日志分析应用',
                IMAGE_APP: '股票分析应用',
            }
            return data.row.appType ? options[data.row.appType] : '-'
        }
    },
    {
        prop: 'status',
        title: '状态',
        minWidth: 100,
        customSlot: 'statusTag'
    },
    {
        prop: 'defaultApp',
        title: '默认应用',
        minWidth: 100,
        customSlot: 'defaultAppTag'
    },
    {
        prop: 'createByUsername',
        title: '创建人',
        minWidth: 140
    },
    {
        prop: 'createDateTime',
        title: '创建时间',
        minWidth: 140
    },
    {
        prop: 'remark',
        title: '备注',
        minWidth: 140
    },
    {
        title: '操作',
        align: 'center',
        customSlot: 'options',
        width: 90,
        fixed: 'right'
    }
]

export const TableConfig: TableConfig = {
    tableData: [],
    colConfigs: colConfigs,
    pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
    },
    seqType: 'seq',
    loading: false
}
