import { createParser, type EventSourceMessage } from 'eventsource-parser'

export interface SSEOptions {
  url: string
  data?: any
  headers?: Record<string, string>
  onStart?: (data: any) => void
  onChat?: (data: any) => void
  onEnd?: () => void
  onError?: (error: any) => void
  timeout?: number
  retryAttempts?: number
  retryDelay?: number
}

export interface SSEClient {
  connect(): void
  disconnect(): void
  isConnected(): boolean
}

export function createSSEClient(options: SSEOptions): SSEClient {
  let abortController: AbortController | null = null
  let isConnected = false
  let retryCount = 0

  const {
    url,
    data,
    headers = {},
    onStart,
    onChat,
    onEnd,
    onError,
    timeout = 30 * 60 * 1000, // 30分钟
    retryAttempts = 3,
    retryDelay = 2000
  } = options

  async function connect() {
    if (isConnected) {
      return
    }

    try {
      abortController = new AbortController()
      isConnected = true

      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'text/event-stream',
          'Cache-Control': 'no-cache',
          ...headers
        },
        body: JSON.stringify(data),
        signal: abortController.signal
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      if (!response.body) {
        throw new Error('Response body is null')
      }

      const parser = createParser({
        onEvent: (event: EventSourceMessage) => {
          const { event: eventType, data: eventData } = event



          try {
            switch (eventType) {
              case 'start':
                onStart?.(eventData)
                break
              case 'chat':
                onChat?.(eventData)
                break
              case 'end':
                onEnd?.()
                disconnect()
                break
              case 'error':
                onError?.(new Error(eventData))
                disconnect()
                break
              default:
                console.warn('Unknown SSE event type:', eventType)
            }
          } catch (error) {
            console.error('Error processing SSE event:', error)
            onError?.(error)
          }
        },
        onRetry: (retryInterval) => {
          // Server requested retry
        }
      })

      const reader = response.body.getReader()
      const decoder = new TextDecoder()

      // 设置超时
      const timeoutId = setTimeout(() => {
        onError?.(new Error('SSE connection timeout'))
        disconnect()
      }, timeout)

      try {
        let rawData = ''
        let buffer = '' // 添加缓冲区来处理不完整的事件

        while (isConnected) {
          const { done, value } = await reader.read()

          if (done) {
            // 处理缓冲区中剩余的数据
            if (buffer.trim()) {
              parser.feed(buffer)
            }
            break
          }

          const chunk = decoder.decode(value, { stream: true })
          rawData += chunk
          buffer += chunk



          // 查找完整的事件（以双换行符结尾）
          let eventEndIndex
          while ((eventEndIndex = buffer.indexOf('\n\n')) !== -1) {
            const completeEvent = buffer.substring(0, eventEndIndex + 2)
            buffer = buffer.substring(eventEndIndex + 2)



            parser.feed(completeEvent)
          }
        }
      } finally {
        clearTimeout(timeoutId)
        reader.releaseLock()
      }

    } catch (error: any) {
      isConnected = false
      
      if (error.name === 'AbortError') {
        // 连接被主动断开，不需要重试
        return
      }

      console.error('SSE connection error:', error)
      
      // 重试逻辑
      if (retryCount < retryAttempts) {
        retryCount++

        setTimeout(() => {
          connect()
        }, retryDelay)
      } else {
        onError?.(error)
      }
    }
  }

  function disconnect() {
    if (abortController) {
      abortController.abort()
      abortController = null
    }
    isConnected = false
    retryCount = 0
  }

  function getIsConnected() {
    return isConnected
  }

  return {
    connect,
    disconnect,
    isConnected: getIsConnected
  }
}
