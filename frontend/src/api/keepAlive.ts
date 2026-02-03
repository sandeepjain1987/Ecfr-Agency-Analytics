const API_BASE = import.meta.env.VITE_API_BASE_URL;

export function startKeepAlive() {
  // Ping every 10 minutes
  const interval = 10 * 60 * 1000;

  setInterval(() => {
    fetch(`${API_BASE}/api/health`)
      .then(() => console.log("Keep-alive ping sent"))
      .catch(() => console.log("Keep-alive failed (service may be sleeping)"));
  }, interval);
}