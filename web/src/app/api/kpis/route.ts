import { NextRequest, NextResponse } from "next/server";
import { proxyRequest } from "@/lib/bff";

export async function GET(request: NextRequest) {
  const result = await proxyRequest({
    method: "GET",
    path: "/api/kpis",
    request,
  });

  if (!result.ok && result.status === 404) {
    return NextResponse.json({ items: [], message: "No KPI endpoint available" }, { status: 200 });
  }

  return NextResponse.json(result.ok ? result.data : result, { status: result.status });
}
