import { useCallback, useEffect, useMemo, useState } from "react";import { ChevronRight, Filter, Building2, CheckCircle2 } from "lucide-react";
import DashboardLayout from "../components/dashboard/DashboardLayout";
import OrganizationCard, {
  OrganizationCardSkeleton,
} from "../components/dashboard/OrganizationCard";
import CreateOrganizationModal from "../components/dashboard/CreateOrganizationModal";
import { api } from "../lib/api";

const STATUS_OPTIONS = [
  { value: "all", label: "All Statuses" },
  { value: "APPROVED", label: "Approved" },
  { value: "PENDING", label: "Pending" },
  { value: "REJECTED", label: "Rejected" },
  { value: "SUSPENDED", label: "Suspended" },
];

const SORT_OPTIONS = [
  { value: "newest", label: "Sort: Newest First" },
  { value: "oldest", label: "Sort: Oldest First" },
  { value: "alphabetical", label: "Sort: A-Z" },
];

function OrgAdminDashboard() {
  const [organizations, setOrganizations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [toast, setToast] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [industryFilter, setIndustryFilter] = useState("all");
  const [sortBy, setSortBy] = useState("newest");
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  const loadOrganizations = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.organization.getMyOrganizations();
      setOrganizations(Array.isArray(response) ? response : []);
    } catch (requestError) {
      setError(requestError.message || "Unable to load organizations.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let cancelled = false;

    async function fetchOrganizations() {
      try {
        const response = await api.organization.getMyOrganizations();
        if (!cancelled) {
          setOrganizations(Array.isArray(response) ? response : []);
          setError("");
        }
      } catch (requestError) {
        if (!cancelled) {
          setError(requestError.message || "Unable to load organizations.");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    void fetchOrganizations();

    return () => {
      cancelled = true;
    };
  }, []);
  useEffect(() => {
    if (!toast) return undefined;

    const timer = setTimeout(() => setToast(""), 3000);
    return () => clearTimeout(timer);
  }, [toast]);

  const industryOptions = useMemo(() => {
    const values = organizations
      .map((organization) => organization.industry)
      .filter(Boolean);

    return ["all", ...new Set(values)];
  }, [organizations]);

  const filteredOrganizations = useMemo(() => {
    let result = [...organizations];

    if (searchQuery.trim()) {
      const query = searchQuery.trim().toLowerCase();
      result = result.filter((organization) => {
        const haystack = [
          organization.organizationName,
          organization.contactEmail,
          organization.industry,
        ]
          .filter(Boolean)
          .join(" ")
          .toLowerCase();

        return haystack.includes(query);
      });
    }

    if (statusFilter !== "all") {
      result = result.filter((organization) => organization.status === statusFilter);
    }

    if (industryFilter !== "all") {
      result = result.filter((organization) => organization.industry === industryFilter);
    }

    result.sort((left, right) => {
      if (sortBy === "alphabetical") {
        return (left.organizationName || "").localeCompare(right.organizationName || "");
      }

      const leftTime = new Date(left.createdAt || 0).getTime();
      const rightTime = new Date(right.createdAt || 0).getTime();

      return sortBy === "oldest" ? leftTime - rightTime : rightTime - leftTime;
    });

    return result;
  }, [organizations, searchQuery, statusFilter, industryFilter, sortBy]);

  const handleCreateOrganization = async (payload) => {
    await api.organization.create(payload);
    setToast("Organization created successfully. Awaiting Super Admin approval.");
    await loadOrganizations();
  };

  const handleOpenDashboard = (organization) => {
    setToast(`${organization.organizationName} workspace will be available after the next sprint.`);
  };

  return (
    <DashboardLayout
      searchQuery={searchQuery}
      onSearchChange={setSearchQuery}
      searchPlaceholder="Search organizations..."
    >
      <div className="mx-auto max-w-7xl px-6 py-8">
        <div className="mb-8 flex flex-col justify-between gap-6 md:flex-row md:items-end">
          <div className="space-y-1">
            <nav className="mb-2 flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-[#3c4947]">
              <span>Organizations</span>
              <ChevronRight className="h-3.5 w-3.5" />
              <span className="text-[#006b5f]">My Organizations</span>
            </nav>
            <h2 className="text-3xl font-semibold tracking-tight text-[#0b1c30]">
              Organizations
            </h2>
            <p className="text-base text-[#3c4947]">
              Manage your organizations and monitor their current approval status.
            </p>
          </div>

          <button
            type="button"
            onClick={() => setIsCreateModalOpen(true)}
            className="inline-flex items-center rounded-lg bg-[#14b8a6] px-6 py-2.5 text-sm font-medium text-[#00423b] shadow-md transition hover:bg-[#006b5f] hover:text-white active:scale-95"
          >
            <Building2 className="mr-2 h-5 w-5" />
            Create Organization
          </button>
        </div>

        <div className="mb-8 flex flex-wrap items-center gap-4 rounded-xl border border-[#bbcac6] bg-white p-4 shadow-[0px_1px_3px_0px_rgba(0,0,0,0.05)]">
          <div className="mr-2 flex items-center gap-2 border-r border-[#bbcac6] pr-4">
            <Filter className="h-5 w-5 text-[#3c4947]" />
            <span className="text-xs font-semibold uppercase tracking-wide text-[#3c4947]">
              Filters
            </span>
          </div>

          <select
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value)}
            className="rounded-lg bg-[#eff4ff] px-4 py-2 text-sm text-[#0b1c30] outline-none focus:ring-2 focus:ring-[#006b5f]/20"
          >
            {STATUS_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>

          <select
            value={industryFilter}
            onChange={(event) => setIndustryFilter(event.target.value)}
            className="rounded-lg bg-[#eff4ff] px-4 py-2 text-sm text-[#0b1c30] outline-none focus:ring-2 focus:ring-[#006b5f]/20"
          >
            <option value="all">All Industries</option>
            {industryOptions
              .filter((option) => option !== "all")
              .map((option) => (
                <option key={option} value={option}>
                  {option}
                </option>
              ))}
          </select>

          <select
            value={sortBy}
            onChange={(event) => setSortBy(event.target.value)}
            className="ml-auto rounded-lg bg-[#eff4ff] px-4 py-2 text-sm text-[#0b1c30] outline-none focus:ring-2 focus:ring-[#006b5f]/20"
          >
            {SORT_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>

        {error ? (
          <p className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </p>
        ) : null}

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2 xl:grid-cols-3">
          {loading
            ? Array.from({ length: 3 }).map((_, index) => (
                <OrganizationCardSkeleton key={index} />
              ))
            : null}

          {!loading && filteredOrganizations.length === 0 ? (
            <div className="col-span-full rounded-2xl border border-dashed border-[#bbcac6] bg-white px-6 py-16 text-center">
              <Building2 className="mx-auto mb-4 h-10 w-10 text-[#006b5f]" />
              <h3 className="text-xl font-semibold text-[#0b1c30]">No organizations yet</h3>
              <p className="mx-auto mt-2 max-w-md text-sm text-[#3c4947]">
                Create your first organization to start the Super Admin approval workflow.
              </p>
              <button
                type="button"
                onClick={() => setIsCreateModalOpen(true)}
                className="mt-6 inline-flex items-center rounded-lg bg-[#006b5f] px-5 py-3 text-sm font-semibold text-white transition hover:bg-[#006b5f]/90"
              >
                Create Organization
              </button>
            </div>
          ) : null}

          {!loading
            ? filteredOrganizations.map((organization) => (
                <OrganizationCard
                  key={organization.organizationId}
                  organization={organization}
                  onOpenDashboard={handleOpenDashboard}
                />
              ))
            : null}
        </div>

        {!loading && filteredOrganizations.length > 0 ? (
          <div className="mt-8 flex items-center justify-between border-t border-[#bbcac6] pt-6">
            <p className="text-sm text-[#3c4947]">
              Showing {filteredOrganizations.length} of {organizations.length} organizations
            </p>
          </div>
        ) : null}
      </div>

      <CreateOrganizationModal
        open={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onCreated={handleCreateOrganization}
      />

      <div
        className={`fixed bottom-6 right-6 z-110 flex items-center rounded-lg border border-white/10 bg-[#213145] px-6 py-3 text-white shadow-xl transition-all duration-300 ${
          toast ? "translate-y-0 opacity-100" : "translate-y-20 opacity-0"
        }`}
      >
        <CheckCircle2 className="mr-3 h-5 w-5 text-emerald-400" />
        <span className="text-sm">{toast}</span>
      </div>
    </DashboardLayout>
  );
}

export default OrgAdminDashboard;
