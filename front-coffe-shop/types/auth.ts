// types/auth.ts
export type LoginPayload = {
  email: string;
  password: string;
};

export type RegisterPayload = {
  nom: string;
  prenom: string;
  email: string;
  password: string;
  telephone?: string;
  cin?: string;
  photo?: string;
  role: "CLIENT" | "ADMIN";
};

// types/auth.ts
export type UserInfo = {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  photo?: string;
  telephone?: string;
  cin?: string;
  roles: string[];
};