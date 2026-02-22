import { NextRequest, NextResponse } from "next/server";
import { proxyRequest, toError } from "@/lib/bff";
import { WorkflowInstanceSchema } from "@/lib/schemas/workflows";
import { validatePage } from "@/lib/schemas/common";

export async function GET(request: NextRequest) {
  const result = await proxyRequest({
    method: "GET",
    path: "/api/workflows/instances",
    query: request.nextUrl.search,
    request,
  });
  if (!result.ok) {
    return NextResponse.json(result, { status: result.status });
  }
  const parsed = validatePage(WorkflowInstanceSchema, result.data);
  if (!parsed.success) {
    const error = toError(502, "UPSTREAM_SCHEMA_MISMATCH", "Invalid workflow list payload", parsed.error.format());
    return NextResponse.json(error, { status: error.status });
  }
  return NextResponse.json(result.data);
}
