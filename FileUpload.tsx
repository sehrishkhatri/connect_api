"use client";

import { useRef, useState } from "react";
import { Upload, FileImage, FileText, X } from "lucide-react";

interface FileUploadProps {
  file: File | null;
  onFileSelect: (file: File | null) => void;
}

export default function FileUpload({
  file,
  onFileSelect,
}: FileUploadProps) {

  const inputRef = useRef<HTMLInputElement>(null);

  const [dragging, setDragging] = useState(false);
  const [error, setError] = useState("");

  function validateFile(selectedFile: File) {

    const allowedTypes = [
      "image/png",
      "image/jpeg",
      "image/bmp",
      "application/pdf",
    ];

    if (!allowedTypes.includes(selectedFile.type)) {
      setError("Please upload PNG, JPG, JPEG, BMP or PDF.");
      return;
    }

    if (selectedFile.size > 50 * 1024 * 1024) {
      setError("Maximum file size is 50 MB.");
      return;
    }

    setError("");
    onFileSelect(selectedFile);
  }

  function handleDrop(
    event: React.DragEvent<HTMLDivElement>
  ) {

    event.preventDefault();
    setDragging(false);

    const selectedFile = event.dataTransfer.files[0];

    if (selectedFile) {
      validateFile(selectedFile);
    }
  }

  const isPdf = file?.type === "application/pdf";

  return (
    <div className="upload-section">

      <div
        className={`upload-box ${dragging ? "dragging" : ""}`}
        onDragOver={(event) => {
          event.preventDefault();
          setDragging(true);
        }}
        onDragLeave={() => setDragging(false)}
        onDrop={handleDrop}
        onClick={() => inputRef.current?.click()}
      >

        <input
          ref={inputRef}
          type="file"
          accept=".png,.jpg,.jpeg,.bmp,.pdf"
          hidden
          onChange={(event) => {
            const selectedFile = event.target.files?.[0];

            if (selectedFile) {
              validateFile(selectedFile);
            }

            event.target.value = "";
          }}
        />

        <div className="upload-icon">
          <Upload size={30} />
        </div>

        <h3>Drop your files here</h3>

        <p>
          or <span>browse files</span> from your computer
        </p>

        <small>
          PNG, JPG, JPEG, BMP and PDF · Maximum 50 MB
        </small>

      </div>

      {error && (
        <p className="error-message">{error}</p>
      )}

      {file && (

        <div className="selected-file">

          <div className="file-icon">
            {isPdf
              ? <FileText size={24} />
              : <FileImage size={24} />
            }
          </div>

          <div className="file-details">
            <strong>{file.name}</strong>

            <span>
              {(file.size / (1024 * 1024)).toFixed(2)} MB
            </span>
          </div>

          <button
            type="button"
            className="remove-button"
            onClick={(event) => {
              event.stopPropagation();
              onFileSelect(null);
              setError("");
            }}
          >
            <X size={20} />
          </button>

        </div>
      )}

    </div>
  );
}