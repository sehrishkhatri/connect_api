import {
  CompressionSettings,
  CompressionResultData,
} from "@/types/compression";

const API_URL =
  process.env.NEXT_PUBLIC_API_URL ||
  "http://localhost:8080";

export async function compressFile(
  file: File,
  settings: CompressionSettings
): Promise<CompressionResultData> {

  const formData = new FormData();

  formData.append("file", file);
  formData.append("algorithm", settings.algorithm);
  formData.append(
    "compressionLevel",
    settings.compressionLevel.toString()
  );

  const response = await fetch(
    `${API_URL}/api/compress`,
    {
      method: "POST",
      body: formData,
    }
  );

  if (!response.ok) {

    let message = "Compression failed.";

    try {
      const error = await response.json();
      message =
        error.message ||
        error.detail ||
        error.error ||
        message;
    } catch {
      message = `Server error: ${response.status}`;
    }

    throw new Error(message);
  }

  const blob = await response.blob();

  const originalSize = Number(
    response.headers.get("X-Original-Size") || 0
  );

  const compressedSize = Number(
    response.headers.get("X-Compressed-Size") || blob.size
  );

  const reductionPercentage = Number(
    response.headers.get("X-Reduction-Percentage") || 0
  );

  const algorithm =
    response.headers.get("X-Algorithm") || "ADAPTIVE";

  return {
    blob,
    originalSize,
    compressedSize,
    reductionPercentage,
    algorithm,
    fileName: `compressed_${file.name}`,
  };
}