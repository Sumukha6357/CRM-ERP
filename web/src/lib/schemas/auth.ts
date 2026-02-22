import { z } from "zod";

export const AuthMeResponseSchema = z.object({
  id: z.string().uuid(),
  orgId: z.string().uuid(),
  email: z.string().email(),
  fullName: z.string(),
  roles: z.array(z.string()),
  permissions: z.array(z.string()),
});
