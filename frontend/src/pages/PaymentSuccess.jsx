import { Link, useLocation } from 'react-router-dom'

export default function PaymentSuccess() {
  const location = useLocation()
  const { organizationName = 'Organization', amount = 0, plan = '', invoiceId, customerEmail } = location.state || {}

  return (
    <main className="min-h-screen bg-slate-50 py-16">
      <div className="mx-auto max-w-3xl rounded-[2rem] bg-white px-8 py-12 shadow-[0_40px_120px_rgba(15,23,42,0.08)]">
        <div className="mb-8 rounded-[1.5rem] border border-cyan-100 bg-cyan-50 p-6 text-cyan-900 shadow-sm">
          <h1 className="text-3xl font-semibold">Payment successful</h1>
          <p className="mt-3 text-sm leading-7">
            Your subscription is now active for <strong>{organizationName}</strong>. An invoice has been sent to <strong>{customerEmail || 'your email'}</strong>.
          </p>
        </div>

        <div className="space-y-6">
          <div className="rounded-[1.75rem] border border-slate-200 bg-slate-50 p-6">
            <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Plan purchased</p>
            <h2 className="mt-3 text-2xl font-semibold text-slate-900">{plan || 'Subscription plan'}</h2>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div className="rounded-[1.75rem] border border-slate-200 bg-white p-6">
              <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Amount paid</p>
              <p className="mt-3 text-3xl font-semibold text-slate-950">{new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount)}</p>
            </div>
            <div className="rounded-[1.75rem] border border-slate-200 bg-white p-6">
              <p className="text-sm uppercase tracking-[0.24em] text-slate-500">Invoice</p>
              <p className="mt-3 text-lg font-semibold text-slate-900">{invoiceId ? `#${invoiceId}` : 'Pending'}</p>
            </div>
          </div>

          <div className="rounded-[1.75rem] border border-slate-200 bg-white p-6">
            <p className="text-sm uppercase tracking-[0.24em] text-slate-500">What happens next</p>
            <ul className="mt-3 list-disc space-y-2 pl-5 text-slate-600">
              <li>Invoice delivered to the customer email.</li>
              <li>Subscription is active and ready to use.</li>
              <li>Contact support if you need assistance.</li>
            </ul>
          </div>

          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <Link
              to="/"
              className="inline-flex items-center justify-center rounded-3xl bg-cyan-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-cyan-700"
            >
              Back to home
            </Link>
            <Link
              to={-1}
              className="inline-flex items-center justify-center rounded-3xl border border-slate-300 bg-white px-5 py-3 text-sm font-semibold text-slate-900 transition hover:bg-slate-100"
            >
              Return to checkout
            </Link>
          </div>
        </div>
      </div>
    </main>
  )
}
