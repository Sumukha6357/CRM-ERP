"use client";

import { useParams, useRouter } from "next/navigation";
import { PageHeader } from "@/components/page-header";
import { DealForm, DealFormValues } from "@/components/crm/deal-form";
import { PermissionGate } from "@/components/permission-gate";
import { useDeal, useUpdateDeal } from "@/hooks/api-hooks";

export default function EditDealPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const dealQuery = useDeal(params.id);
  const mutation = useUpdateDeal(params.id);
  const defaultValues: Partial<DealFormValues> | undefined = dealQuery.data
    ? {
        title: dealQuery.data.title,
        amount: dealQuery.data.amount ?? undefined,
        expectedCloseDate: dealQuery.data.expectedCloseDate ?? undefined,
        status: dealQuery.data.status ?? undefined,
        stageId: dealQuery.data.stageId ?? undefined,
        ownerId: dealQuery.data.ownerId ?? undefined,
      }
    : undefined;

  return (
    <PermissionGate permission="CRM_DEAL_WRITE">
      <div className="space-y-6">
        <PageHeader title="Edit Deal" />
        <DealForm
          defaultValues={defaultValues}
          onSubmit={(values) =>
            mutation.mutate(values, { onSuccess: () => router.push("/crm/deals") })
          }
          submitLabel="Update Deal"
        />
      </div>
    </PermissionGate>
  );
}
