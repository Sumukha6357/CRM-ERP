import axios from "axios";
import { toast } from "sonner";
import { extractErrorCode, extractStatus } from "@/lib/errors";

export const api = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const status = extractStatus(error);
    const code = extractErrorCode(error);
    const url = error?.config?.url ?? "";

    if (typeof window !== "undefined" && url && !url.includes("/auth/login")) {
      if (status === 401 || code === "AUTH_EXPIRED") {
        await fetch("/api/auth/logout", { method: "POST" });
        toast.error("Session expired");
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);
