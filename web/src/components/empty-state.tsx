import { Card, CardContent } from "@/components/ui/card";

export function EmptyState({ title, description }: { title: string; description?: string }) {
  return (
    <Card className="border-dashed">
      <CardContent className="py-10 text-center space-y-2">
        <div className="text-lg font-semibold">{title}</div>
        {description && <div className="text-sm text-muted-foreground">{description}</div>}
      </CardContent>
    </Card>
  );
}
