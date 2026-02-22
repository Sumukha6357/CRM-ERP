"use client";

import Link from "next/link";
import { useState } from "react";
import { useDeleteLead, useLeads, type Lead } from "@/hooks/api-hooks";
import { PageHeader } from "@/components/page-header";
import { DataTable } from "@/components/data-table";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { PermissionGate } from "@/components/permission-gate";
import { ListToolbar } from "@/components/list-toolbar";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { useAuthStore } from "@/store/auth";
import { hasPermission } from "@/lib/permissions";

export default function LeadsPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");
  const [stage, setStage] = useState("");
  const [owner, setOwner] = useState("");
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const deleteMutation = useDeleteLead();
  const user = useAuthStore((state) => state.user);
  const canWrite = hasPermission(user, "CRM_LEAD_WRITE");

  const leadsQuery = useLeads({
    page,
    size: 10,
    status: status || undefined,
    stage: stage || undefined,
    owner: owner || undefined,
  });

  return (
    <PermissionGate permission="CRM_LEAD_READ">
      <div className="space-y-6">
        <PageHeader
          title="CRM Leads"
          description="Track and qualify inbound opportunities"
          actions={
            <Button asChild>
              <Link href="/crm/leads/new">Create Lead</Link>
            </Button>
          }
        />

        <ListToolbar
          search={search}
          onSearchChange={setSearch}
          filters={
            <>
              <Input placeholder="Status" value={status} onChange={(e) => setStatus(e.target.value)} />
              <Input placeholder="Stage ID" value={stage} onChange={(e) => setStage(e.target.value)} />
              <Input placeholder="Owner ID" value={owner} onChange={(e) => setOwner(e.target.value)} />
            </>
          }
          onReset={() => {
            setSearch("");
            setStatus("");
            setStage("");
            setOwner("");
          }}
        />

        <DataTable
          columns={[
            { key: "name", header: "Lead" },
            { key: "email", header: "Email" },
            { key: "status", header: "Status" },
            {
              key: "actions",
              header: "Actions",
              align: "right",
              render: (row: Lead) => (
                <div className="flex items-center justify-end gap-2">
                  <Button variant="outline" size="sm" asChild>
                    <Link href={`/crm/leads/${row.id}`}>View</Link>
                  </Button>
                  {canWrite && (
                    <>
                      <Button variant="secondary" size="sm" asChild>
                        <Link href={`/crm/leads/${row.id}`}>Edit</Link>
                      </Button>
                      <Button variant="destructive" size="sm" onClick={() => setDeleteId(row.id)}>
                        Delete
                      </Button>
                    </>
                  )}
                </div>
              ),
            },
          ]}
          data={(leadsQuery.data?.content ?? []).filter((lead: Lead) =>
            search ? lead.name?.toLowerCase().includes(search.toLowerCase()) : true
          )}
          loading={leadsQuery.isLoading}
          page={page}
          size={10}
          total={leadsQuery.data?.totalElements ?? 0}
          onPageChange={setPage}
          emptyTitle="No leads yet"
          emptyDescription="Create a lead to start tracking opportunities."
          emptyAction={
            <Button asChild>
              <Link href="/crm/leads/new">Create Lead</Link>
            </Button>
          }
        />

        <ConfirmDialog
          open={!!deleteId}
          title="Delete lead?"
          description="This action cannot be undone."
          onCancel={() => setDeleteId(null)}
          onConfirm={() => {
            if (deleteId) {
              deleteMutation.mutate(deleteId, { onSuccess: () => setDeleteId(null) });
            }
          }}
          confirmLabel="Delete"
        />
      </div>
    </PermissionGate>
  );
}
