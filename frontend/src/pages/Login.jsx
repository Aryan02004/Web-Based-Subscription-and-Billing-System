import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { ArrowRight, Eye, EyeOff, Globe2, Lock, Mail, ShieldCheck, Sparkles } from "lucide-react";
import { api } from "../lib/api";

function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const [formData, setFormData] = useState({
    email: "",
    password: "",
    rememberMe: true,
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(location.state?.message || "");
  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target;
    setFormData((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setMessage("");
    setLoading(true);

    try {
      await api.auth.login({
        email: formData.email.trim(),
        password: formData.password,
      });

      navigate("/dashboard/organizations", { replace: true });
    } catch (requestError) {
      setError(requestError.message || "Login failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#f8f9ff] text-slate-900">
      <div className="grid min-h-screen lg:grid-cols-2">
        <section className="relative hidden overflow-hidden bg-[linear-gradient(160deg,#0f766e_0%,#0f172a_70%)] p-12 text-white lg:flex lg:flex-col lg:justify-between">
          <div className="absolute -left-16 top-10 h-72 w-72 rounded-full bg-cyan-400/20 blur-3xl" />
          <div className="absolute bottom-0 right-0 h-80 w-80 rounded-full bg-sky-400/10 blur-3xl" />
          <div className="relative z-10 max-w-xl space-y-6">
            <div className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-4 py-2 text-sm font-semibold">
              <Sparkles className="h-4 w-4 text-cyan-300" />
              Secure backend login
            </div>
            <h1 className="text-5xl font-black tracking-tight">Welcome back to Subscriptor</h1>
            <p className="text-base leading-8 text-slate-200">
              Log in to manage subscriptions, invoices, and revenue workflows for Indian customers.
            </p>
          </div>

          <div className="relative z-10 grid gap-4 sm:grid-cols-2">
            <MetricCard title="UPI ready" description="Fast payment journeys for Indian users." />
            <MetricCard title="RBI aware" description="Recurring billing handled with secure sessions." />
            <div className="sm:col-span-2 rounded-3xl border border-white/10 bg-white/10 p-5 backdrop-blur-xl">
              <div className="flex items-center gap-4">
                <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-cyan-400 text-slate-950">
                  <ShieldCheck className="h-5 w-5" />
                </div>
                <div>
                  <p className="text-sm text-cyan-100">Protected access</p>
                  <p className="text-lg font-bold">JWT session stored after login</p>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="flex items-center justify-center px-4 py-10 sm:px-6 lg:px-10">
          <div className="w-full max-w-140">
            <div className="rounded-4xl border border-slate-200 bg-white p-6 shadow-2xl shadow-slate-200/50 sm:p-8">
              <div className="mb-8 space-y-2">
                <p className="text-sm font-semibold uppercase tracking-[0.28em] text-cyan-700">Login</p>
                <h2 className="text-3xl font-black tracking-tight text-slate-950">Access your account</h2>
                <p className="text-sm leading-7 text-slate-600">Sign in using the backend login endpoint.</p>
              </div>

              {message ? <p className="mb-5 rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">{message}</p> : null}
              {error ? <p className="mb-5 rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p> : null}

              <form className="space-y-5" onSubmit={handleSubmit}>
                <Field label="Email address" icon={Mail} name="email" value={formData.email} onChange={handleChange} placeholder="name@company.com" type="email" required />

                <label className="space-y-2">
                  <span className="block text-sm font-semibold text-slate-700">Password</span>
                  <div className="relative">
                    <Lock className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                    <input
                      className="w-full rounded-2xl border border-slate-200 bg-slate-50 py-3 pl-10 pr-12 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-cyan-500 focus:bg-white focus:ring-4 focus:ring-cyan-100"
                      name="password"
                      onChange={handleChange}
                      placeholder="Enter your password"
                      required
                      type={showPassword ? "text" : "password"}
                      value={formData.password}
                    />
                    <button
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-500 transition hover:text-cyan-700"
                      onClick={() => setShowPassword((current) => !current)}
                      type="button"
                    >
                      {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                    </button>
                  </div>
                </label>

                <div className="flex items-center justify-between gap-4">
                  <label className="flex items-center gap-3 text-sm text-slate-600">
                    <input
                      checked={formData.rememberMe}
                      className="h-4 w-4 rounded border-slate-300 text-cyan-700 focus:ring-cyan-600"
                      name="rememberMe"
                      onChange={handleChange}
                      type="checkbox"
                    />
                    Remember me
                  </label>
                  <Link className="text-sm font-semibold text-cyan-700 hover:underline" to="/register">
                    Create account
                  </Link>
                </div>

                <button
                  className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-cyan-700 px-6 py-4 text-sm font-semibold text-white shadow-lg shadow-cyan-700/20 transition hover:bg-cyan-800 disabled:cursor-not-allowed disabled:opacity-70"
                  disabled={loading}
                  type="submit"
                >
                  {loading ? "Signing in..." : "Login"}
                  <ArrowRight className="h-4 w-4" />
                </button>
              </form>

              <div className="mt-8 rounded-2xl border border-slate-200 bg-slate-50 p-4 text-sm text-slate-600">
                Need help? Make sure the backend is running on <span className="font-semibold text-slate-900">http://localhost:8080</span> or set <span className="font-semibold text-slate-900">VITE_API_BASE_URL</span>.
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}

function MetricCard({ title, description }) {
  return (
    <div className="rounded-3xl border border-white/10 bg-white/10 p-5 backdrop-blur-xl">
      <div className="mb-3 inline-flex h-10 w-10 items-center justify-center rounded-2xl bg-white/15 text-cyan-200">
        <Globe2 className="h-5 w-5" />
      </div>
      <p className="text-sm text-cyan-100">{title}</p>
      <p className="mt-2 text-base font-semibold text-white">{description}</p>
    </div>
  );
}

function Field({ label, icon: Icon, ...props }) {
  return (
    <label className="space-y-2">
      <span className="block text-sm font-semibold text-slate-700">{label}</span>
      <div className="relative">
        <Icon className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
        <input
          className="w-full rounded-2xl border border-slate-200 bg-slate-50 py-3 pl-10 pr-4 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-cyan-500 focus:bg-white focus:ring-4 focus:ring-cyan-100"
          {...props}
        />
      </div>
    </label>
  );
}

export default Login;
