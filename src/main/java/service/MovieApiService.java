package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.PeliculaGuardada;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder; // Agregamos esto para manejar espacios y tildes
import java.util.ArrayList;
import java.util.List;

public class MovieApiService {

    // Tu clave real de TMDB
    private static final String API_KEY = "cd3b79b44b4ea741d7a47ec0085f8e90";

    // Borramos el BASE_URL estático porque ahora cambiaremos entre /movie y /tv

    public List<PeliculaGuardada> buscarPeliculas(String query, String tipoBusqueda) {
        List<PeliculaGuardada> resultados = new ArrayList<>();
        try {
            // Mejoramos la limpieza usando URLEncoder para que soporte tildes y ñ sin romperse
            String queryUrl = URLEncoder.encode(query, "UTF-8");

            // Armamos la URL dinámica y le clavamos el idioma Español Latino (es-MX)
            String urlString = "https://api.themoviedb.org/3/search/" + tipoBusqueda +
                    "?api_key=" + API_KEY +
                    "&query=" + queryUrl +
                    "&language=es-MX";

            // Conexión a la antigua
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

                    // ==========================================
                    // EL TRUCO DE TMDB: ¿Es Peli o es Serie?
                    // ==========================================
                    if (peliJson.has("title")) {
                        // Las películas usan "title"
                        peli.setTitulo(peliJson.get("title").getAsString());
                        peli.setCategoriaLocal("Película");
                    } else if (peliJson.has("name")) {
                        // Las series y animes usan "name"
                        peli.setTitulo(peliJson.get("name").getAsString());
                        peli.setCategoriaLocal("Serie / Anime");
                    } else {
                        peli.setTitulo("Título desconocido");
                        peli.setCategoriaLocal("General");
                    }

                    // ==========================================
                    // EL POSTER
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