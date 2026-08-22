import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  ArrowRight,
  BarChart3,
  CheckCircle2,
  ChevronRight,
  IndianRupee,
  Landmark,
  Menu,
  ReceiptText,
  ShieldCheck,
  Sparkles,
  Wallet,
} from "lucide-react";
import { getStoredUser, isAuthenticated } from "../lib/api/client";

const featureCards = [
  {
    title: "India billing for recurring revenue",
    description:
      "Designed for SaaS products, agencies, and service brands that bill customers in INR with a clean, local-first experience.",
    icon: IndianRupee,
    span: "md:col-span-2",
    accent: "from-cyan-500/15 to-white",
  }
];

const benefits = [
  {
    title: "UPI and cards",
    description:
      "Accept UPI, cards, and recurring payments with a local checkout flow customers recognize.",
    icon: Landmark,
  },
  {
    title: "GST invoicing",
    description:
      "Generate invoice-ready billing records that fit Indian compliance and finance workflows.",
    icon: ReceiptText,
  },
  {
    title: "Revenue analytics",
    description:
      "Track MRR, churn, collections, and renewals with a dashboard tuned for Indian growth teams.",
    icon: BarChart3,
  },
];

const steps = [
  {
    number: "01",
    title: "Register",
    description:
      "Create your company account and verify the basic business profile.",
  },
  {
    number: "02",
    title: "Create Organization",
    description:
      "Set up your brand, billing settings, and local payment preferences.",
  },
  {
    number: "03",
    title: "Create Plans",
    description: "Define monthly or annual pricing in INR with trial options.",
  },
  {
    number: "04",
    title: "Share Links",
    description:
      "Launch hosted checkout links and start collecting revenue across India.",
  },
];

const gateways = ["Razorpay"];

function SectionLabel({ children }) {
  return (
    <span className="inline-flex items-center gap-2 rounded-full border border-cyan-200/70 bg-cyan-50 px-3 py-1 text-xs font-semibold uppercase tracking-[0.24em] text-cyan-700">
      <Sparkles className="h-3.5 w-3.5" />
      {children}
    </span>
  );
}

