package turno5.grupo3.currency.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import turno5.grupo3.currency.CurrencyService;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@PageTitle("Câmbio de Moedas")
@Route(value = "currency")
@Menu(order = 1, icon = "vaadin:dollar", title = "Câmbio de Moedas")
public class CurrencyExchangeView extends Div {

    private final CurrencyService currencyService;

    // Set para evitar duplicações no grid
    private final Set<ExchangeResult> results = new LinkedHashSet<>();
    private final Grid<ExchangeResult> resultsGrid = new Grid<>(ExchangeResult.class);

    // Lista de moedas (código e descrição)
    private static final Map<String, String> CURRENCIES = new LinkedHashMap<>() {{
        put("AED", "United Arab Emirates Dirham");
        put("AFN", "Afghan Afghani");
        put("ALL", "Albanian Lek");
        put("AMD", "Armenian Dram");
        put("ANG", "Netherlands Antillean Guilder");
        put("AOA", "Angolan Kwanza");
        put("ARS", "Argentine Peso");
        put("AUD", "Australian Dollar");
        put("AWG", "Aruban Florin");
        put("AZN", "Azerbaijani Manat");
        put("BAM", "Bosnia-Herzegovina Convertible Mark");
        put("BBD", "Barbadian Dollar");
        put("BDT", "Bangladeshi Taka");
        put("BGN", "Bulgarian Lev");
        put("BHD", "Bahraini Dinar");
        put("BIF", "Burundian Franc");
        put("BMD", "Bermudan Dollar");
        put("BND", "Brunei Dollar");
        put("BOB", "Bolivian Boliviano");
        put("BRL", "Brazilian Real");
        put("BSD", "Bahamian Dollar");
        put("BTC", "Bitcoin");
        put("BTN", "Bhutanese Ngultrum");
        put("BWP", "Botswanan Pula");
        put("BYN", "New Belarusian Ruble");
        put("BYR", "Belarusian Ruble");
        put("BZD", "Belize Dollar");
        put("CAD", "Canadian Dollar");
        put("CDF", "Congolese Franc");
        put("CHF", "Swiss Franc");
        put("CLF", "Chilean Unit of Account (UF)");
        put("CLP", "Chilean Peso");
        put("CNY", "Chinese Yuan");
        put("CNH", "Chinese Yuan Offshore");
        put("COP", "Colombian Peso");
        put("CRC", "Costa Rican Colón");
        put("CUC", "Cuban Convertible Peso");
        put("CUP", "Cuban Peso");
        put("CVE", "Cape Verdean Escudo");
        put("CZK", "Czech Republic Koruna");
        put("DJF", "Djiboutian Franc");
        put("DKK", "Danish Krone");
        put("DOP", "Dominican Peso");
        put("DZD", "Algerian Dinar");
        put("EGP", "Egyptian Pound");
        put("ERN", "Eritrean Nakfa");
        put("ETB", "Ethiopian Birr");
        put("EUR", "Euro");
        put("FJD", "Fijian Dollar");
        put("FKP", "Falkland Islands Pound");
        put("GBP", "British Pound Sterling");
        put("GEL", "Georgian Lari");
        put("GGP", "Guernsey Pound");
        put("GHS", "Ghanaian Cedi");
        put("GIP", "Gibraltar Pound");
        put("GMD", "Gambian Dalasi");
        put("GNF", "Guinean Franc");
        put("GTQ", "Guatemalan Quetzal");
        put("GYD", "Guyanaese Dollar");
        put("HKD", "Hong Kong Dollar");
        put("HNL", "Honduran Lempira");
        put("HRK", "Croatian Kuna");
        put("HTG", "Haitian Gourde");
        put("HUF", "Hungarian Forint");
        put("IDR", "Indonesian Rupiah");
        put("ILS", "Israeli New Sheqel");
        put("IMP", "Manx Pound");
        put("INR", "Indian Rupee");
        put("IQD", "Iraqi Dinar");
        put("IRR", "Iranian Rial");
        put("ISK", "Icelandic Króna");
        put("JEP", "Jersey Pound");
        put("JMD", "Jamaican Dollar");
        put("JOD", "Jordanian Dinar");
        put("JPY", "Japanese Yen");
        put("KES", "Kenyan Shilling");
        put("KGS", "Kyrgystani Som");
        put("KHR", "Cambodian Riel");
        put("KMF", "Comorian Franc");
        put("KPW", "North Korean Won");
        put("KRW", "South Korean Won");
        put("KWD", "Kuwaiti Dinar");
        put("KYD", "Cayman Islands Dollar");
        put("KZT", "Kazakhstani Tenge");
        put("LAK", "Laotian Kip");
        put("LBP", "Lebanese Pound");
        put("LKR", "Sri Lankan Rupee");
        put("LRD", "Liberian Dollar");
        put("LSL", "Lesotho Loti");
        put("LTL", "Lithuanian Litas");
        put("LVL", "Latvian Lats");
        put("LYD", "Libyan Dinar");
        put("MAD", "Moroccan Dirham");
        put("MDL", "Moldovan Leu");
        put("MGA", "Malagasy Ariary");
        put("MKD", "Macedonian Denar");
        put("MMK", "Myanma Kyat");
        put("MNT", "Mongolian Tugrik");
        put("MOP", "Macanese Pataca");
        put("MRU", "Mauritanian Ouguiya");
        put("MUR", "Mauritian Rupee");
        put("MVR", "Maldivian Rufiyaa");
        put("MWK", "Malawian Kwacha");
        put("MXN", "Mexican Peso");
        put("MYR", "Malaysian Ringgit");
        put("MZN", "Mozambican Metical");
        put("NAD", "Namibian Dollar");
        put("NGN", "Nigerian Naira");
        put("NIO", "Nicaraguan Córdoba");
        put("NOK", "Norwegian Krone");
        put("NPR", "Nepalese Rupee");
        put("NZD", "New Zealand Dollar");
        put("OMR", "Omani Rial");
        put("PAB", "Panamanian Balboa");
        put("PEN", "Peruvian Nuevo Sol");
        put("PGK", "Papua New Guinean Kina");
        put("PHP", "Philippine Peso");
        put("PKR", "Pakistani Rupee");
        put("PLN", "Polish Zloty");
        put("PYG", "Paraguayan Guarani");
        put("QAR", "Qatari Rial");
        put("RON", "Romanian Leu");
        put("RSD", "Serbian Dinar");
        put("RUB", "Russian Ruble");
        put("RWF", "Rwandan Franc");
        put("SAR", "Saudi Riyal");
        put("SBD", "Solomon Islands Dollar");
        put("SCR", "Seychellois Rupee");
        put("SDG", "South Sudanese Pound");
        put("SEK", "Swedish Krona");
        put("SGD", "Singapore Dollar");
        put("SHP", "Saint Helena Pound");
        put("SLE", "Sierra Leonean Leone");
        put("SLL", "Sierra Leonean Leone");
        put("SOS", "Somali Shilling");
        put("SRD", "Surinamese Dollar");
        put("STD", "São Tomé and Príncipe Dobra");
        put("STN", "São Tomé and Príncipe Dobra");
        put("SVC", "Salvadoran Colón");
        put("SYP", "Syrian Pound");
        put("SZL", "Swazi Lilangeni");
        put("THB", "Thai Baht");
        put("TJS", "Tajikistani Somoni");
        put("TMT", "Turkmenistani Manat");
        put("TND", "Tunisian Dinar");
        put("TOP", "Tongan Paʻanga");
        put("TRY", "Turkish Lira");
        put("TTD", "Trinidad and Tobago Dollar");
        put("TWD", "New Taiwan Dollar");
        put("TZS", "Tanzanian Shilling");
        put("UAH", "Ukrainian Hryvnia");
        put("UGX", "Ugandan Shilling");
        put("USD", "United States Dollar");
        put("UYU", "Uruguayan Peso");
        put("UZS", "Uzbekistan Som");
        put("VES", "Sovereign Bolivar");
        put("VND", "Vietnamese Dong");
        put("VUV", "Vanuatu Vatu");
        put("WST", "Samoan Tala");
        put("XAF", "CFA Franc BEAC");
        put("XAG", "Silver (troy ounce)");
        put("XAU", "Gold (troy ounce)");
        put("XCD", "East Caribbean Dollar");
        put("XCG", "Caribbean Guilder");
        put("XDR", "Special Drawing Rights");
        put("XOF", "CFA Franc BCEAO");
        put("XPF", "CFP Franc");
        put("YER", "Yemeni Rial");
        put("ZAR", "South African Rand");
        put("ZMK", "Zambian Kwacha (pre-2013)");
        put("ZMW", "Zambian Kwacha");
        put("ZWL", "Zimbabwean Dollar");
    }};

