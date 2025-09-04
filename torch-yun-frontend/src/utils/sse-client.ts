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
          console.log('Server requested retry interval:', retryInterval)
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
        while (isConnected) {
          const { done, value } = await reader.read()
          
          if (done) {
            break
          }

          const chunk = decoder.decode(value, { stream: true })
          parser.feed(chunk)
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
        console.log(`SSE connection failed, retrying... (${retryCount}/${retryAttempts})`)
        
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
