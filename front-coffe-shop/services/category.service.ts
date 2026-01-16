// services/category.service.ts
import { api } from "./api";
import { Category } from "@/types/category";

export const CategoryService = {
  async getAll(): Promise<Category[]> {
    const res = await api.get("/categories");
    return res.data;
  },
};
