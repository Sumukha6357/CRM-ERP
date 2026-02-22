import { NextRequest, NextResponse } from "next/server";
import { toError } from "@/lib/bff";
import { AuthMeResponseSchema } from "@/lib/schemas/auth";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const loginResponse = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json", Accept: "application/json" },
      body: JSON.stringify(body),
    });

    const loginData = await loginResponse.json().catch(() => null);
    if (!loginResponse.ok || !loginData?.token) {
      const error = toError(
        loginResponse.status || 401,
        "AUTH_INVALID",
        loginData?.message ?? "Invalid credentials",
        loginData
      );
      return NextResponse.json(error, { status: error.status });
    }

    const token = loginData.token as string;
    const meResponse = await fetch(`${API_BASE_URL}/api/auth/me`, {
      headers: { Authorization: `Bearer ${token}`, Accept: "application/json" },
      cache: "no-store",
    });
    const meData = await meResponse.json().catch(() => null);

    const parsed = AuthMeResponseSchema.safeParse(meData);
    if (!parsed.success) {
      const error = toError(502, "UPSTREAM_SCHEMA_MISMATCH", "Invalid auth payload", parsed.error.format());
      return NextResponse.json(error, { status: error.status });
    }

    const response = NextResponse.json(parsed.data);
    response.cookies.set("access_token", token, {
      httpOnly: true,
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
      path: "/",
      maxAge: 60 * 60 * 8,
    });
    return response;
  } catch (error) {
    const err = toError(500, "BFF_ERROR", "Login failed", error);
    return NextResponse.json(err, { status: err.status });
  }
}
