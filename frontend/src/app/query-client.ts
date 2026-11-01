import { QueryClient } from '@tanstack/react-query'
import { ApiError } from '@/lib/api-client'

export function createQueryClient() {
  return new QueryClient({
    defaultOptions: {
      queries: {
        // Data is considered fresh briefly so navigation/remounts don't refetch immediately;
        // polling hooks set their own refetchInterval.
        staleTime: 5_000,
        // 4xx won't fix itself on retry; network errors and 5xx get two more attempts.
        retry: (failureCount, error) => {
          if (error instanceof ApiError && error.isClientError) return false
          return failureCount < 2
        },
        refetchOnWindowFocus: true,
      },
      mutations: {
        retry: false,
      },
    },
  })
}
