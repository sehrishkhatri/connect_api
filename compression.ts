
export type CompressionAlgorithm =
  | "LOSSLESS"
  | "QUALITY"
  | "ADAPTIVE";

export interface CompressionSettings {
  algorithm: CompressionAlgorithm;
  compressionLevel: number;
}

export interface CompressionResultData {
  blob: Blob;
  originalSize: number;
  compressedSize: number;
  reductionPercentage: number;
  algorithm: string;
  fileName: string;
}
