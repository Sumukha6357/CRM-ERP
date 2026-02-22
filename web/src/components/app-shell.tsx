"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  Bell,
  ChevronDown,
  Gauge,
  Handshake,
  KanbanSquare,
  LayoutPanelTop,
  Menu,
  Search,
  ShieldCheck,
  Sparkles,
} from "lucide-react";
import { useAuthStore } from "@/store/auth";
import { useUiStore } from "@/store/ui";
import { PermissionGate } from "@/components/permission-gate";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { ThemeToggle } from "@/components/theme-toggle";
import { Separator } from "@/components/ui/separator";
import { hasPermission } from "@/lib/permissions";
import { cn } from "@/lib/utils";
import { LogoutButton } from "@/components/logout-button";

type NavItem = {
  label: string;
  href: string;
  icon: React.ComponentType<{ className?: string }>;
  permission?: string;
};

type NavGroup = {
  label: string;
  items: NavItem[];
};

const navGroups: NavGroup[] = [
  {
    label: "Core",
    items: [
      { label: "Dashboard", href: "/dashboard", icon: Gauge },
      { label: "Notifications", href: "/notifications", icon: Bell, permission: "NOTIFICATION_READ" },
    ],
  },
  {
    label: "CRM",
    items: [
      { label: "Leads", href: "/crm/leads", icon: Handshake, permission: "CRM_LEAD_READ" },
      { label: "Deals", href: "/crm/deals", icon: KanbanSquare, permission: "CRM_DEAL_READ" },
      { label: "Activities", href: "/crm/activities", icon: LayoutPanelTop, permission: "CRM_ACTIVITY_READ" },
    ],
  },
  {
    label: "Workflow",
    items: [{ label: "Workflows", href: "/workflows", icon: Sparkles, permission: "WORKFLOW_READ" }],
  },
  {
    label: "Admin",
    items: [{ label: "RBAC", href: "/admin/rbac", icon: ShieldCheck, permission: "SYS_ADMIN" }],
  },
];

