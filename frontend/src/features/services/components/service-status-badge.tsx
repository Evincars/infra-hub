import { Badge } from '@/components/ui/badge'
import { cn } from '@/lib/utils'
import type { ServiceStatus } from '../types'

const STATUS_STYLES: Record<ServiceStatus, { label: string; badge: string; dot: string }> = {
  UP: {
    label: 'Up',
    badge: 'border-emerald-600/30 bg-emerald-500/10 text-emerald-700 dark:text-emerald-400',
    dot: 'bg-emerald-500',
  },
  DOWN: {
    label: 'Down',
    badge: 'border-destructive/30 bg-destructive/10 text-destructive',
    dot: 'bg-destructive',
  },
  UNKNOWN: {
    label: 'Unknown',
    badge: 'border-border bg-muted text-muted-foreground',
    dot: 'bg-muted-foreground',
  },
}

/** Colour is never the only signal: the text label carries the status for colour-blind and screen-reader users. */
export function ServiceStatusBadge({ status, className }: { status: ServiceStatus; className?: string }) {
  const style = STATUS_STYLES[status]
  return (
    <Badge variant="outline" className={cn(style.badge, className)}>
      <span aria-hidden className={cn('size-1.5 rounded-full', style.dot, status === 'DOWN' && 'animate-pulse')} />
      {style.label}
    </Badge>
  )
}
