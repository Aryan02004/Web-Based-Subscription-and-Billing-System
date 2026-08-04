import { useCallback, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import {
  AlertCircle,
  ArrowRight,
  Banknote,
  ChevronLeft,
  CreditCard,
  Link2,
  Mail,
  ShieldCheck,
  Users,
} from "lucide-react";
import DashboardLayout from "../components/dashboard/DashboardLayout";
import { api } from "../lib/api";

const STATUS_META = {
  APPROVED: { label: "Approved", tone: "bg-emerald-100 text-emerald-700" },
  PENDING: { label: "Pending approval", tone: "bg-amber-100 text-amber-700" },
  REJECTED: { label: "Rejected", tone: "bg-[#ffdad6] text-[#ba1a1a]" },
  SUSPENDED: { label: "Suspended", tone: "bg-purple-100 text-purple-700" },
};

function OrganizationDashboard() {
  const navigate = useNavigate();
  const { organizationId } = useParams();
  const [organization, setOrganization] = useState(null);
  const [plans, setPlans] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [invoices, setInvoices] = useState([]);
  const [subscriptions, setSubscriptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [isPlanModalOpen, setIsPlanModalOpen] = useState(false);
  const [selectedPlan, setSelectedPlan] = useState(null);
  const [planForm, setPlanForm] = useState({
    name: "",
    price: "",
    interval: "monthly",
    description: "",
    maxUsers: "",
    storageLimitGb: "",
    features: "",
    active: true,
  });
  const [creatingPlan, setCreatingPlan] = useState(false);
  const [publicCheckoutLink, setPublicCheckoutLink] = useState("");
  const [publicCheckoutToken, setPublicCheckoutToken] = useState("");
  const [fetchingData, setFetchingData] = useState(false);

  const buildPublicCheckoutUrl = useCallback((token) =>
    token
      ? `${window.location.origin.replace(/\/+$/, "")}/public/org/${encodeURIComponent(token)}`
      : "",
  [])
  const getCheckoutStorageKey = useCallback(
    (orgId) => `subscriptor.checkoutLink.${orgId}`,
    [],
  )

  const persistCheckoutData = useCallback((orgId, token) => {
    if (!orgId || !token) return

    try {
      window.localStorage.setItem(
        getCheckoutStorageKey(orgId),
        JSON.stringify({ token: token.trim() }),
      )
    } catch {
      // ignore local storage failures
    }
  }, [getCheckoutStorageKey])

  const loadPersistedCheckoutData = useCallback(
    (orgId) => {
      if (!orgId) return { token: "", url: "" }

      try {
        const raw = window.localStorage.getItem(getCheckoutStorageKey(orgId))
        if (!raw) return { token: "", url: "" }

        const parsed = JSON.parse(raw)
        const token = typeof parsed?.token === "string" ? parsed.token.trim() : ""
        return {
          token,
          url: token ? buildPublicCheckoutUrl(token) : "",
        }
      } catch {
        return { token: "", url: "" }
      }
    },
    [buildPublicCheckoutUrl, getCheckoutStorageKey],
  )
  const normalizeLinkResponse = useCallback((linkResponse) => {
    const buildUrlFromToken = (token) =>
      typeof token === "string" && token.trim()
        ? token.trim().startsWith("http://") || token.trim().startsWith("https://")
          ? token.trim()
          : buildPublicCheckoutUrl(token.trim())
        : ""

    const extractTokenFromUrl = (url) => {
      if (typeof url !== "string") return ""
      const trimmed = url.trim()
      const publicMatch = trimmed.match(/\/public\/org\/([^/?#]+)/)
      if (publicMatch) return decodeURIComponent(publicMatch[1])
      const billingMatch = trimmed.match(/\/billing\/([^/?#]+)/)
      if (billingMatch) return decodeURIComponent(billingMatch[1])
      return trimmed
    }

    if (typeof linkResponse === "string") {
      const trimmed = linkResponse.trim()
      return {
        token: trimmed.startsWith("http://") || trimmed.startsWith("https://") ? extractTokenFromUrl(trimmed) : trimmed,
        url: buildUrlFromToken(trimmed),
      }
    }

    if (linkResponse && typeof linkResponse === "object") {
      const tokenCandidate = [linkResponse.token, linkResponse.data, linkResponse.path, linkResponse.tokenId, linkResponse.id]
        .find((value) => typeof value === "string" && value.trim())
      const urlCandidate = [linkResponse.url, linkResponse.checkoutUrl, linkResponse.link, linkResponse.path]
        .find((value) => typeof value === "string" && value.trim() && (value.trim().startsWith("http://") || value.trim().startsWith("https://")))

      const token = tokenCandidate?.trim() || (urlCandidate ? extractTokenFromUrl(urlCandidate) : "")
      const url = urlCandidate || buildUrlFromToken(token)

      return { token, url }
    }

    return { token: "", url: "" }
  }, [buildPublicCheckoutUrl])

  const setCheckoutFromOrganization = useCallback(
    (organizationResponse) => {
      if (!organizationResponse) return

      const existingToken = organizationResponse.publicLinkToken || ""
      const normalized = normalizeLinkResponse({ token: existingToken })

      if (normalized.token) {
        setPublicCheckoutToken(normalized.token)
        setPublicCheckoutLink(normalized.url)
        persistCheckoutData(organizationId, normalized.token)
        return
      }

      const persisted = loadPersistedCheckoutData(organizationId)
      if (persisted.token) {
        setPublicCheckoutToken(persisted.token)
        setPublicCheckoutLink(persisted.url)
      } else {
        setPublicCheckoutToken("")
        setPublicCheckoutLink("")
      }
    },
    [loadPersistedCheckoutData, normalizeLinkResponse, organizationId, persistCheckoutData],
  )

  const generateOrgLink = async () => {
    if (!organizationId) return setError("Missing organization context.")

    setFetchingData(true)
    setError("")

    try {
      const linkResponse = await api.organization.generateLink({ organizationId })
      const linkData = normalizeLinkResponse(linkResponse)

      if (!linkData.url || !linkData.token) {
        throw new Error("Unable to generate a valid checkout link.")
      }

      setPublicCheckoutLink(linkData.url)
      setPublicCheckoutToken(linkData.token)
      persistCheckoutData(organizationId, linkData.token)
    } catch (requestError) {
      setError(requestError.message || "Unable to generate checkout link.")
    } finally {
      setFetchingData(false)
    }
  }

  const refreshData = useCallback(async () => {
    if (!organizationId) return

    setLoading(true)
    setError("")
    setFetchingData(true)

    try {
      const [organizationResponse, plansResponse, customersResponse, invoicesResponse, subscriptionsResponse] = await Promise.all([
        api.organization.getById(organizationId),
        api.plan.list({ organizationId }),
        api.customer.list({ organizationId }),
        api.invoice.list({ organizationId }),
        api.subscription.list({ organizationId }),
      ])

      setOrganization(organizationResponse)
      setPlans(plansResponse || [])
      setCustomers(customersResponse || [])
      setInvoices(invoicesResponse || [])
      setSubscriptions(subscriptionsResponse || [])
      setCheckoutFromOrganization(organizationResponse)
    } catch (requestError) {
      setError(requestError.message || "Unable to load organization data.")
    } finally {
      setLoading(false)
      setFetchingData(false)
    }
  }, [organizationId, setCheckoutFromOrganization])

  useEffect(() => {
    if (!organizationId) return

    const persisted = loadPersistedCheckoutData(organizationId)
    if (persisted.token) {
      setPublicCheckoutToken(persisted.token)
      setPublicCheckoutLink(persisted.url)
    }
  }, [organizationId, loadPersistedCheckoutData])

  useEffect(() => {
    if (!organizationId) return

    let cancelled = false
    void (async () => {
      if (cancelled) return
      await refreshData()
    })()

    return () => {
      cancelled = true
    }
  }, [organizationId, refreshData])

  const parseFeatureLines = (rawValue) =>
    rawValue
      .split("\n")
      .map((line) => line.trim())
      .filter(Boolean)
      .reduce((result, line) => {
        const [key, ...rest] = line.split(":")
        if (!key) return result
        result[key.trim()] = rest.join(":").trim() || true
        return result
      }, {})

  const formatFeatureLines = (features) => {
    if (!features || typeof features !== "object") return ""
    return Object.entries(features)
      .map(([key, value]) => `${key}: ${value}`)
      .join("\n")
  }

  const openCreatePlan = () => {
    setSelectedPlan(null)
    setPlanForm({
      name: "",
      price: "",
      interval: "monthly",
      description: "",
      maxUsers: "",
      storageLimitGb: "",
      features: "",
      active: true,
    })
    setIsPlanModalOpen(true)
  }

  const openEditPlan = (plan) => {
    setSelectedPlan(plan)
    setPlanForm({
      name: plan.planName || plan.name || "",
      price: plan.price != null ? String(plan.price) : "",
      interval: plan.billingCycle ? String(plan.billingCycle).toLowerCase() : "monthly",
      description: plan.description || "",
      maxUsers: plan.maxUsers != null ? String(plan.maxUsers) : "",
      storageLimitGb: plan.storageLimitGb != null ? String(plan.storageLimitGb) : "",
      features: formatFeatureLines(plan.features),
      active: plan.active !== false,
    })
    setIsPlanModalOpen(true)
  }

  const openPlanModal = (plan) => {
    if (plan) {
      openEditPlan(plan)
    } else {
      openCreatePlan()
    }
  }

  const handlePlanChange = (field, value) => {
    setPlanForm((s) => ({ ...s, [field]: value }))
  }

  const savePlan = async () => {
    if (!organizationId) return setError("Missing organization context.")

    setCreatingPlan(true)
    setError("")

    const features = parseFeatureLines(planForm.features)

    const payload = {
      planName: planForm.name.trim(),
      description: planForm.description.trim() || null,
      price: Number(planForm.price) || 0,
      billingCycle: planForm.interval === "yearly" ? "YEARLY" : "MONTHLY",
      maxUsers: planForm.maxUsers ? Number(planForm.maxUsers) : null,
      storageLimitGb: planForm.storageLimitGb ? Number(planForm.storageLimitGb) : null,
      features: Object.keys(features).length > 0 ? features : null,
      active: planForm.active,
      organization: { id: Number(organizationId) },
    }

    try {
      if (selectedPlan) {
        await api.plan.update(selectedPlan.id, payload)
      } else {
        await api.plan.create(payload)
      }

      await refreshData()
      setIsPlanModalOpen(false)
      setSelectedPlan(null)
    } catch (requestError) {
      setError(requestError.message || "Unable to save plan.")
    } finally {
      setCreatingPlan(false)
    }
  }

  const handleDeletePlan = async (planId) => {
    if (!window.confirm("Delete this plan? This cannot be undone.")) return
    setFetchingData(true)
    setError("")

    try {
      await api.plan.remove(planId)
      await refreshData()
    } catch (requestError) {
      setError(requestError.message || "Unable to delete plan.")
    } finally {
      setFetchingData(false)
    }
  }

  const handleTogglePlanActive = async (plan) => {
    setFetchingData(true)
    setError("")

    try {
      const payload = {
        planName: plan.planName || plan.name,
        description: plan.description || null,
        price: Number(plan.price) || 0,
        billingCycle: plan.billingCycle || plan.interval || "MONTHLY",
        maxUsers: plan.maxUsers || null,
        storageLimitGb: plan.storageLimitGb || null,
        features: plan.features || null,
        active: plan.active === false ? true : false,
        organization: { id: Number(organizationId) },
      }

      await api.plan.update(plan.id, payload)
      await refreshData()
    } catch (requestError) {
      setError(requestError.message || "Unable to update plan status.")
    } finally {
      setFetchingData(false)
    }
  }

  const totalRevenue = invoices.reduce((sum, invoice) => {
    const amount = Number(invoice.totalAmount || invoice.total_amount || 0)
    return sum + (Number.isFinite(amount) ? amount : 0)
  }, 0)

  const activeSubscriptions = subscriptions.filter((sub) => sub.status === "ACTIVE" || sub.status === "active").length
  const totalCustomers = customers.length
  const totalPlans = plans.length
  const activePlans = plans.filter((plan) => plan.active !== false).length
  const paidInvoices = invoices.filter((invoice) => invoice.status === "PAID" || invoice.status === "paid").length
  const pendingInvoices = invoices.filter((invoice) => invoice.status === "PENDING" || invoice.status === "pending").length

  const formatCurrency = (value) =>
    new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" }).format(value)

  const getPlanId = (plan) => plan.id || plan.planId || plan.organizationPlanId

  const renderTokenValue = () => {
    if (!publicCheckoutToken) {
      return <span className="text-sm text-[#6b7280]">No checkout token yet</span>
    }

    return (
      <div className="space-y-1">
        <div className="rounded-2xl bg-[#f8fafb] px-3 py-2 font-mono text-xs text-[#0b1c30] break-all">
          {publicCheckoutToken}
        </div>
        <button
          type="button"
          onClick={() => navigator.clipboard.writeText(publicCheckoutToken)}
          className="text-xs font-semibold text-[#3c4947] hover:text-[#006b5f]"
        >
          Copy token
        </button>
      </div>
    )
  }

  const status = organization?.status || "PENDING";
  const statusMeta = STATUS_META[status] || STATUS_META.PENDING;

  return (
    <DashboardLayout searchQuery="" onSearchChange={() => {}} searchPlaceholder="Search organizations...">
      <div className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
          <div className="space-y-2">
            <Link
              to="/dashboard/organizations"
              className="inline-flex items-center gap-2 text-sm font-semibold text-[#006b5f] transition hover:text-[#00423b]"
            >
              <ChevronLeft className="h-4 w-4" />
              Back to organizations
            </Link>
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.24em] text-[#3c4947]">Organization dashboard</p>
              <h1 className="mt-3 text-3xl font-semibold tracking-tight text-[#0b1c30]">
                {organization?.organizationName || organization?.name || "Organization detail"}
              </h1>
              <p className="max-w-2xl text-base text-[#3c4947]">
                View the selected organization, its current approval status, and key contact details.
              </p>
            </div>
          </div>

          <div className="flex flex-wrap gap-3">
            <button
              type="button"
              onClick={() => navigate("/dashboard/organizations")}
              className="rounded-lg border border-[#bbcac6] bg-white px-4 py-2 text-sm font-semibold text-[#3c4947] transition hover:bg-[#eff4ff]"
            >
              Change organization
            </button>
            <button
              type="button"
              onClick={() => navigate("/dashboard/organizations")}
              className="inline-flex items-center rounded-lg bg-[#14b8a6] px-4 py-2 text-sm font-semibold text-[#00423b] shadow-sm transition hover:bg-[#006b5f] hover:text-white"
            >
              <ArrowRight className="mr-2 h-4 w-4" />
              Go to workspace
            </button>
          </div>
        </div>

        {loading ? (
          <div className="rounded-3xl border border-[#bbcac6] bg-white p-8 shadow-sm">
            <div className="h-6 w-40 animate-pulse rounded-full bg-slate-200" />
            <div className="mt-6 space-y-4">
              <div className="h-5 w-32 animate-pulse rounded-full bg-slate-200" />
              <div className="h-48 animate-pulse rounded-3xl bg-slate-200" />
            </div>
          </div>
        ) : error ? (
          <div className="rounded-3xl border border-red-200 bg-red-50 p-6 text-sm text-red-700">
            {error}
          </div>
        ) : organization ? (
          <div className="space-y-8">
            <div className="grid gap-4 lg:grid-cols-3">
              <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Status</p>
                <div className="mt-4 flex items-center justify-between gap-4">
                  <p className="text-2xl font-bold text-[#0b1c30]">{statusMeta.label}</p>
                  <span className={`rounded-full px-3 py-1 text-xs font-semibold ${statusMeta.tone}`}>
                    {status}
                  </span>
                </div>
                <p className="mt-3 text-sm text-[#3c4947]">
                  Current approval state for this organization.
                </p>
              </div>
              <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Industry</p>
                <p className="mt-4 text-2xl font-bold text-[#0b1c30]">{organization.industry || "General"}</p>
                <p className="mt-3 text-sm text-[#3c4947]">Primary business vertical.</p>
              </div>
              <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Created on</p>
                <p className="mt-4 text-2xl font-bold text-[#0b1c30]">
                  {organization.createdAt ? new Date(organization.createdAt).toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" }) : "—"}
                </p>
                <p className="mt-3 text-sm text-[#3c4947]">Date the organization was registered.</p>
              </div>
            </div>

            <div className="grid gap-4 lg:grid-cols-2">
              <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                <div className="flex items-center justify-between">
                  <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Contact</p>
                  <Mail className="h-4 w-4 text-[#006b5f]" />
                </div>
                <p className="mt-4 text-lg font-semibold text-[#0b1c30]">{organization.contactEmail || "No email set"}</p>
                <p className="mt-2 text-sm text-[#3c4947]">Primary billing and notification address.</p>
              </div>

              <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                <div className="flex items-center justify-between">
                  <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Overview</p>
                  <ShieldCheck className="h-4 w-4 text-[#006b5f]" />
                </div>
                <p className="mt-4 text-lg font-semibold text-[#0b1c30]">{organization.organizationName}</p>
                <p className="mt-2 text-sm text-[#3c4947]">Organization ID: {organization.organizationId || organization.id}</p>
              </div>
            </div>

            {organization.status === 'APPROVED' ? (
              <>
                <div className="grid gap-4 lg:grid-cols-4">
                  <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                    <div className="flex items-center gap-3">
                      <CreditCard className="h-5 w-5 text-[#006b5f]" />
                      <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Plans</p>
                    </div>
                    <p className="mt-4 text-3xl font-bold text-[#0b1c30]">{totalPlans}</p>
                    <p className="mt-2 text-sm text-[#3c4947]">Active subscription plans</p>
                  </div>
                  <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                    <div className="flex items-center gap-3">
                      <Users className="h-5 w-5 text-[#006b5f]" />
                      <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Customers</p>
                    </div>
                    <p className="mt-4 text-3xl font-bold text-[#0b1c30]">{totalCustomers}</p>
                    <p className="mt-2 text-sm text-[#3c4947]">Registered customers</p>
                  </div>
                  <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                    <div className="flex items-center gap-3">
                      <Banknote className="h-5 w-5 text-[#006b5f]" />
                      <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Revenue</p>
                    </div>
                    <p className="mt-4 text-3xl font-bold text-[#0b1c30]">{formatCurrency(totalRevenue)}</p>
                    <p className="mt-2 text-sm text-[#3c4947]">Invoice value generated</p>
                  </div>
                  <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                    <div className="flex items-center gap-3">
                      <Link2 className="h-5 w-5 text-[#006b5f]" />
                      <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Subscriptions</p>
                    </div>
                    <p className="mt-4 text-3xl font-bold text-[#0b1c30]">{activeSubscriptions}</p>
                    <p className="mt-2 text-sm text-[#3c4947]">Active subscriptions</p>
                  </div>
                </div>

                <div className="grid gap-4 lg:grid-cols-3">
                  <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                    <div className="flex items-center gap-3">
                      <CreditCard className="h-5 w-5 text-[#006b5f]" />
                      <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Active plans</p>
                    </div>
                    <p className="mt-4 text-3xl font-bold text-[#0b1c30]">{activePlans}</p>
                    <p className="mt-2 text-sm text-[#3c4947]">Plans currently available for checkout.</p>
                  </div>
                  <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                    <div className="flex items-center gap-3">
                      <Banknote className="h-5 w-5 text-[#006b5f]" />
                      <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Paid invoices</p>
                    </div>
                    <p className="mt-4 text-3xl font-bold text-[#0b1c30]">{paidInvoices}</p>
                    <p className="mt-2 text-sm text-[#3c4947]">Invoices successfully collected.</p>
                  </div>
                  <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                    <div className="flex items-center gap-3">
                      <AlertCircle className="h-5 w-5 text-[#006b5f]" />
                      <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Pending invoices</p>
                    </div>
                    <p className="mt-4 text-3xl font-bold text-[#0b1c30]">{pendingInvoices}</p>
                    <p className="mt-2 text-sm text-[#3c4947]">Invoices still awaiting payment.</p>
                  </div>
                </div>
              </>
            ) : null}

            {organization.status === "REJECTED" ? (
              <div className="rounded-3xl border border-[#ba1a1a] bg-[#ffefef] p-6 text-sm text-[#93000a]">
                <div className="mb-3 flex items-center gap-2 font-semibold text-[#ba1a1a]">
                  <AlertCircle className="h-4 w-4" /> Rejection reason
                </div>
                <p>{organization.rejectionReason || "No reason provided."}</p>
              </div>
            ) : null}

            {organization.status === "APPROVED" ? (
              <div className="space-y-8">
                <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Plans</p>
                      <p className="mt-2 text-sm text-[#3c4947]">Manage subscription plans and public checkout links.</p>
                    </div>
                    <button
                      type="button"
                      onClick={openCreatePlan}
                      className="rounded-lg bg-[#006b5f] px-4 py-2 text-sm font-semibold text-white transition hover:bg-[#005248]"
                    >
                      Create plan
                    </button>
                  </div>

                  <div className="mt-6 overflow-x-auto">
                    <table className="min-w-full divide-y divide-[#e5e7eb] text-sm text-[#3c4947]">
                      <thead>
                        <tr>
                          <th className="px-4 py-3 text-left font-semibold">Name</th>
                          <th className="px-4 py-3 text-left font-semibold">Price</th>
                          <th className="px-4 py-3 text-left font-semibold">Interval</th>
                          <th className="px-4 py-3 text-left font-semibold">Status</th>
                          <th className="px-4 py-3 text-left font-semibold">Actions</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-[#e5e7eb]">
                        {plans.length > 0 ? (
                          plans.map((plan) => (
                            <tr key={getPlanId(plan)}>
                              <td className="px-4 py-4">{plan.planName || plan.name || "Untitled plan"}</td>
                              <td className="px-4 py-4">{formatCurrency(plan.price || plan.amount || 0)}</td>
                              <td className="px-4 py-4">{(plan.billingCycle || plan.interval || "MONTHLY").toLowerCase()}</td>
                              <td className="px-4 py-4">{plan.active === false ? "Inactive" : "Active"}</td>
                              <td className="px-4 py-4">
                                <div className="flex flex-wrap gap-2">
                                  <button
                                    type="button"
                                    onClick={() => openPlanModal(plan)}
                                    className="rounded-lg border border-[#cbd5e1] px-3 py-1 text-xs font-semibold text-[#0b1c30] transition hover:bg-[#eff8f6]"
                                  >
                                    Edit
                                  </button>
                                  <button
                                    type="button"
                                    onClick={() => handleTogglePlanActive(plan)}
                                    className="rounded-lg border border-[#cbd5e1] px-3 py-1 text-xs font-semibold text-[#0b1c30] transition hover:bg-[#eff8f6]"
                                  >
                                    {plan.active === false ? "Activate" : "Deactivate"}
                                  </button>
                                  <button
                                    type="button"
                                    onClick={() => handleDeletePlan(plan.id)}
                                    className="rounded-lg border border-[#fca5a5] bg-[#fff1f2] px-3 py-1 text-xs font-semibold text-[#9b1c1c] transition hover:bg-[#fee2e2]"
                                  >
                                    Delete
                                  </button>
                                </div>
                              </td>
                            </tr>
                          ))
                        ) : (
                          <tr>
                            <td colSpan={5} className="px-4 py-8 text-center text-sm text-[#6b7280]">
                              No plans found. Create a plan to share a public checkout link.
                            </td>
                          </tr>
                        )}
                      </tbody>
                    </table>
                  </div>
                </div>

                <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                  <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                    <div>
                      <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Organization checkout</p>
                      <p className="mt-2 text-sm text-[#3c4947]">One public checkout link covers every plan for this organization.</p>
                    </div>
                    <button
                      type="button"
                      onClick={generateOrgLink}
                      disabled={fetchingData}
                      className="rounded-lg bg-[#006b5f] px-4 py-2 text-sm font-semibold text-white transition hover:bg-[#005248] disabled:cursor-not-allowed disabled:opacity-60"
                    >
                      {publicCheckoutLink ? "Refresh public link" : "Generate public checkout link"}
                    </button>
                  </div>

                  <div className="mt-6 grid gap-4 sm:grid-cols-2">
                    <div className="rounded-3xl border border-[#e5e7eb] bg-[#f8fafb] p-4">
                      <p className="text-xs uppercase tracking-[0.22em] text-[#6b7280]">Checkout token</p>
                      <div className="mt-3">
                        {renderTokenValue()}
                      </div>
                    </div>
                    <div className="rounded-3xl border border-[#e5e7eb] bg-[#fefdfc] p-4">
                      <p className="text-xs uppercase tracking-[0.22em] text-[#6b7280]">Public URL</p>
                      <div className="mt-3">
                        {publicCheckoutLink ? (
                          <a href={publicCheckoutLink} target="_blank" rel="noreferrer" className="text-[#006b5f] hover:underline break-all">
                            {publicCheckoutLink}
                          </a>
                        ) : (
                          <p className="text-sm text-[#6b7280]">No public checkout URL generated yet.</p>
                        )}
                      </div>
                    </div>
                  </div>
                </div>

                <div className="grid gap-4 lg:grid-cols-2">
                  <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                    <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Recent customers</p>
                    <div className="mt-4 space-y-3">
                      {customers.length > 0 ? (
                        customers.slice(0, 4).map((customer) => (
                          <div key={customer.id} className="rounded-2xl bg-[#f8fafb] p-3">
                            <p className="font-semibold text-[#0b1c30]">{customer.name || customer.fullName || "Customer"}</p>
                            <p className="text-sm text-[#3c4947]">{customer.email || customer.contactEmail || "No email"}</p>
                          </div>
                        ))
                      ) : (
                        <p className="text-sm text-[#6b7280]">No customers have been registered yet.</p>
                      )}
                    </div>
                  </div>

                  <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
                    <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Latest invoices</p>
                    <div className="mt-4 space-y-3">
                      {invoices.length > 0 ? (
                        invoices.slice(0, 4).map((invoice) => (
                          <div key={invoice.id} className="rounded-2xl bg-[#f8fafb] p-3">
                            <div className="flex items-center justify-between gap-2">
                              <p className="font-semibold text-[#0b1c30]">Invoice #{invoice.id}</p>
                              <span className="rounded-full bg-[#e6f5f2] px-2 py-1 text-[11px] font-semibold text-[#0b785d]">
                                {invoice.status || invoice.invoiceStatus || "Pending"}
                              </span>
                            </div>
                            <p className="text-sm text-[#3c4947]">{formatCurrency(invoice.totalAmount || invoice.total_amount || 0)}</p>
                          </div>
                        ))
                      ) : (
                        <p className="text-sm text-[#6b7280]">No invoices generated yet.</p>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ) : null}

            <div className="rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-sm">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-semibold uppercase tracking-[0.22em] text-[#3c4947]">Next step</p>
                  <p className="mt-3 text-base text-[#0b1c30]">
                    {organization.status === "APPROVED"
                      ? "Your organization can create plans and generate checkout links."
                      : organization.status === "PENDING"
                      ? "Waiting for Super Admin approval."
                      : organization.status === "SUSPENDED"
                      ? "Reach out to support to resolve a suspension."
                      : "Review the rejection details and resubmit if needed."}
                  </p>
                </div>
                <div className="rounded-2xl bg-[#eff4ff] px-3 py-2 text-xs font-semibold text-[#006b5f]">
                  {statusMeta.label}
                </div>
              </div>
            <div className="mt-6 flex flex-wrap gap-3">
              <button
                type="button"
                onClick={openCreatePlan}
                className="rounded-lg bg-[#006b5f] px-4 py-2 text-sm font-semibold text-white transition hover:bg-[#005248]"
              >
                Create plan
              </button>
              <button
                type="button"
                onClick={generateOrgLink}
                disabled={fetchingData}
                className="rounded-lg border border-[#bbcac6] px-4 py-2 text-sm font-semibold text-[#3c4947] transition hover:bg-[#eff4ff] disabled:cursor-not-allowed disabled:opacity-60"
              >
                Generate checkout link
              </button>
            </div>
            </div>
          </div>
        ) : (
          <p className="rounded-3xl border border-[#bbcac6] bg-white p-8 text-[#3c4947]">Organization not found.</p>
        )}
      </div>

      {/* Plan creation modal */}
      {isPlanModalOpen ? (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="w-full max-w-2xl rounded-2xl bg-white p-6">
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-lg font-semibold">Create subscription plan</h3>
              <button className="text-sm text-gray-600" onClick={() => setIsPlanModalOpen(false)}>Close</button>
            </div>

            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700">Name</label>
                <input value={planForm.name} onChange={(e) => handlePlanChange('name', e.target.value)} className="mt-1 w-full rounded-md border px-3 py-2" />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Price</label>
                  <input value={planForm.price} onChange={(e) => handlePlanChange('price', e.target.value)} className="mt-1 w-full rounded-md border px-3 py-2" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Interval</label>
                  <select value={planForm.interval} onChange={(e) => handlePlanChange('interval', e.target.value)} className="mt-1 w-full rounded-md border px-3 py-2">
                    <option value="monthly">Monthly</option>
                    <option value="yearly">Yearly</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Max users</label>
                  <input value={planForm.maxUsers} onChange={(e) => handlePlanChange('maxUsers', e.target.value)} className="mt-1 w-full rounded-md border px-3 py-2" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Storage limit (GB)</label>
                  <input value={planForm.storageLimitGb} onChange={(e) => handlePlanChange('storageLimitGb', e.target.value)} className="mt-1 w-full rounded-md border px-3 py-2" />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Description</label>
                <textarea value={planForm.description} onChange={(e) => handlePlanChange('description', e.target.value)} className="mt-1 w-full rounded-md border px-3 py-2" />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Feature lines</label>
                <textarea
                  value={planForm.features}
                  onChange={(e) => handlePlanChange('features', e.target.value)}
                  placeholder="feature_name: value"
                  className="mt-1 h-24 w-full rounded-md border px-3 py-2"
                />
                <p className="mt-2 text-xs text-[#6b7280]">Enter one feature per line like <span className="font-medium">users: 10</span>.</p>
              </div>

              <div className="flex items-center gap-3">
                <input
                  id="plan-active"
                  type="checkbox"
                  checked={planForm.active}
                  onChange={(e) => handlePlanChange('active', e.target.checked)}
                  className="h-4 w-4 rounded border-slate-300 text-cyan-600 focus:ring-cyan-500"
                />
                <label htmlFor="plan-active" className="text-sm text-gray-700">Plan is active</label>
              </div>

              {error ? <div className="text-sm text-red-600">{error}</div> : null}

              <div className="mt-4 flex items-center gap-3">
                <button onClick={savePlan} disabled={creatingPlan} className="rounded-md bg-[#006b5f] px-4 py-2 text-white">
                  {creatingPlan ? (selectedPlan ? 'Saving…' : 'Saving…') : selectedPlan ? 'Save changes' : 'Create plan'}
                </button>
                <button onClick={() => setIsPlanModalOpen(false)} className="rounded-md border px-4 py-2">Cancel</button>
              </div>

            </div>
          </div>
        </div>
      ) : null}
    </DashboardLayout>
  );
}

export default OrganizationDashboard;

