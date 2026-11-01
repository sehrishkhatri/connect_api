package com.example.demo.algorithm;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class LosslessCompression {

    public byte[] compressImage(byte[] original, String format)
            throws IOException {

        format = format.toLowerCase();

        // JPEG cannot be re-encoded using standard ImageIO
        // without potentially losing image information.
        if (format.equals("jpg") || format.equals("jpeg")) {
            return original;
        }

        BufferedImage image = ImageIO.read(
                new ByteArrayInputStream(original)
        );

        if (image == null) {
            throw new IOException("Unsupported image format.");
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        boolean written = ImageIO.write(image, format, output);

        if (!written) {
            throw new IOException(
                    "No image writer available for " + format
            );
        }

        byte[] compressed = output.toByteArray();

        return compressed.length < original.length
                ? compressed
                : original;
    }
}