import { FavoriteService } from "@/services/favorite.service";
import { Product } from "@/types/product";
import { Feather } from "@expo/vector-icons";
import { useFocusEffect } from "expo-router";
import { useCallback, useState } from "react";
import {
  FlatList,
  Image,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

export default function FavoriteScreen() {
  const [favorites, setFavorites] = useState<Product[]>([]);

  // Load favorites when screen is focused
  useFocusEffect(
    useCallback(() => {
      FavoriteService.getFavorites().then(setFavorites);
    }, [])
  );

  const handleRemoveFavorite = async (productId: number) => {
    await FavoriteService.removeFavorite(productId);
    setFavorites((prev) => prev.filter((item) => item.id !== productId));
  };

  // Empty state
  if (favorites.length === 0) {
    return (
      <SafeAreaView edges={["top"]} style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>Favoris</Text>
        </View>
        <View style={styles.emptyContainer}>
          <Feather name="heart" size={64} color="#D0D0D0" />
          <Text style={styles.emptyTitle}>Aucun favori</Text>
          <Text style={styles.emptySubtitle}>
            Ajoutez des produits à vos favoris pour les retrouver ici
          </Text>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView edges={["top"]} style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Favoris</Text>
        <Text style={styles.headerSubtitle}>
          {favorites.length} produit{favorites.length > 1 ? "s" : ""}
        </Text>
      </View>

      <FlatList
        data={favorites}
        keyExtractor={(item) => item.id.toString()}
        numColumns={2}
        columnWrapperStyle={styles.columnWrapper}
        contentContainerStyle={styles.listContent}
        renderItem={({ item }) => (
          <View style={styles.productCard}>
            {/* IMAGE + COEUR FAVORI */}
            <View style={{ position: "relative" }}>
              <Image
                source={{ uri: item.imageUrl }}
                style={styles.productImage}
              />

              {/* ICON HEART TOP RIGHT - Always filled red in favorites */}
              <TouchableOpacity
                onPress={() => handleRemoveFavorite(item.id)}
                style={styles.heartButton}
              >
                <Feather name="heart" size={14} color="#E53935" fill="#E53935" />
              </TouchableOpacity>
            </View>

            <View style={styles.productInfo}>
              <Text style={styles.productName}>{item.name}</Text>

              <Text numberOfLines={2} style={styles.productDescription}>
                {item.description}
              </Text>

              {/* PRIX + BOUTON TOUJOURS EN BAS */}
              <View style={styles.priceContainer}>
                <View style={styles.priceWrapper}>
                  <Text style={styles.price}>{item.basePrice}</Text>
                  <Text style={styles.currency}>DT</Text>
                </View>

                <View style={styles.addButton}>
                  <Text style={styles.addButtonText}>+</Text>
                </View>
              </View>
            </View>
          </View>
        )}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
  header: {
    paddingHorizontal: 24,
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderBottomColor: "#F0F0F0",
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: "700",
    color: "#1E1E1E",
  },
  headerSubtitle: {
    fontSize: 14,
    color: "#7FA79A",
    marginTop: 4,
  },
  emptyContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 40,
  },
  emptyTitle: {
    fontSize: 20,
    fontWeight: "600",
    color: "#1E1E1E",
    marginTop: 16,
  },
  emptySubtitle: {
    fontSize: 14,
    color: "#7FA79A",
    textAlign: "center",
    marginTop: 8,
  },
  listContent: {
    paddingHorizontal: 24,
    paddingTop: 16,
    paddingBottom: 120,
  },
  columnWrapper: {
    justifyContent: "space-between",
  },
  productCard: {
    width: "48%",
    backgroundColor: "#fff",
    borderRadius: 20,
    padding: 12,
    marginBottom: 16,
    elevation: 3,
    height: 250,
  },
  productImage: {
    width: "100%",
    height: 120,
    borderRadius: 16,
    marginBottom: 8,
  },
  heartButton: {
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
  },
  productInfo: {
    flex: 1,
  },
  productName: {
    fontWeight: "600",
    fontSize: 16,
  },
  productDescription: {
    fontSize: 12,
    color: "#777",
    marginVertical: 4,
  },
  priceContainer: {
    marginTop: "auto",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  priceWrapper: {
    flexDirection: "row",
    alignItems: "center",
  },
  price: {
    fontWeight: "700",
    fontSize: 16,
  },
  currency: {
    fontSize: 14,
    marginLeft: 4,
    color: "#555",
  },
  addButton: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: "#0C5A2A",
    alignItems: "center",
    justifyContent: "center",
  },
  addButtonText: {
    color: "#fff",
    fontSize: 18,
  },
});
