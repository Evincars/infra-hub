import { create } from 'zustand'
import { createJSONStorage, persist } from 'zustand/middleware'

export type ThemePreference = 'light' | 'dark' | 'system'
export type TableDensity = 'comfortable' | 'compact'

export const REFRESH_INTERVAL_OPTIONS = [5_000, 15_000, 30_000, 60_000] as const
export type RefreshIntervalMs = (typeof REFRESH_INTERVAL_OPTIONS)[number]

/** localStorage key, also read by the anti-flash script in index.html. Keep them in sync. */
export const UI_STORE_KEY = 'service-watcher:ui'

interface UiState {
  theme: ThemePreference
  density: TableDensity
  autoRefresh: boolean
  refreshIntervalMs: RefreshIntervalMs
}

interface UiActions {
  setTheme: (theme: ThemePreference) => void
  setDensity: (density: TableDensity) => void
  setAutoRefresh: (enabled: boolean) => void
  setRefreshInterval: (ms: RefreshIntervalMs) => void
  reset: () => void
}

type UiStore = UiState & { actions: UiActions }

const initialState: UiState = {
  theme: 'system',
  density: 'comfortable',
  autoRefresh: true,
  refreshIntervalMs: 15_000,
}

/**
 * Client-only UI preferences. Server data (services, statuses) belongs in React Query, never here.
 *
 * Don't export the raw store hook: components use the selector hooks below,
 * so each one re-renders only when the slice it reads changes.
 */
const useUiStore = create<UiStore>()(
  persist(
    (set) => ({
      ...initialState,
      // Actions live in their own object: a stable reference, so `useUiActions()` never re-renders.
      actions: {
        setTheme: (theme) => set({ theme }),
        setDensity: (density) => set({ density }),
        setAutoRefresh: (autoRefresh) => set({ autoRefresh }),
        setRefreshInterval: (refreshIntervalMs) => set({ refreshIntervalMs }),
        reset: () => set(initialState),
      },
    }),
    {
      name: UI_STORE_KEY,
      version: 1, // bump and add `migrate` when the persisted shape changes
      storage: createJSONStorage(() => localStorage),
      partialize: ({ theme, density, autoRefresh, refreshIntervalMs }): UiState => ({
        theme,
        density,
        autoRefresh,
        refreshIntervalMs,
      }),
    },
  ),
)

export const useThemePreference = () => useUiStore((s) => s.theme)
export const useTableDensity = () => useUiStore((s) => s.density)
export const useAutoRefresh = () => useUiStore((s) => s.autoRefresh)
export const useRefreshIntervalMs = () => useUiStore((s) => s.refreshIntervalMs)
export const useUiActions = () => useUiStore((s) => s.actions)

/** Non-React access (e.g. in tests or router loaders). */
export const uiStore = {
  getState: useUiStore.getState,
  subscribe: useUiStore.subscribe,
}
