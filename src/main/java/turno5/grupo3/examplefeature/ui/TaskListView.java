package turno5.grupo3.examplefeature.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import turno5.grupo3.base.ui.component.ViewToolbar;
import turno5.grupo3.examplefeature.Task;
import turno5.grupo3.examplefeature.TaskService;
import turno5.grupo3.examplefeature.service.PdfExportService;

import java.io.ByteArrayInputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("")
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
public class TaskListView extends Main {

    private final TaskService taskService;
    private final PdfExportService pdfExportService;

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    final Grid<Task> taskGrid;

    public TaskListView(TaskService taskService, PdfExportService pdfExportService) {
        this.taskService = taskService;
        this.pdfExportService = pdfExportService;

        description = new TextField();
        description.setPlaceholder("O que queres fazer?");
        description.setAriaLabel("Descrição da task");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        createBtn = new Button("Create", event -> openCreateTaskDialog());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());

        taskGrid = new Grid<>(Task.class, false);
        // Coluna 0: checkbox para done
        taskGrid.addComponentColumn(task -> {
            Checkbox cb = new Checkbox(task.isDone());
            cb.getElement().setProperty("title", task.isDone() ? "Concluído" : "Marcar como concluído");
            cb.addValueChangeListener(ev -> {
                boolean newValue = ev.getValue();
                try {
                    taskService.setDone(task.getId(), newValue);
                    // actualiza visualmente a linha — refresh
                    taskGrid.getDataProvider().refreshItem(task);
                    Notification.show("Tarefa " + (newValue ? "concluída" : "reaberta"), 2000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception ex) {
                    // Reverte checkbox no cliente se falhar
                    cb.setValue(!newValue);
                    Notification.show("Erro ao atualizar estado: " + ex.getMessage(), 4000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            return cb;
        }).setHeader("").setAutoWidth(true).setFlexGrow(0);

        // Coluna 1: descrição (com strike-through se done)
        taskGrid.addComponentColumn(task -> {
            Span span = new Span(task.getDescription());
            if (task.isDone()) {
                span.getStyle().set("text-decoration", "line-through");
                span.getStyle().set("color", "#777");
            } else {
                span.getStyle().set("color", "#111");
            }
            return span;
        }).setHeader("Description").setAutoWidth(true).setFlexGrow(4);

        // Coluna 2: due date
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate()).map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date").setAutoWidth(true).setFlexGrow(2);

        // Coluna 3: creation date
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate()))
                .setHeader("Creation Date").setAutoWidth(true).setFlexGrow(2);

        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
        taskGrid.setSizeFull();

        // Clique numa task (na row) para abrir detalhe (navega para /tarefa/{id})
        taskGrid.addItemClickListener(event -> {
            Task task = event.getItem();
            UI.getCurrent().navigate("tarefa/" + task.getId());
        });

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN, LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        // Toolbar com campos de pesquisa/dueDate e botão create
        add(new ViewToolbar("Task List", ViewToolbar.group(description, dueDate, createBtn),
                createPdfExportAnchor()
        ));
        add(taskGrid);
    }

    // Pop-up para criar task
    private void openCreateTaskDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        TextArea descField = new TextArea("Descrição");
        descField.setPlaceholder("O que queres fazer?");
        descField.setWidthFull();

        DatePicker dueDateField = new DatePicker("Due Date");

        Button submitBtn = new Button("Criar Task", event -> {
            String desc = descField.getValue();
            var due = dueDateField.getValue();
            if (desc == null || desc.isBlank()) {
                Notification.show("Descrição é obrigatória", 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            taskService.createTask(desc, due);
            taskGrid.getDataProvider().refreshAll();
            Notification.show("Task criada com sucesso", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            dialog.close();
        });
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(descField, dueDateField, submitBtn);
        dialog.open();
    }

    @SuppressWarnings("deprecation")
    private Anchor createPdfExportAnchor() {
        Anchor downloadAnchor = new Anchor();
        @SuppressWarnings("deprecation")
        StreamResource resource = new StreamResource("lista_de_tarefas.pdf", () -> {
            List<Task> allTasks = taskService.findAll();
            try {
                byte[] pdfBytes = pdfExportService.generateTasksPdf(allTasks);
                return new ByteArrayInputStream(pdfBytes);
            } catch (Exception ex) {
                Notification.show("Erro ao gerar PDF: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return new ByteArrayInputStream(new byte[0]);
            }
        });

        downloadAnchor.setHref(resource);
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.setTarget("_blank");

        Button pdfButton = new Button("Exportar para PDF", new Icon(VaadinIcon.FILE_TEXT));
        pdfButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        downloadAnchor.removeAll();
        downloadAnchor.add(pdfButton);

        return downloadAnchor;
    }
}
