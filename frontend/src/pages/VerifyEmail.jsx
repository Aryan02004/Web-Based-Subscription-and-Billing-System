import { useEffect, useRef, useState } from "react";
import { Link, Navigate, useLocation, useNavigate } from "react-router-dom";
import {
  ArrowRight,
  Mail,
  ShieldCheck,
  Sparkles,
  CheckCircle2,
  RefreshCcw,
} from "lucide-react";
import { api } from "../lib/api";

const EMAIL_VERIFICATION_PURPOSE = "EMAIL_VERIFICATION";

const securityPoints = [
  "Your email protects your organization account",
  "OTP expires after 5 minutes",
  "Only verified users can access the platform",
];

function VerifyEmail() {
  const navigate = useNavigate();
  const location = useLocation();

  const email = location.state?.email || "";
  const userId = location.state?.userId || null;

  const [otp, setOtp] = useState(["", "", "", "", "", ""]);
  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);

  const [error, setError] = useState("");
  const [message, setMessage] = useState(
    location.state?.message || ""
  );

  const [timer, setTimer] = useState(60);

  const inputRefs = useRef([]);

  useEffect(() => {
    if (timer <= 0) return undefined;

    const interval = setInterval(() => {
      setTimer((previous) => previous - 1);
    }, 1000);

    return () => clearInterval(interval);
  }, [timer]);

  if (!email || !userId) {
    return <Navigate to="/register" replace />;
  }

  const formatTime = () => {
    const minutes = String(Math.floor(timer / 60)).padStart(2, "0");
    const seconds = String(timer % 60).padStart(2, "0");

    return `${minutes}:${seconds}`;
  };

  const handleChange = (index, value) => {
    if (!/^\d*$/.test(value)) return;

    const updatedOtp = [...otp];
    updatedOtp[index] = value.slice(-1);

    setOtp(updatedOtp);

    if (value && index < 5) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index, event) => {
    if (
      event.key === "Backspace" &&
      otp[index] === "" &&
      index > 0
    ) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handlePaste = (event) => {
    event.preventDefault();

    const pastedData = event.clipboardData
      .getData("text")
      .replace(/\D/g, "")
      .slice(0, 6);

    if (!pastedData) return;

    const updatedOtp = [...otp];

    pastedData.split("").forEach((digit, index) => {
      updatedOtp[index] = digit;
    });

    setOtp(updatedOtp);

    const lastIndex = Math.min(
      pastedData.length - 1,
      5
    );

    inputRefs.current[lastIndex]?.focus();
  };

  const handleVerify = async (event) => {
    event.preventDefault();

    setError("");
    setMessage("");

    const verificationCode = otp.join("");

    if (verificationCode.length !== 6) {
      setError("Please enter the complete 6-digit OTP.");
      return;
    }

    setLoading(true);

    try {
      const response = await api.otp.verify({
        userId,
        otp: verificationCode,
        purpose: EMAIL_VERIFICATION_PURPOSE,
      });

      setMessage(
        response.message ||
          "Email verified successfully."
      );

      setTimeout(() => {
        navigate("/login", {
          state: {
            message:
              "Email verified successfully. Please login.",
          },
        });
      }, 2000);
    } catch (requestError) {
      setError(
        requestError.message ||
          "Invalid verification code."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    if (timer > 0) return;

    setResending(true);
    setError("");
    setMessage("");

    try {
      const response = await api.otp.resend({
        userId,
        purpose: EMAIL_VERIFICATION_PURPOSE,
      });

      setMessage(
        response.message ||
          "OTP sent successfully."
      );

      setTimer(60);

      setOtp(["", "", "", "", "", ""]);

      inputRefs.current[0]?.focus();
    } catch (requestError) {
      setError(
        requestError.message ||
          "Unable to resend OTP."
      );
    } finally {
      setResending(false);
    }
  };

    return (
    <div className="min-h-screen bg-[#f8f9ff] text-slate-900">
      <div className="grid min-h-screen lg:grid-cols-2">

        {/* ================= LEFT PANEL ================= */}

        <section className="relative hidden overflow-hidden bg-[linear-gradient(160deg,#0f766e_0%,#0f172a_70%)] p-12 text-white lg:flex lg:flex-col lg:justify-between">

          <div className="absolute -left-16 top-10 h-72 w-72 rounded-full bg-cyan-400/20 blur-3xl" />
          <div className="absolute bottom-0 right-0 h-80 w-80 rounded-full bg-sky-400/10 blur-3xl" />

          <div className="relative z-10 max-w-xl space-y-6">

            <div className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-4 py-2 text-sm font-semibold">
              <Sparkles className="h-4 w-4 text-cyan-300" />
              Secure Email Verification
            </div>

            <h1 className="text-5xl font-black tracking-tight">
              Verify your email
            </h1>

            <p className="text-base leading-8 text-slate-200">
              Activate your account securely before accessing your
              Subscription & Billing Management dashboard.
            </p>

          </div>

          <div className="relative z-10 grid gap-4 sm:grid-cols-2">

            <div className="rounded-3xl border border-white/10 bg-white/10 p-5 backdrop-blur-xl">
              <p className="text-sm text-cyan-100">
                One-Time Verification
              </p>

              <p className="mt-2 text-lg font-bold">
                Secure 6-digit OTP
              </p>
            </div>

            <div className="rounded-3xl border border-white/10 bg-white/10 p-5 backdrop-blur-xl">
              <p className="text-sm text-cyan-100">
                Email Protection
              </p>

              <p className="mt-2 text-lg font-bold">
                Prevent fake registrations
              </p>
            </div>

            <div className="sm:col-span-2 rounded-3xl border border-white/10 bg-white/10 p-5 backdrop-blur-xl">

              <div className="flex items-center gap-4">

                <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-cyan-400 text-slate-950">
                  <ShieldCheck className="h-5 w-5" />
                </div>

                <div>
                  <p className="text-sm text-cyan-100">
                    Enterprise Security
                  </p>

                  <p className="text-lg font-bold">
                    Your account stays protected from unauthorized access.
                  </p>
                </div>

              </div>

            </div>

          </div>

        </section>

        {/* ================= RIGHT PANEL ================= */}

        <section className="flex items-center justify-center px-4 py-10 sm:px-6 lg:px-10">

          <div className="w-full max-w-140">

            <div className="rounded-4xl border border-slate-200 bg-white p-6 shadow-2xl shadow-slate-200/50 sm:p-8">

              <div className="mb-8 text-center">

                <div className="mx-auto mb-5 flex h-16 w-16 items-center justify-center rounded-full bg-cyan-100 text-cyan-700">
                  <Mail className="h-8 w-8" />
                </div>

                <p className="text-sm font-semibold uppercase tracking-[0.28em] text-cyan-700">
                  Email Verification
                </p>

                <h2 className="mt-2 text-3xl font-black tracking-tight">
                  Verify your account
                </h2>

                <p className="mt-4 text-sm leading-7 text-slate-600">
                  We've sent a verification code to
                </p>

                <p className="mt-2 text-lg font-bold text-cyan-700">
                  {email}
                </p>

              </div>

              <form
                className="space-y-6"
                onSubmit={handleVerify}
              >

                {/* OTP BOXES */}

                <div
                  className="flex justify-center gap-3"
                  onPaste={handlePaste}
                >

                  {otp.map((digit, index) => (

                    <input
                      key={index}
                      ref={(element) =>
                        (inputRefs.current[index] = element)
                      }
                      type="text"
                      inputMode="numeric"
                      maxLength={1}
                      value={digit}
                      onChange={(event) =>
                        handleChange(index, event.target.value)
                      }
                      onKeyDown={(event) =>
                        handleKeyDown(index, event)
                      }
                      className="h-14 w-14 rounded-2xl border border-slate-300 bg-slate-50 text-center text-xl font-bold outline-none transition focus:border-cyan-600 focus:bg-white focus:ring-4 focus:ring-cyan-100"
                    />

                  ))}

                </div>

                {/* SECURITY POINTS */}

                <div className="rounded-2xl border border-cyan-100 bg-cyan-50/70 p-4 text-sm text-slate-700">

                  {securityPoints.map((point) => (

                    <div
                      key={point}
                      className="flex items-start gap-2 py-1"
                    >
                      <CheckCircle2 className="mt-0.5 h-4 w-4 text-cyan-700" />
                      <span>{point}</span>
                    </div>

                  ))}

                </div>

                {/* ERROR */}

                {error && (
                  <p className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    {error}
                  </p>
                )}

                {/* SUCCESS */}

                {message && (
                  <p className="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
                    {message}
                  </p>
                )}

                {/* VERIFY BUTTON */}

                <button
                  type="submit"
                  disabled={loading}
                  className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-cyan-700 px-6 py-4 text-sm font-semibold text-white shadow-lg shadow-cyan-700/20 transition hover:bg-cyan-800 disabled:cursor-not-allowed disabled:opacity-70"
                >
                  {loading
                    ? "Verifying..."
                    : "Verify Email"}

                  <ArrowRight className="h-4 w-4" />
                </button>

              </form>

              {/* RESEND */}

              <div className="mt-8 rounded-2xl border border-slate-200 bg-slate-50 p-5">

                <div className="text-center">

                  <p className="text-sm text-slate-600">
                    Didn't receive the code?
                  </p>

                  {timer > 0 ? (

                    <p className="mt-3 text-lg font-bold text-cyan-700">
                      Resend in {formatTime()}
                    </p>

                  ) : (

                    <button
                      onClick={handleResend}
                      disabled={resending}
                      className="mt-4 inline-flex items-center gap-2 rounded-xl bg-cyan-700 px-5 py-3 text-sm font-semibold text-white transition hover:bg-cyan-800"
                    >
                      <RefreshCcw className="h-4 w-4" />

                      {resending
                        ? "Sending..."
                        : "Resend OTP"}
                    </button>

                  )}

                </div>

              </div>

              <div className="mt-8 border-t border-slate-200 pt-6 text-center">

                <Link
                  to="/register"
                  className="text-sm font-semibold text-cyan-700 hover:underline"
                >
                  ← Change Email Address
                </Link>

              </div>

            </div>

          </div>

        </section>

      </div>
    </div>
  );
}

export default VerifyEmail;