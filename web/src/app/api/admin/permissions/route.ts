import { NextRequest, NextResponse } from "next/server";
import { proxyRequest } from "@/lib/bff";

export async function GET(request: NextRequest) {
  const result = await proxyRequest({
    method: "GET",
    path: "/api/admin/permissions",
    request,
  });

  return NextResponse.json(result.ok ? result.data : result, { status: result.status });
}
