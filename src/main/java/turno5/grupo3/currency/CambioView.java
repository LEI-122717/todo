package turno5.grupo3.currency;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;

@Route("cambio")
public class CambioView extends VerticalLayout {

    public CambioView() {
        turno5.grupo3.currency.CambioService service = new turno5.grupo3.currency.ui.CambioService();

        ComboBox<String> fromCurrency = new ComboBox<>("De");
        fromCurrency.setItems("EUR", "USD", "GBP", "BRL");

        ComboBox<String> toCurrency = new ComboBox<>("Para");
        toCurrency.setItems("EUR", "USD", "GBP", "BRL");

        NumberField amountField = new NumberField("Valor");
        amountField.setStep(1);

        Label resultLabel = new Label();

        Button convert = new Button("Converter", e -> {
            try {
                double result = service.converter(
                        fromCurrency.getValue(),
                        toCurrency.getValue(),
                        amountField.getValue()
                );
                resultLabel.setText("Resultado: " + result + " " + toCurrency.getValue());
            } catch (Exception ex) {
                resultLabel.setText("Erro: " + ex.getMessage());
            }
        });

        add(fromCurrency, toCurrency, amountField, convert, resultLabel);
    }
}

