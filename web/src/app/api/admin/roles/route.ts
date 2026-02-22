import { NextRequest, NextResponse } from "next/server";
import { proxyRequest } from "@/lib/bff";

export async function GET(request: NextRequest) {
  const result = await proxyRequest({
    method: "GET",
    path: "/api/admin/roles",
    request,
  });

  return NextResponse.json(result.ok ? result.data : result, { status: result.status });
}

export async function POST(request: NextRequest) {
  const body = await request.json().catch(() => ({}));
  const result = await proxyRequest({
    method: "POST",
    path: "/api/admin/roles",
    body,
    request,
  });

  return NextResponse.json(result.ok ? result.data : result, { status: result.status });
}
