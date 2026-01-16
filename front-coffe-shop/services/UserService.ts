// services/user.service.ts
import { api } from "./api";

export const UserService = {

  // 🔹 Mise à jour des infos texte
  async updateProfile(data: {
    nom: string;
    prenom: string;
    telephone?: string;
    cin?: string;
    password?: string;
  }) {
    const res = await api.put("/user/me", data);
    return res.data;
  },

  // 🔹 Mise à jour de la photo (multipart)
  async updatePhoto(photo: any) {
    const res = await api.put("/user/me", photo, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return res.data;
  },
};
