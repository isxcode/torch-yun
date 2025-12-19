import { http } from '@/utils/http'

export interface QueryParams {
    page: number
    pageSize: number
    searchKeyWord?: string
}

export function QueryModelList(params: QueryParams): Promise<any> {
    return http.request({
        method: 'post',
        url: '/model/pageModel',
        params: params
    })
}

// 新建
export function AddModelData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/model/addModel',
        params: params
    })
}

// 更新
export function UpdateModelData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/model/updateModel',
        params: params
    })
}

// 删除
export function DeleteModelData(params: { id: string }): Promise<any> {
    return http.request({
        method: 'post',
        url: '/model/deleteModel',
        params: params
    })
}
