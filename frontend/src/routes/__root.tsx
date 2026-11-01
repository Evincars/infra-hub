import { createRootRouteWithContext, Outlet } from '@tanstack/react-router'
import { lazy, Suspense } from 'react'
import type { RouterContext } from '@/app/router'
import { AppHeader } from '@/components/layout/app-header'
import { useApplyTheme } from '@/hooks/use-apply-theme'

// Dev-only: excluded from the production bundle entirely.
const TanStackRouterDevtools = import.meta.env.DEV
  ? lazy(() => import('@tanstack/react-router-devtools').then((m) => ({ default: m.TanStackRouterDevtools })))
  : () => null

export const Route = createRootRouteWithContext<RouterContext>()({
  component: RootLayout,
})

function RootLayout() {
  useApplyTheme()

  return (
    <div className="min-h-svh bg-background text-foreground">
      <AppHeader />
      <main className="mx-auto max-w-6xl px-4 py-8">
        <Outlet />
      </main>
      <Suspense>
        <TanStackRouterDevtools position="bottom-right" />
      </Suspense>
    </div>
  )
}
