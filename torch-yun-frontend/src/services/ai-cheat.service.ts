import { http } from '@/utils/http'
import { createSSEClient, SSEClient, SSEOptions } from '@/utils/sse-client'
import { useAuthStore } from '@/store/useAuth'

// 发送对话 - SSE 流式版本
export function SendMessageToAiStream(params: any, callbacks: {
    onStart?: (data: any) => void
    onMessage?: (data: any) => void
    onComplete?: () => void
    onError?: (error: any) => void
}): SSEClient {
    // 获取认证信息
    const authStore = useAuthStore()

    const sseOptions: SSEOptions = {
        url: `${import.meta.env.VITE_VUE_APP_BASE_DOMAIN}/chat/sendChat`,
        data: params,
        headers: {
            authorization: authStore.token || '',
            tenant: authStore.tenantId || '',
            'Content-Type': 'application/json'
        },
        onStart: callbacks.onStart,
        onMessage: callbacks.onMessage,
        onComplete: callbacks.onComplete,
        onError: callbacks.onError,
        timeout: 30 * 60 * 1000, // 30分钟超时
        retryAttempts: 3,
        retryDelay: 2000
    }

    const sseClient = createSSEClient(sseOptions)
    sseClient.connect()
    return sseClient
}

// 获取最大对话
export function GetMaxChatData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/chat/getMaxChatId',
        params: params
    })
}

// 接收对话
export function GetChatDetailData(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/chat/getChat',
        params: params
    })
}

// 获取对话记录
export function GetChatDetailList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/chat/getFullChat',
        params: params
    })
}

// 获取历史对话
export function GetHistoryChatList(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/chat/pageChatHistory',
        params: params
    })
}

// 停止思考
export function StopChatThink(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/chat/stopChat',
        params: params
    })
}
