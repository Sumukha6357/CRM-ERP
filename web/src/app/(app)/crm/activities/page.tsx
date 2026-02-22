"use client";

import { useState } from "react";
import { useActivities, useCreateActivity, type Activity } from "@/hooks/api-hooks";
import { PageHeader } from "@/components/page-header";
import { DataTable } from "@/components/data-table";
import { Card, CardContent } from "@/components/ui/card";
import { PermissionGate } from "@/components/permission-gate";
import { ActivityForm } from "@/components/crm/activity-form";
import { ListToolbar } from "@/components/list-toolbar";

export default function ActivitiesPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const activitiesQuery = useActivities({ page, size: 10 });
  const mutation = useCreateActivity();

  return (
    <PermissionGate permission="CRM_ACTIVITY_READ">
      <div className="space-y-6">
        <PageHeader title="CRM Activities" description="Track calls, meetings, and follow-ups" />

        <Card className="shadow-sm">
          <CardContent className="pt-6">
            <div className="text-sm font-semibold mb-4">Create Activity</div>
            <PermissionGate permission="CRM_ACTIVITY_WRITE">
              <ActivityForm onSubmit={(values) => mutation.mutate(values)} />
            </PermissionGate>
          </CardContent>
        </Card>

        <ListToolbar
          search={search}
          onSearchChange={setSearch}
          onReset={() => setSearch("")}
        />

        <DataTable
          columns={[
            { key: "activityType", header: "Type" },
            { key: "subject", header: "Subject" },
            { key: "dueAt", header: "Due" },
          ]}
          data={(activitiesQuery.data?.content ?? []).filter((activity: Activity) =>
            search ? activity.subject?.toLowerCase().includes(search.toLowerCase()) : true
          )}
          loading={activitiesQuery.isLoading}
          page={page}
          size={10}
          total={activitiesQuery.data?.totalElements ?? 0}
          onPageChange={setPage}
          emptyTitle="No activities yet"
          emptyDescription="Create an activity to log outreach."
        />
      </div>
    </PermissionGate>
  );
}
