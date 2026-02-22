"use client";

import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

const schema = z.object({
  name: z.string().min(2),
  email: z.string().email().optional().or(z.literal("")),
  phone: z.string().optional().or(z.literal("")),
  company: z.string().optional().or(z.literal("")),
  value: z
    .union([z.number(), z.string()])
    .optional()
    .refine((value) => value === undefined || value === "" || !Number.isNaN(Number(value)), {
      message: "Value must be a number",
    })
    .transform((value) => (value === undefined || value === "" ? undefined : Number(value))),
  status: z.string().optional(),
  source: z.string().optional(),
  stageId: z.string().optional(),
  ownerId: z.string().optional(),
});

export type LeadFormValues = z.input<typeof schema>;
export type LeadFormSubmitValues = z.output<typeof schema>;

export function LeadForm({
  defaultValues,
  onSubmit,
  submitLabel = "Save",
}: {
  defaultValues?: Partial<LeadFormValues>;
  onSubmit: (values: LeadFormSubmitValues) => void;
  submitLabel?: string;
}) {
  const form = useForm<LeadFormValues, unknown, LeadFormSubmitValues>({
    resolver: zodResolver(schema),
    defaultValues: defaultValues ?? {
      name: "",
      email: "",
      phone: "",
      company: "",
      status: "OPEN",
      source: "",
    },
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="grid gap-4 md:grid-cols-2">
        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Name</FormLabel>
              <FormControl>
                <Input placeholder="Lead name" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="email"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Email</FormLabel>
              <FormControl>
                <Input placeholder="lead@company.com" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="phone"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Phone</FormLabel>
              <FormControl>
                <Input placeholder="+1 555 555 5555" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="company"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Company</FormLabel>
              <FormControl>
                <Input placeholder="Company name" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="value"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Value</FormLabel>
              <FormControl>
                <Input type="number" placeholder="0" {...field} />
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
          name="source"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Source</FormLabel>
              <FormControl>
                <Input placeholder="Inbound" {...field} />
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
