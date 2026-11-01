
"use client";

import { useState } from "react";

import {
  FileImage,
  ShieldCheck,
  Zap,
  LockKeyhole,
  Loader2,
  Sparkles,
} from "lucide-react";

import FileUpload from "@/components/FileUpload";
import CompressionSettings from "@/components/CompressionSettings";
import CompressionResult from "@/components/CompressionResult";

import { compressFile } from "@/services/compressionService";

import type {
  CompressionSettings as SettingsType,
  CompressionResultData,
} from "@/types/compression";

export default function Home() {
  const [file, setFile] = useState<File | null>(null);

  const [settings, setSettings] = useState<SettingsType>({
    algorithm: "ADAPTIVE",
    compressionLevel: 75,
  });

  const [result, setResult] =
    useState<CompressionResultData | null>(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleCompression() {
    if (!file) {
      setError("Please select a file first.");
      return;
    }

    setLoading(true);
    setError("");
    setResult(null);

    try {
      const response = await compressFile(file, settings);
      setResult(response);
    } catch (exception: unknown) {
      setError(
        exception instanceof Error
          ? exception.message
          : "Something went wrong while compressing your file."
      );
    } finally {
      setLoading(false);
    }
  }

  function handleFileSelect(selectedFile: File | null) {
    setFile(selectedFile);
    setResult(null);
    setError("");
  }

  const isPdf =
    file?.type === "application/pdf" ||
    file?.name.toLowerCase().endsWith(".pdf") ||
    false;

  return (
    <main className="main-container">

      {/* Navigation */}
      <nav className="navbar">
        <div className="brand">
          <div className="brand-icon">
            <FileImage size={24} />
          </div>
          <span>Compressly</span>
        </div>

        <div className="nav-tag">
          Smart File Compression
        </div>
      </nav>

      {/* Hero Section */}
      <section className="hero">
        <div className="hero-badge">
          <Sparkles size={15} />
          Smart compression technology
        </div>

        <h1>
          Compress Files.
          <br />
          <span>Preserve Quality.</span>
        </h1>

        <p>
          Reduce image and PDF file sizes with intelligent
          compression algorithms. Simple, fast and secure.
        </p>
      </section>

      {/* Features */}
      <section className="features">
        <div>
          <ShieldCheck size={20} />
          <span>Quality-conscious processing</span>
        </div>

        <div>
          <Zap size={20} />
          <span>Three compression strategies</span>
        </div>

        <div>
          <LockKeyhole size={20} />
          <span>Temporary file processing</span>
        </div>
      </section>

      {/* Main Workspace */}
      <section className="workspace">

        {/* Left Side */}
        <div className="workspace-left">

          {/* Upload Panel */}
          <div className="panel">
            <div className="panel-heading">
              <span className="step-number">01</span>

              <div>
                <h2>Upload Your File</h2>
                <p>Select an image or PDF to compress.</p>
              </div>
            </div>

            <FileUpload
              file={file}
              onFileSelect={handleFileSelect}
            />
          </div>

          {/* Compression Settings */}
          <div className="panel">
            <div className="panel-heading">
              <span className="step-number">02</span>

              <div>
                <h2>Choose Compression</h2>
                <p>Customize the compression settings.</p>
              </div>
            </div>

            <CompressionSettings
              settings={settings}
              onChange={setSettings}
              disabled={loading}
              isPdf={isPdf}
            />
          </div>

          {/* Compress Button */}
          <button
            className="compress-button"
            type="button"
            disabled={!file || loading}
            onClick={handleCompression}
          >
            {loading ? (
              <>
                <Loader2 className="spinner" size={21} />
                Processing File...
              </>
            ) : (
              <>
                <Zap size={21} />
                Compress Now
              </>
            )}
          </button>

          {/* Error Message */}
          {error && (
            <div className="error-box" role="alert">
              {error}
            </div>
          )}

        </div>

        {/* Right Side */}
        <div className="workspace-right">
          <div className="result-container">

            <div className="panel-heading">
              <span className="step-number">03</span>

              <div>
                <h2>Your Results</h2>
                <p>View your compression statistics.</p>
              </div>
            </div>

            {result ? (
              <CompressionResult result={result} />
            ) : loading ? (
              <div className="empty-result">
                <Loader2 className="spinner" size={38} />

                <h3>Compressing your file</h3>

                <p>
                  Please wait while we process your file.
                </p>
              </div>
            ) : (
              <div className="empty-result">
                <div className="empty-icon">
                  <FileImage size={42} />
                </div>

                <h3>Ready to optimize?</h3>

                <p>
                  Upload a file, choose your compression
                  settings and click Compress Now.
                </p>
              </div>
            )}

          </div>
        </div>

      </section>

      {/* Footer */}
      <footer className="footer">
        <p>
          © 2026 Compressly · Smart Image & PDF Compression
        </p>
      </footer>

    </main>
  );
}
