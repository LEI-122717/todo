package turno5.grupo3.examplefeature.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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

@Route("tarefa")
@PageTitle("Task")
public class TaskDetailView extends Main implements HasUrlParameter<Long> {

    private final TaskService taskService;

    public TaskDetailView(TaskService taskService) {
        this.taskService = taskService;
        setSizeFull();
        addClassName("task-detail-view");
    }

    /**
     * Este método é chamado quando a rota é /tarefa/{id}.
     */
    @Override
    public void setParameter(BeforeEvent event, Long id) {
        // valida id
        if (id == null) {
            Notification.show("ID da tarefa ausente", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }

        java.util.Optional<Task> opt = taskService.findById(id);
        if (opt.isEmpty()) {
            Notification.show("Tarefa não encontrada", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }
        Task task = opt.get();
        if (task == null) {
            Notification.show("Tarefa não encontrada", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }

        // Limpa conteúdo antigo
        removeAll();

        // Cabeçalho
        add(new com.vaadin.flow.component.html.H2("Tarefa: " + task.getDescription()));

        // Conteúdo do QR
        String qrContent = "/tarefa/" + task.getId(); // ou URL absoluto
        final int qrSize = 256;

        try {
            // Gera PNG em memória
            byte[] pngBytes = QrCodeGenerator.generateQrBytes(qrContent, qrSize);

            // Preview (Image)
            StreamResource imgResource = new StreamResource("qr-tarefa-" + task.getId() + ".png",
                    () -> new ByteArrayInputStream(pngBytes));
            imgResource.setContentType("image/png");
            Image qrImage = new Image(imgResource, "QR Code da tarefa");
            qrImage.setWidth(qrSize + "px");
            qrImage.setHeight(qrSize + "px");
            add(qrImage);

            // Botão de download
            StreamResource downloadResource = new StreamResource("qr-tarefa-" + task.getId() + ".png",
                    () -> new ByteArrayInputStream(pngBytes));
            downloadResource.setContentType("image/png");
            Anchor downloadAnchor = new Anchor(downloadResource, "");
            downloadAnchor.getElement().setAttribute("download", true);
            Button downloadButton = new Button("Descarregar QR", new Icon(VaadinIcon.DOWNLOAD));
            downloadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            downloadAnchor.add(downloadButton);
            add(downloadAnchor);

            // Tenta gravar no servidor (opcional)
            try {
                QrCodeGenerator.generateQrToFile(qrContent, "qr-tarefa-" + task.getId(), qrSize);
            } catch (IOException | com.google.zxing.WriterException ex) {
                // não bloqueia a UI
                Notification.show("QR gerado, mas falhou a gravação em disco: " + ex.getMessage(),
                                4000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            }

        } catch (com.google.zxing.WriterException | IOException ex) {
            Notification.show("Falha ao gerar QR Code: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
