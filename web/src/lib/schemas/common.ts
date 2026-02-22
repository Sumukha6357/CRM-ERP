import { z } from "zod";

export const PageSchema = z.object({
  content: z.array(z.unknown()),
  totalElements: z.number().optional(),
  totalPages: z.number().optional(),
}).passthrough();

export function validatePage<T>(schema: z.ZodType<T>, data: unknown, sampleSize = 5) {
  const page = PageSchema.safeParse(data);
  if (!page.success) {
    return page;
  }
  const items = (page.data.content ?? []).slice(0, sampleSize);
  for (const item of items) {
    const parsed = schema.safeParse(item);
    if (!parsed.success) {
      return parsed;
    }
  }
  return page;
}
