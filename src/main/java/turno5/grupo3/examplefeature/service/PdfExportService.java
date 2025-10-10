package turno5.grupo3.examplefeature.service;

import turno5.grupo3.examplefeature.Task;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.List;

// Importações do Apache PDFBox
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font; // Importação correta
import org.apache.pdfbox.pdmodel.font.Standard14Fonts; // <<< NOVO IMPORT
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@Service
public class PdfExportService {

    public byte[] generateTasksPdf(List<Task> tasks) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // 1. CARREGAMENTO DE FONTES CORRIGIDO: Usa a nova API Standard14Fonts
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD); // <<< CORREÇÃO
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);   // <<< CORREÇÃO

            // REMOVEMOS: O bloco try-catch de carregamento de ficheiros (que causava o erro de classpath)

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float yPosition = yStart;
            float leading = 15f;

            // Declarar o contentStream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try { // Bloco try-finally para garantir o fecho do contentStream

                // Título do Documento
                contentStream.setFont(fontBold, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Relatório de Tarefas (TO-DO)");
                contentStream.endText();
                yPosition -= 2 * leading;

                // Loop pelas Tarefas
                contentStream.setFont(fontRegular, 12);

                for (Task task : tasks) {
                    if (yPosition < margin) {
                        // 1. FECHAR o stream da página anterior
                        contentStream.close();

                        // 2. Criar nova página
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);

                        // 3. Criar NOVO stream para a nova página
                        contentStream = new PDPageContentStream(document, page);

                        yPosition = yStart; // Reseta a posição Y
                    }

                    // Status (usa o método isDone() da Task)
                    String status = task.isDone() ? "[CONCLUÍDA]" : "[PENDENTE]";

                    // Descrição
                    contentStream.setFont(fontBold, 10);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(status + " " + task.getDescription());
                    contentStream.endText();
                    yPosition -= leading;

                    // Detalhes (Conversão de Instant para LocalDate CORRIGIDA)
                    String details = String.format("   Criada em: %s | Vencimento: %s",
                            task.getCreationDate().atZone(ZoneId.systemDefault()).toLocalDate(),
                            task.getDueDate() != null ? task.getDueDate() : "N/A");

                    contentStream.setFont(fontRegular, 9);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(details);
                    contentStream.endText();
                    yPosition -= leading * 2;

                    // Desenha linha para separar visualmente
                    contentStream.moveTo(margin, yPosition);
                    contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                    contentStream.stroke();
                    yPosition -= leading;
                }

            } finally {
                // GARANTE que o último contentStream é fechado
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            System.err.println("Erro ao gerar PDF: " + e.getMessage());
            e.printStackTrace();
            return new byte[0];
        }
    }
}