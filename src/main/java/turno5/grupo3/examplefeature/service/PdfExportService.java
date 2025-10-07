package turno5.grupo3.examplefeature.service;

import turno5.grupo3.examplefeature.Task;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

// Importações do Apache PDFBox
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@Service
public class PdfExportService {

    /**
     * Gera o PDF com a lista de tarefas e retorna como um array de bytes.
     * @param tasks Lista de tarefas a serem incluídas no PDF.
     * @return Array de bytes do documento PDF gerado.
     */
    public byte[] generateTasksPdf(List<Task> tasks) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Carrega fonte TrueType que suporta caracteres especiais
            PDType0Font fontBold;
            PDType0Font fontRegular;

            try (InputStream fontStream = getClass().getResourceAsStream("/fonts/LiberationSans-Regular.ttf");
                 InputStream fontBoldStream = getClass().getResourceAsStream("/fonts/LiberationSans-Bold.ttf")) {

                if (fontStream != null && fontBoldStream != null) {
                    fontRegular = PDType0Font.load(document, fontStream);
                    fontBold = PDType0Font.load(document, fontBoldStream);
                } else {
                    // Fallback: usa fonte embutida do sistema (se disponível)
                    throw new IOException("Fontes TrueType não encontradas no classpath");
                }
            }

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Coordenadas iniciais
            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float yPosition = yStart;
            float leading = 15f; // Espaçamento entre linhas

            // Cria o contentStream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

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
                    // Fecha o stream da página anterior
                    contentStream.close();

                    // Cria nova página
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    // Cria NOVO stream para a nova página
                    contentStream = new PDPageContentStream(document, page);

                    yPosition = yStart; // Reseta a posição Y
                }

                // Status


                String status = task.isDone() ? "[CONCLUÍDA]" : "[PENDENTE]";

                // Descrição
                contentStream.setFont(fontBold, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(status + " " + task.getDescription());
                contentStream.endText();
                yPosition -= leading;

                // Detalhes (Data de Criação e Vencimento)
                String details = String.format("   Criada em: %s | Vencimento: %s",
                        task.getCreationDate(),
                        task.getDueDate() != null ? task.getDueDate() : "N/A");

                contentStream.setFont(fontRegular, 9);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(details);
                contentStream.endText();
                yPosition -= leading * 2; // Duplo espaçamento após o item

                // Desenha linha para separar visualmente
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();
                yPosition -= leading;
            }

            // Fecha o último contentStream antes de salvar o documento
            contentStream.close();

            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            System.err.println("Erro ao gerar PDF: " + e.getMessage());
            e.printStackTrace();
            return new byte[0]; // Retorna array vazio em caso de falha
        }
    }
}