export function AppShell({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { sidebarCollapsed, toggleSidebar } = useUiStore();
  const user = useAuthStore((state) => state.user);
  const appName = process.env.NEXT_PUBLIC_APP_NAME ?? "Gully CRM";
  const envLabel = process.env.NEXT_PUBLIC_ENV ?? "local";
  const showFooter = process.env.NODE_ENV !== "production";
  const apiBase = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

  const visibleGroups = navGroups
    .map((group) => ({
      ...group,
      items: group.items.filter((item) => hasPermission(user, item.permission)),
    }))
    .filter((group) => group.items.length > 0);

  return (
    <div className="min-h-screen flex page-enter">
      <aside
        className={cn(
          "hidden md:flex flex-col border-r border-border/60 bg-sidebar/80 backdrop-blur-xl transition-all duration-300",
          sidebarCollapsed ? "w-20" : "w-64"
        )}
      >
        <div className="flex items-center justify-between p-4 border-b border-border/60">
          <div className="flex items-center gap-2">
            <div className="h-10 w-10 rounded-2xl bg-gradient-to-br from-primary to-cyan-400 text-primary-foreground flex items-center justify-center font-semibold shadow-lg shadow-primary/25">
              GC
            </div>
            {!sidebarCollapsed && (
              <div>
                <div className="text-sm font-semibold tracking-tight">{appName}</div>
                <div className="text-xs text-muted-foreground">Operational Command</div>
              </div>
            )}
          </div>
          <Button variant="ghost" size="icon" onClick={toggleSidebar} aria-label="Toggle sidebar" className="hover:bg-sidebar-accent">
            <Menu className="h-4 w-4" />
          </Button>
        </div>
        <nav className="flex-1 overflow-y-auto px-3 pb-6 pt-4 space-y-5">
          {visibleGroups.map((group) => (
            <div key={group.label} className="space-y-2">
              {!sidebarCollapsed && (
                <div className="text-[11px] uppercase tracking-wider text-muted-foreground px-2">
                  {group.label}
                </div>
              )}
              <div className="space-y-1">
                {group.items.map((item) => (
                  <Link
                    key={item.href}
                    href={item.href}
                    className={cn(
                      "group flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm transition-all duration-200",
                      pathname === item.href
                        ? "bg-primary text-primary-foreground shadow-lg shadow-primary/20"
                        : "text-muted-foreground hover:bg-sidebar-accent hover:text-foreground"
                    )}
                  >
                    <item.icon
                      className={cn(
                        "h-4 w-4",
                        pathname === item.href ? "text-primary-foreground" : "text-muted-foreground group-hover:text-foreground"
                      )}
                    />
                    {!sidebarCollapsed && item.label}
                  </Link>
                ))}
              </div>
            </div>
          ))}
        </nav>
        <div className="p-4 border-t border-border/60">
          <ThemeToggle />
        </div>
      </aside>

      <div className="flex-1 flex flex-col">
        <header className="sticky top-0 z-30 border-b border-border/70 bg-background/75 backdrop-blur-xl">
          <div className="flex items-center gap-4 px-4 py-3">
            <Sheet>
              <SheetTrigger asChild>
                <Button variant="ghost" size="icon" className="md:hidden">
                  <Menu className="h-5 w-5" />
                </Button>
              </SheetTrigger>
              <SheetContent side="left" className="w-72 p-0">
                <div className="p-4 border-b">
                  <div className="font-semibold">{appName}</div>
                  <div className="text-xs text-muted-foreground">Operational Command</div>
                </div>
                <div className="p-4 space-y-4">
                  {visibleGroups.map((group) => (
                    <div key={group.label} className="space-y-2">
                      <div className="text-[11px] uppercase tracking-wider text-muted-foreground">
                        {group.label}
                      </div>
                      <div className="space-y-1">
                        {group.items.map((item) => (
                          <Link
                            key={item.href}
                            href={item.href}
                            className={cn(
                              "flex items-center gap-2 rounded-xl px-3 py-2.5 text-sm",
                              pathname === item.href
                                ? "bg-primary text-primary-foreground"
                                : "text-muted-foreground hover:bg-muted hover:text-foreground"
                            )}
                          >
                            <item.icon className="h-4 w-4" />
                            {item.label}
                          </Link>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </SheetContent>
            </Sheet>

            <div className="flex items-center gap-2">
              <div className="text-sm font-semibold tracking-tight">Default Org</div>
              <span className="rounded-full border border-border/70 bg-card/70 px-2 py-0.5 text-[11px] uppercase tracking-wider text-muted-foreground">
                {envLabel}
              </span>
            </div>

            <div className="flex-1 max-w-xl">
              <div className="relative">
                <Search className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input placeholder="Search leads, deals, activities..." className="pl-9 rounded-xl bg-card/65" />
              </div>
            </div>

            <div className="flex items-center gap-2">
              <PermissionGate permission="NOTIFICATION_READ">
                <Button variant="ghost" size="icon" asChild>
                  <Link href="/notifications">
                    <Bell className="h-5 w-5" />
                  </Link>
                </Button>
              </PermissionGate>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="gap-2 rounded-xl">
                    <div className="text-left">
                      <div className="text-xs text-muted-foreground">Signed in</div>
                      <div className="text-sm font-medium">{user?.fullName ?? "User"}</div>
                    </div>
                    <ChevronDown className="h-4 w-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem asChild>
                    <Link href="/profile">Profile</Link>
                  </DropdownMenuItem>
                  <Separator />
                  <LogoutButton />
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          </div>
          <div className="px-4 pb-3">
            <Breadcrumbs pathname={pathname} />
          </div>
        </header>

        <main className="flex-1 p-4 md:p-6 space-y-6">{children}</main>
        {showFooter && (
          <footer className="border-t border-border/70 bg-background/70 px-6 py-3 text-xs text-muted-foreground">
            <div className="flex flex-wrap items-center gap-3">
              <span>Backend: {apiBase}</span>
              <span>User: {user?.email ?? "unknown"}</span>
              <span>Permissions: {user?.permissions?.length ?? 0}</span>
            </div>
          </footer>
        )}
      </div>
    </div>
  );
}

function Breadcrumbs({ pathname }: { pathname: string }) {
  const parts = pathname.split("/").filter(Boolean);
  return (
    <div className="text-xs text-muted-foreground flex items-center gap-2">
      <span>Home</span>
      {parts.map((part) => (
        <span key={part} className="flex items-center gap-2">
          <span>/</span>
          <span className="capitalize">{part.replace("-", " ")}</span>
        </span>
      ))}
    </div>
  );
}
