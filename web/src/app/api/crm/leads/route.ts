import { NextRequest, NextResponse } from "next/server";
import { proxyRequest, toError } from "@/lib/bff";
import { LeadSchema } from "@/lib/schemas/crm";
import { validatePage } from "@/lib/schemas/common";

export async function GET(request: NextRequest) {
  const result = await proxyRequest({
    method: "GET",
    path: "/api/crm/leads",
    query: request.nextUrl.search,
    request,
  });

  if (!result.ok) {
    return NextResponse.json(result, { status: result.status });
  }

  const parsed = validatePage(LeadSchema, result.data);
  if (!parsed.success) {
    const error = toError(502, "UPSTREAM_SCHEMA_MISMATCH", "Invalid lead list payload", parsed.error.format());
    return NextResponse.json(error, { status: error.status });
  }

  return NextResponse.json(result.data);
}

export async function POST(request: NextRequest) {
  const body = await request.json().catch(() => ({}));
  const result = await proxyRequest({
    method: "POST",
    path: "/api/crm/leads",
    body,
    request,
  });

  if (!result.ok) {
    return NextResponse.json(result, { status: result.status });
  }

  const parsed = LeadSchema.safeParse(result.data);
  if (!parsed.success) {
    const error = toError(502, "UPSTREAM_SCHEMA_MISMATCH", "Invalid lead payload", parsed.error.format());
    return NextResponse.json(error, { status: error.status });
  }

  return NextResponse.json(parsed.data);
}
