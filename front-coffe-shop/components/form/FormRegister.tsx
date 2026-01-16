// components/form/FormRegister.tsx
import React, { useState } from "react";
import { View, TextInput, StyleSheet, TouchableOpacity, Text } from "react-native";

type Props = {
  onSubmit: (data: {
    nom: string;
    prenom: string;
    email: string;
    password: string;
  }) => void;
};

export default function FormRegister({ onSubmit }: Props) {
  const [form, setForm] = useState({
    nom: "",
    prenom: "",
    email: "",
    password: "",
  });

  return (
    <View>
      <TextInput
        placeholder="Nom"
        style={styles.input}
        value={form.nom}
        onChangeText={(v) => setForm({ ...form, nom: v })}
      />

      <TextInput
        placeholder="Prénom"
        style={styles.input}
        value={form.prenom}
        onChangeText={(v) => setForm({ ...form, prenom: v })}
      />

      <TextInput
        placeholder="Email"
        style={styles.input}
        autoCapitalize="none"
        value={form.email}
        onChangeText={(v) => setForm({ ...form, email: v })}
      />

      <TextInput
        placeholder="Mot de passe"
        style={styles.input}
        secureTextEntry
        value={form.password}
        onChangeText={(v) => setForm({ ...form, password: v })}
      />

      <TouchableOpacity
        style={styles.button}
        onPress={() => onSubmit(form)}
      >
        <Text style={styles.buttonText}>Créer un compte</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  input: {
    backgroundColor: "#fff",
    padding: 14,
    borderRadius: 10,
    marginBottom: 16,
  },
  button: {
    backgroundColor: "#3E2723",
    padding: 16,
    borderRadius: 10,
    alignItems: "center",
  },
  buttonText: {
    color: "#fff",
    fontWeight: "bold",
  },
});
