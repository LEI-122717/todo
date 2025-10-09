package turno5.grupo3.examplefeature.ui;

import turno5.grupo3.base.ui.component.ViewToolbar;
import turno5.grupo3.examplefeature.Task;
import turno5.grupo3.examplefeature.TaskService;
import turno5.grupo3.service.EmailService;
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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("")
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
public class TaskListView extends Main {

    private final TaskService taskService;
    private final EmailService emailService;

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    final Grid<Task> taskGrid;

    final TextField emailField;
    final Button sendEmailBtn;

    public TaskListView(TaskService taskService, EmailService emailCreator) {
        this.taskService = taskService;
        this.emailService = emailCreator;

        // Campo de descrição
        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("Task description");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        // Campo de data
        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        // Botão de criação
        createBtn = new Button("Create", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Campo de email
        emailField = new TextField();
        emailField.setPlaceholder("Recipient email");
        emailField.setMinWidth("20em");

        // Botão de envio de email
        sendEmailBtn = new Button("Send Tasks by Email", event -> sendTasksByEmail());
        sendEmailBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        // Formatação de datas
        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(getLocale())
                .withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(getLocale());

        // Tabela de tarefas
        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
        taskGrid.addColumn(Task::getDescription).setHeader("Description");
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate())
                .map(dateFormatter::format)
                .orElse("Never")).setHeader("Due Date");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate())).setHeader("Creation Date");
        taskGrid.setSizeFull();

        // Layout principal
        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL);

        // Toolbar e botão de email
        add(new ViewToolbar("Task List", ViewToolbar.group(description, dueDate, createBtn)));
        add(ViewToolbar.group(emailField, sendEmailBtn));
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

    private void sendTasksByEmail() {
        String recipient = emailField.getValue();
        if (recipient == null || recipient.isEmpty()) {
            Notification.show("Please enter a valid email address.", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            String tasksText = taskService.list(null).stream()
                    .map(Task::toString)
                    .collect(Collectors.joining("\n"));

            emailService.sendEmail(recipient, "Your Task List", tasksText);

            Notification.show("Tasks sent to " + recipient, 4000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error sending email: " + e.getMessage(), 4000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}