"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { AuthMeResponseSchema } from "@/lib/schemas/auth";
import { ActivitySchema, DealSchema, LeadSchema } from "@/lib/schemas/crm";
import { WorkflowInstanceSchema } from "@/lib/schemas/workflows";
import { NotificationSchema } from "@/lib/schemas/notifications";
import { validatePage } from "@/lib/schemas/common";
import { toastMutationError } from "@/lib/errors";
import { z } from "zod";

type SchemaError = Error & {
  response: {
    data: {
      code: string;
      details: unknown;
    };
  };
};

function schemaError(details: unknown) {
  const error = new Error("Schema mismatch") as SchemaError;
  error.response = { data: { code: "UPSTREAM_SCHEMA_MISMATCH", details } };
  return error;
}

export function useMe() {
  return useQuery({
    queryKey: ["auth", "me"],
    queryFn: async () => {
      const res = await api.get("/auth/me");
      const parsed = AuthMeResponseSchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
  });
}

export type Activity = z.infer<typeof ActivitySchema>;
export type WorkflowInstance = z.infer<typeof WorkflowInstanceSchema>;
export type NotificationItem = z.infer<typeof NotificationSchema>;
export type Lead = z.infer<typeof LeadSchema>;
export type Deal = z.infer<typeof DealSchema>;

export type LeadInput = Partial<Lead> & Pick<Lead, "name">;
export type DealInput = Partial<Deal> & Pick<Deal, "title">;
export type ActivityInput = Partial<Activity> & Pick<Activity, "activityType" | "subject">;

export function useLeads(params: { page: number; size: number; status?: string; stage?: string; owner?: string }) {
  return useQuery({
    queryKey: ["leads", params],
    queryFn: async () => {
      const res = await api.get("/crm/leads", { params });
      const parsed = validatePage(LeadSchema, res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return res.data;
    },
  });
}

export function useLead(id: string) {
  return useQuery({
    queryKey: ["lead", id],
    queryFn: async () => {
      const res = await api.get(`/crm/leads/${id}`);
      const parsed = LeadSchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
  });
}

export function useCreateLead() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (values: LeadInput) => {
      const res = await api.post("/crm/leads", values);
      const parsed = LeadSchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["leads"] });
    },
    onError: (error) => toastMutationError("Failed to create lead", error),
  });
}

export function useUpdateLead(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (values: LeadInput) => {
      const res = await api.put(`/crm/leads/${id}`, values);
      const parsed = LeadSchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["leads"] });
    },
    onError: (error) => toastMutationError("Failed to update lead", error),
  });
}

export function useDeleteLead() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/crm/leads/${id}`);
      return { ok: true };
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["leads"] });
    },
    onError: (error) => toastMutationError("Failed to delete lead", error),
  });
}

export function useDeals(params: { page: number; size: number; status?: string; stage?: string; owner?: string }) {
  return useQuery({
    queryKey: ["deals", params],
    queryFn: async () => {
      const res = await api.get("/crm/deals", { params });
      const parsed = validatePage(DealSchema, res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return res.data;
    },
  });
}

export function useDeal(id: string) {
  return useQuery({
    queryKey: ["deal", id],
    queryFn: async () => {
      const res = await api.get(`/crm/deals/${id}`);
      const parsed = DealSchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
  });
}

export function useCreateDeal() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (values: DealInput) => {
      const res = await api.post("/crm/deals", values);
      const parsed = DealSchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["deals"] });
    },
    onError: (error) => toastMutationError("Failed to create deal", error),
  });
}

export function useUpdateDeal(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (values: DealInput) => {
      const res = await api.put(`/crm/deals/${id}`, values);
      const parsed = DealSchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["deals"] });
    },
    onError: (error) => toastMutationError("Failed to update deal", error),
  });
}

export function useDeleteDeal() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/crm/deals/${id}`);
      return { ok: true };
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["deals"] });
    },
    onError: (error) => toastMutationError("Failed to delete deal", error),
  });
}

export function useActivities(params: { page: number; size: number }) {
  return useQuery({
    queryKey: ["activities", params],
    queryFn: async () => {
      const res = await api.get("/crm/activities", { params });
      const parsed = validatePage(ActivitySchema, res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return res.data;
    },
  });
}

export function useCreateActivity() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (values: ActivityInput) => {
      const res = await api.post("/crm/activities", values);
      const parsed = ActivitySchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["activities"] });
    },
    onError: (error) => toastMutationError("Failed to create activity", error),
  });
}

