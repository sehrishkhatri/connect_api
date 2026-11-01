"use client";

import { useEffect, useState } from "react";
import {
  Download,
  CheckCircle2,
  FileArchive,
  TrendingDown,
} from "lucide-react";

import { CompressionResultData } from "@/types/compression";

interface Props {
  result: CompressionResultData;
}

function formatSize(bytes: number): string {

  if (bytes < 1024) {
    return `${bytes} B`;
  }

  if (bytes < 1024 * 1024) {
    return `${(bytes / 1024).toFixed(2)} KB`;
  }

  return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
}

export default function CompressionResult({
  result,
}: Props) {

  const [downloadUrl, setDownloadUrl] = useState("");

  useEffect(() => {

    const url = URL.createObjectURL(result.blob);

    setDownloadUrl(url);

    return () => {
      URL.revokeObjectURL(url);
    };

  }, [result.blob]);

  const savedBytes = Math.max(
    0,
    result.originalSize - result.compressedSize
  );

  return (

    <div className="result-panel">

      <div className="result-heading">

        <CheckCircle2 size={25} />

        <div>
          <h2>Compression Complete!</h2>
          <p>Your file is ready for download.</p>
        </div>

      </div>

      <div className="result-stats">

        <div className="stat-card">

          <span>Original Size</span>

          <strong>
            {formatSize(result.originalSize)}
          </strong>

        </div>

        <div className="stat-card">

          <span>Compressed Size</span>

          <strong>
            {formatSize(result.compressedSize)}
          </strong>

        </div>

        <div className="stat-card highlight-stat">

          <span>Space Saved</span>

          <strong>
            {result.reductionPercentage.toFixed(2)}%
          </strong>

        </div>

      </div>

      <div className="saved-info">

        <TrendingDown size={20} />

        <span>
          {formatSize(savedBytes)} saved
        </span>

      </div>

      <div className="result-file">

        <FileArchive size={23} />

        <div>
          <strong>{result.fileName}</strong>
          <span>{result.algorithm} compression</span>
        </div>

      </div>

      <a
        href={downloadUrl || undefined}
        download={result.fileName}
        className={`download-button ${
          !downloadUrl ? "disabled-download" : ""
        }`}
        aria-disabled={!downloadUrl}
        onClick={(event) => {
          if (!downloadUrl) {
            event.preventDefault();
          }
        }}
      >
        <Download size={20} />
        Download Compressed File
      </a>

    </div>
  );
}