type StreamHandlers = {
  onMessage: (chunk: string) => void
  onDone?: () => void
}

function parseEventBlock(block: string) {
  const lines = block.split('\n')
  let event = 'message'
  const dataLines: string[] = []
  for (const line of lines) {
    if (line.startsWith('event:')) {
      event = line.slice(6).trim()
    } else if (line.startsWith('data:')) {
      dataLines.push(line.slice(5).trim())
    }
  }
  return {
    event,
    data: dataLines.join('\n'),
  }
}

export async function streamAppChat(
  appId: number | string,
  userMessage: string,
  handlers: StreamHandlers,
) {
  const url = new URL(`http://localhost:8123/api/app/chat/${appId}`)
  url.searchParams.set('userMessage', userMessage)

  const response = await fetch(url.toString(), {
    method: 'GET',
    credentials: 'include',
    headers: {
      Accept: 'text/event-stream',
    },
  })

  if (!response.ok || !response.body) {
    throw new Error(`流式请求失败: ${response.status}`)
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  while (true) {
    const { done, value } = await reader.read()
    if (done) {
      break
    }
    buffer += decoder.decode(value, { stream: true })
    const parts = buffer.split('\n\n')
    buffer = parts.pop() ?? ''

    for (const part of parts) {
      const payload = parseEventBlock(part.trim())
      if (!payload.data) {
        continue
      }
      if (payload.event === 'done') {
        handlers.onDone?.()
        continue
      }
      try {
        const parsed = JSON.parse(payload.data) as { v?: string }
        if (parsed.v) {
          handlers.onMessage(parsed.v)
        }
      } catch {
        handlers.onMessage(payload.data)
      }
    }
  }

  if (buffer.trim()) {
    const payload = parseEventBlock(buffer.trim())
    if (payload.event === 'done') {
      handlers.onDone?.()
      return
    }
    try {
      const parsed = JSON.parse(payload.data) as { v?: string }
      if (parsed.v) {
        handlers.onMessage(parsed.v)
      }
    } catch {
      if (payload.data) {
        handlers.onMessage(payload.data)
      }
    }
  }
}
