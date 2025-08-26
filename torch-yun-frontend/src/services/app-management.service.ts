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

// 启用应用
export function EnableAppData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/app/enableApp',
        params: params
    })
}

// 禁用应用
export function DisableAppData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/app/disableApp',
        params: params
    })
}

// 设置默认应用
export function SetDefaultAppData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/app/setDefaultApp',
        params: params
    })
}

// 删除应用
export function DeleteAppData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/app/deleteApp',
        params: params
    })
}

