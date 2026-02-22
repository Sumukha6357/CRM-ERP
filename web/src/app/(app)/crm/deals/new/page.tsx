"use client";

import { useRouter } from "next/navigation";
import { PageHeader } from "@/components/page-header";
import { DealForm } from "@/components/crm/deal-form";
import { PermissionGate } from "@/components/permission-gate";
import { useCreateDeal } from "@/hooks/api-hooks";

export default function NewDealPage() {
  const router = useRouter();
  const mutation = useCreateDeal();

  return (
    <PermissionGate permission="CRM_DEAL_WRITE">
      <div className="space-y-6">
        <PageHeader title="Create Deal" />
        <DealForm
          onSubmit={(values) =>
            mutation.mutate(values, { onSuccess: () => router.push("/crm/deals") })
          }
          submitLabel="Create Deal"
        />
      </div>
    </PermissionGate>
  );
}
