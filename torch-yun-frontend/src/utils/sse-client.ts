/**
 * SSE (Server-Sent Events) 客户端工具类
 * 用于处理流式聊天响应
 */

export interface SSEOptions {
  url: string
  data?: any
  headers?: Record<string, string>
  onMessage?: (data: any, event: MessageEvent) => void
  onStart?: (data: any, event: MessageEvent) => void
  onComplete?: () => void
  onError?: (error: any) => void
  timeout?: number
  retryAttempts?: number
  retryDelay?: number
}

export interface SSEEventData {
  type: 'start' | 'message' | 'complete' | 'error'
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
    console.log('开始建立 SSE 连接...')
    console.log('请求 URL:', this.options.url)
    console.log('请求数据:', this.options.data)
    console.log('请求头:', this.options.headers)

    if (!this.options.data) {
      return this.options.url
    }

    try {
      // 发送 POST 请求启动 SSE 流
      const response = await fetch(this.options.url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'text/event-stream',
          'Cache-Control': 'no-cache',
          ...this.options.headers
        },
        body: JSON.stringify(this.options.data),
        signal: this.abortController?.signal
      })

      console.log('SSE 请求响应状态:', response.status, response.statusText)
      console.log('响应头:', Object.fromEntries(response.headers.entries()))

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

    console.log('开始处理 SSE 流式响应...')

    try {
      while (this.isConnected) {
        const { done, value } = await reader.read()

        if (done) {
          console.log('SSE 流读取完成')
          this.options.onComplete?.()
          break
        }

        // 解码数据
        const chunk = decoder.decode(value, { stream: true })
        console.log('收到 SSE 数据块:', chunk)

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
    console.log('解析 SSE 数据块:', JSON.stringify(chunk))

    const lines = chunk.split('\n')
    let eventType = ''
    let eventData = ''
    let hasSSEFormat = false

    for (const line of lines) {
      const trimmedLine = line.trim()

      if (trimmedLine.startsWith('event:')) {
        eventType = trimmedLine.substring(6).trim()
        hasSSEFormat = true
        console.log('SSE 事件类型:', eventType)
      } else if (trimmedLine.startsWith('data:')) {
        eventData = trimmedLine.substring(5).trim()
        hasSSEFormat = true
        console.log('SSE 事件数据:', eventData)
      } else if (trimmedLine === '' && eventType && eventData) {
        // 处理完整的事件
        console.log('处理 SSE 事件:', eventType, eventData)
        this.handleSSEEvent(eventType, eventData)
        eventType = ''
        eventData = ''
      }
    }

    // 如果没有检测到标准 SSE 格式，将整个 chunk 当作消息内容处理
    if (!hasSSEFormat && chunk.trim()) {
      console.log('检测到非标准 SSE 格式，当作消息内容处理:', chunk)
      this.handleSSEEvent('message', chunk.trim())
      return
    }

    // 处理可能没有空行结尾的事件
    if (eventType && eventData) {
      console.log('处理未完成的 SSE 事件:', eventType, eventData)
      this.handleSSEEvent(eventType, eventData)
    }
  }

  /**
   * 处理 SSE 事件
   */
  private handleSSEEvent(eventType: string, eventData: string): void {
    try {
      let data: any = null

      // 尝试解析 JSON，如果失败则使用原始字符串
      if (eventData) {
        try {
          data = JSON.parse(eventData)
        } catch (jsonError) {
          console.log('数据不是 JSON 格式，使用原始文本:', eventData)
          // 对于非 JSON 数据，根据事件类型处理
          if (eventType === 'message') {
            // 对于 message 事件，将文本包装成标准格式
            data = {
              chatContent: {
                content: eventData
              }
            }
          } else {
            data = eventData
          }
        }
      }

      switch (eventType) {
        case 'start':
          this.options.onStart?.(data, { data: eventData } as MessageEvent)
          break
        case 'message':
          this.options.onMessage?.(data, { data: eventData } as MessageEvent)
          break
        case 'complete':
          this.options.onComplete?.()
          this.disconnect()
          break
        case 'error':
          this.handleError(data || eventData)
          break
        default:
          console.warn('未知的 SSE 事件类型:', eventType, '数据:', eventData)
          // 对于未知事件类型，如果有数据内容，当作 message 处理
          if (eventData && eventData.trim()) {
            const messageData = {
              chatContent: {
                content: eventData
              }
            }
            this.options.onMessage?.(messageData, { data: eventData } as MessageEvent)
          }
      }
    } catch (error) {
      console.error('处理 SSE 事件失败:', error, '事件类型:', eventType, '数据:', eventData)
      this.handleError(error)
    }
  }

  /**
   * 设置 EventSource 事件监听器（备用方案）
   */
  private setupEventListeners(): void {
    if (!this.eventSource) return

    this.eventSource.onopen = () => {
      console.log('SSE 连接已建立')
      this.retryCount = 0
    }

    this.eventSource.onerror = (error) => {
      console.error('SSE 连接错误:', error)
      this.handleConnectionError()
    }

    // 监听自定义事件
    this.eventSource.addEventListener('start', (event) => {
      try {
        const data = JSON.parse(event.data)
        this.options.onStart?.(data, event)
      } catch (error) {
        console.error('解析 start 事件数据失败:', error)
      }
    })

    this.eventSource.addEventListener('message', (event) => {
      try {
        const data = JSON.parse(event.data)
        this.options.onMessage?.(data, event)
      } catch (error) {
        console.error('解析 message 事件数据失败:', error)
      }
    })

    this.eventSource.addEventListener('complete', () => {
      this.options.onComplete?.()
      this.disconnect()
    })

    this.eventSource.addEventListener('error', (event) => {
      try {
        const data = JSON.parse(event.data)
        this.handleError(data)
      } catch (error) {
        this.handleError(event.data || '未知错误')
      }
    })
  }

  /**
   * 处理连接错误和重连
   */
  private handleConnectionError(): void {
    if (this.retryCount < (this.options.retryAttempts || 3)) {
      this.retryCount++
      console.log(`SSE 连接重试 ${this.retryCount}/${this.options.retryAttempts}`)
      
      setTimeout(() => {
        this.connect()
      }, this.options.retryDelay || 1000)
    } else {
      this.handleError('SSE 连接失败，已达到最大重试次数')
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

  /**
   * 检查连接状态
   */
  isConnectionActive(): boolean {
    return this.isConnected && this.eventSource?.readyState === EventSource.OPEN
  }
}

/**
 * 创建 SSE 客户端的便捷函数
 */
export function createSSEClient(options: SSEOptions): SSEClient {
  return new SSEClient(options)
}
