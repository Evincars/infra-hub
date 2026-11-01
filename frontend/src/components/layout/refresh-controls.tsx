import { useIsFetching } from '@tanstack/react-query'
import { RefreshCwIcon } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { cn } from '@/lib/utils'
import {
  REFRESH_INTERVAL_OPTIONS,
  type RefreshIntervalMs,
  type TableDensity,
  useAutoRefresh,
  useRefreshIntervalMs,
  useTableDensity,
  useUiActions,
} from '@/stores/ui-store'

/** Auto-refresh + layout preferences, all backed by the persisted Zustand UI store. */
export function RefreshControls() {
  const autoRefresh = useAutoRefresh()
  const intervalMs = useRefreshIntervalMs()
  const density = useTableDensity()
  const { setAutoRefresh, setRefreshInterval, setDensity } = useUiActions()
  const isFetching = useIsFetching() > 0

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="sm" aria-label="Refresh settings">
          <RefreshCwIcon className={cn(isFetching && 'animate-spin')} />
          {autoRefresh ? `Every ${intervalMs / 1000}s` : 'Paused'}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-48">
        <DropdownMenuCheckboxItem checked={autoRefresh} onCheckedChange={setAutoRefresh}>
          Auto-refresh
        </DropdownMenuCheckboxItem>
        <DropdownMenuSeparator />
        <DropdownMenuLabel>Interval</DropdownMenuLabel>
        <DropdownMenuRadioGroup
          value={String(intervalMs)}
          onValueChange={(value) => setRefreshInterval(Number(value) as RefreshIntervalMs)}
        >
          {REFRESH_INTERVAL_OPTIONS.map((ms) => (
            <DropdownMenuRadioItem key={ms} value={String(ms)} disabled={!autoRefresh}>
              {ms / 1000} seconds
            </DropdownMenuRadioItem>
          ))}
        </DropdownMenuRadioGroup>
        <DropdownMenuSeparator />
        <DropdownMenuLabel>Density</DropdownMenuLabel>
        <DropdownMenuRadioGroup value={density} onValueChange={(value) => setDensity(value as TableDensity)}>
          <DropdownMenuRadioItem value="comfortable">Comfortable</DropdownMenuRadioItem>
          <DropdownMenuRadioItem value="compact">Compact</DropdownMenuRadioItem>
        </DropdownMenuRadioGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
