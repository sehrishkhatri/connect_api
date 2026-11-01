package com.example.demo.algorithm;

public enum CompressionAlgorithm {

    LOSSLESS,
    QUALITY,
    ADAPTIVE;

    public static CompressionAlgorithm fromString(String value) {

        if (value == null || value.isBlank()) {
            return ADAPTIVE;
        }

        try {
            return CompressionAlgorithm.valueOf(
                    value.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid algorithm. Choose LOSSLESS, QUALITY or ADAPTIVE."
            );
        }
    }
}