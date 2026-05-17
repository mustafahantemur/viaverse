import Link from "next/link";

export default function HomePage() {
    return (
        <main className="shell">
            <div className="auth-card">
                <h1>Viaverse</h1>
                <p className="secondary-link">
                    <Link href="/login">Sign in</Link>
                </p>
                <p className="secondary-link">
                    <Link href="/register">Create account</Link>
                </p>
            </div>
        </main>
    );
}
