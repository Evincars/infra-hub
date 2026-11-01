import { Card, CardDescription, CardHeader } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { cn } from '@/lib/utils'
import { useServiceStatusSummary } from '../hooks/use-monitored-services'

const TILES = [
  { key: 'total', label: 'Monitored', tone: '' },
  { key: 'UP', label: 'Up', tone: 'text-emerald-600 dark:text-emerald-400' },
  { key: 'DOWN', label: 'Down', tone: 'text-destructive' },
  { key: 'UNKNOWN', label: 'Unknown', tone: 'text-muted-foreground' },
] as const

export function ServiceStatusSummary() {
  const { data: summary } = useServiceStatusSummary()

  return (
    <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
      {TILES.map(({ key, label, tone }) => (
        <Card key={key} size="sm">
          <CardHeader>
            <CardDescription>{label}</CardDescription>
            {/* Plain element, not CardTitle: its size="sm" variant would override the font size */}
            <p className={cn('font-heading text-3xl font-semibold tabular-nums', tone)}>
              {summary ? summary[key] : <Skeleton className="h-9 w-12" />}
            </p>
          </CardHeader>
        </Card>
      ))}
    </div>
  )
}
