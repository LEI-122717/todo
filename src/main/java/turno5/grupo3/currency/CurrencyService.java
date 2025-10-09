package turno5.grupo3.currency;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate;

    private static final String ACCESS_KEY = "e073ae8fb306bae23359ae19f80c637e"; // Chave de acesso
    private static final String API_URL = "https://api.exchangerate.host/convert";

    public CurrencyService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Método para converter um valor de uma moeda para outra.
     * @param from Moeda de origem (código ISO, ex.: "USD").
     * @param to Moeda de destino (código ISO, ex.: "EUR").
     * @param amount Valor a ser convertido.
     * @return Resultado da conversão como Double.
     */
    public Double convertCurrency(String from, String to, Double amount) {
        // Construir a URL da API com os parâmetros necessários e a access_key
        String url = String.format("%s?access_key=%s&from=%s&to=%s&amount=%s",
                API_URL, ACCESS_KEY, from, to, amount);

        // Requisitar a API e processar a resposta
        var response = restTemplate.getForObject(url, ConversionResponse.class);

        if (response != null && response.isSuccess()) {
            return response.getResult();
        }

        // Caso a conversão falhe
        throw new IllegalArgumentException("Falha ao converter moedas. Verifique os códigos de moeda e tente novamente.");
    }

    // Classe interna para mapear a resposta do JSON da API
    private static class ConversionResponse {
        private boolean success;
        private Double result;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public Double getResult() {
            return result;
        }

        public void setResult(Double result) {
            this.result = result;
        }
    }
}
