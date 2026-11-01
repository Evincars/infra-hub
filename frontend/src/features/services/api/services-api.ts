import { api } from '@/lib/api-client'
import type { MonitoredService } from '../types'

const RESOURCE = '/services'

/** Raw HTTP calls for the services resource. No React, no caching: that's the query layer's job. */
export const servicesApi = {
  list: (signal?: AbortSignal) => api.get<MonitoredService[]>(RESOURCE, { signal }),
  get: (id: number, signal?: AbortSignal) => api.get<MonitoredService>(`${RESOURCE}/${id}`, { signal }),
}
