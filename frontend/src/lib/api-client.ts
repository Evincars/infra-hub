import { env } from '@/config/env'

/** RFC 9457 error body, as returned by Spring Boot with `spring.mvc.problemdetails.enabled=true`. */
export interface ProblemDetail {
  type?: string
  title?: string
  status?: number
  detail?: string
  instance?: string
  [extension: string]: unknown
}

export class ApiError extends Error {
  readonly status: number
  readonly problem: ProblemDetail | undefined

  constructor(status: number, problem?: ProblemDetail) {
    super(problem?.detail ?? problem?.title ?? `Request failed with status ${status}`)
    this.name = 'ApiError'
    this.status = status
    this.problem = problem
  }

  get isClientError() {
    return this.status >= 400 && this.status < 500
  }
}

type RequestOptions = Omit<RequestInit, 'body'> & { body?: unknown }

/** Thin fetch wrapper: JSON in/out, typed errors, AbortSignal passthrough (React Query cancels via `signal`). */
export async function apiRequest<T>(path: string, { body, headers, ...init }: RequestOptions = {}): Promise<T> {
  const requestHeaders = new Headers(headers)
  requestHeaders.set('Accept', 'application/json')
  if (body !== undefined) requestHeaders.set('Content-Type', 'application/json')

  const response = await fetch(`${env.apiBaseUrl}${path}`, {
    ...init,
    headers: requestHeaders,
    body: body === undefined ? undefined : JSON.stringify(body),
  })

  if (!response.ok) {
    throw new ApiError(response.status, await readProblem(response))
  }
  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}

async function readProblem(response: Response): Promise<ProblemDetail | undefined> {
  try {
    return (await response.json()) as ProblemDetail
  } catch {
    return undefined // e.g. proxy returned an HTML error page
  }
}

export const api = {
  get: <T>(path: string, init?: RequestOptions) => apiRequest<T>(path, { ...init, method: 'GET' }),
  post: <T>(path: string, body?: unknown, init?: RequestOptions) => apiRequest<T>(path, { ...init, method: 'POST', body }),
  put: <T>(path: string, body?: unknown, init?: RequestOptions) => apiRequest<T>(path, { ...init, method: 'PUT', body }),
  delete: <T = void>(path: string, init?: RequestOptions) => apiRequest<T>(path, { ...init, method: 'DELETE' }),
}
