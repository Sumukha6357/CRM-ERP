import { NextRequest, NextResponse } from "next/server";

const PUBLIC_ROUTES = ["/login"];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  if (pathname.startsWith("/api") || pathname.startsWith("/_next") || pathname === "/favicon.ico") {
    return NextResponse.next();
  }

  if (PUBLIC_ROUTES.includes(pathname)) {
    return NextResponse.next();
  }

  const token = request.cookies.get("access_token")?.value;
  if (!token) {
    const url = request.nextUrl.clone();
    url.pathname = "/login";
    return NextResponse.redirect(url);
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!_next/static|_next/image).*)"],
};
