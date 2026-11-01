import { useEffect } from 'react'
import { useThemePreference } from '@/stores/ui-store'

/** Syncs the theme preference from the store to the `.dark` class shadcn/ui's CSS variables key off. */
export function useApplyTheme() {
  const theme = useThemePreference()

  useEffect(() => {
    const media = window.matchMedia('(prefers-color-scheme: dark)')

    const apply = () => {
      const dark = theme === 'dark' || (theme === 'system' && media.matches)
      document.documentElement.classList.toggle('dark', dark)
      document.documentElement.style.colorScheme = dark ? 'dark' : 'light'
    }

    apply()
    if (theme !== 'system') return

    media.addEventListener('change', apply)
    return () => media.removeEventListener('change', apply)
  }, [theme])
}
