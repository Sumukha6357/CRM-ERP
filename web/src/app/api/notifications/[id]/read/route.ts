import { NextRequest, NextResponse } from "next/server";
import { proxyRequest, toError } from "@/lib/bff";
import { NotificationSchema } from "@/lib/schemas/notifications";

export async function PUT(_request: NextRequest, context: { params: Promise<{ id: string }> }) {
  const { id } = await context.params;
  const result = await proxyRequest({
    method: "PUT",
    path: `/api/notifications/${id}/read`,
    request: _request,
  });
  if (!result.ok) {
    return NextResponse.json(result, { status: result.status });
  }
  const parsed = NotificationSchema.safeParse(result.data);
  if (!parsed.success) {
    const error = toError(502, "UPSTREAM_SCHEMA_MISMATCH", "Invalid notification payload", parsed.error.format());
    return NextResponse.json(error, { status: error.status });
  }
  return NextResponse.json(parsed.data);
}
