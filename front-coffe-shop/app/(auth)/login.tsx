import { router } from "expo-router";
import React from "react";
import { Alert, StyleSheet, Text, View } from "react-native";

import FormLogin from "@/components/form/FormLogin";
import { AuthService } from "@/services/auth.service";
import { useAuth } from "../context/AuthContext";


export default function LoginScreen() {
  const { setUser } = useAuth(); // 🔥 récupère le setter global

  const handleLogin = async (email: string, password: string) => {
    try {
      // 🔥 ON GARDE la réponse cette fois
      const user = await AuthService.login({ email, password });

      // 🔥 on stocke l'utilisateur connecté
      setUser(user);

      Alert.alert("Succès", `Bienvenue ${user.prenom} 👋`);
      router.replace("/(tabs)/home");
    } catch (e: any) {
      Alert.alert("Erreur", "Email ou mot de passe incorrect");
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Bienvenue 👋</Text>
      <Text style={styles.subtitle}>Connectez-vous pour continuer</Text>

      <FormLogin
        onLogin={handleLogin}
        onRegister={() => router.push("/(auth)/register")}
      />
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
    fontSize: 34,
    fontWeight: "bold",
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    marginBottom: 40,
    color: "#444",
  },
});
