import { useEffect, useState } from "react";
//import { fetchAgencies } from "../api/client";

export default function AgencyIngestor() {
  const [agencies, setAgencies] = useState([]);
  const [selected, setSelected] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  // Load agencies on mount
  useEffect(() => {
  fetch("https://ecfr-agency-analytics.onrender.com/api/agencies")
      .then(res => res.json())
      .then(data => setAgencies(data))
      .catch(err => console.error("Failed to load agencies", err));
  }, []);

  const ingestAgency = () => {
    if (!selected) return;

    setLoading(true);
    setMessage("");

      fetch(`https://ecfr-agency-analytics.onrender.com/api/ingest/agency/${selected}`, {
      method: "POST"
    })
      .then(res => {
        if (res.ok) {
          setMessage("Ingestion started for selected agency.");
        } else {
          setMessage("Failed to start ingestion.");
        }
      })
      .catch(() => setMessage("Error contacting backend."))
      .finally(() => setLoading(false));
  };

  return (
    <div style={{ padding: "20px", maxWidth: "500px" }}>
      <h2>Ingest CFR for Agency</h2>

      <label>Select Agency:</label>
      <select
        value={selected}
        onChange={(e) => setSelected(e.target.value)}
        style={{ width: "100%", padding: "8px", marginTop: "8px" }}
      >
        <option value="">-- Choose Agency --</option>
        {agencies.map(a => (
          <option key={a.id} value={a.id}>
            { a.name}
          </option>
        ))}
      </select>

      <button
        onClick={ingestAgency}
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
        {loading ? "Ingesting..." : "Start Ingestion"}
      </button>

      {message && (
        <p style={{ marginTop: "15px", color: "#444" }}>{message}</p>
      )}
    </div>
  );
}