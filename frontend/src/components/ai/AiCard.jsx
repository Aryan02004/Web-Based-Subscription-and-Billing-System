import { useEffect, useState } from "react";
import { api } from "../../lib/api";

const isObject = (v) => v !== null && typeof v === "object";

const parseAiReport = (text) => {
  if (!text || !text.trim()) return {};
  const sections = {};
  const patterns = {
    businessHealth: /Business Health:\s*([^\n]+)/i,
    healthScore: /Health Score:\s*([^\n]+)/i,
    executiveSummary: /Executive Summary:\s*([\s\S]*?)(?:\n\s*Key Insights:|\n\s*Recommendations:|$)/i,
    keyInsights: /Key Insights:\s*([\s\S]*?)(?:\n\s*Recommendations:|$)/i,
    recommendations: /Recommendations:\s*([\s\S]*)/i,
  };

  Object.keys(patterns).forEach((k) => {
    const m = text.match(patterns[k]);
    if (m) sections[k] = m[1].trim();
  });

  if (sections.keyInsights) {
    sections.keyInsights = sections.keyInsights
      .split(/\r?\n/)
      .map((l) => l.replace(/^[-\d.)\s]+/, "").trim())
      .filter(Boolean);
  }
  if (sections.recommendations) {
    sections.recommendations = sections.recommendations
      .split(/\r?\n/)
      .map((l) => l.replace(/^[-\d.)\s]+/, "").trim())
      .filter(Boolean);
  }

  if (sections.healthScore) {
    const n = Number(sections.healthScore);
    if (!Number.isNaN(n)) sections.healthScore = n;
  }

  return sections;
};

export default function AiCard({ organizationId }) {
  const [loading, setLoading] = useState(false);
  const [aiReport, setAiReport] = useState("");
  const [error, setError] = useState("");
  const [rawError, setRawError] = useState("");
  const [cooldown, setCooldown] = useState(0);

  const load = async () => {
    setLoading(true);
    setError("");
    setRawError("");
    try {
      const res = await api.ai.dashboardSummary();
      setAiReport(res?.aiReport || "");
    } catch (e) {
      const debug = isObject(e)
        ? { message: e.message, status: e.status, data: e.data }
        : String(e);
      try {
        setRawError(JSON.stringify(debug, null, 2));
      } catch {
        setRawError(String(debug));
      }
      setError("Something went wrong on our side. Please try again in a moment.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // avoid calling setState synchronously inside effects
    const t = setTimeout(() => {
      void load();
    }, 0);
    return () => clearTimeout(t);
  }, [organizationId]);

  useEffect(() => {
    if (cooldown <= 0) return;
    const t = setInterval(() => setCooldown((c) => (c <= 1 ? 0 : c - 1)), 1000);
    return () => clearInterval(t);
  }, [cooldown]);



  const parsed = parseAiReport(aiReport || "");

  return (
    <div className="rounded-3xl border border-[#bbcac6] bg-white shadow-sm">
      <div className="rounded-t-3xl bg-linear-to-r from-[#06b6d4] to-[#8b5cf6] px-6 py-4">
        <div className="flex items-center justify-between">
          <p className="text-sm font-semibold uppercase tracking-[0.22em] text-white">AI Insights</p>
          <div className="flex items-center gap-3">
            {loading ? <p className="text-sm text-white/90">Generating…</p> : null}
          </div>
        </div>
      </div>

      <div className="p-6 text-sm text-[#0b1c30]">
        {error ? (
          <div className="space-y-3">
            <div className="text-rose-600">{error}</div>
            <div className="flex items-center gap-2">
              <button
                onClick={load}
                className="rounded-md bg-[#006b5f] px-3 py-1 text-sm font-semibold text-white"
              >
                Retry
              </button>
              <details className="text-xs text-[#6b7280]">
                <summary>Show details</summary>
                <pre className="mt-2 max-h-40 overflow-auto whitespace-pre-wrap text-xs">{rawError || "No error details."}</pre>
              </details>
            </div>
          </div>
        ) : !aiReport && !loading ? (
          <div className="text-[#6b7280]">No AI report available.</div>
        ) : (
          <div className="space-y-4">
            {parsed.businessHealth ? (
              <div className="flex items-center gap-3">
                <div className="h-10 w-10 rounded-full bg-white/20 flex items-center justify-center text-white font-semibold text-lg" style={{ background: parsed.healthScore && parsed.healthScore < 40 ? '#ef4444' : parsed.healthScore && parsed.healthScore < 65 ? '#f59e0b' : '#10b981' }}>
                  {parsed.healthScore != null ? parsed.healthScore : (parsed.businessHealth ? parsed.businessHealth[0]?.toUpperCase() : '—')}
                </div>
                <div>
                  <div className="text-xs font-semibold text-[#0b1c30]">Business Health</div>
                  <div className="text-sm text-[#374151]">{parsed.businessHealth}</div>
                </div>
              </div>
            ) : null}

            {parsed.executiveSummary ? (
              <div className="rounded-lg border border-[#eef2ff] bg-[#f8fafc] p-3">
                <p className="font-semibold text-[#0b1c30]">Executive summary</p>
                <p className="mt-2 text-sm text-[#374151]">{parsed.executiveSummary}</p>
              </div>
            ) : null}

            <div className="grid gap-4 sm:grid-cols-2">
              <div>
                <p className="font-semibold text-[#0b1c30]">Key insights</p>
                {Array.isArray(parsed.keyInsights) && parsed.keyInsights.length ? (
                  <ul className="mt-2 list-disc list-inside text-[#374151]">
                    {parsed.keyInsights.map((it, i) => (
                      <li key={i} className="text-sm">{it}</li>
                    ))}
                  </ul>
                ) : (
                  <div className="mt-2 text-sm text-[#6b7280]">No key insights provided.</div>
                )}
              </div>

              <div>
                <p className="font-semibold text-[#0b1c30]">Recommendations</p>
                {Array.isArray(parsed.recommendations) && parsed.recommendations.length ? (
                  <ol className="mt-2 list-decimal list-inside text-[#374151]">
                    {parsed.recommendations.map((it, i) => (
                      <li key={i} className="text-sm">{it}</li>
                    ))}
                  </ol>
                ) : (
                  <div className="mt-2 text-sm text-[#6b7280]">No recommendations provided.</div>
                )}
              </div>
            </div>

            <details className="text-xs text-[#6b7280]">
              <summary>Show raw AI output</summary>
              <pre className="mt-2 max-h-48 overflow-auto whitespace-pre-wrap text-xs">{aiReport || "(empty)"}</pre>
            </details>
          </div>
        )}
      </div>
    </div>
  );
}
