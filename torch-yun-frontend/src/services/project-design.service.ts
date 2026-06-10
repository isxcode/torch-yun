import { http } from '@/utils/http'
import { createSSEClient, SSEClient, SSEOptions } from '@/utils/sse-client'
import { useAuthStore } from '@/store/useAuth'

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

export function GetProjectDesignMaxChatData(params: any): Promise<any> {
  return http.request({ method: 'post', url: '/project-design/getProjectDesignMaxChatId', params })
}

export function SendProjectDesignMessageStream(params: any, callbacks: {
  onStart?: (data: any) => void
  onChat?: (data: any) => void
  onEnd?: () => void
  onError?: (error: any) => void
}): SSEClient {
  const authStore = useAuthStore()
  const sseOptions: SSEOptions = {
    url: `${import.meta.env.VITE_VUE_APP_BASE_DOMAIN}/project-design/sendProjectDesignChat`,
    data: params,
    headers: {
      authorization: authStore.token || '',
      tenant: authStore.tenantId || '',
      'Content-Type': 'application/json'
    },
    onStart: callbacks.onStart,
    onChat: callbacks.onChat,
    onEnd: callbacks.onEnd,
    onError: callbacks.onError,
    timeout: 30 * 60 * 1000,
    retryAttempts: 3,
    retryDelay: 2000
  }
  const sseClient = createSSEClient(sseOptions)
  sseClient.connect()
  return sseClient
}
