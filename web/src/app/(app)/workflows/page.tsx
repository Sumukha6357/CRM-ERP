"use client";

import { useState } from "react";
import { useApproveWorkflow, useRejectWorkflow, useWorkflows } from "@/hooks/api-hooks";
import { PageHeader } from "@/components/page-header";
import { DataTable } from "@/components/data-table";
import { Button } from "@/components/ui/button";
import { PermissionGate } from "@/components/permission-gate";
import { ListToolbar } from "@/components/list-toolbar";

export default function WorkflowsPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const workflowsQuery = useWorkflows({ page, size: 10 });
  const approveMutation = useApproveWorkflow();
  const rejectMutation = useRejectWorkflow();

  return (
    <PermissionGate permission="WORKFLOW_READ">
      <div className="space-y-6">
        <PageHeader title="Workflows" description="Approve or reject workflow instances" />

        <ListToolbar
          search={search}
          onSearchChange={setSearch}
          onReset={() => setSearch("")}
        />

        <DataTable
          columns={[
            { key: "definitionCode", header: "Workflow" },
            { key: "status", header: "Status" },
            { key: "currentStep", header: "Step" },
            {
              key: "actions",
              header: "Actions",
              align: "right",
              render: (row: any) => (
                <div className="flex items-center justify-end gap-2">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => approveMutation.mutate(row.id)}
                    disabled={row.status === "APPROVED"}
                  >
                    Approve
                  </Button>
                  <Button
                    size="sm"
                    variant="destructive"
                    onClick={() => rejectMutation.mutate(row.id)}
                    disabled={row.status === "REJECTED"}
                  >
                    Reject
                  </Button>
                </div>
              ),
            },
          ]}
          data={(workflowsQuery.data?.content ?? []).filter((instance: any) =>
            search ? instance.definitionCode?.toLowerCase().includes(search.toLowerCase()) : true
          )}
          loading={workflowsQuery.isLoading}
          page={page}
          size={10}
          total={workflowsQuery.data?.totalElements ?? 0}
          onPageChange={setPage}
          emptyTitle="No workflows yet"
          emptyDescription="Workflow instances will appear when started."
        />
      </div>
    </PermissionGate>
  );
}
