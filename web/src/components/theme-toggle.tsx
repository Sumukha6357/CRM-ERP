"use client";

import { Moon, Sun } from "lucide-react";
import { useTheme } from "next-themes";
import { Button } from "@/components/ui/button";

export function ThemeToggle() {
  const { setTheme, theme } = useTheme();
  const isDark = theme === "dark";

  return (
    <Button
      variant="outline"
      className="w-full justify-between gap-2 rounded-xl"
      onClick={() => setTheme(isDark ? "light" : "dark")}
    >
      <span className="inline-flex items-center gap-2">
        {isDark ? <Sun className="h-4 w-4 text-amber-400" /> : <Moon className="h-4 w-4 text-sky-500" />}
        {isDark ? "Light Mode" : "Dark Mode"}
      </span>
      <span className="text-[11px] uppercase tracking-wider text-muted-foreground">
        {isDark ? "ON" : "OFF"}
      </span>
    </Button>
  );
}
