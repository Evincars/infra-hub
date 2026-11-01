import { createFileRoute } from '@tanstack/react-router'
import { CircleAlertIcon } from 'lucide-react'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { ServicesTable, ServiceStatusSummary, servicesQueries, useMonitoredServices } from '@/features/services'

export const Route = createFileRoute('/services/')({
  // Kick off the request as early as possible (on link hover with preload="intent").
  // prefetchQuery never throws and doesn't block rendering; the page handles loading/error itself.
  loader: ({ context }) => context.queryClient.prefetchQuery(servicesQueries.list()),
  component: ServicesPage,
})

function ServicesPage() {
  const { data: services, error, isPending, refetch, isRefetching } = useMonitoredServices()

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Services</h1>
        <p className="text-sm text-muted-foreground">Uptime of every monitored endpoint.</p>
      </div>

      {error && (
        <Alert variant="destructive">
          <CircleAlertIcon />
          <AlertTitle>{services ? 'Showing stale data' : "Couldn't load services"}</AlertTitle>
          <AlertDescription>
            <p>{error.message}</p>
            <Button variant="outline" size="sm" className="mt-2" disabled={isRefetching} onClick={() => refetch()}>
              Retry now
            </Button>
          </AlertDescription>
        </Alert>
      )}

      <ServiceStatusSummary />

      <Card>
        <CardContent>
          <ServicesTable services={services} isLoading={isPending && !error} />
        </CardContent>
      </Card>
    </div>
  )
}
