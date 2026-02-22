import { NextRequest, NextResponse } from "next/server";
import { proxyRequest } from "@/lib/bff";

export async function PUT(request: NextRequest, context: { params: Promise<{ id: string }> }) {
  const { id } = await context.params;
  const body = await request.json().catch(() => ({}));
  const result = await proxyRequest({
    method: "PUT",
    path: `/api/admin/roles/${id}`,
    body,
    request,
  });
  return NextResponse.json(result.ok ? result.data : result, { status: result.status });
}

export async function DELETE(request: NextRequest, context: { params: Promise<{ id: string }> }) {
  const { id } = await context.params;
  const result = await proxyRequest({
    method: "DELETE",
    path: `/api/admin/roles/${id}`,
    request,
  });
  return NextResponse.json(result.ok ? result.data : result, { status: result.status });
}
