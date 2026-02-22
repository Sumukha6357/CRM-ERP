import { z } from "zod";

export const NotificationSchema = z.object({
  id: z.string().uuid(),
  title: z.string(),
  body: z.string().optional().nullable(),
  type: z.string(),
  data: z.string().optional().nullable(),
  readAt: z.string().optional().nullable(),
  createdAt: z.string().optional().nullable(),
});
