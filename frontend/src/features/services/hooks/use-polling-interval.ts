import type { QueryStatus } from '@tanstack/react-query'
import { useAutoRefresh, useRefreshIntervalMs } from '@/stores/ui-store'

/** While the API is failing, poll no more often than this, to avoid hammering a struggling backend. */
const ERROR_BACKOFF_MS = 30_000

/**
 * Turns the user's auto-refresh preference (Zustand) into a React Query `refetchInterval`.
 * Returns a function so the interval adapts to the query's current state on every tick.
 */
export function usePollingInterval() {
  const autoRefresh = useAutoRefresh()
  const intervalMs = useRefreshIntervalMs()

  if (!autoRefresh) return false as const

  // Structural param type so it fits any useQuery<TData, TKey> without generics plumbing.
  return (query: { state: { status: QueryStatus } }) =>
    query.state.status === 'error' ? Math.max(intervalMs, ERROR_BACKOFF_MS) : intervalMs
}
