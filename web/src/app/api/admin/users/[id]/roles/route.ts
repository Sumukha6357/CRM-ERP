import { NextRequest, NextResponse } from "next/server";
import { proxyRequest } from "@/lib/bff";

export async function GET(request: NextRequest, context: { params: Promise<{ id: string }> }) {
  const { id } = await context.params;
  const result = await proxyRequest({
    method: "GET",
    path: `/api/admin/users/${id}/roles`,
    request,
  });
  return NextResponse.json(result.ok ? result.data : result, { status: result.status });
}

export async function PUT(request: NextRequest, context: { params: Promise<{ id: string }> }) {
  const { id } = await context.params;
  const body = await request.json().catch(() => ({}));
  const result = await proxyRequest({
    method: "PUT",
    path: `/api/admin/users/${id}/roles`,
    body,
    request,
  });
  return NextResponse.json(result.ok ? result.data : result, { status: result.status });
}
