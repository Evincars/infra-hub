export const SERVICE_STATUSES = ['UP', 'DOWN', 'UNKNOWN'] as const
export type ServiceStatus = (typeof SERVICE_STATUSES)[number]

/** Mirrors `MonitoredServiceResponse` in the Spring Boot backend. */
export interface MonitoredService {
  id: number
  name: string
  url: string
  lastStatus: ServiceStatus
  /** ISO-8601 instant, null until the first check */
  lastCheckedAt: string | null
}
