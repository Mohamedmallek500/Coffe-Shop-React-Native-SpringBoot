// services/favorite.service.ts
import { Product } from "@/types/product";
import AsyncStorage from "@react-native-async-storage/async-storage";

const FAVORITES_KEY = "@coffee_shop_favorites";

export const FavoriteService = {
    /**
     * Get all favorited products
     */
    async getFavorites(): Promise<Product[]> {
        try {
            const favoritesJson = await AsyncStorage.getItem(FAVORITES_KEY);
            if (favoritesJson) {
                return JSON.parse(favoritesJson);
            }
            return [];
        } catch (error) {
            console.error("Error getting favorites:", error);
            return [];
        }
    },

    /**
     * Add a product to favorites
     */
    async addFavorite(product: Product): Promise<void> {
        try {
            const favorites = await this.getFavorites();

            // Check if product is already in favorites
            const exists = favorites.some((fav) => fav.id === product.id);
            if (!exists) {
                favorites.push(product);
                await AsyncStorage.setItem(FAVORITES_KEY, JSON.stringify(favorites));
            }
        } catch (error) {
            console.error("Error adding favorite:", error);
        }
    },

    /**
     * Remove a product from favorites
     */
    async removeFavorite(productId: number): Promise<void> {
        try {
            const favorites = await this.getFavorites();
            const filtered = favorites.filter((fav) => fav.id !== productId);
            await AsyncStorage.setItem(FAVORITES_KEY, JSON.stringify(filtered));
        } catch (error) {
            console.error("Error removing favorite:", error);
        }
    },

    /**
     * Check if a product is in favorites
     */
    async isFavorite(productId: number): Promise<boolean> {
        try {
            const favorites = await this.getFavorites();
            return favorites.some((fav) => fav.id === productId);
        } catch (error) {
            console.error("Error checking favorite:", error);
            return false;
        }
    },

    /**
     * Get favorite product IDs (for quick lookup)
     */
    async getFavoriteIds(): Promise<number[]> {
        try {
            const favorites = await this.getFavorites();
            return favorites.map((fav) => fav.id);
        } catch (error) {
            console.error("Error getting favorite IDs:", error);
            return [];
        }
    },
};
