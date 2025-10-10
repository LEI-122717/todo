package turno5.grupo3.examplefeature.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import turno5.grupo3.examplefeature.Task;
import turno5.grupo3.examplefeature.TaskService;
import turno5.grupo3.examplefeature.service.QrCodeGenerator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Route("tarefa")
@PageTitle("Task Detail")
public class TaskDetailView extends Main implements HasUrlParameter<Long> {

    private final TaskService taskService;

    public TaskDetailView(TaskService taskService) {
        this.taskService = taskService;
        setSizeFull();
        addClassName("task-detail-view");
    }

    @Override
    public void setParameter(BeforeEvent event, Long id) {
        if (id == null) {
            Notification.show("ID da tarefa ausente", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }

        Optional<Task> opt = taskService.findById(id);
        if (opt.isEmpty()) {
            Notification.show("Tarefa não encontrada", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }

        Task task = opt.get();

        // Limpa conteúdo antigo
        removeAll();

        // Layout principal
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        layout.getStyle().set("padding", "2rem");
        layout.getStyle().set("gap", "2rem");

        // Cabeçalho com botão de voltar
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        headerLayout.getStyle().set("gap", "1rem");

        Button backButton = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        H2 header = new H2("Tarefa: " + task.getDescription());
        headerLayout.add(backButton, header);

        layout.add(headerLayout);

        // QR Code container com borda
        Div qrContainer = new Div();
        qrContainer.getStyle().set("border", "2px solid #ccc");
        qrContainer.getStyle().set("padding", "1rem");
        qrContainer.getStyle().set("border-radius", "8px");
        qrContainer.getStyle().set("display", "flex");
        qrContainer.getStyle().set("justify-content", "center");
        qrContainer.getStyle().set("align-items", "center");
        qrContainer.getStyle().set("background-color", "#f9f9f9");

        String qrContent = "/tarefa/" + task.getId();
        final int qrSize = 256;

        try {
            byte[] pngBytes = QrCodeGenerator.generateQrBytes(qrContent, qrSize);

            StreamResource imgResource = new StreamResource("qr-tarefa-" + task.getId() + ".png",
                    () -> new ByteArrayInputStream(pngBytes));
            imgResource.setContentType("image/png");

            Image qrImage = new Image(imgResource, "QR Code da tarefa");
            qrImage.setWidth(qrSize + "px");
            qrImage.setHeight(qrSize + "px");

            qrContainer.add(qrImage);
            layout.add(qrContainer);

            // Botão de download centralizado
            StreamResource downloadResource = new StreamResource("qr-tarefa-" + task.getId() + ".png",
                    () -> new ByteArrayInputStream(pngBytes));
            downloadResource.setContentType("image/png");

            Anchor downloadAnchor = new Anchor(downloadResource, "");
            downloadAnchor.getElement().setAttribute("download", true);

            Button downloadButton = new Button("Descarregar QR", new Icon(VaadinIcon.DOWNLOAD));
            downloadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            downloadButton.getStyle().set("margin-top", "1rem");
            downloadAnchor.add(downloadButton);

            layout.add(downloadAnchor);

            // Opcional: grava no servidor
            try {
                QrCodeGenerator.generateQrToFile(qrContent, "qr-tarefa-" + task.getId(), qrSize);
            } catch (IOException | com.google.zxing.WriterException ex) {
                Notification.show("QR gerado, mas falhou a gravação em disco: " + ex.getMessage(),
                                4000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            }

        } catch (IOException | com.google.zxing.WriterException ex) {
            Notification.show("Falha ao gerar QR Code: " + ex.getMessage(),
                            5000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        add(layout);
    }
}
