import { NextResponse } from "next/server";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export async function GET() {
  const backendUrl = `${API_BASE_URL}/actuator/health`;
  try {
    const response = await fetch(backendUrl, { cache: "no-store" });
    const data = await response.json().catch(() => null);
    return NextResponse.json({
      ok: response.ok,
      backend: backendUrl,
      status: response.status,
      data,
    });
  } catch (error) {
    return NextResponse.json(
      { ok: false, backend: backendUrl, status: 500, error: String(error) },
      { status: 500 }
    );
  }
}
