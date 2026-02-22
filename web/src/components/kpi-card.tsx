"use client";

import { useEffect, useMemo, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Activity, CircleDollarSign, Handshake, TrendingUp } from "lucide-react";

export function KpiCard({
  title,
  value,
  trend,
}: {
  title: string;
  value: string | number;
  trend?: string;
}) {
  const animatedValue = useAnimatedMetric(value);

  const Icon =
    title.toLowerCase().includes("lead")
      ? Handshake
      : title.toLowerCase().includes("deal") || title.toLowerCase().includes("pipeline")
        ? CircleDollarSign
        : title.toLowerCase().includes("activit")
          ? Activity
          : TrendingUp;

  return (
    <Card className="hover-lift">
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <CardTitle className="text-sm font-medium text-muted-foreground">{title}</CardTitle>
        <div className="flex items-center gap-2">
          {trend && <Badge variant="secondary">{trend}</Badge>}
          <span className="inline-flex h-7 w-7 items-center justify-center rounded-lg bg-accent/70 text-accent-foreground">
            <Icon className="h-4 w-4" />
          </span>
        </div>
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-semibold tracking-tight">{animatedValue}</div>
      </CardContent>
    </Card>
  );
}

function useAnimatedMetric(value: string | number) {
  const parsed = useMemo(() => parseDisplayValue(value), [value]);
  const [display, setDisplay] = useState(() => formatDisplayValue(parsed, parsed.numeric ?? 0));

  useEffect(() => {
    if (parsed.numeric === null) {
      setDisplay(parsed.raw);
      return;
    }

    if (typeof window !== "undefined" && window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
      setDisplay(formatDisplayValue(parsed, parsed.numeric));
      return;
    }

    let frame = 0;
    const duration = 850;
    const start = performance.now();
    const target = parsed.numeric;

    const tick = (now: number) => {
      const elapsed = now - start;
      const progress = Math.min(1, elapsed / duration);
      const eased = 1 - Math.pow(1 - progress, 3);
      const current = target * eased;
      setDisplay(formatDisplayValue(parsed, current));
      if (progress < 1) {
        frame = requestAnimationFrame(tick);
      }
    };

    frame = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(frame);
  }, [parsed]);

  return display;
}

function parseDisplayValue(value: string | number) {
  if (typeof value === "number") {
    return { raw: String(value), prefix: "", suffix: "", numeric: value };
  }

  const raw = String(value);
  const match = raw.match(/^([^0-9-]*)(-?\d+(?:\.\d+)?)(.*)$/);
  if (!match) {
    return { raw, prefix: "", suffix: "", numeric: null as number | null };
  }

  return {
    raw,
    prefix: match[1] ?? "",
    suffix: match[3] ?? "",
    numeric: Number(match[2]),
  };
}

function formatDisplayValue(
  parsed: { raw: string; prefix: string; suffix: string; numeric: number | null },
  current: number
) {
  if (parsed.numeric === null) {
    return parsed.raw;
  }

  const rounded = Math.abs(parsed.numeric) >= 100 ? Math.round(current) : Number(current.toFixed(1));
  return `${parsed.prefix}${rounded}${parsed.suffix}`;
}
