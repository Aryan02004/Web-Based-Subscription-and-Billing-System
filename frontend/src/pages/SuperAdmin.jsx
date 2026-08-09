import { useEffect, useMemo, useState } from "react";
import {
  Building2,
  Clock3,
  LayoutDashboard,
  RefreshCw,
  Search,
  ShieldCheck,
  UserCheck,
  UserX,
  Ban,
  X,
} from "lucide-react";
import DashboardLayout from "../components/dashboard/DashboardLayout";
import { api } from "../lib/api";

const navItems = [
  {
    label: "Dashboard",
    icon: LayoutDashboard,
    href: "/dashboard/super-admin",
    activeMatch: "/dashboard/super-admin",
  },
];

const SORT_OPTIONS = [
  { value: "newest", label: "Sort: Newest ID First" },
  { value: "oldest", label: "Sort: Oldest ID First" },
  { value: "name", label: "Sort: Organization A-Z" },
];

function formatOrganizationKey(value) {
  if (value === null || value === undefined || value === "") {
    return "—";
  }

  return `#${value}`;
}

function SuperAdminActionModal({ open, action, organization, reason, onReasonChange, loading, error, onClose, onConfirm }) {
  if (!open || !organization || !action) return null;

  const isDecisionAction = action !== "approve";
  const actionLabel = action === "approve" ? "Approve" : action === "reject" ? "Reject" : "Suspend";
  const actionTone = action === "approve" ? "bg-emerald-600" : action === "reject" ? "bg-[#ba1a1a]" : "bg-[#6b4eff]";

  return (
    <div className="fixed inset-0 z-[120] flex items-center justify-center bg-[#0b1c30]/55 px-4 backdrop-blur-sm">
      <div className="w-full max-w-xl rounded-3xl border border-[#bbcac6] bg-white p-6 shadow-2xl">
        <div className="mb-6 flex items-start justify-between gap-4">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.24em] text-[#006b5f]">Action Required</p>
            <h2 className="mt-2 text-2xl font-bold text-[#0b1c30]">
              {actionLabel} {organization.name}
            </h2>
            <p className="mt-2 text-sm text-[#3c4947]">
              {action === "approve"
                ? "Approve this organization to make it available in the platform."
                : action === "reject"
                ? "Rejection requires a short reason for the audit trail."
                : "Suspension requires a reason so support can trace the decision."}
            </p>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="rounded-lg p-2 text-[#3c4947] transition-colors hover:bg-[#eff4ff]"
            aria-label="Close action modal"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="mb-5 rounded-2xl border border-[#bbcac6] bg-[#f8f9ff] p-4 text-sm text-[#0b1c30]">
          <div className="grid gap-3 sm:grid-cols-2">
            <div>
              <p className="text-xs font-semibold uppercase tracking-wide text-[#3c4947]">Organization</p>
              <p className="mt-1 font-semibold">{organization.name}</p>
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wide text-[#3c4947]">Industry</p>
              <p className="mt-1 font-semibold">{organization.industry || "General"}</p>
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wide text-[#3c4947]">Contact email</p>
              <p className="mt-1 font-semibold">{organization.contactEmail || "No contact email"}</p>
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wide text-[#3c4947]">Organization ID</p>
              <p className="mt-1 font-semibold">{formatOrganizationKey(organization.id)}</p>
            </div>
          </div>
        </div>

        {isDecisionAction ? (
          <label className="mb-4 block space-y-2">
            <span className="text-sm font-semibold text-[#0b1c30]">Reason</span>
            <textarea
              value={reason}
              onChange={(event) => onReasonChange(event.target.value)}
              rows={4}
              placeholder={
                action === "reject"
                  ? "Explain why this application was rejected"
                  : "Explain why the organization is being suspended"
              }
              className="w-full rounded-2xl border border-[#bbcac6] bg-[#f8f9ff] px-4 py-3 text-sm outline-none transition focus:border-[#006b5f] focus:bg-white focus:ring-2 focus:ring-[#006b5f]/10"
            />
          </label>
        ) : null}

        {error ? (
          <p className="mb-4 rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>
        ) : null}

        <div className="flex flex-col gap-3 sm:flex-row">
          <button
            type="button"
            onClick={onClose}
            className="rounded-2xl border border-[#bbcac6] px-4 py-3 text-sm font-semibold text-[#3c4947] transition hover:bg-[#eff4ff]"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={onConfirm}
            disabled={loading}
            className={`rounded-2xl px-4 py-3 text-sm font-semibold text-white transition ${actionTone} disabled:cursor-not-allowed disabled:opacity-70`}
          >
            {loading ? "Processing..." : `${actionLabel} Organization`}
          </button>
        </div>
      </div>
    </div>
  );
}

function SuperAdminOrganizationCard({ organization, onAction }) {
  return (
    <article className="flex h-full flex-col rounded-2xl border border-[#bbcac6] bg-white p-5 shadow-[0px_1px_3px_0px_rgba(0,0,0,0.05)] transition hover:-translate-y-0.5 hover:shadow-lg">
      <div className="mb-4 flex items-start justify-between gap-4">
        <div>
          <div className="mb-3 inline-flex items-center rounded-full bg-amber-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.2em] text-amber-700">
            Pending review
          </div>
          <h3 className="text-xl font-semibold text-[#0b1c30]">{organization.name}</h3>
          <p className="mt-1 text-sm text-[#3c4947]">{organization.industry || "General"}</p>
        </div>

        <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl border border-[#bbcac6] bg-[#eff4ff]">
          <Building2 className="h-6 w-6 text-[#006b5f]" />
        </div>
      </div>

      <div className="mb-5 space-y-3 text-sm text-[#3c4947]">
        <div className="flex items-center justify-between gap-3 rounded-xl bg-[#f8f9ff] px-4 py-3">
          <span>Organization ID</span>
          <span className="font-semibold text-[#0b1c30]">{formatOrganizationKey(organization.id)}</span>
        </div>
        <div className="flex items-center justify-between gap-3 rounded-xl bg-[#f8f9ff] px-4 py-3">
          <span>Contact email</span>
          <span className="font-semibold text-[#0b1c30]">{organization.contactEmail || "No contact email"}</span>
        </div>
      </div>

      <div className="mt-auto grid grid-cols-1 gap-3 sm:grid-cols-3">
        <button
          type="button"
          onClick={() => onAction("approve", organization)}
          className="inline-flex items-center justify-center rounded-xl bg-emerald-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-emerald-700"
        >
          <UserCheck className="mr-2 h-4 w-4" />
          Approve
        </button>
        <button
          type="button"
          onClick={() => onAction("reject", organization)}
          className="inline-flex items-center justify-center rounded-xl border border-[#ba1a1a] px-4 py-3 text-sm font-semibold text-[#ba1a1a] transition hover:bg-[#ba1a1a]/5"
        >
          <UserX className="mr-2 h-4 w-4" />
          Reject
        </button>
        <button
          type="button"
          onClick={() => onAction("suspend", organization)}
          className="inline-flex items-center justify-center rounded-xl border border-[#6b4eff] px-4 py-3 text-sm font-semibold text-[#6b4eff] transition hover:bg-[#6b4eff]/5"
        >
          <Ban className="mr-2 h-4 w-4" />
          Suspend
        </button>
      </div>
    </article>
  )
}

function SuperAdminOrganizationSkeleton() {
  return (
    <article className="flex h-full flex-col rounded-2xl border border-[#bbcac6] bg-white p-5">
      <div className="mb-4 flex items-start justify-between gap-4">
        <div className="space-y-3">
          <div className="h-5 w-28 animate-pulse rounded-full bg-slate-200" />
          <div className="h-6 w-52 animate-pulse rounded bg-slate-200" />
          <div className="h-4 w-24 animate-pulse rounded bg-slate-200/70" />
        </div>
        <div className="h-12 w-12 animate-pulse rounded-2xl bg-slate-200" />
      </div>
      <div className="mb-5 space-y-3">
        <div className="h-12 w-full animate-pulse rounded-xl bg-slate-200" />
        <div className="h-12 w-full animate-pulse rounded-xl bg-slate-200/80" />
      </div>
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
        <div className="h-12 animate-pulse rounded-xl bg-slate-200" />
        <div className="h-12 animate-pulse rounded-xl bg-slate-200" />
        <div className="h-12 animate-pulse rounded-xl bg-slate-200" />
      </div>
    </article>
  )
}

function SuperAdmin() {
  const [organizations, setOrganizations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [searchQuery, setSearchQuery] = useState("")
  const [sortBy, setSortBy] = useState("newest")
  const [lastRefreshedAt, setLastRefreshedAt] = useState(null)
  const [toast, setToast] = useState("")
  const [modalState, setModalState] = useState({ open: false, action: null, organization: null })
  const [reason, setReason] = useState("")
  const [modalError, setModalError] = useState("")
  const [actionLoading, setActionLoading] = useState(false)

  const loadPendingOrganizations = async () => {
    setLoading(true)
    setError("")

    try {
      const response = await api.superAdminOrganization.getPendingOrganizations()
      setOrganizations(Array.isArray(response) ? response : [])
      setLastRefreshedAt(new Date())
    } catch (requestError) {
      setError(requestError.message || "Unable to load pending organizations.")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    let cancelled = false

    const fetchPendingOrganizations = async () => {
      setLoading(true)
      setError("")

      try {
        const response = await api.superAdminOrganization.getPendingOrganizations()
        if (!cancelled) {
          setOrganizations(Array.isArray(response) ? response : [])
          setLastRefreshedAt(new Date())
        }
      } catch (requestError) {
        if (!cancelled) {
          setError(requestError.message || "Unable to load pending organizations.")
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    void fetchPendingOrganizations()

    return () => {
      cancelled = true
    }
  }, [])

  useEffect(() => {
    if (!toast) return undefined

    const timer = setTimeout(() => setToast(""), 3200)
    return () => clearTimeout(timer)
  }, [toast])

  const filteredOrganizations = useMemo(() => {
    let result = [...organizations]

    if (searchQuery.trim()) {
      const query = searchQuery.trim().toLowerCase()
      result = result.filter((organization) => {
        const haystack = [organization.name, organization.contactEmail, organization.industry, organization.status]
          .filter(Boolean)
          .join(" ")
          .toLowerCase()

        return haystack.includes(query)
      })
    }

    result.sort((left, right) => {
      if (sortBy === "name") {
        return (left.name || "").localeCompare(right.name || "")
      }

      return sortBy === "oldest" ? (left.id || 0) - (right.id || 0) : (right.id || 0) - (left.id || 0)
    })

    return result
  }, [organizations, searchQuery, sortBy])

  const industryBreakdown = useMemo(() => {
    return organizations.reduce((counts, organization) => {
      const key = organization.industry || "General"
      counts[key] = (counts[key] || 0) + 1
      return counts
    }, {})
  }, [organizations])

  const queueSummary = useMemo(() => {
    return [
      { label: "Pending organizations", value: organizations.length, icon: Clock3, tone: "text-amber-700" },
      { label: "Visible after filters", value: filteredOrganizations.length, icon: Search, tone: "text-[#006b5f]" },
      {
        label: "Industries represented",
        value: Object.keys(industryBreakdown).length,
        icon: ShieldCheck,
        tone: "text-[#6b4eff]",
      },
    ]
  }, [filteredOrganizations.length, industryBreakdown, organizations.length])

  const handleAction = (action, organization) => {
    setModalState({ open: true, action, organization })
    setReason("")
    setModalError("")
  }

  const closeActionModal = () => {
    if (actionLoading) return

    setModalState({ open: false, action: null, organization: null })
    setReason("")
    setModalError("")
  }

  const confirmAction = async () => {
    if (!modalState.organization || !modalState.action) {
      return
    }

    if (modalState.action !== "approve" && !reason.trim()) {
      setModalError("A reason is required for this action.")
      return
    }

    setActionLoading(true)
    setModalError("")

    try {
      if (modalState.action === "approve") {
        await api.superAdminOrganization.approve(modalState.organization.id)
      }

      if (modalState.action === "reject") {
        await api.superAdminOrganization.reject(modalState.organization.id, reason.trim())
      }

      if (modalState.action === "suspend") {
        await api.superAdminOrganization.suspend(modalState.organization.id, reason.trim())
      }

      setToast(`${modalState.organization.name} has been ${modalState.action}d.`)
      setModalState({ open: false, action: null, organization: null })
      setReason("")
      await loadPendingOrganizations()
    } catch (requestError) {
      setModalError(requestError.message || "Unable to complete the action.")
    } finally {
      setActionLoading(false)
    }
  }

  return (
    <DashboardLayout navItems={navItems} searchQuery={searchQuery} onSearchChange={setSearchQuery} searchPlaceholder="Search pending organizations...">
      <div className="mx-auto flex max-w-7xl flex-col gap-8 px-6 py-8">
        <section className="rounded-[2rem] border border-[#bbcac6] bg-white p-6 shadow-[0px_10px_30px_rgba(11,28,48,0.06)]">
          <div className="flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between">
            <div className="max-w-3xl space-y-4">
              <div className="inline-flex items-center gap-2 rounded-full bg-[#e5eeff] px-3 py-1 text-xs font-semibold uppercase tracking-[0.22em] text-[#006b5f]">
                <ShieldCheck className="h-3.5 w-3.5" />
                Super Admin Dashboard
              </div>
              <div>
                <h1 className="text-4xl font-semibold tracking-tight text-[#0b1c30]">Review and govern organization access</h1>
                <p className="mt-3 max-w-2xl text-base leading-7 text-[#3c4947]">
                  Pending organizations appear here for final approval, rejection, or suspension. Use the queue to keep onboarding decisions visible and auditable.
                </p>
              </div>
            </div>

            <div className="flex flex-col gap-3 sm:flex-row">
              <button
                type="button"
                onClick={loadPendingOrganizations}
                className="inline-flex items-center justify-center rounded-2xl border border-[#bbcac6] bg-[#f8f9ff] px-5 py-3 text-sm font-semibold text-[#0b1c30] transition hover:bg-[#eff4ff]"
              >
                <RefreshCw className="mr-2 h-4 w-4" />
                Refresh queue
              </button>
            </div>
          </div>

          <div className="mt-6 grid gap-4 md:grid-cols-3">
            {queueSummary.map((item) => {
              const Icon = item.icon

              return (
                <div key={item.label} className="rounded-2xl border border-[#bbcac6] bg-[#f8f9ff] p-4">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <p className="text-sm text-[#3c4947]">{item.label}</p>
                      <p className="mt-2 text-3xl font-semibold text-[#0b1c30]">{item.value}</p>
                    </div>
                    <div className={`rounded-2xl bg-white p-3 ${item.tone}`}>
                      <Icon className="h-5 w-5" />
                    </div>
                  </div>
                </div>
              )
            })}
          </div>

          <div className="mt-6 flex flex-wrap items-center gap-3 text-sm text-[#3c4947]">
            <span className="rounded-full bg-[#eff4ff] px-3 py-1 font-medium text-[#0b1c30]">
              {lastRefreshedAt ? `Last refreshed ${lastRefreshedAt.toLocaleTimeString([], { hour: "numeric", minute: "2-digit" })}` : "Queue not refreshed yet"}
            </span>
            <span className="rounded-full bg-[#eff4ff] px-3 py-1 font-medium text-[#0b1c30]">
              Reject and suspend actions require reasons
            </span>
          </div>
        </section>

        {error ? (
          <section className="rounded-2xl border border-red-200 bg-red-50 px-5 py-4 text-sm text-red-700">
            {error}
          </section>
        ) : null}

        <section className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_320px]">
          <div>
            <div className="mb-4 flex items-end justify-between gap-4">
              <div>
                <h2 className="text-2xl font-semibold text-[#0b1c30]">Pending approvals</h2>
                <p className="mt-1 text-sm text-[#3c4947]">
                  Each card is a decision point. Approve for access, reject for invalid requests, or suspend when a compliance review is required.
                </p>
              </div>

              <select
                value={sortBy}
                onChange={(event) => setSortBy(event.target.value)}
                className="rounded-xl border border-[#bbcac6] bg-white px-4 py-2.5 text-sm text-[#0b1c30] outline-none transition focus:border-[#006b5f] focus:ring-2 focus:ring-[#006b5f]/10"
              >
                {SORT_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="grid grid-cols-1 gap-5 lg:grid-cols-2">
              {loading
                ? Array.from({ length: 4 }).map((_, index) => <SuperAdminOrganizationSkeleton key={index} />)
                : null}

              {!loading && filteredOrganizations.length === 0 ? (
                <div className="col-span-full rounded-3xl border border-dashed border-[#bbcac6] bg-white px-6 py-20 text-center">
                  <ShieldCheck className="mx-auto h-12 w-12 text-[#006b5f]" />
                  <h3 className="mt-4 text-2xl font-semibold text-[#0b1c30]">No pending organizations</h3>
                  <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-[#3c4947]">
                    The queue is clear. New submissions will appear here once organizations request onboarding.
                  </p>
                  <button
                    type="button"
                    onClick={loadPendingOrganizations}
                    className="mt-6 inline-flex items-center rounded-2xl bg-[#006b5f] px-5 py-3 text-sm font-semibold text-white transition hover:bg-[#005146]"
                  >
                    Refresh queue
                  </button>
                </div>
              ) : null}

              {!loading
                ? filteredOrganizations.map((organization) => (
                    <SuperAdminOrganizationCard key={organization.id} organization={organization} onAction={handleAction} />
                  ))
                : null}
            </div>
          </div>

          <aside className="space-y-5">
            <div className="rounded-3xl border border-[#bbcac6] bg-white p-5 shadow-[0px_1px_3px_0px_rgba(0,0,0,0.05)]">
              <h3 className="text-lg font-semibold text-[#0b1c30]">Review checklist</h3>
              <div className="mt-4 space-y-3 text-sm text-[#3c4947]">
                <div className="rounded-2xl bg-[#f8f9ff] p-4">
                  <p className="font-semibold text-[#0b1c30]">Approve</p>
                  <p className="mt-1 leading-6">Use when the organization details are valid and the account can move into production.</p>
                </div>
                <div className="rounded-2xl bg-[#f8f9ff] p-4">
                  <p className="font-semibold text-[#0b1c30]">Reject</p>
                  <p className="mt-1 leading-6">Use when the application is incomplete, invalid, or violates onboarding rules.</p>
                </div>
                <div className="rounded-2xl bg-[#f8f9ff] p-4">
                  <p className="font-semibold text-[#0b1c30]">Suspend</p>
                  <p className="mt-1 leading-6">Use when an existing organization needs temporary restriction pending review.</p>
                </div>
              </div>
            </div>

            <div className="rounded-3xl border border-[#bbcac6] bg-white p-5 shadow-[0px_1px_3px_0px_rgba(0,0,0,0.05)]">
              <h3 className="text-lg font-semibold text-[#0b1c30]">Queue breakdown</h3>
              <div className="mt-4 space-y-3">
                {Object.keys(industryBreakdown).length === 0 ? (
                  <p className="text-sm text-[#3c4947]">No industries to display yet.</p>
                ) : (
                  Object.entries(industryBreakdown)
                    .sort(([left], [right]) => left.localeCompare(right))
                    .map(([industry, count]) => (
                      <div key={industry} className="flex items-center justify-between rounded-2xl bg-[#f8f9ff] px-4 py-3 text-sm">
                        <span className="text-[#3c4947]">{industry}</span>
                        <span className="font-semibold text-[#0b1c30]">{count}</span>
                      </div>
                    ))
                )}
              </div>
            </div>
          </aside>
        </section>
      </div>

      <SuperAdminActionModal
        open={modalState.open}
        action={modalState.action}
        organization={modalState.organization}
        reason={reason}
        onReasonChange={setReason}
        loading={actionLoading}
        error={modalError}
        onClose={closeActionModal}
        onConfirm={confirmAction}
      />

      <div
        className={`fixed bottom-6 right-6 z-[130] flex items-center rounded-2xl border border-white/10 bg-[#213145] px-6 py-3 text-white shadow-xl transition-all duration-300 ${
          toast ? "translate-y-0 opacity-100" : "translate-y-20 opacity-0"
        }`}
      >
        <ShieldCheck className="mr-3 h-5 w-5 text-emerald-400" />
        <span className="text-sm">{toast}</span>
      </div>
    </DashboardLayout>
  )
}

export default SuperAdmin;
