import { useEffect, useRef, useState } from "react";
import Chart from "chart.js/auto";

export default function AgencyPieChart() {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);
  const [data, setData] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/metrics/agencies")
      .then(res => res.json())
      .then(json => setData(json))
      .catch(err => console.error("Failed to load agency metrics", err));
  }, []);

  useEffect(() => {
    if (data.length === 0) return;

    const ctx = chartRef.current.getContext("2d");

    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    chartInstance.current = new Chart(ctx, {
      type: "pie",
      data: {
        labels: data.map(a => a.name),
        datasets: [
          {
            label: "Total Words per Agency",
            data: data.map(a => a.totalWords),
            backgroundColor: [
              "#0078d4", "#00a65a", "#f39c12", "#d81b60",
              "#6f42c1", "#17a2b8", "#ffc107", "#28a745"
            ]
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: "bottom" }
        }
      }
    });
  }, [data]);

  return (
    <div style={{ padding: "20px" }}>
      <h2>Total Words Distribution by Agency</h2>
      <canvas ref={chartRef} height="200"></canvas>
    </div>
  );
}