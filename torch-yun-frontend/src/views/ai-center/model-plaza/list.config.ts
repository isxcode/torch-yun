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
    pagination?: Pagination;
    loading?: boolean;
}

export const BreadCrumbList: Array<BreadCrumb> = [
    {
        name: '模型广场',
        code: 'model-plaza'
    }
]

export const colConfigs: colConfig[] = [
    {
        prop: 'orgName',
        title: '组织名称',
        minWidth: 80,
        showOverflowTooltip: true
    },
    {
        prop: 'modelName',
        title: '模型名称',
        minWidth: 80,
        showOverflowTooltip: true
    },
    {
        prop: 'label',
        title: '分类',
        minWidth: 80,
        showOverflowTooltip: true,
        formatter: (data: any): string => {
            const label = data.row.label;
            switch (label) {
                case 'Natural Language Processing':
                    return '自然语言处理';
                default:
                    return label || '';
            }
        }
    },
    {
        prop: 'modelType',
        title: '类型',
        minWidth: 80,
        showOverflowTooltip: true,
        formatter: (data: any): string => {
            const modelType = data.row.modelType;
            switch (modelType) {
                case 'Text Generation':
                    return '文本生成';
                default:
                    return modelType || '';
            }
        }
    },
    {
        prop: 'modelParam',
        title: '参数',
        minWidth: 50,
        showOverflowTooltip: true
    },
    {
        prop: 'isOnline',
        title: '状态',
        minWidth: 20,
        customSlot: 'statusTag'
    },
    {
        prop: 'remark',
        title: '备注',
        minWidth: 240,
        showOverflowTooltip: true
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

