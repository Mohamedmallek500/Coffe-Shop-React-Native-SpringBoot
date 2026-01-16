// types/product.ts
export type Product = {
  id: number;
  name: string;
  description: string;
  basePrice: number;
  imageUrl: string;
  category: {
    id: number;
    name: string;
  };
};
