import { NextRequest, NextResponse } from "next/server";
import { proxyRequest, toError } from "@/lib/bff";
import { WorkflowInstanceSchema } from "@/lib/schemas/workflows";

export async function POST(_request: NextRequest, context: { params: Promise<{ id: string }> }) {
  const { id } = await context.params;
  const result = await proxyRequest({
    method: "POST",
    path: `/api/workflows/instances/${id}/approve`,
    request: _request,
  });
  if (!result.ok) {
    return NextResponse.json(result, { status: result.status });
  }
  const parsed = WorkflowInstanceSchema.safeParse(result.data);
  if (!parsed.success) {
    const error = toError(502, "UPSTREAM_SCHEMA_MISMATCH", "Invalid workflow payload", parsed.error.format());
    return NextResponse.json(error, { status: error.status });
  }
  return NextResponse.json(parsed.data);
}
