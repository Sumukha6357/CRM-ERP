import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";

type Column<T> = {
  key: string;
  header: string;
  render?: (row: T) => React.ReactNode;
  align?: "left" | "right";
};

export function DataTable<T>({
  columns,
  data,
  loading,
  page,
  size,
  total,
  onPageChange,
  emptyTitle = "No results",
  emptyDescription,
  emptyAction,
}: {
  columns: Column<T>[];
  data: T[];
  loading?: boolean;
  page: number;
  size: number;
  total: number;
  onPageChange: (page: number) => void;
  emptyTitle?: string;
  emptyDescription?: string;
  emptyAction?: React.ReactNode;
}) {
  const totalPages = Math.max(1, Math.ceil(total / size));

  return (
    <div className="space-y-3">
      <div className="glass-panel rounded-2xl overflow-x-auto">
        <Table>
          <TableHeader className="[&_tr]:border-border/60">
            <TableRow>
              {columns.map((column) => (
                <TableHead key={column.key} className={column.align === "right" ? "text-right" : ""}>
                  {column.header}
                </TableHead>
              ))}
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading
              ? Array.from({ length: 5 }).map((_, index) => (
                  <TableRow key={index}>
                    {columns.map((column) => (
                      <TableCell key={column.key} className={column.align === "right" ? "text-right" : ""}>
                        <Skeleton className="h-4 w-full" style={{ animationDelay: `${index * 70}ms` }} />
                      </TableCell>
                    ))}
                  </TableRow>
                ))
              : data.length > 0
                ? data.map((row, index) => (
                    <TableRow
                      key={index}
                      className="row-reveal hover:bg-accent/35 transition-colors duration-200"
                      style={{ animationDelay: `${index * 45}ms` }}
                    >
                      {columns.map((column) => (
                        <TableCell key={column.key} className={column.align === "right" ? "text-right" : ""}>
                          {column.render ? column.render(row) : (row as any)[column.key]}
                        </TableCell>
                      ))}
                    </TableRow>
                  ))
                : (
                    <TableRow>
                      <TableCell colSpan={columns.length}>
                        <div className="py-10">
                          <div className="rounded-2xl border border-dashed border-border/80 p-6 text-center bg-background/45">
                            <div className="text-sm font-semibold">{emptyTitle}</div>
                            {emptyDescription && (
                              <div className="text-xs text-muted-foreground mt-1">{emptyDescription}</div>
                            )}
                            {emptyAction && <div className="mt-3 flex justify-center">{emptyAction}</div>}
                          </div>
                        </div>
                      </TableCell>
                    </TableRow>
                  )}
          </TableBody>
        </Table>
      </div>

      <div className="flex items-center justify-between">
        <div className="text-sm text-muted-foreground">
          Page {page + 1} of {totalPages}
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" disabled={page <= 0} onClick={() => onPageChange(page - 1)}>
            Previous
          </Button>
          <Button
            variant="outline"
            size="sm"
            disabled={page + 1 >= totalPages}
            onClick={() => onPageChange(page + 1)}
          >
            Next
          </Button>
        </div>
      </div>
    </div>
  );
}
