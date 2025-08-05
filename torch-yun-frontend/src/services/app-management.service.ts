import { http } from '@/utils/http'

export interface QueryParams {
    page: number
    pageSize: number
    searchKeyWord?: string
}

// 查询
export function QueryAppList(params: QueryParams): Promise<any> {
    return http.request({
        method: 'post',
        url: '/app/pageApp',
        params: params
    })
}

// 新建
export function AddAppData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/app/addApp',
        params: params
    })
}

// 更新
export function UpdateAppData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/app/updateApp',
        params: params
    })
}

// 获取配置
export function GetConfigAppData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/app/getAppConfig',
        params: params
    })
}

// 配置
export function ConfigAppData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/app/configApp',
        params: params
    })
}

