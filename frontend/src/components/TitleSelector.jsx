import { useEffect, useState } from "react";

export default function TitleSelector() {
  const [titles, setTitles] = useState([]);
  const [selected, setSelected] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/api/titles")
      .then(res => res.json())
      .then(data => setTitles(data));
  }, []);

  const ingest = () => {
    fetch(`http://localhost:8080/api/ingest/title/${selected}`, {
      method: "GET"
    });
  };

  return (
    <div>
      <label>Select CFR Title:</label>
      <select
        value={selected}
        onChange={e => setSelected(e.target.value)}
      >
        <option value="">-- choose --</option>
        {titles.map(t => (
          <option key={t.number} value={t.number}>
            Title {t.number} — {t.name}
          </option>
        ))}
      </select>

      <button onClick={ingest} disabled={!selected}>
        Ingest
      </button>
    </div>
  );
}