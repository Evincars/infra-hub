import { createFileRoute, Link, notFound } from '@tanstack/react-router'
import { ArrowLeftIcon, ExternalLinkIcon } from 'lucide-react'
import { NotFound } from '@/components/layout/not-found'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { ServiceStatusBadge, servicesQueries, useMonitoredService } from '@/features/services'
import { ApiError } from '@/lib/api-client'
import { formatDateTime, formatRelativeTime } from '@/lib/format'

export const Route = createFileRoute('/services/$serviceId')({
  // Typed path params: the URL segment is a string, components receive a number.
  params: {
    parse: ({ serviceId }) => {
      const id = Number(serviceId)
      if (!Number.isInteger(id) || id <= 0) throw notFound()
      return { serviceId: id }
    },
    stringify: ({ serviceId }) => ({ serviceId: String(serviceId) }),
  },
  loader: ({ context, params }) => context.queryClient.prefetchQuery(servicesQueries.detail(params.serviceId)),
  component: ServiceDetailPage,
})

function ServiceDetailPage() {
  const { serviceId } = Route.useParams()
  const { data: service, error } = useMonitoredService(serviceId)

  if (error instanceof ApiError && error.status === 404) {
    return <NotFound message={`Service #${serviceId} doesn't exist or was deleted.`} />
  }

  return (
    <div className="flex flex-col gap-6">
      <Button asChild variant="ghost" size="sm" className="w-fit">
        <Link to="/services">
          <ArrowLeftIcon />
          All services
        </Link>
      </Button>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-3 text-xl">
            {service ? service.name : <Skeleton className="h-6 w-48" />}
            {service && <ServiceStatusBadge status={service.lastStatus} />}
          </CardTitle>
          <CardDescription>
            {service ? (
              <a
                href={service.url}
                target="_blank"
                rel="noreferrer noopener"
                className="inline-flex items-center gap-1 underline-offset-4 hover:underline"
              >
                {service.url}
                <ExternalLinkIcon className="size-3" aria-hidden />
              </a>
            ) : (
              <Skeleton className="h-4 w-72" />
            )}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <dl className="grid grid-cols-[max-content_1fr] gap-x-6 gap-y-2 text-sm">
            <dt className="text-muted-foreground">Last checked</dt>
            <dd>
              {service?.lastCheckedAt
                ? `${formatRelativeTime(service.lastCheckedAt)} (${formatDateTime(service.lastCheckedAt)})`
                : service
                  ? 'Never'
                  : '-'}
            </dd>
            {error && (
              <>
                <dt className="text-muted-foreground">Error</dt>
                <dd className="text-destructive">{error.message}</dd>
              </>
            )}
          </dl>
        </CardContent>
      </Card>
    </div>
  )
}