    public CurrencyExchangeView(CurrencyService currencyService) {
        this.currencyService = currencyService;

        // Título da página
        H1 title = new H1("Conversão de Moedas");
        title.getStyle().set("color", "#2b579a");

        // ComboBox para moeda de origem
        ComboBox<String> fromCurrency = new ComboBox<>("Moeda de Origem");
        fromCurrency.setItems(CURRENCIES.entrySet().stream().map(e -> e.getKey() + " - " + e.getValue()).toList());
        fromCurrency.setPlaceholder("Selecione a moeda");
        fromCurrency.setWidth("250px");

        // ComboBox para moeda de destino
        ComboBox<String> toCurrency = new ComboBox<>("Moeda de Destino");
        toCurrency.setItems(CURRENCIES.entrySet().stream().map(e -> e.getKey() + " - " + e.getValue()).toList());
        toCurrency.setPlaceholder("Selecione a moeda");
        toCurrency.setWidth("250px");

        // Campo de entrada para o valor
        TextField amount = new TextField("Valor");
        amount.setPlaceholder("Ex: 100");
        amount.setWidth("100px");

        // Botão para realizar a conversão
        Button convertButton = new Button("Converter", e -> {
            try {
                // Obtém apenas o código da moeda (ex.: "USD")
                String from = fromCurrency.getValue() != null ? fromCurrency.getValue().split(" - ")[0] : null;
                String to = toCurrency.getValue() != null ? toCurrency.getValue().split(" - ")[0] : null;

                Double amountValue = Double.parseDouble(amount.getValue());

                if (from == null || to == null || from.isBlank() || to.isBlank() || amountValue <= 0) {
                    Notification.show("Por favor, preencha todos os campos corretamente.", 3000, Notification.Position.TOP_CENTER);
                    return;
                }

                // Realizar a conversão de moedas
                Double result = currencyService.convertCurrency(from, to, amountValue);

                // Adicionar apenas resultados únicos
                ExchangeResult newResult = new ExchangeResult(from, to, amountValue, result);
                boolean isAdded = results.add(newResult); // Adiciona ao Set
                if (isAdded) {
                    resultsGrid.setItems(results); // Atualizar os itens do grid
                } else {
                    Notification.show("Já existe um resultado idêntico na tabela.", 3000, Notification.Position.TOP_CENTER);
                }
            } catch (NumberFormatException ex) {
                Notification.show("Valor inválido!", 3000, Notification.Position.TOP_CENTER);
            } catch (Exception ex) {
                Notification.show("Erro: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        // Botão para limpar os campos
        Button clearButton = new Button("Limpar", e -> {
            fromCurrency.clear();
            toCurrency.clear();
            amount.clear();
        });

        // Layout horizontal para os campos e botão
        HorizontalLayout formLayout = new HorizontalLayout(fromCurrency, toCurrency, amount, convertButton, clearButton);
        formLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        formLayout.setSpacing(true);


        // Configuração do grid
        resultsGrid.setColumns("from", "to", "amount", "result");
        resultsGrid.getColumnByKey("from").setHeader("De").setAutoWidth(true);
        resultsGrid.getColumnByKey("to").setHeader("Para").setAutoWidth(true);
        resultsGrid.getColumnByKey("amount").setHeader("Valor").setAutoWidth(true);
        resultsGrid.getColumnByKey("result").setHeader("Resultado").setAutoWidth(true);
        resultsGrid.setWidth("500px");
        resultsGrid.setHeight("300px");

        // Centralizar a tabela
        Div gridWrapper = new Div(resultsGrid);
        gridWrapper.getStyle().set("margin", "0 auto").set("padding-top", "20px");

        // Layout principal
        VerticalLayout mainLayout = new VerticalLayout(title, formLayout, gridWrapper);
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainLayout.setHeightFull();

        // Adicionar o layout principal
        add(mainLayout);
    }

    // Classe para representar os resultados únicos
    public static class ExchangeResult {
        private final String from;
        private final String to;
        private final Double amount;
        private final Double result;

        public ExchangeResult(String from, String to, Double amount, Double result) {
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.result = result;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public Double getAmount() {
            return amount;
        }

        public Double getResult() {
            return result;
        }

        // equals e hashCode para evitar duplicações
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExchangeResult that = (ExchangeResult) o;
            return from.equals(that.from) && to.equals(that.to) && amount.equals(that.amount);
        }

        @Override
        public int hashCode() {
            int result = from.hashCode();
            result = 31 * result + to.hashCode();
            result = 31 * result + amount.hashCode();
            return result;
        }
    }
}
