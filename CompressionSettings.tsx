"use client";

import { CompressionSettings as SettingsType } from "@/types/compression";
import {
  ShieldCheck,
  Image as ImageIcon,
  WandSparkles,
  SlidersHorizontal,
} from "lucide-react";

interface Props {
  settings: SettingsType;
  onChange: (settings: SettingsType) => void;
  disabled?: boolean;
  isPdf?: boolean;
}

export default function CompressionSettings({
  settings,
  onChange,
  disabled = false,
  isPdf = false,
}: Props) {

  function updateSettings(
    key: keyof SettingsType,
    value: string | number
  ) {
    onChange({
      ...settings,
      [key]: value,
    });
  }

  const algorithms = [
    {
      id: "LOSSLESS" as const,
      name: "Lossless",
      description: "Preserve original image pixels where possible.",
      icon: ShieldCheck,
    },
    {
      id: "QUALITY" as const,
      name: "Quality Based",
      description: "Control JPEG image quality.",
      icon: ImageIcon,
    },
    {
      id: "ADAPTIVE" as const,
      name: "Adaptive",
      description: "Automatically select suitable image settings.",
      icon: WandSparkles,
    },
  ];

  return (
    <div className="settings-panel">

      <div className="section-heading">
        <SlidersHorizontal size={21} />
        <h2>Compression Settings</h2>
      </div>

      <p className="settings-description">
        Select how you want your file to be compressed.
      </p>

      <div className="algorithm-list">

        {algorithms.map((algorithm) => {

          const Icon = algorithm.icon;

          return (

            <button
              type="button"
              key={algorithm.id}
              disabled={disabled}
              onClick={() =>
                updateSettings("algorithm", algorithm.id)
              }
              className={`algorithm-card ${
                settings.algorithm === algorithm.id
                  ? "selected"
                  : ""
              }`}
            >

              <div className="algorithm-icon">
                <Icon size={23} />
              </div>

              <div className="algorithm-info">

                <strong>{algorithm.name}</strong>

                <span>{algorithm.description}</span>

              </div>

              <div className="radio-indicator">
                {settings.algorithm === algorithm.id && (
                  <div />
                )}
              </div>

            </button>
          );
        })}

      </div>

      <div className="quality-section">

        <div className="quality-heading">

          <label htmlFor="compression-level">
            Compression Intensity
          </label>

          <strong>
            {settings.compressionLevel}%
          </strong>

        </div>

        <input
          id="compression-level"
          type="range"
          min="10"
          max="95"
          step="5"
          value={settings.compressionLevel}
          disabled={
            disabled ||
            settings.algorithm === "LOSSLESS"
          }
          onChange={(event) =>
            updateSettings(
              "compressionLevel",
              Number(event.target.value)
            )
          }
        />

        <div className="range-labels">
          <span>Conservative</span>
          <span>Stronger compression</span>
        </div>

        {isPdf ? (

          <p className="setting-note">
            PDF processing currently uses lossless stream
            optimization. The image quality slider does not
            alter embedded PDF images.
          </p>

        ) : settings.algorithm === "QUALITY" ? (

          <p className="setting-note">
            Quality-based mode currently supports JPEG images only.
          </p>

        ) : (

          <p className="setting-note">
            Higher intensity requests stronger compression.
            Actual savings depend on the original file.
          </p>

        )}

      </div>

    </div>
  );
}