package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.PeliculaGuardada;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieApiService {

    // Tu clave real de TMDB
    private static final String API_KEY = "cd3b79b44b4ea741d7a47ec0085f8e90";
    private static final String BASE_URL = "https://api.themoviedb.org/3/search/movie";

    public List<PeliculaGuardada> buscarPeliculas(String query) {
        List<PeliculaGuardada> resultados = new ArrayList<>();
        try {
            // Limpiamos los espacios
            String queryUrl = query.replace(" ", "%20");

            // Armamos la URL final
            String urlString = BASE_URL + "?api_key=" + API_KEY + "&query=" + queryUrl + "&language=es-ES";

            // Conexión a la antigua (100% compatible con Java 8)
            URL url = new URL(urlString);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("Accept", "application/json");

            if (conexion.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conexion.getInputStream());

                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("results");

                for (JsonElement element : jsonArray) {
                    JsonObject peliJson = element.getAsJsonObject();

                    PeliculaGuardada peli = new PeliculaGuardada();
                    peli.setIdExternoApi(peliJson.get("id").getAsInt());
                    peli.setTitulo(peliJson.get("title").getAsString());
                    peli.setCategoriaLocal("General");

                    // ==========================================
                    // ¡AQUÍ ESTÁ LA MAGIA QUE SE ME OLVIDÓ PONER!
                    // ==========================================
                    JsonElement posterElement = peliJson.get("poster_path");
                    if (posterElement != null && !posterElement.isJsonNull()) {
                        peli.setPosterUrl("https://image.tmdb.org/t/p/w500" + posterElement.getAsString());
                    } else {
                        peli.setPosterUrl("https://via.placeholder.com/300x450?text=Sin+Imagen");
                    }

                    resultados.add(peli);
                }
                reader.close();
            } else {
                System.out.println("Error de conexion. Codigo: " + conexion.getResponseCode());
            }
            conexion.disconnect();

        } catch (Exception e) {
            System.out.println("Error al consultar TMDB: " + e.getMessage());
        }
        return resultados;
    }
}