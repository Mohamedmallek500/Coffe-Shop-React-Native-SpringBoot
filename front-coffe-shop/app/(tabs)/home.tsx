import { useAuth } from "@/app/context/AuthContext";
import { CategoryService } from "@/services/category.service";
import { FavoriteService } from "@/services/favorite.service";
import { ProductService } from "@/services/product.service";
import { Category } from "@/types/category";
import { Product } from "@/types/product";
import { Feather } from "@expo/vector-icons";
import * as Location from "expo-location";
import { useFocusEffect } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import {
  FlatList,
  Image,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

export default function HomeScreen() {
  const { user } = useAuth();

  const [city, setCity] = useState<string | null>(null);
  const [country, setCountry] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [categories, setCategories] = useState<Category[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
  const [favoriteIds, setFavoriteIds] = useState<number[]>([]);

  /* =======================
     LOCATION
  ======================= */
  useEffect(() => {
    (async () => {
      try {
        const { status } =
          await Location.requestForegroundPermissionsAsync();

        if (status !== "granted") {
          setError("Localisation indisponible");
          return;
        }

        const location = await Location.getCurrentPositionAsync({
          accuracy: Location.Accuracy.Balanced,
        });

        const address = await Location.reverseGeocodeAsync(
          location.coords
        );

        if (address.length > 0) {
          setCity(address[0].city ?? "Unknown city");
          setCountry(address[0].country ?? "");
        }
      } catch {
        setError("Localisation indisponible");
      }
    })();
  }, []);

  /* =======================
     LOAD DATA
  ======================= */
  useEffect(() => {
    CategoryService.getAll().then(setCategories);
    ProductService.getAll().then(setProducts);
  }, []);

  /* =======================
     LOAD FAVORITES
  ======================= */
  useFocusEffect(
    useCallback(() => {
      // Reload favorites when screen is focused
      FavoriteService.getFavoriteIds().then(setFavoriteIds);
    }, [])
  );

  const handleCategoryPress = async (categoryId: number | null) => {
    setSelectedCategory(categoryId);

    if (categoryId === null) {
      const all = await ProductService.getAll();
      setProducts(all);
    } else {
      const filtered = await ProductService.getByCategory(categoryId);
      setProducts(filtered);
    }
  };

  /* =======================
     FAVORITE HANDLING
  ======================= */
  const handleFavoritePress = async (product: Product) => {
    const isFav = favoriteIds.includes(product.id);

    if (isFav) {
      // Remove from favorites
      await FavoriteService.removeFavorite(product.id);
      setFavoriteIds((prev) => prev.filter((id) => id !== product.id));
    } else {
      // Add to favorites
      await FavoriteService.addFavorite(product);
      setFavoriteIds((prev) => [...prev, product.id]);
    }
  };

  /* =======================
     UI HELPERS
  ======================= */
  const hour = new Date().getHours();
  const greeting =
    hour < 12
      ? "Good morning"
      : hour < 18
        ? "Good afternoon"
        : "Good evening";

  const avatarSource =
    user?.photo && user.photo.startsWith("http")
      ? { uri: user.photo }
      : require("@/assets/images/icons/user.png");

  /* =======================
     RENDER
  ======================= */
  return (
    <SafeAreaView edges={["top"]} style={{ flex: 1, backgroundColor: "#fff" }}>
      <FlatList
        data={products}
        keyExtractor={(item) => item.id.toString()}
        numColumns={2}
        columnWrapperStyle={{ justifyContent: "space-between" }}
        contentContainerStyle={{ paddingHorizontal: 24, paddingBottom: 120 }}
        ListHeaderComponent={
          <>
            {/* HEADER */}
            <View style={{ marginBottom: 16 }}>
              <View style={{ height: 44, justifyContent: "center" }}>
                <Image
                  source={avatarSource}
                  style={{
                    position: "absolute",
                    left: 0,
                    width: 44,
                    height: 44,
                    borderRadius: 22,
                  }}
                />

                <View
                  style={{
                    position: "absolute",
                    left: 0,
                    right: 0,
                    flexDirection: "row",
                    justifyContent: "center",
                    alignItems: "center",
                  }}
                >
                  <Feather name="map-pin" size={14} color="#0C5A2A" />
                  <Text
                    style={{
                      marginLeft: 6,
                      fontSize: 14,
                      fontWeight: "500",
                      color: "#0C5A2A",
                    }}
                  >
                    {error
                      ? "Localisation indisponible"
                      : city && country
                        ? `${city}, ${country}`
                        : "Localisation..."}
                  </Text>
                </View>

                <View
                  style={{
                    position: "absolute",
                    right: 0,
                    width: 44,
                    height: 44,
                    borderRadius: 22,
                    borderWidth: 1,
                    borderColor: "#0C5A2A",
                    alignItems: "center",
                    justifyContent: "center",
                  }}
                >
                  <Feather name="bell" size={18} color="#0C5A2A" />
                </View>
              </View>

              <Text
                style={{
                  marginTop: 12,
                  fontSize: 20,
                  fontWeight: "600",
                  color: "#1E1E1E",
                }}
              >
                {greeting}, {user?.prenom}
              </Text>
            </View>

            {/* SEARCH */}
            <View
              style={{
                flexDirection: "row",
                alignItems: "center",
                backgroundColor: "#F6F7F5",
                borderRadius: 999,
                paddingHorizontal: 18,
                height: 54,
                marginBottom: 24,
              }}
            >
              <Feather name="search" size={18} color="#7FA79A" />
              <TextInput
                placeholder="Search Coffee ..."
                placeholderTextColor="#7FA79A"
                editable={false}
                style={{
                  flex: 1,
                  marginLeft: 10,
                  fontSize: 15,
                  color: "#7FA79A",
                }}
              />
              <Feather name="sliders" size={18} color="#1F4A45" />
            </View>

            {/* CATEGORIES */}
            <Text
              style={{
                fontSize: 18,
                fontWeight: "600",
                marginBottom: 12,
              }}
            >
              Categories
            </Text>

            <FlatList
              horizontal
              showsHorizontalScrollIndicator={false}
              data={categories}
              keyExtractor={(item) => item.id.toString()}
              contentContainerStyle={{ paddingBottom: 24 }}
              renderItem={({ item }) => {
                const active = item.id === selectedCategory;

                return (
                  <TouchableOpacity
                    onPress={() => handleCategoryPress(item.id)}
                    style={{
                      paddingHorizontal: 16,
                      paddingVertical: 10,
                      borderRadius: 24,
                      backgroundColor: active ? "#0C5A2A" : "#fff",
                      marginRight: 12,
                      elevation: 2,
                    }}
                  >
                    <Text
                      style={{
                        color: active ? "#fff" : "#0C5A2A",
                        fontWeight: "600",
                      }}
                    >
                      {item.name}
                    </Text>
                  </TouchableOpacity>
                );
              }}
            />
          </>
        }
        renderItem={({ item }) => (
          <View
            style={{
              width: "48%",
              backgroundColor: "#fff",
              borderRadius: 20,
              padding: 12,
              marginBottom: 16,
              elevation: 3,
              height: 250,
            }}
          >
            {/* IMAGE + COEUR FAVORI */}
            <View style={{ position: "relative" }}>
              <Image
                source={{ uri: item.imageUrl }}
                style={{
                  width: "100%",
                  height: 120,
                  borderRadius: 16,
                  marginBottom: 8,
                }}
              />

              {/* ICON HEART TOP RIGHT */}
              <TouchableOpacity
                onPress={() => handleFavoritePress(item)}
                style={{
                  position: "absolute",
                  top: 8,
                  right: 8,
                  width: 28,
                  height: 28,
                  borderRadius: 14,
                  backgroundColor: "#fff",
                  alignItems: "center",
                  justifyContent: "center",
                  elevation: 3,
                }}
              >
                <Feather
                  name={favoriteIds.includes(item.id) ? "heart" : "heart"}
                  size={14}
                  color={favoriteIds.includes(item.id) ? "#E53935" : "#0C5A2A"}
                  fill={favoriteIds.includes(item.id) ? "#E53935" : "none"}
                />
              </TouchableOpacity>
            </View>

            <View style={{ flex: 1 }}>
              <Text style={{ fontWeight: "600", fontSize: 16 }}>
                {item.name}
              </Text>

              <Text
                numberOfLines={2}
                style={{ fontSize: 12, color: "#777", marginVertical: 4 }}
              >
                {item.description}
              </Text>

              {/* PRIX + BOUTON TOUJOURS EN BAS */}
              <View
                style={{
                  marginTop: "auto",
                  flexDirection: "row",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <View style={{ flexDirection: "row", alignItems: "center" }}>
                  <Text style={{ fontWeight: "700", fontSize: 16 }}>
                    {item.basePrice}
                  </Text>
                  <Text style={{ fontSize: 14, marginLeft: 4, color: "#555" }}>
                    DT
                  </Text>
                </View>

                <View
                  style={{
                    width: 28,
                    height: 28,
                    borderRadius: 14,
                    backgroundColor: "#0C5A2A",
                    alignItems: "center",
                    justifyContent: "center",
                  }}
                >
                  <Text style={{ color: "#fff", fontSize: 18 }}>+</Text>
                </View>
              </View>
            </View>
          </View>
        )}
      />
    </SafeAreaView>
  );
}