export function useWorkflows(params: { page: number; size: number }) {
  return useQuery({
    queryKey: ["workflows", params],
    queryFn: async () => {
      const res = await api.get("/workflows/instances", { params });
      const parsed = validatePage(WorkflowInstanceSchema, res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return res.data;
    },
  });
}

export function useApproveWorkflow() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      const res = await api.post(`/workflows/instances/${id}/approve`);
      const parsed = WorkflowInstanceSchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["workflows"] }),
    onError: (error) => toastMutationError("Failed to approve workflow", error),
  });
}

export function useRejectWorkflow() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      const res = await api.post(`/workflows/instances/${id}/reject`);
      const parsed = WorkflowInstanceSchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["workflows"] }),
    onError: (error) => toastMutationError("Failed to reject workflow", error),
  });
}

export function useNotifications(params: { page: number; size: number }) {
  return useQuery({
    queryKey: ["notifications", params],
    queryFn: async () => {
      const res = await api.get("/notifications", { params });
      const parsed = validatePage(NotificationSchema, res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return res.data;
    },
  });
}

export function useMarkNotificationRead() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      const res = await api.put(`/notifications/${id}/read`);
      const parsed = NotificationSchema.safeParse(res.data);
      if (!parsed.success) {
        throw schemaError(parsed.error.format());
      }
      return parsed.data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["notifications"] }),
    onError: (error) => toastMutationError("Failed to update notification", error),
  });
}

export type Role = { id: string; code: string; name: string };
export type Permission = { id: string; code: string; name: string };
export type UserSummary = { id: string; email: string; fullName: string; status: string };

export function useAdminPermissions() {
  return useQuery({
    queryKey: ["admin", "permissions"],
    queryFn: async () => {
      const res = await api.get("/admin/permissions");
      return res.data as Permission[];
    },
  });
}

export function useAdminRoles() {
  return useQuery({
    queryKey: ["admin", "roles"],
    queryFn: async () => {
      const res = await api.get("/admin/roles");
      return res.data as Role[];
    },
  });
}

export function useCreateRole() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (values: { code: string; name: string }) => {
      const res = await api.post("/admin/roles", values);
      return res.data as Role;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin", "roles"] }),
    onError: (error) => toastMutationError("Failed to create role", error),
  });
}

export function useUpdateRole(roleId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (values: { code: string; name: string }) => {
      const res = await api.put(`/admin/roles/${roleId}`, values);
      return res.data as Role;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin", "roles"] }),
    onError: (error) => toastMutationError("Failed to update role", error),
  });
}

export function useDeleteRole() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (roleId: string) => {
      await api.delete(`/admin/roles/${roleId}`);
      return { ok: true };
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin", "roles"] }),
    onError: (error) => toastMutationError("Failed to delete role", error),
  });
}

export function useRolePermissions(roleId?: string) {
  return useQuery({
    queryKey: ["admin", "roles", roleId, "permissions"],
    queryFn: async () => {
      const res = await api.get(`/admin/roles/${roleId}/permissions`);
      return res.data as string[];
    },
    enabled: !!roleId,
  });
}

export function useUpdateRolePermissions(roleId?: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (permissions: string[]) => {
      const res = await api.put(`/admin/roles/${roleId}/permissions`, { permissions });
      return res.data as string[];
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "roles", roleId, "permissions"] });
      queryClient.invalidateQueries({ queryKey: ["auth", "me"] });
    },
    onError: (error) => toastMutationError("Failed to update role permissions", error),
  });
}

export function useAdminUsers(params: { page: number; size: number; search?: string }) {
  return useQuery({
    queryKey: ["admin", "users", params],
    queryFn: async () => {
      const res = await api.get("/admin/users", { params: { page: params.page, size: params.size, q: params.search } });
      return res.data as { content: UserSummary[]; totalElements: number };
    },
  });
}

export function useUserRoles(userId?: string) {
  return useQuery({
    queryKey: ["admin", "users", userId, "roles"],
    queryFn: async () => {
      const res = await api.get(`/admin/users/${userId}/roles`);
      return res.data as string[];
    },
    enabled: !!userId,
  });
}

export function useUpdateUserRoles(userId?: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (roleIds: string[]) => {
      const res = await api.put(`/admin/users/${userId}/roles`, { roleIds });
      return res.data as string[];
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "users"] });
      queryClient.invalidateQueries({ queryKey: ["admin", "users", userId, "roles"] });
      queryClient.invalidateQueries({ queryKey: ["auth", "me"] });
    },
    onError: (error) => toastMutationError("Failed to update user roles", error),
  });
}
