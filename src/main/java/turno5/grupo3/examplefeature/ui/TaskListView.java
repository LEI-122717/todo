package turno5.grupo3.examplefeature.ui;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.server.StreamResource;
import turno5.grupo3.base.ui.component.ViewToolbar;
import turno5.grupo3.examplefeature.Task;
import turno5.grupo3.examplefeature.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.ByteArrayInputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;


import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Anchor;

import turno5.grupo3.examplefeature.service.PdfExportService; // NOVO


import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("")
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
class TaskListView extends Main {

    private final TaskService taskService;
    private final PdfExportService pdfExportService; // NOVO CAMPO

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    final Grid<Task> taskGrid;

    // NOVO: Anchor oculto para acionar o download (truque Vaadin)
    private final Anchor downloadLink = new Anchor();

    TaskListView(TaskService taskService, PdfExportService pdfExportService) {
        this.taskService = taskService;
        this.pdfExportService = pdfExportService; // Atribuição do novo serviço



        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("Task description");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        createBtn = new Button("Create", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(getLocale())
                .withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
        taskGrid.addColumn(Task::getDescription).setHeader("Description");
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate()).map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate())).setHeader("Creation Date");
        taskGrid.setSizeFull();

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        add(new ViewToolbar("Task List", ViewToolbar.group(description, dueDate, createBtn),
                createPdfExportAnchor() // ADICIONA O BOTÃO DE EXPORTAÇÃO
        ));
        add(taskGrid);
    }

    private void createTask() {
        taskService.createTask(description.getValue(), dueDate.getValue());
        taskGrid.getDataProvider().refreshAll();
        description.clear();
        dueDate.clear();
        Notification.show("Task added", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    // NOVO MÉTODO (Substitui o anterior createPdfExportAnchor)
    @SuppressWarnings("deprecation")
    private Anchor createPdfExportAnchor() {

        // 1. Crie o Anchor (Sem o recurso no construtor para evitar o warning)
        Anchor downloadAnchor = new Anchor();

        @SuppressWarnings("deprecation")
        // 2. Crie o StreamResource (A lógica de geração de PDF é a mesma)
        StreamResource resource = new StreamResource("lista_de_tarefas.pdf", () -> {
            // Obtém todos os dados do serviço
            List<Task> allTasks = taskService.findAll(); // buscar aqui
            try {
                // Usa o PdfExportService para gerar o PDF
                byte[] pdfBytes = pdfExportService.generateTasksPdf(allTasks);
                return new ByteArrayInputStream(pdfBytes);
            } catch (Exception ex) {
                // Trata erros
                Notification.show("Erro ao gerar PDF: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return new ByteArrayInputStream(new byte[0]);
            }
        });

        // 3. Defina as propriedades do download no Anchor

        // Define o recurso como o destino do link (o que deve ser descarregado)
        downloadAnchor.setHref(resource);

        // CRUCIAL: Força o download (em vez de tentar abrir no browser)
        downloadAnchor.getElement().setAttribute("download", true);

        // Opcional: Abre numa nova janela
        downloadAnchor.setTarget("_blank");

        // 4. Cria o componente visual (o botão) e insere-o no Anchor
        Button pdfButton = new Button("Exportar para PDF", new Icon(VaadinIcon.FILE_TEXT));
        pdfButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        downloadAnchor.removeAll();
        downloadAnchor.add(pdfButton);

        return downloadAnchor;
    }

}


