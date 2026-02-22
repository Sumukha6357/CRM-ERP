"use client";

import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

const schema = z.object({
  title: z.string().min(2),
  amount: z
    .union([z.number(), z.string()])
    .optional()
    .refine((value) => value === undefined || value === "" || !Number.isNaN(Number(value)), {
      message: "Amount must be a number",
    })
    .transform((value) => (value === undefined || value === "" ? undefined : Number(value))),
  expectedCloseDate: z.string().optional(),
  status: z.string().optional(),
  stageId: z.string().optional(),
  ownerId: z.string().optional(),
});

export type DealFormValues = z.input<typeof schema>;
export type DealFormSubmitValues = z.output<typeof schema>;

export function DealForm({
  defaultValues,
  onSubmit,
  submitLabel = "Save",
}: {
  defaultValues?: Partial<DealFormValues>;
  onSubmit: (values: DealFormSubmitValues) => void;
  submitLabel?: string;
}) {
  const form = useForm<DealFormValues, unknown, DealFormSubmitValues>({
    resolver: zodResolver(schema),
    defaultValues: defaultValues ?? {
      title: "",
      status: "OPEN",
    },
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="grid gap-4 md:grid-cols-2">
        <FormField
          control={form.control}
          name="title"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Title</FormLabel>
              <FormControl>
                <Input placeholder="Deal title" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="amount"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Amount</FormLabel>
              <FormControl>
                <Input type="number" placeholder="0" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="expectedCloseDate"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Expected Close Date</FormLabel>
              <FormControl>
                <Input type="date" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="status"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Status</FormLabel>
              <FormControl>
                <Input placeholder="OPEN" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="stageId"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Stage ID</FormLabel>
              <FormControl>
                <Input placeholder="UUID" {...field} />
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
        <div className="md:col-span-2">
          <Button type="submit" className="w-full md:w-auto">
            {submitLabel}
          </Button>
        </div>
      </form>
    </Form>
  );
}
