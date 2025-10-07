package turno5.grupo3.currency;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CambioService {

    private static final String API_URL = "https://api.exchangerate.host/convert";

    public double converter(String from, String to, double amount) {
        try {
            String url = API_URL + "?from=" + from + "&to=" + to + "&amount=" + amount;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            if (json.has("result")) {
                return json.get("result").getAsDouble();
            } else {
                throw new RuntimeException("Resposta inválida da API: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao converter moedas: " + e.getMessage());
        }
    }
}