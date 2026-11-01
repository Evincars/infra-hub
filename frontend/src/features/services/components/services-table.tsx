import { Link } from '@tanstack/react-router'
import { Skeleton } from '@/components/ui/skeleton'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { formatDateTime, formatRelativeTime } from '@/lib/format'
import { cn } from '@/lib/utils'
import { useTableDensity } from '@/stores/ui-store'
import type { MonitoredService } from '../types'
import { ServiceStatusBadge } from './service-status-badge'

interface ServicesTableProps {
  services: MonitoredService[] | undefined
  isLoading: boolean
}

export function ServicesTable({ services, isLoading }: ServicesTableProps) {
  const density = useTableDensity()
  const cellClass = density === 'compact' ? 'py-1' : 'py-3'

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Name</TableHead>
          <TableHead>URL</TableHead>
          <TableHead>Status</TableHead>
          <TableHead className="text-right">Last checked</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {isLoading &&
          Array.from({ length: 4 }, (_, i) => (
            <TableRow key={i}>
              {Array.from({ length: 4 }, (_, j) => (
                <TableCell key={j} className={cellClass}>
                  <Skeleton className="h-4 w-full max-w-40" />
                </TableCell>
              ))}
            </TableRow>
          ))}

        {services?.length === 0 && (
          <TableRow>
            <TableCell colSpan={4} className="h-24 text-center text-muted-foreground">
              No services are being monitored yet.
            </TableCell>
          </TableRow>
        )}

        {services?.map((service) => (
          <TableRow key={service.id}>
            <TableCell className={cn('font-medium', cellClass)}>
              <Link
                to="/services/$serviceId"
                params={{ serviceId: service.id }}
                className="underline-offset-4 hover:underline focus-visible:underline"
              >
                {service.name}
              </Link>
            </TableCell>
            <TableCell className={cn('max-w-80 truncate text-muted-foreground', cellClass)} title={service.url}>
              {service.url}
            </TableCell>
            <TableCell className={cellClass}>
              <ServiceStatusBadge status={service.lastStatus} />
            </TableCell>
            <TableCell className={cn('text-right text-muted-foreground tabular-nums', cellClass)}>
              {service.lastCheckedAt ? (
                <time dateTime={service.lastCheckedAt} title={formatDateTime(service.lastCheckedAt)}>
                  {formatRelativeTime(service.lastCheckedAt)}
                </time>
              ) : (
                'Never'
              )}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}
