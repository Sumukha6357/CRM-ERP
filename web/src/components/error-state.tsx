import { Card, CardContent } from "@/components/ui/card";

export function ErrorState({ title, description }: { title: string; description?: string }) {
  return (
    <Card className="border-destructive/40">
      <CardContent className="py-10 text-center space-y-2">
        <div className="text-lg font-semibold text-destructive">{title}</div>
        {description && <div className="text-sm text-muted-foreground">{description}</div>}
      </CardContent>
    </Card>
  );
}
