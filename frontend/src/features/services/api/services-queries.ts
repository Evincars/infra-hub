import { queryOptions } from '@tanstack/react-query'
import { servicesApi } from './services-api'

/**
 * Hierarchical query keys: invalidating `serviceKeys.all` refreshes every services query,
 * `serviceKeys.lists()` only the lists, etc.
 */
export const serviceKeys = {
  all: ['services'] as const,
  lists: () => [...serviceKeys.all, 'list'] as const,
  details: () => [...serviceKeys.all, 'detail'] as const,
  detail: (id: number) => [...serviceKeys.details(), id] as const,
}

/**
 * Reusable, fully-typed query definitions. Shared by hooks (useQuery), route loaders
 * (queryClient.prefetchQuery / ensureQueryData) and cache reads (getQueryData).
 */
export const servicesQueries = {
  list: () =>
    queryOptions({
      queryKey: serviceKeys.lists(),
      queryFn: ({ signal }) => servicesApi.list(signal),
    }),

  detail: (id: number) =>
    queryOptions({
      queryKey: serviceKeys.detail(id),
      queryFn: ({ signal }) => servicesApi.get(id, signal),
    }),
}
