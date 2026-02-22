"use client";

import { useState } from "react";
import { useMarkNotificationRead, useNotifications, type NotificationItem } from "@/hooks/api-hooks";
import { PageHeader } from "@/components/page-header";
import { DataTable } from "@/components/data-table";
import { Button } from "@/components/ui/button";
import { PermissionGate } from "@/components/permission-gate";
import { ListToolbar } from "@/components/list-toolbar";

export default function NotificationsPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const notificationsQuery = useNotifications({ page, size: 10 });
  const markReadMutation = useMarkNotificationRead();

  return (
    <PermissionGate permission="NOTIFICATION_READ">
      <div className="space-y-6">
        <PageHeader title="Notifications" description="Keep up with workflow and CRM updates" />

        <ListToolbar
          search={search}
          onSearchChange={setSearch}
          onReset={() => setSearch("")}
        />

        <DataTable
          columns={[
            { key: "title", header: "Title" },
            { key: "type", header: "Type" },
            { key: "createdAt", header: "Created" },
            {
              key: "actions",
              header: "Actions",
              align: "right",
              render: (row: NotificationItem) => (
                <div className="flex items-center justify-end">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => markReadMutation.mutate(row.id)}
                    disabled={!!row.readAt}
                  >
                    Mark Read
                  </Button>
                </div>
              ),
            },
          ]}
          data={(notificationsQuery.data?.content ?? []).filter((note: NotificationItem) =>
            search ? note.title?.toLowerCase().includes(search.toLowerCase()) : true
          )}
          loading={notificationsQuery.isLoading}
          page={page}
          size={10}
          total={notificationsQuery.data?.totalElements ?? 0}
          onPageChange={setPage}
          emptyTitle="No notifications"
          emptyDescription="You're all caught up."
        />
      </div>
    </PermissionGate>
  );
}
