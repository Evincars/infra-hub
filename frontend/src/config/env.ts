/**
 * Typed access to build-time environment variables (see .env.example).
 * Keep all `import.meta.env` reads in this file.
 */
export const env = {
  /** Base path of the Spring Boot REST API. Relative by default so the Vite dev proxy / reverse proxy handles it. */
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL ?? '/api/v1',
} as const
