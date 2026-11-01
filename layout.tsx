import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Compressly | Image & PDF Compressor",
  description:
    "Compress images and PDF documents while preserving quality.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {

  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}