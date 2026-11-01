import type { QueryClient } from '@tanstack/react-query'
import { createRouter } from '@tanstack/react-router'
import { RouteErrorFallback } from '@/components/layout/route-error-fallback'
import { RouteNotFound } from '@/components/layout/not-found'
import { routeTree } from '@/routeTree.gen'

/** Available to every route's `beforeLoad` / `loader` via `context`. */
export interface RouterContext {
  queryClient: QueryClient
}

export function createAppRouter(queryClient: QueryClient) {
  return createRouter({
    routeTree,
    context: { queryClient } satisfies RouterContext,
    // Start loaders (and therefore React Query prefetches) when a link is hovered/focused.
    defaultPreload: 'intent',
    // React Query owns caching; always let loaders run so they can consult the query cache.
    defaultPreloadStaleTime: 0,
    defaultErrorComponent: RouteErrorFallback,
    defaultNotFoundComponent: RouteNotFound,
    scrollRestoration: true,
  })
}

declare module '@tanstack/react-router' {
  interface Register {
    router: ReturnType<typeof createAppRouter>
  }
}
