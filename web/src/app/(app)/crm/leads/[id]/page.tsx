"use client";

import { useParams, useRouter } from "next/navigation";
import { PageHeader } from "@/components/page-header";
import { LeadForm, LeadFormValues } from "@/components/crm/lead-form";
import { PermissionGate } from "@/components/permission-gate";
import { useLead, useUpdateLead } from "@/hooks/api-hooks";

export default function EditLeadPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const leadQuery = useLead(params.id);
  const mutation = useUpdateLead(params.id);
  const defaultValues: Partial<LeadFormValues> | undefined = leadQuery.data
    ? {
        name: leadQuery.data.name,
        email: leadQuery.data.email ?? "",
        phone: leadQuery.data.phone ?? "",
        company: leadQuery.data.company ?? "",
        value: leadQuery.data.value ?? undefined,
        status: leadQuery.data.status ?? undefined,
        source: leadQuery.data.source ?? undefined,
        stageId: leadQuery.data.stageId ?? undefined,
        ownerId: leadQuery.data.ownerId ?? undefined,
      }
    : undefined;

  return (
    <PermissionGate permission="CRM_LEAD_WRITE">
      <div className="space-y-6">
        <PageHeader title="Edit Lead" />
        <LeadForm
          defaultValues={defaultValues}
          onSubmit={(values) =>
            mutation.mutate(values, { onSuccess: () => router.push("/crm/leads") })
          }
          submitLabel="Update Lead"
        />
      </div>
    </PermissionGate>
  );
}
