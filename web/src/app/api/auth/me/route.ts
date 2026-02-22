import { NextRequest, NextResponse } from "next/server";
import { proxyRequest, toError } from "@/lib/bff";
import { AuthMeResponseSchema } from "@/lib/schemas/auth";

export async function GET(request: NextRequest) {
  const result = await proxyRequest({
    method: "GET",
    path: "/api/auth/me",
    request,
  });

  if (!result.ok) {
    return NextResponse.json(result, { status: result.status });
  }

  const parsed = AuthMeResponseSchema.safeParse(result.data);
  if (!parsed.success) {
    const error = toError(502, "UPSTREAM_SCHEMA_MISMATCH", "Invalid auth payload", parsed.error.format());
    return NextResponse.json(error, { status: error.status });
  }

  return NextResponse.json(parsed.data);
}
