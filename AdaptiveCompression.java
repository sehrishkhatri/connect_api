package com.example.demo.algorithm;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AdaptiveCompression {

    private final LosslessCompression losslessCompression;
    private final QualityCompression qualityCompression;

    public AdaptiveCompression(
            LosslessCompression losslessCompression,
            QualityCompression qualityCompression) {

        this.losslessCompression = losslessCompression;
        this.qualityCompression = qualityCompression;
    }

    public byte[] compress(
            byte[] original,
            String format,
            int compressionLevel
    ) throws IOException {

        format = format.toLowerCase();

        // Preserve JPEG without lossy processing
        // when the requested intensity is conservative.
        if (format.equals("jpg") || format.equals("jpeg")) {

            if (compressionLevel < 30) {
                return original;
            }

            int quality = compressionLevel >= 80
                    ? 85
                    : compressionLevel >= 50
                    ? 75
                    : 65;

            return qualityCompression.compressImage(
                    original,
                    quality
            );
        }

        // For PNG and other supported lossless formats,
        // use lossless re-encoding.
        return losslessCompression.compressImage(
                original,
                format
        );
    }
}