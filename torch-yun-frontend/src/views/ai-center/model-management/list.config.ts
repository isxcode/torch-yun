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
        name: '模型仓库',
        code: 'model-management'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'name',
        title: '名称',
        minWidth: 140,
        showOverflowTooltip: true
    },
    {
        prop: 'code',
        title: '编码',
        minWidth: 130,
        showOverflowTooltip: true
    },
    {
        prop: 'modelLabel',
        title: '标签',
        minWidth: 80,
        showOverflowTooltip: true
    },
    {
        prop: 'modelType',
        title: '类型',
        minWidth: 100,
        formatter: (data: any): string => {
            const modelType = data.row.modelType;
            switch (modelType) {
                case 'API':
                    return '远程模型';
                case 'MANUAL':
                    return '本地模型';
                case 'LOCAL':
                    return '本地模型';
                case 'BUILD':
                    return '构建模型';
                default:
                    return modelType || '';
            }
        }
    },
    {
        prop: 'modelFileName',
        title: '模型文件',
        minWidth: 120,
        showOverflowTooltip: true
    },
    {
        prop: 'status',
        title: '状态',
        minWidth: 100,
        customSlot: 'statusTag'
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
