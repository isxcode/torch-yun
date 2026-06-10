import { http } from '@/utils/http'

export interface QueryParams {
    page: number
    pageSize: number
    searchKeyWord?: string
}

export function QueryProjectList(params: QueryParams): Promise<any> {
    return http.request({ method: 'post', url: '/project/pageProject', params })
}

export function AddProjectData(params: any): Promise<any> {
    return http.request({ method: 'post', url: '/project/addProject', params })
}

export function UpdateProjectData(params: any): Promise<any> {
    return http.request({ method: 'post', url: '/project/updateProject', params })
}

export function DeleteProjectData(params: any): Promise<any> {
    return http.request({ method: 'post', url: '/project/deleteProject', params })
}
