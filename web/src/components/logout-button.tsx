"use client";

import { useRouter } from "next/navigation";
import { DropdownMenuItem } from "@/components/ui/dropdown-menu";

export function LogoutButton() {
  const router = useRouter();

  const handleLogout = async () => {
    await fetch("/api/auth/logout", { method: "POST" });
    router.replace("/login");
  };

  return <DropdownMenuItem onClick={handleLogout}>Logout</DropdownMenuItem>;
}
