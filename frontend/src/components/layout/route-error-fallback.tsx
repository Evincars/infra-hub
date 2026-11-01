import { useQueryErrorResetBoundary } from '@tanstack/react-query'
import { type ErrorComponentProps, useRouter } from '@tanstack/react-router'
import { CircleAlertIcon } from 'lucide-react'
import { useEffect } from 'react'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'

/** Default error boundary for every route. "Try again" resets both the router and React Query. */
export function RouteErrorFallback({ error }: ErrorComponentProps) {
  const router = useRouter()
  const queryErrorResetBoundary = useQueryErrorResetBoundary()

  useEffect(() => {
    queryErrorResetBoundary.reset()
  }, [queryErrorResetBoundary])

  return (
    <Alert variant="destructive" className="mx-auto max-w-xl">
      <CircleAlertIcon />
      <AlertTitle>Something went wrong</AlertTitle>
      <AlertDescription>
        <p>{error instanceof Error ? error.message : String(error)}</p>
        <Button variant="outline" size="sm" className="mt-2" onClick={() => router.invalidate()}>
          Try again
        </Button>
      </AlertDescription>
    </Alert>
  )
}
