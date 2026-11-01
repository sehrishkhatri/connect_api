
package com.example.demo.model;

public class CompressionResult{

    private final byte[] compressedFile;
    private final String originalFileName;
    private final String outputFormat;
    private final long originalSize;
    private final long compressedSize;
    private final double reductionPercentage;
    private final String algorithm;

    public CompressionResult(
            byte[] compressedFile,
            String originalFileName,
            String outputFormat,
            long originalSize,
            long compressedSize,
            double reductionPercentage,
            String algorithm) {

        this.compressedFile = compressedFile;
        this.originalFileName = originalFileName;
        this.outputFormat = outputFormat;
        this.originalSize = originalSize;
        this.compressedSize = compressedSize;
        this.reductionPercentage = reductionPercentage;
        this.algorithm = algorithm;
    }

    public byte[] getCompressedFile() {
        return compressedFile;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public long getOriginalSize() {
        return originalSize;
    }

    public long getCompressedSize() {
        return compressedSize;
    }

    public double getReductionPercentage() {
        return reductionPercentage;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
