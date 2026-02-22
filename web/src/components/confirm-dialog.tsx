import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

export function ConfirmDialog({
  open,
  title,
  description,
  onCancel,
  onConfirm,
  confirmLabel = "Confirm",
}: {
  open: boolean;
  title: string;
  description?: string;
  onCancel: () => void;
  onConfirm: () => void;
  confirmLabel?: string;
}) {
  return (
    <Dialog open={open} onOpenChange={(value) => !value && onCancel()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          {description && <DialogDescription>{description}</DialogDescription>}
        </DialogHeader>
        <DialogFooter>
          <Button variant="ghost" onClick={onCancel}>
            Cancel
          </Button>
          <Button variant="destructive" onClick={onConfirm}>
            {confirmLabel}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
