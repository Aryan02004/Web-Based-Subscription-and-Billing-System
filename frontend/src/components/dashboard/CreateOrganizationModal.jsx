import { useState } from "react";
import { Building2, X } from "lucide-react";

const INDUSTRY_OPTIONS = [
  "Technology",
  "Retail",
  "Finance",
  "Cloud Computing",
  "Manufacturing",
  "Healthcare",
  "Education",
  "Logistics",
];

function CreateOrganizationModal({ open, onClose, onCreated }) {
  const [formData, setFormData] = useState({
    name: "",
    industry: "",
    contactEmail: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  if (!open) return null;

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      await onCreated({
        name: formData.name.trim(),
        industry: formData.industry || null,
        contactEmail: formData.contactEmail.trim() || null,
      });

      setFormData({ name: "", industry: "", contactEmail: "" });
      onClose();
    } catch (requestError) {
      setError(requestError.message || "Unable to create organization.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-100 flex items-center justify-center bg-[#0b1c30]/40 px-4 backdrop-blur-sm">
      <div
        className="w-full max-w-lg rounded-2xl border border-[#bbcac6] bg-white p-6 shadow-2xl"
        role="dialog"
        aria-modal="true"
        aria-labelledby="create-org-title"
      >
        <div className="mb-6 flex items-start justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#006b5f]">
              New Organization
            </p>
            <h2 id="create-org-title" className="mt-1 text-2xl font-bold text-[#0b1c30]">
              Create Organization
            </h2>
            <p className="mt-2 text-sm text-[#3c4947]">
              New organizations start as pending until a Super Admin approves them.
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg p-2 text-[#3c4947] transition-colors hover:bg-[#eff4ff]"
            aria-label="Close"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <form className="space-y-4" onSubmit={handleSubmit}>
          <label className="block space-y-2">
            <span className="text-sm font-semibold text-[#0b1c30]">Organization name</span>
            <div className="relative">
              <Building2 className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[#3c4947]" />
              <input
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
                placeholder="Acme Corp"
                className="w-full rounded-xl border border-[#bbcac6] bg-[#f8f9ff] py-3 pl-10 pr-4 text-sm outline-none transition focus:border-[#006b5f] focus:bg-white focus:ring-2 focus:ring-[#006b5f]/10"
              />
            </div>
          </label>

          <label className="block space-y-2">
            <span className="text-sm font-semibold text-[#0b1c30]">Industry</span>
            <select
              name="industry"
              value={formData.industry}
              onChange={handleChange}
              className="w-full rounded-xl border border-[#bbcac6] bg-[#f8f9ff] px-4 py-3 text-sm outline-none transition focus:border-[#006b5f] focus:bg-white focus:ring-2 focus:ring-[#006b5f]/10"
            >
              <option value="">Select industry</option>
              {INDUSTRY_OPTIONS.map((option) => (
                <option key={option} value={option}>
                  {option}
                </option>
              ))}
            </select>
          </label>

          <label className="block space-y-2">
            <span className="text-sm font-semibold text-[#0b1c30]">Contact email</span>
            <input
              name="contactEmail"
              type="email"
              value={formData.contactEmail}
              onChange={handleChange}
              placeholder="billing@company.com"
              className="w-full rounded-xl border border-[#bbcac6] bg-[#f8f9ff] px-4 py-3 text-sm outline-none transition focus:border-[#006b5f] focus:bg-white focus:ring-2 focus:ring-[#006b5f]/10"
            />
          </label>

          {error ? (
            <p className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </p>
          ) : null}

          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 rounded-xl border border-[#bbcac6] px-4 py-3 text-sm font-semibold text-[#3c4947] transition hover:bg-[#eff4ff]"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 rounded-xl bg-[#14b8a6] px-4 py-3 text-sm font-semibold text-[#00423b] transition hover:bg-[#006b5f] hover:text-white disabled:cursor-not-allowed disabled:opacity-70"
            >
              {loading ? "Creating..." : "Create Organization"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CreateOrganizationModal;
