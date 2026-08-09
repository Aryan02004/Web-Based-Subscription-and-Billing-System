import {
  LayoutDashboard,
  LogOut,
  Bell,
  Search,
  Sparkles,
} from "lucide-react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { api } from "../../lib/api";
import { clearAuthSession, getStoredUser } from "../../lib/api/client";

const defaultNavItems = [
  { label: "Dashboard", icon: LayoutDashboard, href: "/dashboard/organizations", activeMatch: "/dashboard/organizations" },
];

function DashboardLayout({
  children,
  searchQuery = "",
  onSearchChange,
  searchPlaceholder = "Search organizations...",
  navItems = defaultNavItems,
}) {
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
    <div className="min-h-screen overflow-hidden bg-[#f8f9ff] text-slate-900">
      <aside className="fixed left-0 top-0 z-50 flex h-screen w-65 flex-col border-r border-slate-200 bg-white/90 py-4 shadow-sm backdrop-blur-xl">
        <div className="mb-8 px-6">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-cyan-600 text-white shadow-lg shadow-cyan-600/20">
              <Sparkles className="h-5 w-5" />
            </div>
            <div>
              <h1 className="text-lg font-bold text-cyan-700">Subscriptor</h1>
              <p className="text-xs text-slate-500">India billing</p>
            </div>
          </div>
        </div>

        <nav className="flex-1 space-y-1 overflow-y-auto px-4">
          {navItems.map(({ label, icon: Icon, href, activeMatch }) => {
            const isActive = activeMatch
              ? location.pathname.startsWith(activeMatch)
              : location.pathname === href;

            return (
              <Link
                key={label}
                to={href}
                className={`relative flex items-center rounded-xl px-4 py-3 text-sm transition-all ${
                  isActive
                    ? "bg-cyan-50 font-semibold text-cyan-700 shadow-sm before:absolute before:left-0 before:h-2/3 before:w-1 before:rounded-r-full before:bg-cyan-600"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`}
              >
                <Icon className={`mr-3 h-5 w-5 ${isActive ? "text-cyan-600" : ""}`} />
                {label}
              </Link>
            );
          })}
        </nav>

        <div className="mt-auto border-t border-slate-200 px-4 pt-4">
          <button
            type="button"
            onClick={handleLogout}
            className="flex w-full items-center rounded-xl px-4 py-3 text-sm text-slate-600 transition-colors hover:bg-red-50 hover:text-red-600"
          >
            <LogOut className="mr-3 h-5 w-5" />
            Logout
          </button>
        </div>
      </aside>

      <header className="fixed right-0 top-0 z-40 flex h-16 w-[calc(100%-(--spacing(65)))] items-center justify-between border-b border-slate-200 bg-white/80 px-6 backdrop-blur-xl">
        <div className="relative max-w-md flex-1">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(event) => onSearchChange?.(event.target.value)}
            placeholder={searchPlaceholder}
            className="w-full rounded-xl border border-slate-200 bg-slate-50 py-2 pl-10 pr-4 text-sm outline-none transition focus:border-cyan-500 focus:bg-white focus:ring-2 focus:ring-cyan-100"
          />
        </div>

        <div className="ml-6 flex items-center gap-4">


          <div className="flex items-center gap-3 border-l border-slate-200 pl-4">
            <div className="text-right">
              <p className="text-sm font-medium text-slate-900">{displayName}</p>
              <p className="text-[10px] font-medium uppercase tracking-wider text-slate-500">
                {roleLabel}
              </p>
            </div>
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-cyan-600 text-xs font-bold text-white">
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
