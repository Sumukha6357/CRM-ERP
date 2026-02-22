import { Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

export function ListToolbar({
  search,
  onSearchChange,
  filters,
  onReset,
  actions,
}: {
  search?: string;
  onSearchChange?: (value: string) => void;
  filters?: React.ReactNode;
  onReset?: () => void;
  actions?: React.ReactNode;
}) {
  return (
    <div className="glass-panel rounded-2xl p-3 md:p-4 flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
      <div className="flex flex-1 flex-col gap-3 md:flex-row md:items-center">
        {onSearchChange && (
          <div className="relative w-full md:max-w-xs">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Search..."
              value={search ?? ""}
              onChange={(event) => onSearchChange(event.target.value)}
              className="pl-9"
            />
          </div>
        )}
        <div className="flex flex-wrap gap-2">{filters}</div>
        {onReset && (
          <Button variant="ghost" size="sm" onClick={onReset} className="rounded-lg">
            Reset
          </Button>
        )}
      </div>
      {actions && <div className="flex items-center gap-2">{actions}</div>}
    </div>
  );
}
