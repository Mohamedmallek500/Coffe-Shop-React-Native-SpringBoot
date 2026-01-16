// app/(tabs)/_layout.tsx
import { Ionicons } from "@expo/vector-icons";
import { Tabs } from "expo-router";

export default function TabsLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarShowLabel: false,
        tabBarStyle: {
          height: 99,
          paddingBottom: 20,
          paddingTop: 10,
          borderTopLeftRadius: 30,
          borderTopRightRadius: 30,
          backgroundColor: "#FFFFFF",
          position: "absolute",
        },
      }}
    >
      <Tabs.Screen
        name="home"
        options={{
          tabBarIcon: ({ focused }) => (
            <Ionicons
              name="home"
              size={26}
              color={focused ? "#0C5A2A" : "#9DB8A0"}
            />
          ),
        }}
      />

      <Tabs.Screen
        name="favorite"
        options={{
          tabBarIcon: ({ focused }) => (
            <Ionicons
              name="heart"
              size={26}
              color={focused ? "#0C5A2A" : "#9DB8A0"}
            />
          ),
        }}
      />

      <Tabs.Screen
        name="cart"
        options={{
          tabBarIcon: ({ focused }) => (
            <Ionicons
              name="cart"
              size={26}
              color={focused ? "#0C5A2A" : "#9DB8A0"}
            />
          ),
        }}
      />

      <Tabs.Screen
        name="profile"
        options={{
          tabBarIcon: ({ focused }) => (
            <Ionicons
              name="person"
              size={26}
              color={focused ? "#0C5A2A" : "#9DB8A0"}
            />
          ),
        }}
      />
    </Tabs>
  );
}
