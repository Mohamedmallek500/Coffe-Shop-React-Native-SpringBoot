// app/(auth)/register.tsx
import FormRegister from "@/components/form/FormRegister";
import { AuthService } from "@/services/auth.service";
import { router } from "expo-router";
import React from "react";
import { Alert, StyleSheet, Text, View } from "react-native";

export default function RegisterScreen() {

  const handleRegister = async (data: {
    nom: string;
    prenom: string;
    email: string;
    password: string;
  }) => {
    try {
      await AuthService.register({
        ...data,
        role: "CLIENT", // 🔥 OBLIGATOIRE pour ton backend
      });

      Alert.alert(
        "Succès",
        "Compte créé avec succès. Vous pouvez vous connecter."
      );

      router.replace("/(auth)/login");
    } catch (error: any) {
      Alert.alert(
        "Erreur",
        error?.response?.data?.message || "Erreur lors de l'inscription"
      );
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Créer un compte ✨</Text>
      <Text style={styles.subtitle}>
        Remplissez le formulaire pour continuer
      </Text>

      <FormRegister onSubmit={handleRegister} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F6E6CF",
    padding: 24,
    justifyContent: "center",
  },
  title: {
    fontSize: 32,
    fontWeight: "bold",
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    marginBottom: 40,
    color: "#444",
  },
});
