import { useState } from "react";
import AgencyIngestor from "./components/AgencyIngestor";
import Metrics from "./components/Metrics";
import AgencyPieChart from "./components/AgencyPieChart";

export default function App() {
  const [activeTab, setActiveTab] = useState("ingest");

  return (
    <div style={{ maxWidth: "900px", margin: "0 auto", padding: "20px" }}>
      <h1>ECFR Dashboard</h1>

      <div style={{
        display: "flex",
        borderBottom: "2px solid #ccc",
        marginBottom: "20px"
      }}>
        <button
          onClick={() => setActiveTab("ingest")}
          style={{
            padding: "10px 20px",
            border: "none",
            background: "none",
            cursor: "pointer",
            borderBottom: activeTab === "ingest" ? "3px solid #0078d4" : "3px solid transparent",
            fontWeight: activeTab === "ingest" ? "600" : "400"
          }}
        >
          Ingest Data
        </button>

        <button
          onClick={() => setActiveTab("metrics")}
          style={{
            padding: "10px 20px",
            border: "none",
            background: "none",
            cursor: "pointer",
            borderBottom: activeTab === "metrics" ? "3px solid #0078d4" : "3px solid transparent",
            fontWeight: activeTab === "metrics" ? "600" : "400"
          }}
        >
          Metrics
        </button>
      </div>

      {activeTab === "ingest" && <AgencyIngestor />}
{/*       {activeTab === "metrics" && <Metrics />} */}
    {activeTab === "metrics" && (
      <div>
        <Metrics />
        <AgencyPieChart />
      </div>
)}




    </div>
  );
}