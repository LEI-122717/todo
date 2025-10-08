package turno5.grupo3.examplefeature.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public final class QrCodeGenerator {

    private QrCodeGenerator() {}

    /**
     * Gera um BufferedImage com as configurações pedidas (256x256, margem legível, nível M).
     */
    public static BufferedImage generateQrBufferedImage(String text, int size) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 2); // margem legível
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // nível M

        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Gera e escreve para disco em qrcodes/qr-tarefa-{id}.png (cria pasta se necessário).
     * Retorna o Path para o ficheiro criado.
     */
    public static Path generateQrToFile(String text, String filenameWithoutExt, int size) throws WriterException, IOException {
        BufferedImage img = generateQrBufferedImage(text, size);

        File dir = new File("qrcodes");
        if (!dir.exists()) dir.mkdirs();

        File out = new File(dir, filenameWithoutExt + ".png");
        ImageIO.write(img, "PNG", out);
        return out.toPath();
    }

    /**
     * Gera e devolve os bytes PNG (útil para criar StreamResource para download).
     */
    public static byte[] generateQrBytes(String text, int size) throws WriterException, IOException {
        BufferedImage img = generateQrBufferedImage(text, size);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "PNG", baos);
            baos.flush();
            return baos.toByteArray();
        }
    }

    /**
     * Gera um ByteArrayInputStream (útil se preferires essa API).
     */
    public static ByteArrayInputStream generateQrInputStream(String text, int size) throws WriterException, IOException {
        return new ByteArrayInputStream(generateQrBytes(text, size));
    }
}
