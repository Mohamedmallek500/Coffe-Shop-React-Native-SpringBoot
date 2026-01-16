// app/index.tsx
import { router } from "expo-router";
import {
  Image,
  ImageBackground,
  StyleSheet,
  Text,
  TouchableOpacity,
} from "react-native";

export default function WelcomeScreen() {
  return (
    <ImageBackground
      source={require("@/assets/images/arriereplan.png")}
      style={styles.container}
      resizeMode="cover"
    >
      {/* IMAGE */}
      <Image
        source={require("@/assets/images/splash.png")}
        style={styles.image}
      />

      {/* TITLE */}
      <Text style={styles.title}>
        Coffee so good,{"\n"}
        your taste buds{"\n"}
        will love it
      </Text>

      {/* SUBTITLE */}
      <Text style={styles.subtitle}>
        The best grain, the finest roast,{"\n"}
        the most powerful flavor.
      </Text>

      {/* BUTTON */}
      <TouchableOpacity
        style={styles.button}
        onPress={() => router.push("/(auth)/login")}
      >
        <Text style={styles.buttonText}>Get Started</Text>
      </TouchableOpacity>
    </ImageBackground>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },

  /* IMAGE — Figma */
  image: {
    position: "absolute",
    top: 141,
    left: -21,
    width: 453,
    height: 302,
    resizeMode: "contain",
  },

  /* TITLE — Figma */
  title: {
    position: "absolute",
    top: 470,              // 🔥 clé ici
    width: "100%",
    textAlign: "center",
        fontWeight: 'bold',

  transform: [{ scaleX: 1.5 }], // 🔥 élargit l’écriture

    fontFamily: "MontserratSemiBold",
    fontSize: 24,
    lineHeight: 24,
    color: "#FFFFFF",
  },

  /* SUBTITLE — Figma */
  subtitle: {
    position: "absolute",
    top: 580,              // 🔥 clé ici
    width: "100%",
    textAlign: "center",

    fontFamily: "MontserratMedium",
    fontSize: 14,
    color: "#FFFFFF",
    opacity: 0.9,
        marginBottom: 30,
  transform: [{ scaleX: 1.5 }], // 🔥 élargit l’écriture








  },

  /* BUTTON — Figma */
  button: {
    position: "absolute",
    top: 700,              // 🔥 clé ici
    alignSelf: "center",

    width: 235,
    height: 54,
    borderRadius: 30,
    backgroundColor: "#0C5A2A",
    justifyContent: "center",
    alignItems: "center",
  },

  buttonText: {
    fontFamily: "MontserratSemiBold",
    fontSize: 16,
    color: "#FFFFFF",
  },
});
