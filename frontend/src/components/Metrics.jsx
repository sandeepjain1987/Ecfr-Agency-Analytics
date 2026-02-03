import { useEffect, useState } from "react";
import { fetchAgencies } from "../api/client";

export default function AgencyMetrics() {
  const [agencies, setAgencies] = useState([]);
  const [selected, setSelected] = useState("");
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(false);

  // Load agencies on mount
  useEffect(() => {
    fetch("https://ecfr-agency-analytics.onrender.com/api/agencies")
      .then(res => res.json())
      .then(data => setAgencies(data))
      .catch(err => console.error("Failed to load agencies", err));
  }, []);

  const loadMetrics = () => {
    if (!selected) return;

    setLoading(true);
    setMetrics(null);

    fetch(`https://ecfr-agency-analytics.onrender.com/api/metrics/agency/${selected}`)
      .then(res => res.json())
      .then(data => setMetrics(data))
      .catch(err => console.error("Failed to load metrics", err))
      .finally(() => setLoading(false));
  };

  return (
    <div style={{ padding: "20px", maxWidth: "600px" }}>
      <h2>Per‑Agency Metrics</h2>

      <label>Select Agency:</label>
      <select
        value={selected}
        onChange={(e) => setSelected(e.target.value)}
        style={{ width: "100%", padding: "8px", marginTop: "8px" }}
      >
        <option value="">-- Choose Agency --</option>
        {agencies.map(a => (
          <option key={a.id} value={a.id}>
            {a.name}
          </option>
        ))}
      </select>

      <button
        onClick={loadMetrics}
        disabled={!selected || loading}
        style={{
          marginTop: "15px",
          padding: "10px 15px",
          width: "100%",
          background: "#0078d4",
          color: "white",
          border: "none",
          cursor: "pointer"
        }}
      >
        {loading ? "Loading..." : "Load Metrics"}
      </button>

      {metrics && (
        <div style={{ marginTop: "20px" }}>
          <h3>Results</h3>
          <ul>
            <li><strong>Total Parts:</strong> {metrics.totalParts}</li>
            <li><strong>Total Words:</strong> {metrics.totalWords}</li>
            <li><strong>Total Complexity:</strong> {metrics.totalComplexity}</li>
          </ul>
        </div>
      )}
    </div>
  );
}