import {
  ArrowRight,
  Building2,
  Calendar,
  CloudOff,
  Hourglass,
  Mail,
  AlertCircle,
  ShoppingBag,
  Cpu,
} from "lucide-react";

const STATUS_CONFIG = {
  APPROVED: {
    label: "Approved",
    badgeClass: "bg-emerald-100 text-emerald-700",
    dotClass: "bg-emerald-600 animate-pulse",
    cardClass: "hover:shadow-md",
  },
  PENDING: {
    label: "Pending",
    badgeClass: "bg-amber-100 text-amber-700",
    dotClass: "bg-amber-600",
    cardClass: "",
  },
  REJECTED: {
    label: "Rejected",
    badgeClass: "bg-[#ffdad6] text-[#ba1a1a]",
    dotClass: "",
    cardClass: "border-[#ba1a1a]/20 relative overflow-hidden",
  },
  SUSPENDED: {
    label: "Suspended",
    badgeClass: "bg-purple-100 text-purple-700",
    dotClass: "",
    cardClass: "",
  },
  EXPIRED: {
    label: "Expired",
    badgeClass: "bg-gray-200 text-gray-700",
    dotClass: "",
    cardClass: "opacity-80",
  },
};

const INDUSTRY_ICONS = {
  Technology: Cpu,
  Retail: ShoppingBag,
  Finance: Building2,
  "Cloud Computing": CloudOff,
};

