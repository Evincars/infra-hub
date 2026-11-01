import { useQuery } from '@tanstack/react-query'
import { servicesQueries } from '../api/services-queries'
import type { MonitoredService, ServiceStatus } from '../types'
import { usePollingInterval } from './use-polling-interval'

/**
 * All monitored services, auto-refreshed on the interval chosen in the UI store.
 *
 * - Polling pauses while the tab is hidden (`refetchIntervalInBackground: false`) and resumes
 *   with an immediate refetch on focus (`refetchOnWindowFocus`, set globally).
 * - If a poll fails, `data` keeps the last good list and `error` is set, so the UI can show
 *   "stale" instead of blanking the dashboard.
 * - Multiple components calling this share one cache entry and one polling timer.
 */
export function useMonitoredServices() {
  const refetchInterval = usePollingInterval()

  return useQuery({
    ...servicesQueries.list(),
    refetchInterval,
    refetchIntervalInBackground: false,
  })
}

export type StatusSummary = Record<ServiceStatus, number> & { total: number }

function summarize(services: MonitoredService[]): StatusSummary {
  const summary: StatusSummary = { UP: 0, DOWN: 0, UNKNOWN: 0, total: services.length }
  for (const service of services) summary[service.lastStatus]++
  return summary
}

/**
 * Derived view over the same cached list via `select`: no extra request, and the component
 * only re-renders when the counts change, not on every poll.
 */
export function useServiceStatusSummary() {
  const refetchInterval = usePollingInterval()

  return useQuery({
    ...servicesQueries.list(),
    refetchInterval,
    refetchIntervalInBackground: false,
    select: summarize,
  })
}
