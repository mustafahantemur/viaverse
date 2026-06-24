"use client";

import { useEffect, useState } from "react";

const ADMIN_BFF_BASE_URL =
  process.env.NEXT_PUBLIC_ADMIN_BFF_BASE_URL ?? "http://localhost:8110";

type BusinessSubmission = {
  accountId: string;
  tradeName?: string;
  legalName?: string;
  sector?: string;
  city?: string;
  verificationStatus: string;
};

export default function AdminHomePage() {
  const [submissions, setSubmissions] = useState<BusinessSubmission[]>([]);
  const [status, setStatus] = useState<"loading" | "ready" | "error">("loading");
  const [busyAccountId, setBusyAccountId] = useState<string | null>(null);

  async function load() {
    try {
      setStatus("loading");
      const response = await fetch(
        `${ADMIN_BFF_BASE_URL}/api/admin/business-profiles/submissions`,
      );
      const body = await response.json();
      setSubmissions(body.data ?? []);
      setStatus("ready");
    } catch {
      setStatus("error");
    }
  }

  useEffect(() => {
    void load();
  }, []);

  async function decide(accountId: string, action: "approve" | "reject") {
    setBusyAccountId(accountId);
    try {
      await fetch(`${ADMIN_BFF_BASE_URL}/api/admin/business-profiles/${accountId}/${action}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(action === "reject" ? { reason: "manual review rejected" } : {}),
      });
      await load();
    } finally {
      setBusyAccountId(null);
    }
  }

  return (
    <main className="shell">
      <header>
        <p>Viaverse Admin</p>
        <h1>İşletme onay kuyruğu</h1>
      </header>

      {status === "loading" && <p>Yükleniyor…</p>}
      {status === "error" && <p>Kuyruk yüklenemedi.</p>}
      {status === "ready" && submissions.length === 0 && <p>Bekleyen işletme yok.</p>}

      <section className="queue">
        {submissions.map((submission) => (
          <article key={submission.accountId} className="card">
            <div>
              <strong>{submission.tradeName ?? submission.legalName ?? "İsimsiz işletme"}</strong>
              <span>
                {submission.sector ?? "OTHER"} · {submission.city ?? "—"}
              </span>
            </div>
            <div className="actions">
              <button
                disabled={busyAccountId === submission.accountId}
                onClick={() => decide(submission.accountId, "approve")}
              >
                Onayla
              </button>
              <button
                className="danger"
                disabled={busyAccountId === submission.accountId}
                onClick={() => decide(submission.accountId, "reject")}
              >
                Reddet
              </button>
            </div>
          </article>
        ))}
      </section>
    </main>
  );
}
