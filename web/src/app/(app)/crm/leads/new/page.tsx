"use client";

import { useRouter } from "next/navigation";
import { PageHeader } from "@/components/page-header";
import { LeadForm } from "@/components/crm/lead-form";
import { PermissionGate } from "@/components/permission-gate";
import { useCreateLead } from "@/hooks/api-hooks";

export default function NewLeadPage() {
  const router = useRouter();
  const mutation = useCreateLead();

  return (
    <PermissionGate permission="CRM_LEAD_WRITE">
      <div className="space-y-6">
        <PageHeader title="Create Lead" />
        <LeadForm
          onSubmit={(values) =>
            mutation.mutate(values, { onSuccess: () => router.push("/crm/leads") })
          }
          submitLabel="Create Lead"
        />
      </div>
    </PermissionGate>
  );
}
