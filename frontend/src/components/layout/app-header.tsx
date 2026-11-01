import { Link } from '@tanstack/react-router'
import { ActivityIcon } from 'lucide-react'
import { RefreshControls } from './refresh-controls'
import { ThemeToggle } from './theme-toggle'

export function AppHeader() {
  return (
    <header className="sticky top-0 z-10 border-b bg-background/80 backdrop-blur">
      <div className="mx-auto flex h-14 max-w-6xl items-center gap-6 px-4">
        <Link to="/" className="flex items-center gap-2 font-semibold">
          <ActivityIcon className="size-5" aria-hidden />
          Service Watcher
        </Link>
        <nav aria-label="Main" className="flex gap-4 text-sm">
          <Link
            to="/services"
            className="transition-colors"
            activeProps={{ className: 'font-medium text-foreground', 'aria-current': 'page' }}
            inactiveProps={{ className: 'text-muted-foreground hover:text-foreground' }}
          >
            Services
          </Link>
        </nav>
        <div className="ml-auto flex items-center gap-2">
          <RefreshControls />
          <ThemeToggle />
        </div>
      </div>
    </header>
  )
}
