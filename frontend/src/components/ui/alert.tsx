import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "cn"

const alertVariants = cva(
  "group/alert relative grid w-full gap-0.5 rounded-lg border px-2.5 py-2 text-left text-sm has-data-[slot=alert-action]:relative has-data-[slot=alert-action]:pr-18 has-[>svg]:grid-cols-[auto_1fr] has-[>svg]:gap-x-2 *:[svg]:row-span-2 *:[svg]:translate-y-0.5 *:[svg]:text-current *:[svg:not([class*='size-'])]:size-4",
  {
    variants: {
      variant: {
        default: "bg-card text-card-foreground",
        destructive:
          "bg-card text-destructive *:data-[slot=alert-description]:text-destructive/90 *:[svg]:text-current",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
)

/**
 * LEARN:
 * ten objekt jsou React props. Jde o destructuring (`const {neco1, neco2} = fc(...)`) prvního A JEDINEHO argumentu funkce.
 * className a variant si komponenta bere stranou, protože s nimi pracuje sama. className spojí 
 * s vlastními třídami přes cn(...) a variant použije v alertVariants({ variant }). 
 * Všechno ostatní (id, onClick, style, children, aria-*, data-* …) se sesbírá do objektu `props `
 * (rest operátor). Ten se pak předá na <div {...props} /> (spread operátor). 
 * Díky tomu se <Alert onClick={...}> chová jako normální <div>. Kdyby tam ...props nebylo, 
 * všechny ostatní props by se tiše zahodily, včetně children. Pořadí je důležité. {...props} je až na konci, 
 * takže může přepsat data-slot nebo role. className tam spadnout nemůže, protože už byl vyjmut.
 */
function Alert({
  className,
  variant,
  ...props
}: React.ComponentProps<"div"> & VariantProps<typeof alertVariants>) { // merge obou typů: standardní div props + varianty alertu
  return (
    <div
      data-slot="alert"
      role="alert"
      className={cn(alertVariants({ variant }), className)}
      // všechny ostatní props se předají na div, children (text, tagy, dalsi komponenty) se vykresli uvnitr => default content-projection
      {...props}
    />
  ) // return f-ce je React.TSX.Element
}

function AlertTitle({ className, ...props }: React.ComponentProps<"div">) {
  return (
    <div
      data-slot="alert-title"
      className={cn(
        "font-medium group-has-[>svg]/alert:col-start-2 [&_a]:underline [&_a]:underline-offset-3 [&_a]:hover:text-foreground",
        className
      )}
      {...props}
    />
  )
}

function AlertDescription({
  className,
  ...props
}: React.ComponentProps<"div">) {
  return (
    <div
      data-slot="alert-description"
      className={cn(
        "text-sm text-balance text-muted-foreground md:text-pretty [&_a]:underline [&_a]:underline-offset-3 [&_a]:hover:text-foreground [&_p:not(:last-child)]:mb-4",
        className
      )}
      {...props}
    />
  )
}

function AlertAction({ className, ...props }: React.ComponentProps<"div">) {
  return (
    <div
      data-slot="alert-action"
      className={cn("absolute top-2 right-2", className)}
      {...props}
    />
  )
}

export { Alert, AlertTitle, AlertDescription, AlertAction }
