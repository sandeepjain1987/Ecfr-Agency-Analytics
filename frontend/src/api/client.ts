const api = import.meta.env.VITE_API_BASE_URL;

export async function fetchAgencies() {
  const res = await fetch(`${api}/api/agencies`);
  return res.json();
}