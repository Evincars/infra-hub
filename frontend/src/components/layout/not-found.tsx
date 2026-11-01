import { Link } from '@tanstack/react-router'
import { Button } from '@/components/ui/button'

export function NotFound({ message = "This page doesn't exist." }: { message?: string }) {
  return (
    <div className="flex flex-col items-center gap-4 py-24 text-center">
      <h1 className="text-2xl font-semibold">Not found</h1>
      <p className="text-muted-foreground">{message}</p>
      <Button asChild variant="outline">
        <Link to="/services">Back to services</Link>
      </Button>
    </div>
  )
}

/** Adapter for the router's `defaultNotFoundComponent` slot. */
export function RouteNotFound() {
  return <NotFound />
}