function formatDate(value) {
  if (!value) return "—";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "—";

  return date.toLocaleDateString("en-IN", {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
}

function OrganizationCard({ organization, onOpenDashboard }) {
  const status = organization.status || "PENDING";
  const config = STATUS_CONFIG[status] || STATUS_CONFIG.PENDING;
  const Icon = INDUSTRY_ICONS[organization.industry] || Building2;

  return (
    <article
      className={`flex flex-col rounded-xl border border-[#bbcac6] bg-white p-4 shadow-[0px_1px_3px_0px_rgba(0,0,0,0.05)] transition-all ${config.cardClass}`}
    >
      {status === "REJECTED" ? (
        <div className="pointer-events-none absolute inset-0 bg-[#ba1a1a]/5" />
      ) : null}

      <div className="relative z-10 mb-4 flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg border border-[#bbcac6] bg-[#eff4ff]">
            <Icon className="h-7 w-7 text-[#006b5f]" />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-[#0b1c30]">
              {organization.organizationName}
            </h3>
            <p className="text-sm text-[#3c4947]">
              {organization.industry || "General"}
            </p>
          </div>
        </div>

        <span
          className={`flex items-center gap-1 rounded-full px-2.5 py-1 text-xs font-semibold uppercase tracking-wide ${config.badgeClass}`}
        >
          {config.dotClass ? (
            <span className={`h-1.5 w-1.5 rounded-full ${config.dotClass}`} />
          ) : null}
          {config.label}
        </span>
      </div>

      {status === "PENDING" ? (
        <div className="relative z-10 mb-4 rounded-lg bg-[#eff4ff] p-3">
          <div className="flex items-start gap-3">
            <Hourglass className="mt-0.5 h-5 w-5 shrink-0 text-amber-600" />
            <div>
              <p className="mb-1 text-xs font-semibold uppercase tracking-wide text-[#0b1c30]">
                Waiting for Super Admin Approval
              </p>
              <p className="text-[11px] leading-relaxed text-[#3c4947]">
                Your organization cannot create plans or checkout links until it is approved.
              </p>
            </div>
          </div>
        </div>
      ) : null}

      {status === "REJECTED" ? (
        <div className="relative z-10 mb-4 flex-1">
          <div className="flex items-start gap-3 rounded-lg border border-[#ba1a1a]/10 bg-[#ffdad6]/50 p-3">
            <AlertCircle className="h-5 w-5 shrink-0 text-[#ba1a1a]" />
            <p className="text-sm text-[#93000a]">
              {organization.rejectionReason ||
                "Features are unavailable. Organization profile failed verification."}
            </p>
          </div>
        </div>
      ) : null}

      {status === "SUSPENDED" ? (
        <div className="relative z-10 mb-4 rounded-lg bg-purple-50 p-3">
          <p className="text-sm font-medium text-purple-900">Account Suspended</p>
          <p className="text-[11px] text-purple-700">
            Checkout links are inactive. Contact the system administrator to resolve compliance issues.
          </p>
        </div>
      ) : null}

      {status === "EXPIRED" ? (
        <div className="relative z-10 mb-4 flex-1">
          <div className="rounded-lg border border-dashed border-[#bbcac6] p-3 text-center">
            <p className="mb-2 text-sm text-[#3c4947]">Organization Expired</p>
            <button
              type="button"
              className="rounded-lg bg-[#14b8a6] px-4 py-1.5 text-xs font-semibold text-[#00423b]"
            >
              Renew Subscription
            </button>
          </div>
        </div>
      ) : null}

      <div className="relative z-10 mb-6 space-y-2">
        <div className="flex items-center text-sm text-[#3c4947]">
          <Mail className="mr-2 h-4 w-4" />
          {organization.contactEmail || "No contact email"}
        </div>
        <div className="flex items-center text-sm text-[#3c4947]">
          <Calendar className="mr-2 h-4 w-4" />
          Created {formatDate(organization.createdAt)}
        </div>
      </div>

      <OrganizationAction
        status={status}
        onOpenDashboard={() => onOpenDashboard?.(organization)}
      />
    </article>
  );
}

function OrganizationAction({ status, onOpenDashboard }) {
  if (status === "APPROVED") {
    return (
      <button
        type="button"
        onClick={onOpenDashboard}
        className="flex w-full items-center justify-center rounded-lg bg-[#006b5f] py-2.5 text-sm font-medium text-white transition-colors hover:bg-[#006b5f]/90"
      >
        Open Dashboard
        <ArrowRight className="ml-2 h-4 w-4" />
      </button>
    );
  }

  if (status === "PENDING") {
    return (
      <button
        type="button"
        disabled
        className="w-full cursor-not-allowed rounded-lg border border-[#bbcac6] bg-white py-2.5 text-sm font-medium text-[#3c4947]/60 opacity-60"
      >
        Awaiting Verification
      </button>
    );
  }

  if (status === "REJECTED") {
    return (
      <button
        type="button"
        className="relative z-10 flex w-full items-center justify-center rounded-lg border border-[#ba1a1a] py-2.5 text-sm font-medium text-[#ba1a1a] transition-colors hover:bg-[#ba1a1a]/5"
      >
        Contact Support
      </button>
    );
  }

  if (status === "SUSPENDED") {
    return (
      <button
        type="button"
        className="w-full rounded-lg bg-[#d3e4fe] py-2.5 text-sm font-medium text-[#0b1c30] transition-colors hover:bg-[#dce9ff]"
      >
        Review Case Files
      </button>
    );
  }

  return (
    <button
      type="button"
      className="text-sm font-semibold text-[#006b5f] underline underline-offset-4"
    >
      Details
    </button>
  );
}

export default OrganizationCard;

function OrganizationCardSkeleton() {
  return (
    <article className="flex flex-col rounded-xl border border-[#bbcac6] bg-white p-4">
      <div className="mb-4 flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div className="h-12 w-12 animate-pulse rounded-lg bg-slate-200" />
          <div className="space-y-2">
            <div className="h-4 w-28 animate-pulse rounded bg-slate-200" />
            <div className="h-3 w-16 animate-pulse rounded bg-slate-200/70" />
          </div>
        </div>
        <div className="h-6 w-20 animate-pulse rounded-full bg-slate-200" />
      </div>
      <div className="mb-6 space-y-3">
        <div className="h-3 w-3/4 animate-pulse rounded bg-slate-200" />
        <div className="h-3 w-1/2 animate-pulse rounded bg-slate-200" />
      </div>
      <div className="h-10 w-full animate-pulse rounded-lg bg-slate-200" />
    </article>
  );
}

export { OrganizationCardSkeleton };
