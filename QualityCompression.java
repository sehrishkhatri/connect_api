package com.example.demo.algorithm;

import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Component
public class QualityCompression {

    public byte[] compressImage(byte[] original, int quality)
            throws IOException {

        BufferedImage source = ImageIO.read(
                new ByteArrayInputStream(original)
        );

        if (source == null) {
            throw new IOException("Unable to read image.");
        }

        Iterator<ImageWriter> writers =
                ImageIO.getImageWritersByFormatName("jpeg");

        if (!writers.hasNext()) {
            throw new IOException("JPEG writer unavailable.");
        }

        ImageWriter writer = writers.next();

        // JPEG does not support transparency.
        // This method is only used for JPEG input.
        BufferedImage image = source;

        if (source.getType() != BufferedImage.TYPE_BYTE_GRAY
                && source.getType() != BufferedImage.TYPE_3BYTE_BGR) {

            BufferedImage converted = new BufferedImage(
                    source.getWidth(),
                    source.getHeight(),
                    source.getColorModel().getNumColorComponents() == 1
                            ? BufferedImage.TYPE_BYTE_GRAY
                            : BufferedImage.TYPE_3BYTE_BGR
            );

            Graphics2D graphics = converted.createGraphics();
            graphics.drawImage(source, 0, 0, null);
            graphics.dispose();

            image = converted;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (ImageOutputStream imageOutput =
                     ImageIO.createImageOutputStream(output)) {

            writer.setOutput(imageOutput);

            ImageWriteParam parameters = writer.getDefaultWriteParam();

            parameters.setCompressionMode(
                    ImageWriteParam.MODE_EXPLICIT
            );

            float compressionQuality = Math.max(
                    0.10f,
                    Math.min(0.95f, quality / 100.0f)
            );

            parameters.setCompressionQuality(compressionQuality);

            writer.write(
                    null,
                    new IIOImage(image, null, null),
                    parameters
            );

            imageOutput.flush();

        } finally {
            writer.dispose();
        }

        byte[] compressed = output.toByteArray();

        return compressed.length < original.length
                ? compressed
                : original;
    }
}