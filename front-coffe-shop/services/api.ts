// services/api.ts
import axios from "axios";

 export const API_URL = "http://10.0.2.2:9092/api";

  //export const API_URL = "http://192.168.1.164:9092/api";


 //export const API_URL = "http://localhost:8082/api";


export const api = axios.create({
  baseURL: API_URL,
  withCredentials: true, // cookies JWT
  headers: {
    "Content-Type": "application/json",
  },
});

// 🔥 INTERCEPTOR POUR REFRESH TOKEN
let isRefreshing = false;
let failedQueue: any[] = [];

const processQueue = (error: any, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });

  failedQueue = [];
};

api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;

    // Si pas 401 → on ne touche pas
    if (error.response?.status !== 401) {
      return Promise.reject(error);
    }

    // Si on est déjà en train de refresh → on met en attente
    if (isRefreshing) {
      return new Promise(function (resolve, reject) {
        failedQueue.push({ resolve, reject });
      }).then(() => {
        return api(originalRequest);
      });
    }

    isRefreshing = true;

    try {
      // 🔥 Appel refresh token
      await api.post("/auth/refreshtoken");

      processQueue(null);
      return api(originalRequest); // 🔥 Rejoue la requête originale
    } catch (err) {
      processQueue(err, null);
      return Promise.reject(err);
    } finally {
      isRefreshing = false;
    }
  }
);
