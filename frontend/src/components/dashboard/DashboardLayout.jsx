import {
  LayoutDashboard,
  CreditCard,
  Users,
  Link2,
  BarChart3,
  Settings,
  LogOut,
  Bell,
  Search,
} from "lucide-react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { api } from "../../lib/api";
import { clearAuthSession, getStoredUser } from "../../lib/api/client";

const navItems = [
  { label: "Dashboard", icon: LayoutDashboard, href: "/dashboard/organizations", disabled: true },
  { label: "Subscription Plans", icon: CreditCard, href: "#", disabled: true },
  { label: "Organizations", icon: Users, href: "/dashboard/organizations", activeMatch: "/dashboard/organizations" },
  { label: "Checkout Links", icon: Link2, href: "#", disabled: true },
  { label: "Reports", icon: BarChart3, href: "#", disabled: true },
  { label: "Settings", icon: Settings, href: "#", disabled: true },
];

function DashboardLayout({ children, searchQuery, onSearchChange, searchPlaceholder = "Search organizations..." }) {
  const navigate = useNavigate();
  const location = useLocation();
  const user = getStoredUser();

  const displayName = [user?.firstName, user?.lastName].filter(Boolean).join(" ") || user?.email || "User";
  const roleLabel = (user?.role || "ORGANIZATION_ADMIN").replace(/_/g, " ");

  const handleLogout = async () => {
    try {
      await api.auth.logout();
    } catch {
      clearAuthSession();
    }

    navigate("/login", { replace: true });
  };

  return (
    <div className="min-h-screen overflow-hidden bg-[#f8f9ff] text-[#0b1c30]">
      <aside className="fixed left-0 top-0 z-50 flex h-screen w-65 flex-col border-r border-[#bbcac6] bg-white py-4 shadow-sm">
        <div className="mb-8 px-6">
          <h1 className="text-xl font-bold text-[#006b5f]">Subscriptor</h1>
          <p className="text-sm text-[#3c4947]/70">Enterprise SaaS</p>
        </div>

        <nav className="flex-1 space-y-1 overflow-y-auto px-4">
          {navItems.map(({ label, icon: Icon, href, activeMatch, disabled }) => {
            const isActive = activeMatch
              ? location.pathname.startsWith(activeMatch)
              : location.pathname === href;

            if (disabled) {
              return (
                <span
                  key={label}
                  className="flex cursor-not-allowed items-center rounded-lg px-4 py-3 text-sm text-[#3c4947]/50"
                  title="Coming soon"
                >
                  <Icon className="mr-3 h-5 w-5" />
                  {label}
                </span>
              );
            }

            return (
              <Link
                key={label}
                to={href}
                className={`relative flex items-center rounded-lg px-4 py-3 text-sm transition-colors ${
                  isActive
                    ? "bg-[#e5eeff] font-bold text-[#006b5f] before:absolute before:left-0 before:h-2/3 before:w-1 before:rounded-r-full before:bg-[#006b5f]"
                    : "text-[#3c4947] hover:bg-[#eff4ff]"
                }`}
              >
                <Icon className={`mr-3 h-5 w-5 ${isActive ? "text-[#006b5f]" : ""}`} />
                {label}
              </Link>
            );
          })}
        </nav>

        <div className="mt-auto border-t border-[#bbcac6] px-4 pt-4">
          <button
            type="button"
            onClick={handleLogout}
            className="flex w-full items-center rounded-lg px-4 py-3 text-sm text-[#3c4947] transition-colors hover:bg-[#ffdad6] hover:text-[#ba1a1a]"
          >
            <LogOut className="mr-3 h-5 w-5" />
            Logout
          </button>
        </div>
      </aside>

      <header className="fixed right-0 top-0 z-40 flex h-16 w-[calc(100%-theme(spacing.65))] items-center justify-between border-b border-[#bbcac6] bg-[#f8f9ff] px-6">
        <div className="relative max-w-md flex-1">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-[#3c4947]" />
          <input
            type="text"
            value={searchQuery}
            onChange={(event) => onSearchChange?.(event.target.value)}
            placeholder={searchPlaceholder}
            className="w-full rounded-lg border border-[#bbcac6] bg-white py-2 pl-10 pr-4 text-sm outline-none transition focus:border-[#006b5f] focus:ring-2 focus:ring-[#006b5f]/10"
          />
        </div>

        <div className="ml-6 flex items-center gap-4">
          <button
            type="button"
            className="relative rounded-full p-2 transition-colors hover:bg-[#eff4ff]"
            aria-label="Notifications"
          >
            <Bell className="h-5 w-5 text-[#3c4947]" />
            <span className="absolute right-2 top-2 h-2 w-2 rounded-full border-2 border-[#f8f9ff] bg-[#ba1a1a]" />
          </button>

          <div className="flex items-center gap-3 border-l border-[#bbcac6] pl-4">
            <div className="text-right">
              <p className="text-sm font-medium text-[#0b1c30]">{displayName}</p>
              <p className="text-[10px] font-medium uppercase tracking-wider text-[#3c4947]">
                {roleLabel}
              </p>
            </div>
            <div className="flex h-8 w-8 items-center justify-center rounded-full border border-[#bbcac6] bg-[#dce9ff] text-xs font-bold text-[#006b5f]">
              {displayName.charAt(0).toUpperCase()}
            </div>
          </div>
        </div>
      </header>

      <main className="ml-65 h-screen overflow-y-auto bg-[#f8f9ff] pt-16">
        {children}
      </main>
    </div>
  );
}

export default DashboardLayout;
