import { http } from '@/utils/http'

export interface QueryParams {
    page: number
    pageSize: number
    searchKeyWord?: string
}

// 查询
export function QueryAiItemList(params: QueryParams): Promise<any> {
    return http.request({
        method: 'post',
        url: '/ai/pageAi',
        params: params
    })
}

// 新建
export function AddAiItemData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/ai/addAi',
        params: params
    })
}

// 更新
export function UpdateAiItemData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/ai/updateAi',
        params: params
    })
}

// 获取智能体日志
export function GetAiItemLogData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/ai/getAiLog',
        params: params
    })
}

// 部署
export function DeployAiItemLogData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/ai/deployAi',
        params: params
    })
}

// 下线
export function StopAiItemLogData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/ai/stopAi',
        params: params
    })
}

// 检测智能体
export function CheckAiItemData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/ai/checkAi',
        params: params
    })
}
