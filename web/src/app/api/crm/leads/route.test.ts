import { describe, it, expect, vi, beforeEach } from "vitest";
import { GET } from "./route";

vi.mock("next/headers", () => ({
  cookies: () => ({
    get: () => ({ value: "test-token" }),
  }),
}));

describe("BFF leads route", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it("returns data when schema is valid", async () => {
    vi.stubGlobal("fetch", vi.fn(async () =>
      new Response(
        JSON.stringify({
          content: [
            {
              id: "11111111-1111-1111-1111-111111111111",
              name: "Lead One",
            },
          ],
        }),
        { status: 200, headers: { "content-type": "application/json" } }
      )
    ));

    const request = new Request("http://localhost/api/crm/leads");
    const response = await GET(request as any);
    const data = await response.json();
    expect(response.status).toBe(200);
    expect(data.content.length).toBe(1);
  });

  it("returns schema mismatch when invalid payload", async () => {
    vi.stubGlobal("fetch", vi.fn(async () =>
      new Response(
        JSON.stringify({ content: [{ name: "Missing ID" }] }),
        { status: 200, headers: { "content-type": "application/json" } }
      )
    ));

    const request = new Request("http://localhost/api/crm/leads");
    const response = await GET(request as any);
    const data = await response.json();
    expect(response.status).toBe(502);
    expect(data.code).toBe("UPSTREAM_SCHEMA_MISMATCH");
  });

  it("normalizes auth expired", async () => {
    vi.stubGlobal("fetch", vi.fn(async () =>
      new Response(JSON.stringify({ message: "Unauthorized" }), {
        status: 401,
        headers: { "content-type": "application/json" },
      })
    ));

    const request = new Request("http://localhost/api/crm/leads");
    const response = await GET(request as any);
    const data = await response.json();
    expect(response.status).toBe(401);
    expect(data.code).toBe("AUTH_EXPIRED");
  });

  it("wraps non-json upstream error", async () => {
    vi.stubGlobal("fetch", vi.fn(async () =>
      new Response("<html>error</html>", {
        status: 500,
        headers: { "content-type": "text/html" },
      })
    ));

    const request = new Request("http://localhost/api/crm/leads");
    const response = await GET(request as any);
    const data = await response.json();
    expect(response.status).toBe(500);
    expect(data.code).toBe("UPSTREAM_NON_JSON");
  });
});
