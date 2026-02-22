import { cookies } from "next/headers";
import { NextRequest } from "next/server";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
const DEBUG = process.env.NEXT_PUBLIC_DEBUG === "true";

export type ProxyOptions = {
  method: string;
  path: string;
  body?: unknown;
  query?: string;
  request?: NextRequest;
};

export type BffError = {
  ok: false;
  status: number;
  code: string;
  message: string;
  details?: unknown;
};

type ApiEnvelopeMeta = {
  totalElements?: number;
  totalPages?: number;
  size?: number;
  number?: number;
};

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null;
}

function looksLikeApiEnvelope(value: unknown): value is { data?: unknown; meta?: ApiEnvelopeMeta; error?: unknown } {
  if (!isRecord(value)) {
    return false;
  }
  return "data" in value && ("meta" in value || "error" in value);
}

function normalizeSuccessPayload(value: unknown): unknown {
  if (!looksLikeApiEnvelope(value)) {
    return value;
  }

  const envelopeData = value.data;
  const meta = isRecord(value.meta) ? (value.meta as ApiEnvelopeMeta) : undefined;

  // Normalize API envelopes to the page shape used by frontend hooks.
  if (Array.isArray(envelopeData) && meta) {
    return {
      content: envelopeData,
      totalElements: meta.totalElements ?? envelopeData.length,
      totalPages: meta.totalPages ?? 1,
      size: meta.size ?? envelopeData.length,
      number: meta.number ?? 0,
    };
  }

  return envelopeData;
}

export async function proxyRequest({ method, path, body, query, request }: ProxyOptions) {
  const cookieStore = await cookies();
  const token = cookieStore.get("access_token")?.value;
  const url = `${API_BASE_URL}${path}${query ?? ""}`;
  const requestId = request?.headers.get("x-request-id") ?? crypto.randomUUID();
  const contentType = request?.headers.get("content-type") ?? "application/json";
  const accept = request?.headers.get("accept") ?? "application/json";
  const userAgent = request?.headers.get("user-agent") ?? undefined;

  const response = await fetch(url, {
    method,
    headers: {
      "Content-Type": contentType,
      Accept: accept,
      "x-request-id": requestId,
      ...(userAgent ? { "User-Agent": userAgent } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
    cache: "no-store",
  });

  if (DEBUG) {
    console.log(`[BFF] ${method} ${url} -> ${response.status} (${requestId})`);
  }

  const responseContentType = response.headers.get("content-type") ?? "";
  const isJson = responseContentType.includes("application/json");
  const rawData = isJson ? await response.json().catch(() => null) : await response.text().catch(() => null);

  if (!response.ok) {
    if (!isJson) {
      return toError(response.status, "UPSTREAM_NON_JSON", "Upstream error", rawData);
    }
    const upstreamMessage =
      (isRecord(rawData) && typeof rawData.message === "string" ? rawData.message : undefined) ??
      (isRecord(rawData) && isRecord(rawData.error) && typeof rawData.error.message === "string"
        ? rawData.error.message
        : undefined) ??
      "Request failed";
    const code =
      response.status === 401
        ? "AUTH_EXPIRED"
        : response.status === 403
          ? "FORBIDDEN"
          : "UPSTREAM_ERROR";
    return toError(response.status, code, upstreamMessage, rawData);
  }

  return { ok: true as const, status: response.status, data: normalizeSuccessPayload(rawData), requestId };
}

export function toError(status: number, code: string, message: string, details?: unknown): BffError {
  return {
    ok: false,
    status,
    code,
    message,
    details,
  };
}
