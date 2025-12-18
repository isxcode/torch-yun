import { http } from '@/utils/http'

export interface QueryParams {
    page: number
    pageSize: number
    searchKeyWord?: string
}

export function QueryModelPlazaList(params: QueryParams): Promise<any> {
    return http.request({
        method: 'post',
        url: '/model/plaza/pageModel',
        params: params
    })
}

