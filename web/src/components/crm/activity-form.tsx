"use client";

import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";

const schema = z.object({
  activityType: z.string().min(2),
  subject: z.string().min(2),
  notes: z.string().optional(),
  dueAt: z.string().optional(),
  leadId: z.string().optional(),
  dealId: z.string().optional(),
  ownerId: z.string().optional(),
});

export type ActivityFormValues = z.infer<typeof schema>;

export function ActivityForm({
  onSubmit,
}: {
  onSubmit: (values: ActivityFormValues) => void;
}) {
  const form = useForm<ActivityFormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      activityType: "CALL",
      subject: "",
    },
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="grid gap-4 md:grid-cols-2">
        <FormField
          control={form.control}
          name="activityType"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Type</FormLabel>
              <FormControl>
                <Input placeholder="CALL" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="subject"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Subject</FormLabel>
              <FormControl>
                <Input placeholder="Discovery call" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="dueAt"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Due At</FormLabel>
              <FormControl>
                <Input type="datetime-local" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="ownerId"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Owner ID</FormLabel>
              <FormControl>
                <Input placeholder="UUID" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="leadId"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Lead ID</FormLabel>
              <FormControl>
                <Input placeholder="UUID" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="dealId"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Deal ID</FormLabel>
              <FormControl>
                <Input placeholder="UUID" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="notes"
          render={({ field }) => (
            <FormItem className="md:col-span-2">
              <FormLabel>Notes</FormLabel>
              <FormControl>
                <Textarea placeholder="Add notes..." {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <div className="md:col-span-2">
          <Button type="submit">Create Activity</Button>
        </div>
      </form>
    </Form>
  );
}
