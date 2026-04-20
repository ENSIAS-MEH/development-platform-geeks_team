import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { Label } from "../components/ui/label";
import { Input } from "../components/ui/input";
import { Button } from "../components/ui/button";
import { userService } from "../services/userService";
import { ArrowLeft, User, Globe, Link2 } from "lucide-react";

export function EditProfilePage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    headline: "",
    bio: "",
    email: "",
    location: "",
    avatarUrl: "",
    githubUrl: "",
    linkedinUrl: "",
    portfolioUrl: "",
    websiteUrl: "",
  });

  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { id, value } = e.target;
    setForm((prev) => ({ ...prev, [id]: value }));
  };
  useEffect(() => {
  const loadProfile = async () => {
    try {
      const data = await userService.getMyProfile();

      setForm({
        name: data.name || "",
        headline: data.headline || "",
        bio: data.bio || "",
        email: data.email || "",
        location: data.location || "",
        avatarUrl: data.avatarUrl || "",
        githubUrl: data.githubUrl || "",
        linkedinUrl: data.linkedinUrl || "",
        portfolioUrl: data.portfolioUrl || "",
        websiteUrl: data.websiteUrl || "",
      });

    } catch (e) {
      console.error("Failed to load profile", e);
    }
  };

  loadProfile();
}, []);
  const handleSave = async () => {
    setLoading(true);
    setSuccess(false);
    try {
      await userService.updateMyProfile(form);
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (e) {
      console.error(e);
      alert("Error saving profile changes");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-8 max-w-3xl">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <button
          onClick={() => navigate(-1)}
          className="p-2 rounded-lg hover:bg-[#F0F4F8] transition-colors text-[#717182]"
        >
          <ArrowLeft size={20} />
        </button>
        <h1 className="text-4xl font-bold text-[#1D2233]">Edit Profile</h1>
      </div>

      <div className="space-y-6">
        {/* Basic Info */}
        <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
          <div className="flex items-center gap-2 mb-5">
            <User size={18} className="text-[#56B2BB]" />
            <h3 className="font-bold text-[#1D2233]">Basic Information</h3>
          </div>
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="name">Full Name</Label>
                <Input
                  id="name"
                  value={form.name}
                  onChange={handleChange}
                  placeholder="John Doe"
                  className="mt-1.5 bg-[#F0F4F8]"
                />
              </div>
              <div>
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  value={form.email}
                  onChange={handleChange}
                  placeholder="john@example.com"
                  className="mt-1.5 bg-[#F0F4F8]"
                />
              </div>
            </div>

            <div>
              <Label htmlFor="headline">Headline</Label>
              <Input
                id="headline"
                value={form.headline}
                onChange={handleChange}
                placeholder="e.g. Full-Stack Developer · Open to work"
                maxLength={160}
                className="mt-1.5 bg-[#F0F4F8]"
              />
              <p className="text-xs text-[#717182] mt-1">{form.headline.length}/160</p>
            </div>

            <div>
              <Label htmlFor="bio">Bio</Label>
              <textarea
                id="bio"
                value={form.bio}
                onChange={handleChange}
                placeholder="Tell us a bit about yourself..."
                maxLength={500}
                rows={4}
                className="mt-1.5 w-full rounded-md border border-input bg-[#F0F4F8] px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 resize-none"
              />
              <p className="text-xs text-[#717182] mt-1">{form.bio.length}/500</p>
            </div>

            <div>
              <Label htmlFor="location">Location</Label>
              <Input
                id="location"
                value={form.location}
                onChange={handleChange}
                placeholder="e.g. San Francisco, CA"
                className="mt-1.5 bg-[#F0F4F8]"
              />
            </div>

            <div>
              <Label htmlFor="avatarUrl">Avatar URL</Label>
              <Input
                id="avatarUrl"
                type="url"
                value={form.avatarUrl}
                onChange={handleChange}
                placeholder="https://..."
                className="mt-1.5 bg-[#F0F4F8]"
              />
            </div>
          </div>
        </div>

        {/* Social Links */}
        <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
          <div className="flex items-center gap-2 mb-5">
            <Link2 size={18} className="text-[#56B2BB]" />
            <h3 className="font-bold text-[#1D2233]">Links & Social</h3>
          </div>
          <div className="space-y-4">
            {[
              { id: "githubUrl", label: "GitHub URL", placeholder: "https://github.com/username" },
              { id: "linkedinUrl", label: "LinkedIn URL", placeholder: "https://linkedin.com/in/username" },
              { id: "portfolioUrl", label: "Portfolio URL", placeholder: "https://myportfolio.com" },
              { id: "websiteUrl", label: "Website URL", placeholder: "https://mywebsite.com" },
            ].map(({ id, label, placeholder }) => (
              <div key={id}>
                <Label htmlFor={id}>{label}</Label>
                <div className="flex mt-1.5">
                  <span className="inline-flex items-center px-3 rounded-l-md border border-r-0 border-input bg-[#E8EEF2] text-[#717182]">
                    <Globe size={14} />
                  </span>
                  <Input
                    id={id}
                    type="url"
                    value={form[id as keyof typeof form]}
                    onChange={handleChange}
                    placeholder={placeholder}
                    className="rounded-l-none bg-[#F0F4F8]"
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center gap-3">
          <Button
            onClick={handleSave}
            disabled={loading}
            className="bg-[#56B2BB] hover:bg-[#56B2BB]/90"
          >
            {loading ? "Saving..." : "Save Changes"}
          </Button>
          <Button variant="outline" onClick={() => navigate(-1)}>
            Cancel
          </Button>
          {success && (
            <span className="text-sm text-green-600 font-medium">
              ✓ Profile updated successfully
            </span>
          )}
        </div>
      </div>
    </div>
  );
}