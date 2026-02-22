"use client";

import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/auth";
import { hasPermission } from "@/lib/permissions";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

export function PermissionGate({
  permission,
  permissions,
  children,
}: {
  permission?: string;
  permissions?: string[];
  children: React.ReactNode;
}) {
  const router = useRouter();
  const user = useAuthStore((state) => state.user);
  const required = permissions ?? (permission ? [permission] : []);
  const allowed = required.length === 0 || required.every((perm) => hasPermission(user, perm));

  if (!allowed) {
    return (
      <Card className="max-w-xl">
        <CardContent className="py-10 text-center space-y-3">
          <div className="text-lg font-semibold">Access denied</div>
          <div className="text-sm text-muted-foreground">
            You do not have permission to view this page.
          </div>
          <Button variant="outline" onClick={() => router.back()}>
            Go back
          </Button>
        </CardContent>
      </Card>
    );
  }

  return <>{children}</>;
}
