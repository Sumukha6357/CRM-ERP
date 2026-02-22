import { z } from "zod";

export const WorkflowInstanceSchema = z.object({
  id: z.string().uuid(),
  definitionCode: z.string(),
  status: z.string(),
  currentStep: z.string().optional().nullable(),
  data: z.string().optional().nullable(),
  createdAt: z.string().optional().nullable(),
});