function Home() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    const syncAuthState = () => {
      const authenticated = isAuthenticated();
      setIsLoggedIn(authenticated);
      setUser(authenticated ? getStoredUser() : null);
    };

    syncAuthState();
    window.addEventListener("storage", syncAuthState);

    return () => window.removeEventListener("storage", syncAuthState);
  }, []);

  const displayName = [user?.firstName, user?.lastName]
    .filter(Boolean)
    .join(" ")
    .trim();
  const profileLabel = displayName || user?.email || "User";
  const profileInitials =
    profileLabel
      .split(" ")
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase())
      .join("") || "U";
  const dashboardPath =
    user?.role === "SUPER_ADMIN"
      ? "/dashboard/super-admin"
      : "/dashboard/organizations";

  return (
    <div className="min-h-screen bg-[#f8f9ff] text-slate-900">
      <header className="fixed inset-x-0 top-0 z-50 border-b border-slate-200/80 bg-white/80 backdrop-blur-xl">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-cyan-600 text-white shadow-lg shadow-cyan-600/20">
              <Wallet className="h-5 w-5" />
            </div>
            <div>
              <p className="text-base font-extrabold tracking-tight text-cyan-700">
                Subscriptor
              </p>
              <p className="text-xs text-slate-500">
                India subscription billing
              </p>
            </div>
          </div>

          <div className="hidden items-center gap-3 sm:flex">
            {isLoggedIn ? (
              <Link
                className="inline-flex items-center gap-2 rounded-full border border-slate-200 bg-white px-3 py-2 text-sm font-semibold text-slate-700 shadow-sm transition hover:border-cyan-200 hover:bg-cyan-50"
                to={dashboardPath}
              >
                <span className="flex h-8 w-8 items-center justify-center rounded-full bg-cyan-600 text-sm font-bold text-white">
                  {profileInitials}
                </span>
                <span className="max-w-[10rem] truncate">{profileLabel}</span>
              </Link>
            ) : (
              <>
                <Link
                  className="rounded-full px-4 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-slate-900"
                  to="/login"
                >
                  Login
                </Link>
                <Link
                  className="rounded-full bg-slate-900 px-5 py-2.5 text-sm font-semibold text-white shadow-lg shadow-slate-900/20 transition hover:translate-y-[-1px] hover:bg-slate-800"
                  to="/register"
                >
                  Register
                </Link>
              </>
            )}
          </div>

          <div className="flex items-center gap-2 sm:hidden">
            {isLoggedIn ? (
              <Link
                className="inline-flex h-10 w-10 items-center justify-center rounded-full bg-cyan-600 text-sm font-bold text-white shadow-sm"
                to={dashboardPath}
                aria-label="Open profile"
              >
                {profileInitials}
              </Link>
            ) : null}
            <button className="inline-flex h-10 w-10 items-center justify-center rounded-full border border-slate-200 bg-white text-slate-700 shadow-sm transition hover:bg-slate-50">
              <Menu className="h-5 w-5" />
            </button>
          </div>
        </div>
      </header>

      <main className="pt-16">
        <section className="relative overflow-hidden px-4 py-16 sm:px-6 lg:px-8 lg:py-24">
          <div className="absolute inset-x-0 top-0 -z-10 h-80 bg-gradient-to-b from-cyan-100/70 via-transparent to-transparent" />
          <div className="mx-auto grid max-w-7xl items-center gap-14 lg:grid-cols-2">
            <div className="space-y-8">
              <SectionLabel>Built for India</SectionLabel>
              <div className="space-y-5">
                <h1 className="max-w-2xl text-4xl font-black tracking-tight text-slate-950 sm:text-5xl lg:text-6xl">
                  Billing for <span className="text-cyan-700">India</span>,
                  built for modern subscription teams
                </h1>
                <p className="max-w-xl text-base leading-8 text-slate-600 sm:text-lg">
                  Run recurring revenue in INR, support UPI and cards, and keep
                  invoices, renewals, and collections under one roof.
                </p>
              </div>

              <div className="flex flex-col gap-4 sm:flex-row">
                <Link
                  className="inline-flex items-center justify-center gap-2 rounded-2xl bg-cyan-700 px-7 py-4 text-sm font-semibold text-white shadow-xl shadow-cyan-700/20 transition hover:-translate-y-0.5 hover:bg-cyan-800"
                  to="/register"
                >
                  Start Here
                  <ArrowRight className="h-4 w-4" />
                </Link>
                <Link
                  className="rounded-2xl border border-slate-200 bg-white px-7 py-4 text-sm font-semibold text-cyan-700 transition hover:border-cyan-200 hover:bg-cyan-50"
                  to="/login"
                >
                  Login
                </Link>
              </div>
            </div>

            <div className="relative hidden lg:block">
              <div className="absolute -left-12 -top-12 h-56 w-56 rounded-full bg-cyan-300/20 blur-3xl" />
              <div className="absolute -bottom-8 -right-8 h-56 w-56 rounded-full bg-sky-300/20 blur-3xl" />
              <div className="relative rounded-[2rem] border border-white/60 bg-white/80 p-5 shadow-2xl shadow-slate-300/30 backdrop-blur-xl">
                <div className="rounded-[1.5rem] border border-slate-200 bg-slate-950 p-5 text-white">
                  <div className="mb-6 flex items-center justify-between">
                    <div>
                      <p className="text-xs uppercase tracking-[0.3em] text-cyan-300">
                        India revenue dashboard
                      </p>
                      <h2 className="mt-2 text-2xl font-bold">
                        Monthly recurring revenue
                      </h2>
                    </div>
                    <div className="rounded-2xl bg-white/10 px-4 py-2 text-right">
                      <p className="text-xs text-slate-300">Growth</p>
                      <p className="text-lg font-bold text-emerald-300">
                        +24.8%
                      </p>
                    </div>
                  </div>

                  <div className="grid gap-4 sm:grid-cols-2">
                    <div className="rounded-2xl bg-white/8 p-4 ring-1 ring-white/10">
                      <p className="text-sm text-slate-300">Active plans</p>
                      <p className="mt-2 text-3xl font-black">18.4k</p>
                      <div className="mt-4 h-2 rounded-full bg-white/10">
                        <div className="h-2 w-3/4 rounded-full bg-cyan-400" />
                      </div>
                    </div>
                    <div className="rounded-2xl bg-white/8 p-4 ring-1 ring-white/10">
                      <p className="text-sm text-slate-300">Collections</p>
                      <p className="mt-2 text-3xl font-black">96.1%</p>
                      <div className="mt-4 h-2 rounded-full bg-white/10">
                        <div className="h-2 w-[96%] rounded-full bg-emerald-400" />
                      </div>
                    </div>
                  </div>

                  <div className="mt-5 rounded-[1.5rem] bg-gradient-to-br from-cyan-500 to-sky-700 p-5">
                    <div className="flex items-center justify-between text-sm text-cyan-50/90">
                      <span>Live checkout</span>
                      <span>Secure</span>
                    </div>
                    <div className="mt-5 flex items-end justify-between gap-4">
                      <div>
                        <p className="text-xs uppercase tracking-[0.25em] text-cyan-100/80">
                          Subscription
                        </p>
                        <p className="mt-2 text-2xl font-bold">Pro Plan</p>
                        <p className="mt-1 text-cyan-50/90">
                          UPI, cards, and local invoices in one flow.
                        </p>
                      </div>
                      <div className="rounded-2xl bg-white/15 px-4 py-3 text-right backdrop-blur-sm">
                        <p className="text-xs text-cyan-100/80">Status</p>
                        <p className="text-base font-semibold text-white">
                          Active
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="px-4 py-20 sm:px-6 lg:px-8">
          <div className="mx-auto max-w-7xl">
            <div className="mb-14 text-center">
              <p className="text-sm font-semibold uppercase tracking-[0.3em] text-cyan-700">
                Made for Indian SaaS
              </p>
              <h2 className="mt-4 text-3xl font-black tracking-tight text-slate-950 sm:text-4xl">
                Everything you need to run recurring revenue in India
              </h2>
              <p className="mx-auto mt-4 max-w-2xl text-base leading-7 text-slate-600">
                Subscriptor removes the technical debt of building your own
                billing stack so your team can focus on product and growth.
              </p>
            </div>

            <div className="flex justify-center ">
              {featureCards.map(
                ({ title, description, icon: Icon, span, accent, inverse }) => (
                  <article
                    key={title}
                    className={`overflow-hidden rounded-[2rem] border border-slate-200 p-8 shadow-sm transition hover:-translate-y-1 hover:shadow-xl ${span ?? ""} ${
                      inverse
                        ? "bg-slate-950 text-white"
                        : `bg-gradient-to-br ${accent} from-white to-white`
                    }`}
                  >
                    <div className="flex h-full flex-col justify-between gap-8">
                      <div className="space-y-5">
                        <div
                          className={`flex h-14 w-14 items-center justify-center rounded-2xl ${inverse ? "bg-white/10 text-cyan-300" : "bg-cyan-50 text-cyan-700"}`}
                        >
                          <Icon className="h-6 w-6" />
                        </div>
                        <h3 className="max-w-xl text-2xl font-bold tracking-tight">
                          {title}
                        </h3>
                        <p
                          className={`max-w-xl text-base leading-7 ${inverse ? "text-slate-300" : "text-slate-600"}`}
                        >
                          {description}
                        </p>
                      </div>

                      <div
                        className={`flex items-center justify-between border-t pt-5 ${inverse ? "border-white/10" : "border-slate-200"}`}
                      >
                        <span
                          className={`text-sm font-semibold uppercase tracking-[0.22em] ${inverse ? "text-cyan-300" : "text-cyan-700"}`}
                        >
                          Learn more
                        </span>
                        <ChevronRight
                          className={`h-5 w-5 ${inverse ? "text-cyan-300" : "text-cyan-700"}`}
                        />
                      </div>
                    </div>
                  </article>
                ),
              )}
            </div>
          </div>
        </section>

        <section className="bg-white px-4 py-20 sm:px-6 lg:px-8">
          <div className="mx-auto grid max-w-7xl gap-10 md:grid-cols-3">
            {benefits.map(({ title, description, icon: Icon }) => (
              <article
                key={title}
                className="rounded-[2rem] border border-slate-200 bg-slate-50 p-8 text-center shadow-sm"
              >
                <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-cyan-100 text-cyan-700">
                  <Icon className="h-7 w-7" />
                </div>
                <h3 className="mt-6 text-xl font-bold text-slate-950">
                  {title}
                </h3>
                <p className="mt-3 text-sm leading-7 text-slate-600">
                  {description}
                </p>
              </article>
            ))}
          </div>
        </section>

        <section className="bg-[#eef4ff] px-4 py-20 sm:px-6 lg:px-8">
          <div className="mx-auto max-w-7xl">
            <div className="mb-12 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
              <div>
                <p className="text-sm font-semibold uppercase tracking-[0.3em] text-cyan-700">
                  How it works
                </p>
                <h2 className="mt-3 text-3xl font-black tracking-tight text-slate-950">
                  Zero to live in minutes
                </h2>
                <p className="mt-3 max-w-2xl text-base leading-7 text-slate-600">
                  Four simple steps to launch your Indian billing workflow.
                </p>
              </div>

              <div className="flex gap-2">
                <button className="inline-flex h-11 w-11 items-center justify-center rounded-full border border-slate-200 bg-white text-slate-700 transition hover:bg-slate-50">
                  <ChevronRight className="h-5 w-5 rotate-180" />
                </button>
                <button className="inline-flex h-11 w-11 items-center justify-center rounded-full border border-slate-200 bg-white text-slate-700 transition hover:bg-slate-50">
                  <ChevronRight className="h-5 w-5" />
                </button>
              </div>
            </div>

            <div className="grid gap-6 sm:grid-cols-2 xl:grid-cols-4">
              {steps.map(({ number, title, description }) => (
                <article key={title} className="group">
                  <div className="mb-[-1.5rem] text-7xl font-black tracking-tight text-slate-900/10 transition group-hover:text-cyan-700/15 sm:text-8xl">
                    {number}
                  </div>
                  <div className="rounded-[1.75rem] border border-slate-200 bg-white p-6 shadow-sm transition group-hover:-translate-y-1 group-hover:shadow-xl">
                    <h3 className="text-lg font-bold text-slate-950">
                      {title}
                    </h3>
                    <p className="mt-3 text-sm leading-7 text-slate-600">
                      {description}
                    </p>
                  </div>
                </article>
              ))}
            </div>
          </div>
        </section>

        <section className="px-4 py-20 sm:px-6 lg:px-8">
          <div className="mx-auto grid max-w-7xl gap-16 lg:grid-cols-2">
            <div className="order-2 space-y-8 lg:order-1">
              <div>
                <p className="text-sm font-semibold uppercase tracking-[0.3em] text-cyan-700">
                  Hosted checkout
                </p>
                <h2 className="mt-3 text-3xl font-black tracking-tight text-slate-950 sm:text-4xl">
                  Conversion-optimized checkout pages
                </h2>
              </div>
              <p className="max-w-xl text-base leading-7 text-slate-600">
                Use pre-built pages that support UPI, cards, and multi-currency
                billing without building your own checkout from scratch.
              </p>
              <ul className="space-y-4">
                {[
                  "Mobile-first responsive design",
                  "Secure one-click renewals",
                  "GST-ready invoice records",
                ].map((item) => (
                  <li
                    key={item}
                    className="flex items-start gap-3 text-sm font-medium text-slate-700 sm:text-base"
                  >
                    <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0 text-cyan-700" />
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="order-1 lg:order-2">
              <div className="aspect-[4/3] rounded-[2rem] border border-slate-200 bg-gradient-to-br from-slate-950 via-slate-900 to-cyan-900 p-6 text-white shadow-2xl shadow-slate-300/40">
                <div className="flex items-center justify-between text-sm text-slate-300">
                  <span>Checkout preview</span>
                  <span>Secure payment</span>
                </div>
                <div className="mt-6 rounded-[1.75rem] bg-white/10 p-6 ring-1 ring-white/10 backdrop-blur-sm">
                  <div className="flex items-center justify-between gap-4">
                    <div>
                      <p className="text-xs uppercase tracking-[0.25em] text-cyan-200/80">
                        Premium plan
                      </p>
                      <h3 className="mt-2 text-2xl font-bold">
                        Rs 4,999 / month
                      </h3>
                    </div>
                    <div className="rounded-2xl bg-emerald-400/20 px-4 py-2 text-sm font-semibold text-emerald-200">
                      UPI ready
                    </div>
                  </div>
                  <div className="mt-6 space-y-4">
                    <div className="h-12 rounded-2xl bg-white/10" />
                    <div className="h-12 rounded-2xl bg-white/10" />
                    <div className="h-12 rounded-2xl bg-cyan-400/80" />
                  </div>
                </div>

                <div className="mt-6 grid gap-4 sm:grid-cols-2">
                  <div className="rounded-[1.5rem] bg-white/10 p-4 ring-1 ring-white/10">
                    <p className="text-sm text-slate-300">Recurring payments</p>
                    <p className="mt-2 text-xl font-bold">Automatic</p>
                  </div>
                  <div className="rounded-[1.5rem] bg-white/10 p-4 ring-1 ring-white/10">
                    <p className="text-sm text-slate-300">Fraud checks</p>
                    <p className="mt-2 text-xl font-bold">Enabled</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="px-4 py-20 sm:px-6 lg:px-8">
          <div className="mx-auto grid max-w-7xl gap-16 lg:grid-cols-2">
            <div className="space-y-8">
              <div>
                <p className="text-sm font-semibold uppercase tracking-[0.3em] text-cyan-700">
                  Payment gateways
                </p>
                <h2 className="mt-3 text-3xl font-black tracking-tight text-slate-950 sm:text-4xl">
                  Razorpay gateway
                </h2>
              </div>
              <p className="max-w-xl text-base leading-7 text-slate-600">
                Manage mandates, recurring debits, and partial refunds directly
                from a dashboard built for Indian billing teams.
              </p>

              <div className="flex flex-wrap gap-3">
                {gateways.map((gateway) => (
                  <span
                    key={gateway}
                    className="rounded-full border border-slate-200 bg-white px-4 py-2 text-sm font-semibold text-slate-700 shadow-sm"
                  >
                    {gateway.toUpperCase()}
                  </span>
                ))}
              </div>
            </div>

            <div>
              <div className="aspect-[4/3] rounded-[2rem] border border-slate-200 bg-[radial-gradient(circle_at_top_left,_rgba(34,211,238,0.22),_transparent_32%),radial-gradient(circle_at_bottom_right,_rgba(14,165,233,0.22),_transparent_28%),linear-gradient(135deg,_#ffffff,_#eef6ff)] p-6 shadow-2xl shadow-slate-300/40">
                <div className="grid h-full gap-4 sm:grid-cols-2">
                  <div className="rounded-[1.75rem] border border-cyan-100 bg-white/75 p-5 backdrop-blur-sm">
                    <p className="text-sm font-medium text-slate-500">
                      Gateway health
                    </p>
                    <p className="mt-2 text-3xl font-black text-slate-950">
                      99.98%
                    </p>
                    <div className="mt-6 h-2 rounded-full bg-slate-100">
                      <div className="h-2 w-[98%] rounded-full bg-cyan-600" />
                    </div>
                  </div>
                  <div className="rounded-[1.75rem] border border-cyan-100 bg-white/75 p-5 backdrop-blur-sm">
                    <p className="text-sm font-medium text-slate-500">
                      Transactions
                    </p>
                    <p className="mt-2 text-3xl font-black text-slate-950">
                      1.2M
                    </p>
                    <div className="mt-6 h-2 rounded-full bg-slate-100">
                      <div className="h-2 w-4/5 rounded-full bg-emerald-500" />
                    </div>
                  </div>
                  <div className="sm:col-span-2 rounded-[1.75rem] border border-cyan-100 bg-slate-950 p-5 text-white shadow-lg">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-xs uppercase tracking-[0.25em] text-cyan-300">
                          Live infrastructure
                        </p>
                        <h3 className="mt-2 text-xl font-bold">
                          Settlement stream
                        </h3>
                      </div>
                      <ShieldCheck className="h-6 w-6 text-emerald-300" />
                    </div>
                    <div className="mt-5 grid gap-3 sm:grid-cols-3">
                      {[
                        ["Auth", "97.4%"],
                        ["Capture", "96.9%"],
                        ["Payouts", "99.2%"],
                      ].map(([label, value]) => (
                        <div
                          key={label}
                          className="rounded-2xl bg-white/8 p-4 ring-1 ring-white/10"
                        >
                          <p className="text-sm text-slate-300">{label}</p>
                          <p className="mt-2 text-2xl font-bold text-white">
                            {value}
                          </p>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="px-4 py-20 sm:px-6 lg:px-8">
          <div className="mx-auto max-w-5xl rounded-[2.5rem] bg-slate-950 px-6 py-14 text-center text-white shadow-2xl shadow-slate-300/30 sm:px-10 sm:py-16">
            <h2 className="text-3xl font-black tracking-tight sm:text-4xl">
              Ready to automate your revenue?
            </h2>
            <p className="mx-auto mt-4 max-w-2xl text-base leading-7 text-slate-300">
              Join Indian teams scaling faster with Subscriptor&apos;s automated
              billing infrastructure.
            </p>
            <div className="mt-8 flex flex-col justify-center gap-4 sm:flex-row">
              <Link
                className="rounded-2xl bg-cyan-500 px-7 py-4 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400"
                to="/register"
              >
                Start Free Trial
              </Link>
            </div>
          </div>
        </section>
      </main>

      <footer className="border-t border-slate-200 bg-white px-4 py-12 sm:px-6 lg:px-8">
        <div className="mx-auto grid max-w-7xl gap-10 lg:grid-cols-[1.2fr_1fr]">
          <div className="space-y-4">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-cyan-600 text-white shadow-lg shadow-cyan-600/20">
                <Wallet className="h-5 w-5" />
              </div>
              <span className="text-lg font-extrabold tracking-tight text-slate-950">
                Subscriptor
              </span>
            </div>
            <p className="max-w-sm text-sm leading-7 text-slate-600">
              The reliable backbone for Indian recurring businesses. Built for
              scale, designed for simplicity.
            </p>
            <p className="text-sm text-slate-500">
              © 2026 Subscriptor Billing Systems. All rights reserved.
            </p>
          </div>

          <div className="grid grid-cols-2 gap-8 sm:grid-cols-3">
            {[
              ["Resources", ["Help Center", "Contact Sales", "Documentation"]],
              ["Legal", ["Privacy Policy", "Terms of Service"]],
            ].map(([heading, items]) => (
              <div key={heading} className="space-y-4">
                <p className="text-xs font-semibold uppercase tracking-[0.25em] text-slate-500">
                  {heading}
                </p>
                <ul className="space-y-2 text-sm text-slate-600">
                  {items.map((item) => (
                    <li key={item}>
                      <a className="transition hover:text-cyan-700" href="#">
                        {item}
                      </a>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        </div>
      </footer>
    </div>
  );
}

export default Home;
