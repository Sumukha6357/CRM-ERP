import { AxiosError } from "axios";
import { toast } from "sonner";

export type BffError = {
  ok?: false;
  status?: number;
  code?: string;
  message?: string;
  details?: unknown;
};

export function extractErrorCode(error: unknown): string {
  const axiosError = error as AxiosError<BffError>;
  return axiosError?.response?.data?.code ?? "UNKNOWN_ERROR";
}

export function extractStatus(error: unknown): number | undefined {
  const axiosError = error as AxiosError<BffError>;
  return axiosError?.response?.status;
}

export function toastMutationError(title: string, error: unknown) {
  const code = extractErrorCode(error);
  toast.error(title, { description: `Error code: ${code}` });
}
