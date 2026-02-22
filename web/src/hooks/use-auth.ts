import { useEffect } from "react";
import { useMe } from "@/hooks/api-hooks";
import { useAuthStore } from "@/store/auth";

export function useAuth() {
  const { user, setUser } = useAuthStore();

  const query = useMe();

  useEffect(() => {
    if (query.data) {
      setUser(query.data);
    }
  }, [query.data, setUser]);

  return {
    ...query,
    user: query.data ?? user,
  };
}
