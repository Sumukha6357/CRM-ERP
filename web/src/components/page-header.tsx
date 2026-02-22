import { Button } from "@/components/ui/button";

export function PageHeader({
  title,
  description,
  actions,
  breadcrumbs,
}: {
  title: string;
  description?: string;
  actions?: React.ReactNode;
  breadcrumbs?: React.ReactNode;
}) {
  return (
    <div className="glass-panel rounded-2xl p-4 md:p-5 hover-lift flex flex-col gap-3">
      {breadcrumbs && <div>{breadcrumbs}</div>}
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
        <h1 className="text-2xl md:text-3xl font-semibold tracking-tight">{title}</h1>
        {description && <p className="text-sm text-muted-foreground mt-1.5">{description}</p>}
        </div>
        <div className="flex items-center gap-2">{actions ?? <Button variant="secondary">Export</Button>}</div>
      </div>
    </div>
  );
}
