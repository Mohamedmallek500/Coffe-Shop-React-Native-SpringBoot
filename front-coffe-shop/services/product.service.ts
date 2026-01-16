// services/product.service.ts
import { api } from "./api";
import { Product } from "@/types/product";

export const ProductService = {
  async getAll(): Promise<Product[]> {
    const res = await api.get("/products");
    return res.data;
  },

  async getByCategory(categoryId: number): Promise<Product[]> {
    const res = await api.get(`/products/category/${categoryId}`);
    return res.data;
  },
};
