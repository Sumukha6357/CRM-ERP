"use client";

import { useQuery } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { PageHeader } from "@/components/page-header";
import { KpiCard } from "@/components/kpi-card";
import { DataTable } from "@/components/data-table";
import { Card, CardContent } from "@/components/ui/card";

export default function DashboardPage() {
  const kpiQuery = useQuery({
    queryKey: ["kpis"],
    queryFn: async () => {
      const res = await api.get("/kpis");
      if (res.data?.message) {
        return { items: [], missing: true };
      }
      const items = Array.isArray(res.data) ? res.data : res.data?.items ?? [];
      return { items, missing: items.length === 0 };
    },
    staleTime: 60_000,
  });

  const leadsQuery = useQuery({
    queryKey: ["leads", { page: 0, size: 5 }],
    queryFn: async () => {
      const res = await api.get("/crm/leads", { params: { page: 0, size: 5 } });
      return res.data;
    },
  });

  const activitiesQuery = useQuery({
    queryKey: ["activities", { page: 0, size: 5 }],
    queryFn: async () => {
      const res = await api.get("/crm/activities", { params: { page: 0, size: 5 } });
      return res.data;
    },
  });

  return (
    <div className="space-y-6 page-enter">
      <PageHeader title="Dashboard" description="Overview of CRM performance and workflow activity" />

      {kpiQuery.isLoading ? (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          {Array.from({ length: 4 }).map((_, index) => (
            <Card key={index}>
              <CardContent className="pt-6 space-y-2">
                <div className="h-4 w-24 bg-muted rounded" />
                <div className="h-8 w-32 bg-muted rounded" />
              </CardContent>
            </Card>
          ))}
        </div>
      ) : kpiQuery.isError || kpiQuery.data?.missing ? (
        <Card>
          <CardContent className="pt-6 space-y-2">
            <div className="text-sm font-semibold">KPIs not configured yet</div>
            <div className="text-sm text-muted-foreground">Enable KPI endpoint in backend (kpi_snapshots).</div>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <KpiCard title="Leads (7d)" value={kpiQuery.data?.items?.[0]?.metricValue ?? "--"} trend="+4%" />
          <KpiCard title="Open Deals" value="12" trend="+2%" />
          <KpiCard title="Pipeline Value" value="$214k" trend="+8%" />
          <KpiCard title="Activities" value="42" trend="+6%" />
        </div>
      )}

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardContent className="pt-6">
            <div className="text-sm font-semibold mb-4">Recent Leads</div>
            <DataTable
              columns={[
                { key: "name", header: "Lead" },
                { key: "email", header: "Email" },
                { key: "status", header: "Status" },
              ]}
              data={leadsQuery.data?.content ?? []}
              loading={leadsQuery.isLoading}
              page={0}
              size={5}
              total={leadsQuery.data?.totalElements ?? 0}
              onPageChange={() => {}}
            />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="text-sm font-semibold mb-4">Recent Activities</div>
            <DataTable
              columns={[
                { key: "activityType", header: "Type" },
                { key: "subject", header: "Subject" },
                { key: "dueAt", header: "Due" },
              ]}
              data={activitiesQuery.data?.content ?? []}
              loading={activitiesQuery.isLoading}
              page={0}
              size={5}
              total={activitiesQuery.data?.totalElements ?? 0}
              onPageChange={() => {}}
            />
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
