
package com.example.demo.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

@Component
public class FileValidator {

    private static final long MAX_SIZE = 50L * 1024 * 1024;

    public String validateAndGetFormat(MultipartFile file)
            throws IOException {

        // 1. Check whether a file was uploaded
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please upload a file."
            );
        }

        // 2. Check file size
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "File size cannot exceed 50 MB."
            );
        }

        // 3. Get and validate extension
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException(
                    "File extension is missing."
            );
        }

        String extension = filename.substring(
                filename.lastIndexOf('.') + 1
        ).toLowerCase(Locale.ROOT);

        // 4. Check PDF signature
        byte[] data = file.getBytes();

        if (data.length >= 5 &&
                data[0] == '%' &&
                data[1] == 'P' &&
                data[2] == 'D' &&
                data[3] == 'F' &&
                data[4] == '-') {

            if (!extension.equals("pdf")) {
                throw new IllegalArgumentException(
                        "The file content does not match its extension."
                );
            }

            return "pdf";
        }

        // 5. Only allow supported image extensions
        if (!extension.equals("png")
                && !extension.equals("jpg")
                && !extension.equals("jpeg")
                && !extension.equals("bmp")) {

            throw new IllegalArgumentException(
                    "Unsupported format. Upload PNG, JPG, JPEG, BMP or PDF."
            );
        }

        // 6. Check whether Java has a reader for the actual image
        try (ImageInputStream input = ImageIO.createImageInputStream(
                new ByteArrayInputStream(data))) {

            if (input == null) {
                throw new IllegalArgumentException(
                        "Unable to read the uploaded image."
                );
            }

            Iterator<ImageReader> readers =
                    ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new IllegalArgumentException(
                        "Java cannot decode this image. Please upload a valid PNG, JPG, JPEG or BMP file."
                );
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(input);

                // Validate image dimensions and image data
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                if (width <= 0 || height <= 0) {
                    throw new IllegalArgumentException(
                            "Invalid image dimensions."
                    );
                }

            } finally {
                reader.dispose();
            }
        }

        return extension;
    }
}
