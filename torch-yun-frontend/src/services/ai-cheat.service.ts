import { http } from '@/utils/http'

// 发送对话
export function SendMessageToAi(params: any): Promise<any> {
    return http.request({
        method: 'post',
        url: '/chat/sendChatStream',
        params: params
    })
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
