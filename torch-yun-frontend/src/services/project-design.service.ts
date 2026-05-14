import { http } from '@/utils/http'

export interface QueryParams {
  page: number
  pageSize: number
  searchKeyWord?: string
  projectId: string
}

export function QueryProjectDesignList(params: QueryParams): Promise<any> {
  return http.request({ method: 'post', url: '/project-design/pageProjectDesign', params })
}

export function AddProjectDesignData(params: any): Promise<any> {
  return http.request({ method: 'post', url: '/project-design/addProjectDesign', params })
}

export function UpdateProjectDesignData(params: any): Promise<any> {
  return http.request({ method: 'post', url: '/project-design/updateProjectDesign', params })
}

export function DeleteProjectDesignData(params: any): Promise<any> {
  return http.request({ method: 'post', url: '/project-design/deleteProjectDesign', params })
}
