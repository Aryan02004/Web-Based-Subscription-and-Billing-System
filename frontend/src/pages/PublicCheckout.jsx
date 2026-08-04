import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../lib/api/services'

const placeholderLogoColors = [
  'from-sky-500 to-cyan-500',
  'from-indigo-500 to-violet-500',
  'from-emerald-500 to-teal-500',
  'from-orange-500 to-amber-500',
]

function formatCurrency(value) {
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(Number(value) || 0)
}

function getInitials(name) {
  return (name || '')
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0].toUpperCase())
    .join('')
}

function normalizeFeatures(features) {
  if (!features || typeof features !== 'object') return []
  return Object.entries(features).map(([key, value]) => ({
    label: key,
    value: value === null || value === undefined ? 'Included' : String(value),
  }))
}

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export default function PublicCheckout() {
  const { token } = useParams()
  const [loading, setLoading] = useState(Boolean(token))
  const [organization, setOrganization] = useState(null)
  const [plans, setPlans] = useState([])
  const [selectedPlan, setSelectedPlan] = useState(null)
  const [customer, setCustomer] = useState({ firstName: '', lastName: '', email: '', phone: '' })
  const [checkoutLoading, setCheckoutLoading] = useState(false)
  const [pageError, setPageError] = useState(token ? '' : 'Missing checkout token.')
  const [checkoutError, setCheckoutError] = useState('')
  const [successMessage, setSuccessMessage] = useState('')

  useEffect(() => {
    async function loadCheckoutPage() {
      setLoading(true)
      setPageError('')
      setCheckoutError('')
      setSuccessMessage('')
      setSelectedPlan(null)

      try {
        const res = await api.public.getOrganizationPlans(token)
        if (!res || !res.organization || !Array.isArray(res.plans)) {
          throw new Error('Invalid checkout information.')
        }

        setOrganization(res.organization)
        setPlans(res.plans)
      } catch (error) {
        setPageError(error?.message || 'Unable to load checkout details. Please verify the link.')
      } finally {
        setLoading(false)
      }
    }

    if (token) {
      void loadCheckoutPage()
    }
  }, [token])

  const cardPlans = useMemo(
    () =>
      plans.map((plan) => ({
        ...plan,
        amount: plan.price || plan.amount || 0,
        billingCycle: plan.billingCycle || plan.interval || 'MONTHLY',
        featuresList: normalizeFeatures(plan.features),
        isPopular:
          plan.features?.mostPopular === true ||
          plan.features?.popular === true ||
          plan.features?.badge === 'Most Popular' ||
          plan.mostPopular === true,
      })),
    [plans],
  )

  const navigate = useNavigate()

  const startCheckout = async () => {
    setCheckoutError('')
    setSuccessMessage('')

    if (!selectedPlan) {
      setCheckoutError('Select a plan to continue.')
      return
    }

    const firstName = customer.firstName.trim()
    const lastName = customer.lastName.trim()
    const email = customer.email.trim()
    const phone = customer.phone.trim()

    if (!firstName) {
      setCheckoutError('Please enter your first name before payment.')
      return
    }

    if (!email) {
      setCheckoutError('Please enter your email address before payment.')
      return
    }

    if (!emailRegex.test(email)) {
      setCheckoutError('Please enter a valid email address before payment.')
      return
    }

    if (!lastName || !phone) {
      setCheckoutError('Please fill in all customer fields before payment.')
      return
    }

    setCheckoutLoading(true)

    try {
      if (!window.Razorpay) {
        throw new Error('Razorpay checkout script not loaded. Refresh the page and try again.')
      }

      const payload = {
        planId: selectedPlan.id,
        firstName,
        lastName,
        email,
        phone,
      }

      const data = await api.public.checkout(token, payload)
      const order = typeof data.order === 'string' ? JSON.parse(data.order) : data.order
      const fullName = `${customer.firstName} ${customer.lastName}`

      const options = {
        key: data.razorpayKey,
        amount: order.amount,
        currency: order.currency,
        name: organization?.name || 'Organization Checkout',
        description: selectedPlan.planName || selectedPlan.name || 'Subscription plan',
        order_id: order.id,
        prefill: {
          name: fullName,
          email: customer.email,
          contact: customer.phone,
        },
        notes: {
          organization: organization?.name || 'Organization checkout',
          plan: selectedPlan.planName || selectedPlan.name || 'Subscription',
        },
        theme: {
          color: '#0f766e',
        },
        handler: async function (response) {
          try {
            await api.public.verifyPayment(token, data.paymentId, {
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
            })

            navigate('/public/success', {
              state: {
                organizationName: organization?.name,
                amount: order.amount / 100,
                plan: selectedPlan.planName || selectedPlan.name || 'Subscription plan',
                invoiceId: data.invoiceId,
                customerEmail: customer.email,
              },
            })
          } catch (verificationError) {
            setCheckoutError(verificationError?.message || 'Payment verification failed.')
          } finally {
            setCheckoutLoading(false)
          }
        },
        modal: {
          ondismiss: function () {
            setCheckoutLoading(false)
          },
        },
      }

      const rzp = new window.Razorpay(options)
      rzp.open()
    } catch (error) {
      setCheckoutError(error?.message || 'Checkout could not be initiated.')
      setCheckoutLoading(false)
    }
  }

  const logoColor = placeholderLogoColors[
    organization?.name ? organization.name.length % placeholderLogoColors.length : 0
  ]

  return (
    <main className="min-h-screen bg-slate-50 py-10">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="mb-10 rounded-[2rem] bg-white p-8 shadow-[0_40px_120px_rgba(15,23,42,0.08)]">
          <div className="flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
            <div className="space-y-4">
              <div className="flex items-center gap-4">
                <div className={`flex h-16 w-16 items-center justify-center rounded-3xl bg-gradient-to-br ${logoColor} text-2xl font-semibold text-white shadow-lg shadow-slate-200/40`}>
                  {getInitials(organization?.name || 'OR')}
                </div>
                <div>
                  <p className="text-sm font-semibold uppercase tracking-[0.3em] text-slate-400">Organization checkout</p>
                  <h1 className="mt-2 text-3xl font-semibold tracking-tight text-slate-950">
                    {organization?.name || 'Organization Name'}
                  </h1>
                </div>
              </div>
              <p className="max-w-2xl text-base leading-8 text-slate-600">
                Choose the best subscription plan for your organization and complete checkout securely.
              </p>
            </div>

            <div className="rounded-3xl border border-slate-200 bg-slate-50 px-5 py-4 text-sm text-slate-600 shadow-sm">
              <p className="font-semibold text-slate-900">Secure organization token</p>
              <p className="mt-2 break-all text-[0.95rem] text-slate-600">{token || '—'}</p>
            </div>
          </div>
        </div>

        {loading ? (
          <div className="grid gap-6 lg:grid-cols-[1.4fr_0.9fr]">
            <div className="space-y-6">
              <div className="h-48 animate-pulse rounded-[2rem] bg-slate-200" />
              <div className="grid gap-4 sm:grid-cols-2">
                <div className="h-40 animate-pulse rounded-[2rem] bg-slate-200" />
                <div className="h-40 animate-pulse rounded-[2rem] bg-slate-200" />
              </div>
            </div>
            <div className="space-y-4">
              <div className="h-72 animate-pulse rounded-[2rem] bg-slate-200" />
              <div className="h-40 animate-pulse rounded-[2rem] bg-slate-200" />
            </div>
          </div>
        ) : pageError ? (
          <div className="rounded-[2rem] border border-red-200 bg-red-50 p-8 text-red-700 shadow-sm">
            <h2 className="text-xl font-semibold">Link invalid or expired</h2>
            <p className="mt-3 text-sm leading-7 text-red-700/90">{pageError}</p>
          </div>
        ) : (
          <div className="grid gap-6 lg:grid-cols-[1.5fr_0.85fr]">
            <section className="space-y-6">
              <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm">
                <div className="mb-5 flex items-center justify-between gap-4">
                  <div>
                    <p className="text-sm font-semibold uppercase tracking-[0.24em] text-slate-400">Subscription plans</p>
                    <p className="mt-2 text-base text-slate-600">All active plans available for {organization.name}.</p>
                  </div>
                  <span className="rounded-full bg-slate-100 px-3 py-2 text-xs font-semibold uppercase tracking-[0.24em] text-slate-600">
                    {cardPlans.length} plans
                  </span>
                </div>

                <div className="grid gap-4 xl:grid-cols-2">
                  {cardPlans.map((plan) => {
                    const isSelected = selectedPlan?.id === plan.id
                    return (
                      <button
                        key={plan.id}
                        type="button"
                        onClick={() => setSelectedPlan(plan)}
                        className={`group rounded-[1.75rem] border p-6 text-left transition hover:-translate-y-1 hover:border-slate-300 ${isSelected ? 'border-cyan-500 shadow-[0_16px_48px_rgba(15,118,110,0.15)]' : 'border-slate-200 bg-slate-50'}`}
                      >
                        <div className="flex items-start justify-between gap-4">
                          <div>
                            <p className="text-xs font-semibold uppercase tracking-[0.28em] text-slate-500">{plan.planName}</p>
                            <h2 className="mt-3 text-2xl font-semibold text-slate-950">{formatCurrency(plan.amount)}</h2>
                            <p className="mt-2 text-sm text-slate-500">Billed {plan.billingCycle.toLowerCase()}</p>
                          </div>
                          {plan.isPopular ? (
                            <span className="rounded-full bg-cyan-500 px-3 py-1 text-xs font-semibold uppercase tracking-[0.24em] text-white">
                              Most Popular
                            </span>
                          ) : null}
                        </div>

                        <p className="mt-5 text-sm leading-7 text-slate-600">{plan.description || 'A flexible plan for your organization.'}</p>

                        <div className="mt-6 grid gap-3 sm:grid-cols-2">
                          <div className="rounded-3xl bg-slate-50 p-4">
                            <p className="text-xs uppercase tracking-[0.24em] text-slate-400">Users</p>
                            <p className="mt-2 text-lg font-semibold text-slate-900">{plan.maxUsers ?? 'Unlimited'}</p>
                          </div>
                          <div className="rounded-3xl bg-slate-50 p-4">
                            <p className="text-xs uppercase tracking-[0.24em] text-slate-400">Storage</p>
                            <p className="mt-2 text-lg font-semibold text-slate-900">{plan.storageLimitGb != null ? `${plan.storageLimitGb} GB` : 'Unlimited'}</p>
                          </div>
                        </div>

                        {plan.featuresList.length > 0 ? (
                          <div className="mt-6 space-y-3">
                            {plan.featuresList.slice(0, 5).map((feature) => (
                              <div key={feature.label} className="flex items-center gap-3 text-sm text-slate-600">
                                <span className="inline-flex h-5 w-5 items-center justify-center rounded-full bg-slate-100 text-slate-700">✓</span>
                                <span>{feature.label.replace(/([A-Z])/g, ' $1').replace(/_/g, ' ')}: {feature.value}</span>
                              </div>
                            ))}
                          </div>
                        ) : null}

                        <div className="mt-6 flex items-center justify-between gap-3 text-sm">
                          <span className={`rounded-full px-3 py-1 font-semibold ${isSelected ? 'bg-cyan-500 text-white' : 'bg-slate-100 text-slate-700'}`}>
                            {isSelected ? 'Selected' : 'Select plan'}
                          </span>
                          <span className={`rounded-full px-3 py-1 text-xs ${plan.active === false ? 'bg-red-100 text-red-700' : 'bg-emerald-100 text-emerald-700'}`}>
                            {plan.active === false ? 'Inactive' : 'Active'}
                          </span>
                        </div>
                      </button>
                    )
                  })}
                </div>
              </div>
            </section>

            <aside className="space-y-6">
              <div className="rounded-[2rem] border border-slate-200 bg-gradient-to-br from-slate-950 via-slate-900 to-slate-800 p-6 shadow-xl shadow-slate-900/10 text-white">
                <p className="text-sm font-semibold uppercase tracking-[0.24em] text-slate-300">Who is this for?</p>
                <h3 className="mt-3 text-2xl font-semibold tracking-tight text-white">{organization.name}</h3>
                <p className="mt-3 text-sm leading-7 text-slate-200">{organization.industry || 'Business plan'} • Secure checkout experience</p>
                <div className="mt-6 rounded-[1.75rem] bg-slate-900/70 p-4">
                  <p className="text-xs uppercase tracking-[0.24em] text-slate-400">Checkout note</p>
                  <p className="mt-3 text-sm text-slate-200">Customer details are persisted securely and the payment is processed via Razorpay. This link works for all active plans.</p>
                </div>
              </div>

              <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm">
                <div className="mb-5 flex items-center justify-between">
                  <div>
                    <p className="text-sm font-semibold uppercase tracking-[0.24em] text-slate-400">Customer details</p>
                    <p className="mt-1 text-sm text-slate-500">Enter contact info for billing and support.</p>
                  </div>
                </div>
                <div className="space-y-4">
                  <div className="grid gap-4 sm:grid-cols-2">
                    <input
                      aria-label="First name"
                      type="text"
                      placeholder="First name"
                      required
                      value={customer.firstName}
                      onChange={(event) => setCustomer((current) => ({ ...current, firstName: event.target.value }))}
                      className="w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-cyan-500 focus:bg-white"
                    />
                    <input
                      aria-label="Last name"
                      type="text"
                      placeholder="Last name"
                      value={customer.lastName}
                      onChange={(event) => setCustomer((current) => ({ ...current, lastName: event.target.value }))}
                      className="w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-cyan-500 focus:bg-white"
                    />
                  </div>
                  <input
                    aria-label="Email"
                    type="email"
                    placeholder="Email"
                    required
                    value={customer.email}
                    onChange={(event) => setCustomer((current) => ({ ...current, email: event.target.value }))}
                    className="w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-cyan-500 focus:bg-white"
                  />
                  <input
                    aria-label="Phone"
                    type="tel"
                    placeholder="Phone number"
                    value={customer.phone}
                    onChange={(event) => setCustomer((current) => ({ ...current, phone: event.target.value }))}
                    className="w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-cyan-500 focus:bg-white"
                  />
                </div>
              </div>

              <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-sm">
                <p className="text-sm font-semibold uppercase tracking-[0.24em] text-slate-400">Checkout summary</p>
                <div className="mt-5 space-y-4">
                  <div className="rounded-[1.75rem] bg-slate-50 p-5">
                    <div className="flex items-center justify-between gap-4">
                      <div>
                        <p className="text-xs uppercase tracking-[0.24em] text-slate-500">Selected plan</p>
                        <p className="mt-2 text-lg font-semibold text-slate-950">{selectedPlan?.planName || 'No plan selected'}</p>
                      </div>
                      <span className="rounded-full bg-cyan-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.24em] text-cyan-700">
                        {selectedPlan ? `${selectedPlan.billingCycle.toLowerCase()} billing` : 'Choose a plan'}
                      </span>
                    </div>
                    <p className="mt-3 text-sm text-slate-600">{selectedPlan?.description || 'Pick the best plan to complete checkout.'}</p>
                  </div>
                  <div className="grid gap-3 sm:grid-cols-2">
                    <div className="rounded-3xl bg-slate-50 p-4">
                      <p className="text-xs uppercase tracking-[0.24em] text-slate-500">Amount</p>
                      <p className="mt-2 text-lg font-semibold text-slate-950">{selectedPlan ? formatCurrency(selectedPlan.amount) : '₹0.00'}</p>
                    </div>
                    <div className="rounded-3xl bg-slate-50 p-4">
                      <p className="text-xs uppercase tracking-[0.24em] text-slate-500">Organization</p>
                      <p className="mt-2 text-lg font-semibold text-slate-950">{organization.name}</p>
                    </div>
                  </div>
                  {checkoutError ? <p className="text-sm text-rose-600">{checkoutError}</p> : null}
                  {successMessage ? <p className="text-sm text-emerald-700">{successMessage}</p> : null}
                  <button
                    type="button"
                    onClick={startCheckout}
                    disabled={!selectedPlan || checkoutLoading}
                    className="inline-flex w-full items-center justify-center rounded-3xl bg-gradient-to-r from-cyan-600 to-slate-900 px-5 py-4 text-sm font-semibold text-white shadow-lg shadow-cyan-500/10 transition hover:from-cyan-700 hover:to-slate-950 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {checkoutLoading ? 'Preparing payment…' : 'Proceed to Razorpay'}
                  </button>
                </div>
              </div>
            </aside>
          </div>
        )}
      </div>
    </main>
  )
}
