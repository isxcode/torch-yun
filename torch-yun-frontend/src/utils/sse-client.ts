/**
 * SSE (Server-Sent Events) 客户端工具类
 * 用于处理流式聊天响应
 */
export interface SSEOptions {
    url: string
    data?: any
    headers?: Record<string, string>
    onStart?: (data: any, event: MessageEvent) => void
    onChat?: (data: any, event: MessageEvent) => void
    onEnd?: () => void
    onError?: (error: any) => void
    timeout?: number
    retryAttempts?: number
    retryDelay?: number
}

export interface SSEEventType {
    type: 'start' | 'chat' | 'end' | 'error'
    data: any
}

export class SSEClient {
    private eventSource: EventSource | null = null
    private options: SSEOptions
    private retryCount = 0
    private isConnected = false
    private abortController: AbortController | null = null

    constructor(options: SSEOptions) {
        this.options = {
            timeout: 30 * 60 * 1000, // 30分钟默认超时
            retryAttempts: 3,
            retryDelay: 1000,
            ...options
        }
    }

    /**
     * 开始 SSE 连接
     */
    async connect(): Promise<void> {
        try {
            // 如果已经连接，先断开
            if (this.isConnected) {
                this.disconnect()
            }

            // 创建 AbortController 用于取消请求
            this.abortController = new AbortController()

            this.isConnected = true
            this.retryCount = 0

            // 直接处理流式响应（不使用 EventSource）
            await this.buildSSEUrl()

            // 设置超时
            if (this.options.timeout) {
                setTimeout(() => {
                    if (this.isConnected) {
                        this.disconnect()
                        this.options.onError?.('连接超时')
                    }
                }, this.options.timeout)
            }
        } catch (error) {
            console.error('SSE 连接失败:', error)
            this.handleError(error)
        }
    }

    /**
     * 构建 SSE URL（通过 POST 请求发送数据，然后连接 SSE）
     */
    private async buildSSEUrl(): Promise<string> {
        if (!this.options.data) {
            return this.options.url
        }

        try {
            // 发送 POST 请求启动 SSE 流
            const response = await fetch(this.options.url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Accept: 'text/event-stream',
                    'Cache-Control': 'no-cache',
                    ...this.options.headers
                },
                body: JSON.stringify(this.options.data),
                signal: this.abortController?.signal
            })

            if (!response.ok) {
                const errorText = await response.text()
                console.error('SSE 请求失败:', response.status, response.statusText, errorText)
                throw new Error(`HTTP ${response.status}: ${response.statusText} - ${errorText}`)
            }

            // 对于 SSE，我们需要直接处理 response 的流
            await this.handleStreamResponse(response)

            // 返回空字符串，因为我们直接处理了响应流
            return ''
        } catch (error) {
            console.error('SSE 连接建立失败:', error)
            throw error
        }
    }

    /**
     * 处理流式响应
     */
    private async handleStreamResponse(response: Response): Promise<void> {
        const reader = response.body?.getReader()
        const decoder = new TextDecoder()

        if (!reader) {
            throw new Error('无法获取响应流')
        }

        try {
            while (this.isConnected) {
                const { done, value } = await reader.read()

                if (done) {
                    console.log('SSE 流读取完成')
                    this.options.onEnd?.()
                    break
                }

                // 解码数据
                const chunk = decoder.decode(value, { stream: true })

                // 处理 SSE 数据格式
                this.parseSSEData(chunk)
            }
        } catch (error) {
            console.error('SSE 流读取错误:', error)
            if (error.name !== 'AbortError') {
                this.handleError(error)
            }
        } finally {
            reader.releaseLock()
            this.disconnect()
        }
    }

    /**
     * 解析 SSE 数据格式
     */
    private parseSSEData(chunk: string): void {

        console.log('SSE 接受数据:', chunk)

        let eventType = ''
        let eventData = ''
        let hasSSEFormat = false

        if (chunk.startsWith('event:')) {
            eventType = chunk.substring(6).trim()
            hasSSEFormat = true
            console.log('SSE 事件类型:', eventType)
        } else if (chunk.startsWith('data:')) {
            eventData = chunk.substring(5).trim()
            hasSSEFormat = true
            console.log('SSE 事件数据:', eventData)
        } else if (chunk === '' && eventType && eventData) {
            // 处理完整的事件
            console.log('处理 SSE 事件:', eventType, eventData)
            this.handleSSEEvent(eventType, eventData)
            eventType = ''
            eventData = ''
        }
    }

    /**
     * 处理 SSE 事件
     */
    private handleSSEEvent(eventType: string, eventData: string): void {
        try {
            let data: any = null

            switch (eventType) {
                case 'start':
                    this.options.onStart?.(eventData, { data: eventData } as MessageEvent)
                    break
                case 'chat':
                    const deltaData = {
                        chatContent: {
                            content: eventData
                        }
                    }
                    this.options.onChat?.(deltaData, { data: eventData } as MessageEvent)
                    break
                case 'end':
                    // 只触发完成回调，不显示 complete 事件的数据内容
                    this.options.onEnd?.()
                    this.disconnect()
                    break
                case 'error':
                    this.handleError(data || eventData)
                    break
                default:
                    console.warn('未知的 SSE 事件类型:', eventType, '数据:', eventData)
                    if (eventData && eventData.trim()) {
                        const messageData = {
                            chatContent: {
                                content: eventData
                            }
                        }
                        this.options.onChat?.(messageData, { data: eventData } as MessageEvent)
                    }
            }
        } catch (error) {
            console.error('处理 SSE 事件失败:', error, '事件类型:', eventType, '数据:', eventData)
            this.handleError(error)
        }
    }

    /**
     * 处理错误
     */
    private handleError(error: any): void {
        console.error('SSE 错误:', error)

        // 格式化错误消息
        let errorMessage = '聊天连接失败，请重试'
        if (typeof error === 'string') {
            errorMessage = error
        } else if (error?.message) {
            errorMessage = error.message
        } else if (error?.toString) {
            errorMessage = error.toString()
        }

        this.options.onError?.(errorMessage)
        this.disconnect()
    }

    /**
     * 断开 SSE 连接
     */
    disconnect(): void {
        if (this.eventSource) {
            this.eventSource.close()
            this.eventSource = null
        }

        if (this.abortController) {
            this.abortController.abort()
            this.abortController = null
        }

        this.isConnected = false
    }
}

/**
 * 创建 SSE 客户端的便捷函数
 */
export function createSSEClient(options: SSEOptions): SSEClient {
    return new SSEClient(options)
}
