import { PageHeader } from "@/components/page-header";
import { Card, CardContent } from "@/components/ui/card";

export default function ProfilePage() {
  return (
    <div className="space-y-6">
      <PageHeader title="Profile" description="Manage your account settings" />
      <Card className="shadow-sm">
        <CardContent className="pt-6 text-sm text-muted-foreground">
          Profile settings will be available once the user management endpoints are exposed.
        </CardContent>
      </Card>
    </div>
  );
}
