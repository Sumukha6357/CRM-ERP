"use client";

import Link from "next/link";
import { useState } from "react";
import { useDeals, useDeleteDeal } from "@/hooks/api-hooks";
import { PageHeader } from "@/components/page-header";
import { DataTable } from "@/components/data-table";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { PermissionGate } from "@/components/permission-gate";
import { ListToolbar } from "@/components/list-toolbar";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { useAuthStore } from "@/store/auth";
import { hasPermission } from "@/lib/permissions";

export default function DealsPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");
  const [stage, setStage] = useState("");
  const [owner, setOwner] = useState("");
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const deleteMutation = useDeleteDeal();
  const user = useAuthStore((state) => state.user);
  const canWrite = hasPermission(user, "CRM_DEAL_WRITE");

  const dealsQuery = useDeals({
    page,
    size: 10,
    status: status || undefined,
    stage: stage || undefined,
    owner: owner || undefined,
  });

  return (
    <PermissionGate permission="CRM_DEAL_READ">
      <div className="space-y-6">
        <PageHeader
          title="CRM Deals"
          description="Track pipeline and forecast revenue"
          actions={
            <Button asChild>
              <Link href="/crm/deals/new">Create Deal</Link>
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
            { key: "title", header: "Deal" },
            { key: "amount", header: "Amount" },
            { key: "status", header: "Status" },
            {
              key: "actions",
              header: "Actions",
              align: "right",
              render: (row: any) => (
                <div className="flex items-center justify-end">
                  <div className="flex items-center gap-2">
                    <Button variant="outline" size="sm" asChild>
                      <Link href={`/crm/deals/${row.id}`}>View</Link>
                    </Button>
                    {canWrite && (
                      <>
                        <Button variant="secondary" size="sm" asChild>
                          <Link href={`/crm/deals/${row.id}`}>Edit</Link>
                        </Button>
                        <Button variant="destructive" size="sm" onClick={() => setDeleteId(row.id)}>
                          Delete
                        </Button>
                      </>
                    )}
                  </div>
                </div>
              ),
            },
          ]}
          data={(dealsQuery.data?.content ?? []).filter((deal: any) =>
            search ? deal.title?.toLowerCase().includes(search.toLowerCase()) : true
          )}
          loading={dealsQuery.isLoading}
          page={page}
          size={10}
          total={dealsQuery.data?.totalElements ?? 0}
          onPageChange={setPage}
          emptyTitle="No deals yet"
          emptyDescription="Create a deal to start tracking pipeline."
          emptyAction={
            <Button asChild>
              <Link href="/crm/deals/new">Create Deal</Link>
            </Button>
          }
        />

        <ConfirmDialog
          open={!!deleteId}
          title="Delete deal?"
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
