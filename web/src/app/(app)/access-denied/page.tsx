import { ErrorState } from "@/components/error-state";

export default function AccessDeniedPage() {
  return (
    <ErrorState
      title="Access denied"
      description="You do not have permission to view this page."
    />
  );
}
