const relativeFormatter = new Intl.RelativeTimeFormat(undefined, { numeric: 'auto' })
const absoluteFormatter = new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'medium' })

const UNITS: [Intl.RelativeTimeFormatUnit, number][] = [
  ['day', 86_400],
  ['hour', 3_600],
  ['minute', 60],
  ['second', 1],
]

/** "12 seconds ago", "3 minutes ago"... */
export function formatRelativeTime(iso: string, now: number = Date.now()): string {
  const diffSeconds = Math.round((new Date(iso).getTime() - now) / 1000)
  for (const [unit, seconds] of UNITS) {
    if (Math.abs(diffSeconds) >= seconds || unit === 'second') {
      return relativeFormatter.format(Math.round(diffSeconds / seconds), unit)
    }
  }
  return iso
}

export function formatDateTime(iso: string): string {
  return absoluteFormatter.format(new Date(iso))
}
