import { z } from "zod";

export const LeadSchema = z.object({
  id: z.string().uuid(),
  name: z.string(),
  email: z.string().optional().nullable(),
  phone: z.string().optional().nullable(),
  company: z.string().optional().nullable(),
  value: z.number().optional().nullable(),
  status: z.string().optional().nullable(),
  source: z.string().optional().nullable(),
  stageId: z.string().uuid().optional().nullable(),
  ownerId: z.string().uuid().optional().nullable(),
  createdAt: z.string().optional().nullable(),
  updatedAt: z.string().optional().nullable(),
});

export const DealSchema = z.object({
  id: z.string().uuid(),
  title: z.string(),
  amount: z.number().optional().nullable(),
  status: z.string().optional().nullable(),
  expectedCloseDate: z.string().optional().nullable(),
  stageId: z.string().uuid().optional().nullable(),
  ownerId: z.string().uuid().optional().nullable(),
  createdAt: z.string().optional().nullable(),
  updatedAt: z.string().optional().nullable(),
});

export const ActivitySchema = z.object({
  id: z.string().uuid(),
  activityType: z.string(),
  subject: z.string(),
  notes: z.string().optional().nullable(),
  dueAt: z.string().optional().nullable(),
  leadId: z.string().uuid().optional().nullable(),
  dealId: z.string().uuid().optional().nullable(),
  ownerId: z.string().uuid().optional().nullable(),
  createdAt: z.string().optional().nullable(),
});
