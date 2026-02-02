const API_BASE = 'http://localhost:8080/api';

export async function fetchSnapshots() {
  const res = await fetch(`${API_BASE}/snapshots`);
  return res.json();
}

export async function fetchAgencies(snapshotDate?: string) {
  const url = snapshotDate
    ? `${API_BASE}/agencies?snapshotDate=${snapshotDate}`
    : `${API_BASE}/agencies`;
  const res = await fetch(url);
  return res.json();
}

export async function fetchAgencyDetail(id: number) {
  const res = await fetch(`${API_BASE}/agencies/${id}`);
  return res.json();
}