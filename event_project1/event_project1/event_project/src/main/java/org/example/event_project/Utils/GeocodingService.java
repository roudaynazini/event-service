package org.example.event_project.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service pour gérer les requêtes de géocodage avec limitation de débit
 * pour éviter les problèmes de quota avec l'API geocode.xyz
 */
public class GeocodingService {
    private static final String API_KEY = "516978462101196299454x78616";
    private static final String GEOCODE_URL = "https://geocode.xyz/";

    // Cache pour éviter de refaire les mêmes requêtes
    private static final Map<String, GeoLocation> geocodeCache = new HashMap<>();

    // Variables pour limiter le débit des requêtes
    private static long lastRequestTime = 0;
    private static final long REQUEST_DELAY_MS = 1000; // 1 seconde entre les requêtes

    public static class GeoLocation {
        public final String address;
        public final double latitude;
        public final double longitude;

        public GeoLocation(String address, double latitude, double longitude) {
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    /**
     * Trouve les coordonnées d'une adresse de façon asynchrone
     */
    public static CompletableFuture<GeoLocation> geocodeAddressAsync(String address) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Vérifier le cache d'abord
                if (geocodeCache.containsKey(address)) {
                    return geocodeCache.get(address);
                }

                // Limiter le débit des requêtes
                synchronized (GeocodingService.class) {
                    long currentTime = System.currentTimeMillis();
                    long timeElapsed = currentTime - lastRequestTime;

                    if (timeElapsed < REQUEST_DELAY_MS) {
                        Thread.sleep(REQUEST_DELAY_MS - timeElapsed);
                    }

                    lastRequestTime = System.currentTimeMillis();
                }

                // Faire la requête API
                String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
                URL url = new URL(GEOCODE_URL + encodedAddress + "?json=1&auth=" + API_KEY);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.has("latt") && jsonResponse.has("longt")) {
                    double lat = Double.parseDouble(jsonResponse.getString("latt"));
                    double lng = Double.parseDouble(jsonResponse.getString("longt"));

                    GeoLocation location = new GeoLocation(address, lat, lng);
                    geocodeCache.put(address, location);
                    return location;
                }

                throw new RuntimeException("Coordonnées non trouvées pour: " + address);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Erreur lors du géocodage: " + e.getMessage());
            }
        });
    }

    /**
     * Trouve les coordonnées d'une adresse (méthode synchrone)
     */
    public static GeoLocation geocodeAddress(String address) {
        try {
            return geocodeAddressAsync(address).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du géocodage: " + e.getMessage());
        }
    }
}
