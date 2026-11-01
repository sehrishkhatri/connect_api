package com.example.demo.service;

import com.example.demo.algorithm.*;
import com.example.demo.model.CompressionResult;
import com.example.demo.validation.FileValidator;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class CompressionService {

    private final LosslessCompression losslessCompression;
    private final QualityCompression qualityCompression;
    private final AdaptiveCompression adaptiveCompression;
    private final FileValidator fileValidator;

    public CompressionService(
            LosslessCompression losslessCompression,
            QualityCompression qualityCompression,
            AdaptiveCompression adaptiveCompression,
            FileValidator fileValidator) {

        this.losslessCompression = losslessCompression;
        this.qualityCompression = qualityCompression;
        this.adaptiveCompression = adaptiveCompression;
        this.fileValidator = fileValidator;
    }

    public CompressionResult compress(
            MultipartFile file,
            String algorithm,
            int compressionLevel
    ) throws IOException {

        String format = fileValidator.validateAndGetFormat(file);

        if (compressionLevel < 10 || compressionLevel > 95) {
            throw new IllegalArgumentException(
                    "Compression level must be between 10 and 95."
            );
        }

        CompressionAlgorithm selectedAlgorithm =
                CompressionAlgorithm.fromString(algorithm);

        byte[] original = file.getBytes();
        byte[] compressed;

        if (format.equals("pdf")) {
            compressed = compressPdf(original, selectedAlgorithm, compressionLevel);
        } else {
            compressed = compressImage(
                    original,
                    format,
                    selectedAlgorithm,
                    compressionLevel
            );
        }

        if (compressed.length >= original.length) {
            compressed = original;
        }

        long originalSize = original.length;
        long compressedSize = compressed.length;

        double reduction = originalSize == 0
                ? 0
                : ((double) (originalSize - compressedSize) / originalSize) * 100;

        return new CompressionResult(
                compressed,
                file.getOriginalFilename(),
                format,
                originalSize,
                compressedSize,
                Math.round(reduction * 100.0) / 100.0,
                selectedAlgorithm.name()
        );
    }

    private byte[] compressImage(
            byte[] original,
            String format,
            CompressionAlgorithm algorithm,
            int level
    ) throws IOException {

        return switch (algorithm) {
            case LOSSLESS -> losslessCompression.compressImage(original, format);
            case QUALITY -> {
                if (!format.equals("jpg") && !format.equals("jpeg")) {
                    throw new IllegalArgumentException(
                            "Quality-based compression currently supports JPEG images only. Use LOSSLESS or ADAPTIVE for other formats."
                    );
                }
                yield qualityCompression.compressImage(original, level);
            }
            case ADAPTIVE -> adaptiveCompression.compress(original, format, level);
        };
    }

    private byte[] compressPdf(byte[] original, CompressionAlgorithm algorithm, int level)
            throws IOException {

        if (algorithm == CompressionAlgorithm.LOSSLESS) {
            try (PDDocument document = Loader.loadPDF(original);
                 ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                document.save(output);
                return output.toByteArray();
            }
        }

        try (PDDocument document = Loader.loadPDF(original);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            float quality = level / 100f;

            for (PDPage page : document.getPages()) {
                PDResources resources = page.getResources();
                if (resources == null) continue;

                for (COSName name : resources.getXObjectNames()) {
                    PDXObject xObject = resources.getXObject(name);

                    if (xObject instanceof PDImageXObject imageXObject) {
                        BufferedImage rawImage = imageXObject.getImage();
                        if (rawImage == null) continue;

                        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();

                        try (ImageOutputStream ios = ImageIO.createImageOutputStream(imageStream)) {
                            writer.setOutput(ios);
                            ImageWriteParam param = writer.getDefaultWriteParam();
                            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                            param.setCompressionQuality(quality);
                            writer.write(null, new IIOImage(rawImage, null, null), param);
                        } finally {
                            writer.dispose();
                        }

                        PDImageXObject compressedImage = JPEGFactory.createFromStream(
                                document,
                                new java.io.ByteArrayInputStream(imageStream.toByteArray())
                        );

                        resources.put(name, compressedImage);
                    }
                }
            }

            document.save(output);
            return output.toByteArray();
        }
    }
}
