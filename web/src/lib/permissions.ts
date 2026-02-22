import { AuthUser } from "@/store/auth";

export function hasPermission(user: AuthUser | null | undefined, permission?: string) {
  if (!permission) {
    return true;
  }
  if (!user) {
    return false;
  }
  return user.permissions?.includes(permission) || user.roles?.includes("ADMIN");
}
