import { create } from "zustand";

export type AuthUser = {
  id: string;
  orgId: string;
  email: string;
  fullName: string;
  roles: string[];
  permissions: string[];
};

type AuthState = {
  user: AuthUser | null;
  setUser: (user: AuthUser | null) => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  setUser: (user) => set({ user }),
}));
