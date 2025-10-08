package turno5.grupo3.currency.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import turno5.grupo3.currency.CurrencyService;

import java.util.List;

@PageTitle("Câmbio de Moedas")
@Route(value = "currency")
@Menu(order = 1, icon = "vaadin:dollar", title = "Câmbio de Moedas")
public class CurrencyExchangeView extends Div {

    private final CurrencyService currencyService;

    public CurrencyExchangeView(CurrencyService currencyService) {
        this.currencyService = currencyService;

        // Lista fixa com opções de moedas. Pode ser expandida ou carregada dinamicamente.
        List<String> availableCurrencies = List.of("USD", "EUR", "GBP", "JPY", "BRL", "INR", "AUD", "CAD");

        // Criar menu suspenso para selecionar a moeda de origem
        ComboBox<String> fromCurrency = new ComboBox<>("Moeda de Origem");
        fromCurrency.setItems(availableCurrencies);
        fromCurrency.setPlaceholder("Selecione a moeda de origem");

        // Criar menu suspenso para selecionar a moeda de destino
        ComboBox<String> toCurrency = new ComboBox<>("Moeda de Destino");
        toCurrency.setItems(availableCurrencies);
        toCurrency.setPlaceholder("Selecione a moeda de destino");

        // Campo para o valor a ser convertido
        TextField amount = new TextField("Valor");
        amount.setPlaceholder("Ex: 100");

        // Botão para realizar a conversão
        Button convertButton = new Button("Converter", e -> {
            try {
                String from = fromCurrency.getValue();
                String to = toCurrency.getValue();
                Double amountValue = Double.parseDouble(amount.getValue());

                // Valida se as escolhas estão completas
                if (from == null || to == null || from.isEmpty() || to.isEmpty()) {
                    Notification.show("Por favor, selecione ambas as moedas.", 3000, Notification.Position.TOP_CENTER);
                    return;
                }

                Double result = currencyService.convertCurrency(from, to, amountValue);

                // Exibir o resultado na interface
                this.add(new Div(new com.vaadin.flow.component.html.Span(
                        String.format("Resultado: %.2f %s", result, to))));
            } catch (NumberFormatException exception) {
                Notification.show("Por favor, insira um valor válido.", 3000, Notification.Position.TOP_CENTER);
            } catch (Exception exception) {
                Notification.show("Erro na conversão: " + exception.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        // Adicionar os elementos na View
        add(fromCurrency, toCurrency, amount, convertButton);
    }
}