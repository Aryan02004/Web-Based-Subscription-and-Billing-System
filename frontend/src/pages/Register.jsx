import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  ArrowRight,
  CheckCircle2,
  Lock,
  Mail,
  ShieldCheck,
  Sparkles,
  User,
} from "lucide-react";
import { api } from "../lib/api";

const trustPoints = [
  "UPI, cards, and recurring billing support",
  "GST-friendly invoices and local billing data",
  "Built to connect with the backend auth API",
];

function Register() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setMessage("");

    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);

    try {
      const response = await api.auth.register({
        firstName: formData.firstName.trim(),
        lastName: formData.lastName.trim() || null,
        email: formData.email.trim(),
        password: formData.password,
      });

      setMessage(response.message || "Registration successful.");
      navigate("/verify-email", {
        state: {
          userId: response.userId,
          email: formData.email.trim(),
          message:
            response.message ||
            "Registration complete. Please log in after verifying your email, if required.",
        },
      });
    } catch (requestError) {
      setError(requestError.message || "Registration failed.");
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
              India-ready registration
            </div>
            <h1 className="text-5xl font-black tracking-tight">
              Create your Subscriptor account
            </h1>
            <p className="text-base leading-8 text-slate-200">
              Set up your billing workspace for India-first subscriptions with
              clean onboarding, secure account creation, and a backend-backed
              signup flow.
            </p>
          </div>

          <div className="relative z-10 grid gap-4 sm:grid-cols-2">
            <div className="rounded-[1.5rem] border border-white/10 bg-white/10 p-5 backdrop-blur-xl">
              <p className="text-sm text-cyan-100">Fast integration</p>
              <p className="mt-2 text-lg font-bold">Connect in minutes</p>
            </div>
            <div className="rounded-[1.5rem] border border-white/10 bg-white/10 p-5 backdrop-blur-xl">
              <p className="text-sm text-cyan-100">Secure ledger</p>
              <p className="mt-2 text-lg font-bold">
                Protect your revenue data
              </p>
            </div>
            <div className="sm:col-span-2 rounded-[1.5rem] border border-white/10 bg-white/10 p-5 backdrop-blur-xl">
              <div className="flex items-center gap-4">
                <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-cyan-400 text-slate-950">
                  <ShieldCheck className="h-5 w-5" />
                </div>
                <div>
                  <p className="text-sm text-cyan-100">Built for Indian SaaS</p>
                  <p className="text-lg font-bold">
                    UPI, GST, and recurring billing support
                  </p>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="flex items-center justify-center px-4 py-10 sm:px-6 lg:px-10">
          <div className="w-full max-w-[560px]">
            <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-2xl shadow-slate-200/50 sm:p-8">
              <div className="mb-8 space-y-2">
                <p className="text-sm font-semibold uppercase tracking-[0.28em] text-cyan-700">
                  Create account
                </p>
                <h2 className="text-3xl font-black tracking-tight text-slate-950">
                  Register your business
                </h2>
                <p className="text-sm leading-7 text-slate-600">
                  Fill in your details to start working with the backend
                  registration endpoint.
                </p>
              </div>

              <form className="space-y-5" onSubmit={handleSubmit}>
                <div className="grid gap-4 sm:grid-cols-2">
                  <Field
                    label="First name"
                    icon={User}
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                    placeholder="Aarav"
                    required
                  />
                  <Field
                    label="Last name"
                    icon={User}
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                    placeholder="Sharma"
                  />
                </div>

                <Field
                  label="Email address"
                  icon={Mail}
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="name@company.com"
                  type="email"
                  required
                />
                <Field
                  label="Password"
                  icon={Lock}
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Create a secure password"
                  type="password"
                  required
                />
                <Field
                  label="Confirm password"
                  icon={Lock}
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="Repeat your password"
                  type="password"
                  required
                />

                <div className="rounded-2xl border border-cyan-100 bg-cyan-50/70 p-4 text-sm text-slate-700">
                  {trustPoints.map((point) => (
                    <div key={point} className="flex items-start gap-2 py-1">
                      <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-cyan-700" />
                      <span>{point}</span>
                    </div>
                  ))}
                </div>

                {error ? (
                  <p className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                  </p>
                ) : null}
                {message ? (
                  <p className="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
                    {message}
                  </p>
                ) : null}

                <label className="flex items-start gap-3 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700">
                  <input
                    className="mt-1 h-4 w-4 rounded border-slate-300 text-cyan-700 focus:ring-cyan-600"
                    required
                    type="checkbox"
                  />
                  <span>
                    I agree to the Terms of Service and Privacy Policy.
                  </span>
                </label>

                <button
                  className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-cyan-700 px-6 py-4 text-sm font-semibold text-white shadow-lg shadow-cyan-700/20 transition hover:bg-cyan-800 disabled:cursor-not-allowed disabled:opacity-70"
                  disabled={loading}
                  type="submit"
                >
                  {loading ? "Creating account..." : "Register"}
                  <ArrowRight className="h-4 w-4" />
                </button>
              </form>

              <div className="mt-8 border-t border-slate-200 pt-6 text-center text-sm text-slate-600">
                Already have an account?{" "}
                <Link
                  className="font-semibold text-cyan-700 hover:underline"
                  to="/login"
                >
                  Login here
                </Link>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}

function Field({ label, icon: Icon, ...props }) {
  return (
    <label className="space-y-2">
      <span className="block text-sm font-semibold text-slate-700">
        {label}
      </span>
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

export default Register;
