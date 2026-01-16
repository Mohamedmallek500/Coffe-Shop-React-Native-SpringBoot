// services/auth.service.ts
import { api } from "./api";
import { LoginPayload, RegisterPayload, UserInfo } from "@/types/auth";

export const AuthService = {
  async login(data: LoginPayload): Promise<UserInfo> {
    const res = await api.post("/auth/signin", data);
    return res.data;
  },

  async register(data: RegisterPayload): Promise<void> {
    await api.post("/auth/signup", data);
  },

  async logout(): Promise<void> {
    await api.post("/auth/signout");
  },

  async refresh(): Promise<void> {
    await api.post("/auth/refreshtoken");
  },
};
