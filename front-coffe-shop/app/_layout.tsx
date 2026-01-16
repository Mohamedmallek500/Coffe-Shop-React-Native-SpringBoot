// app/_layout.tsx
import { AuthProvider } from "@/app/context/AuthContext";
import { Slot } from "expo-router";

export default function RootLayout() {
  return (
    <AuthProvider>
      <Slot />
    </AuthProvider>
  );
}
