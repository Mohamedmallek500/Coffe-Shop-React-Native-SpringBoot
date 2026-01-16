import { useAuth } from "@/app/context/AuthContext";
import { UserService } from "@/services/UserService";
import * as ImagePicker from "expo-image-picker";
import React, { useState } from "react";
import {
    ActivityIndicator,
    Alert,
    Image,
    ScrollView,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
} from "react-native";

export default function ProfileScreen() {
  const { user, setUser } = useAuth();

  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [selectedImage, setSelectedImage] = useState<string | null>(null);

  const [form, setForm] = useState({
    nom: user?.nom || "",
    prenom: user?.prenom || "",
    telephone: user?.telephone || "",
    cin: user?.cin || "",
  });

  if (!user) {
    return (
      <View style={styles.center}>
        <Text>Vous n'êtes pas connecté</Text>
      </View>
    );
  }

  const photoUrl = user.photo
    ? `http://10.0.2.2:9092/uploads/${user.photo}`
    : "https://via.placeholder.com/150";

  // =========================
  //   CHOISIR PHOTO
  // =========================
  const pickImage = async () => {
    const permission = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (!permission.granted) {
      Alert.alert("Permission requise", "Accès à la galerie nécessaire");
      return;
    }

    let result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ["images"],
      allowsEditing: false,
      aspect: [1, 1],
      quality: 0.8,
    });

    if (result.canceled) return;

    setSelectedImage(result.assets[0].uri);
  };

  // =========================
  //   UPLOAD PHOTO
  // =========================
  const uploadPhoto = async () => {
    if (!selectedImage) return;

    const filename = selectedImage.split("/").pop() ?? `photo_${Date.now()}.jpg`;
    const match = /\.(\w+)$/.exec(filename);
    const type = match ? `image/${match[1]}` : "image/jpeg";

    const formData = new FormData();
    // @ts-ignore
    formData.append("photo", {
      uri: selectedImage,
      name: filename,
      type,
    });

    try {
      setLoading(true);
      const updated = await UserService.updatePhoto(formData);
      if (updated) setUser(updated);
      setSelectedImage(null);
      Alert.alert("Succès", "Photo mise à jour !");
    } catch (e: any) {
      Alert.alert("Erreur", e.response?.data || "Impossible de changer la photo");
    } finally {
      setLoading(false);
    }
  };

  // =========================
  //   UPDATE PROFIL
  // =========================
  const saveProfile = async () => {
    try {
      setLoading(true);

      const updated = await UserService.updateProfile({
        nom: form.nom,
        prenom: form.prenom,
        telephone: form.telephone,
        cin: form.cin,
      });

      if (updated) {
        setUser(updated);
      } else {
        setUser({
          ...user,
          nom: form.nom,
          prenom: form.prenom,
          telephone: form.telephone,
          cin: form.cin,
        });
      }

      setEditing(false);
      Alert.alert("Succès", "Profil mis à jour");
    } catch (e: any) {
      Alert.alert("Erreur", e.response?.data || "Échec de la mise à jour");
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={pickImage} disabled={loading}>
          <Image
            source={{ uri: selectedImage || photoUrl }}
            style={styles.avatar}
          />
          {loading && (
            <ActivityIndicator size="large" color="#3E2723" style={styles.loading} />
          )}
        </TouchableOpacity>

        {selectedImage && (
          <TouchableOpacity
            style={[styles.button, styles.saveButton, { marginTop: 12 }]}
            onPress={uploadPhoto}
            disabled={loading}
          >
            <Text style={styles.buttonText}>Enregistrer la photo</Text>
          </TouchableOpacity>
        )}

        <Text style={styles.name}>
          {user.prenom} {user.nom}
        </Text>
        <Text style={styles.role}>{user.roles.join(", ")}</Text>
      </View>

      <View style={styles.form}>
        <Text style={styles.label}>Prénom</Text>
        <TextInput
          style={[styles.input, !editing && styles.inputDisabled]}
          value={form.prenom}
          onChangeText={(v) => setForm({ ...form, prenom: v })}
          editable={editing}
        />

        <Text style={styles.label}>Nom</Text>
        <TextInput
          style={[styles.input, !editing && styles.inputDisabled]}
          value={form.nom}
          onChangeText={(v) => setForm({ ...form, nom: v })}
          editable={editing}
        />

        <Text style={styles.label}>Email</Text>
        <TextInput
          style={[styles.input, styles.inputDisabled]}
          value={user.email}
          editable={false}
        />

        <Text style={styles.label}>Téléphone</Text>
        <TextInput
          style={[styles.input, !editing && styles.inputDisabled]}
          value={form.telephone}
          onChangeText={(v) => setForm({ ...form, telephone: v })}
          editable={editing}
          keyboardType="phone-pad"
        />

        <Text style={styles.label}>CIN</Text>
        <TextInput
          style={[styles.input, !editing && styles.inputDisabled]}
          value={form.cin}
          onChangeText={(v) => setForm({ ...form, cin: v })}
          editable={editing}
        />

        <View style={styles.buttons}>
          {editing ? (
            <>
              <TouchableOpacity
                style={[styles.button, styles.saveButton]}
                onPress={saveProfile}
                disabled={loading}
              >
                <Text style={styles.buttonText}>Sauvegarder</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={[styles.button, styles.cancelButton]}
                onPress={() => {
                  setEditing(false);
                  setForm({
                    nom: user.nom,
                    prenom: user.prenom,
                    telephone: user.telephone || "",
                    cin: user.cin || "",
                  });
                }}
              >
                <Text style={styles.buttonText}>Annuler</Text>
              </TouchableOpacity>
            </>
          ) : (
            <TouchableOpacity
              style={[styles.button, styles.editButton]}
              onPress={() => setEditing(true)}
            >
              <Text style={styles.buttonText}>Modifier le profil</Text>
            </TouchableOpacity>
          )}
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#F6E6CF",
  },
  center: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  header: {
    alignItems: "center",
    padding: 30,
    backgroundColor: "#fff",
    borderBottomLeftRadius: 30,
    borderBottomRightRadius: 30,
  },
  avatar: {
    width: 120,
    height: 120,
    borderRadius: 60,
    borderWidth: 4,
    borderColor: "#3E2723",
  },
  loading: {
    position: "absolute",
    top: 40,
  },
  name: {
    fontSize: 24,
    fontWeight: "bold",
    marginTop: 16,
  },
  role: {
    fontSize: 16,
    color: "#666",
    marginTop: 4,
  },
  form: {
    padding: 24,
  },
  label: {
    fontSize: 16,
    fontWeight: "600",
    marginBottom: 8,
    color: "#3E2723",
  },
  input: {
    backgroundColor: "#fff",
    padding: 14,
    borderRadius: 10,
    marginBottom: 16,
    fontSize: 16,
  },
  inputDisabled: {
    backgroundColor: "#f0f0f0",
    color: "#666",
  },
  buttons: {
    marginTop: 20,
    gap: 12,
  },
  button: {
    padding: 16,
    borderRadius: 10,
    alignItems: "center",
  },
  editButton: {
    backgroundColor: "#3E2723",
  },
  saveButton: {
    backgroundColor: "#2e7d32",
  },
  cancelButton: {
    backgroundColor: "#d32f2f",
  },
  buttonText: {
    color: "#fff",
    fontWeight: "bold",
    fontSize: 16,
  },
});
