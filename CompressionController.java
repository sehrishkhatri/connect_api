package com.example.demo.controller;

import com.example.demo.model.CompressionResult;
import com.example.demo.service.CompressionService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/compress")
public class CompressionController {

    private final CompressionService compressionService;

    public CompressionController(CompressionService compressionService) {
        this.compressionService = compressionService;
    }

    @PostMapping
    public ResponseEntity<byte[]> compressFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "ADAPTIVE") String algorithm,
            @RequestParam(defaultValue = "75") int compressionLevel
    ) throws IOException {

        CompressionResult result;

        try {
            result = compressionService.compress(
                    file,
                    algorithm,
                    compressionLevel
            );
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }

        String originalName = result.getOriginalFileName();

        String outputName = "compressed_"
                + originalName.replaceAll("[^a-zA-Z0-9._-]", "_");

        String encodedName = URLEncoder.encode(
                outputName,
                StandardCharsets.UTF_8
        ).replace("+", "%20");

        // Dynamically assign Content-Type so clients know how to render the preview
        MediaType contentType;
        String format = result.getOutputFormat().toLowerCase().replace(".", "");

        if (format.equals("pdf")) {
            contentType = MediaType.APPLICATION_PDF;
        } else if (format.equals("jpg") || format.equals("jpeg")) {
            contentType = MediaType.IMAGE_JPEG;
        } else if (format.equals("png")) {
            contentType = MediaType.IMAGE_PNG;
        } else {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedName
                )
                .header(
                        "X-Original-Size",
                        String.valueOf(result.getOriginalSize())
                )
                .header(
                        "X-Compressed-Size",
                        String.valueOf(result.getCompressedSize())
                )
                .header(
                        "X-Reduction-Percentage",
                        String.valueOf(result.getReductionPercentage())
                )
                .header(
                        "X-Algorithm",
                        result.getAlgorithm()
                )
                .body(result.getCompressedFile());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Compressly Backend is Running!");
    }
}
