import { useQuery, useQueryClient } from '@tanstack/react-query'
import { servicesQueries } from '../api/services-queries'
import { usePollingInterval } from './use-polling-interval'

/** A single service, rendered instantly from the cached list (if present) while the detail request runs. */
export function useMonitoredService(id: number) {
  const queryClient = useQueryClient()
  const refetchInterval = usePollingInterval()

  return useQuery({
    ...servicesQueries.detail(id),
    refetchInterval,
    refetchIntervalInBackground: false,
    placeholderData: () => queryClient.getQueryData(servicesQueries.list().queryKey)?.find((s) => s.id === id),
  })
}
