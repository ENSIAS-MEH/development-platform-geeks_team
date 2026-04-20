import { Tabs, TabsContent, TabsList, TabsTrigger } from "../components/ui/tabs";
import { Label } from "../components/ui/label";
import { Input } from "../components/ui/input";
import { Button } from "../components/ui/button";
import { Switch } from "../components/ui/switch";
import { useEffect, useState } from "react";
import { userService } from "../services/userService";

export function SettingsPage() {

  const [form, setForm] = useState({
    currentPassword: "",
    password: "",
    confirmedPassword: "",
  });

  const [showEmail, setShowEmail] = useState(false)
  const [authProvider, setAuthProvider] = useState('local')
  const [user, setUser] = useState<any>(null)


  useEffect(() => {
    const loadUser = async () =>{
      try{
        const data = await userService.getMyProfile();
        setUser(data)
        setShowEmail(data.showEmail || false)
        setAuthProvider(data.authProvider || 'local')
      } catch (e) {
      console.error('Failed to load profile', e)
    }
  }
  loadUser()
}, [])
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    setForm((prev) => ({ ...prev, [id]: value }));
  };

  const handleChangePassword = async () => {
    if (form.password !== form.confirmedPassword) {
      alert("New passwords do not match");
      return;
    }
    try {
      await userService.changePassword({
        currentPassword: form.currentPassword,
        newPassword: form.password,
        confirmPassword: form.confirmedPassword,
      });
      alert("Password updated successfully");
      setForm({ currentPassword: "", password: "", confirmedPassword: "" });
    } catch (e) {
      console.error(e);
      alert("Error changing password");
    }
  };

  return (
    <div className="p-8">
      <h1 className="text-4xl font-bold text-[#1D2233] mb-8">Settings</h1>

      <Tabs defaultValue="account" className="w-full">
        <TabsList className="mb-6">
          <TabsTrigger value="account">Account</TabsTrigger>
          <TabsTrigger value="notifications">Notifications</TabsTrigger>
          <TabsTrigger value="privacy">Privacy</TabsTrigger>
          <TabsTrigger value="connected">Connected Accounts</TabsTrigger>
        </TabsList>

        {/* ─── Account Tab ─── */}
        <TabsContent value="account">
          <div className="space-y-6 max-w-2xl">


          {authProvider === 'local' ? (
            
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-6">Change Password</h3>
              <div className="space-y-4">
                <div>
                  <Label htmlFor="currentPassword">Current Password</Label>
                  <Input
                    id="currentPassword"
                    type="password"
                    value={form.currentPassword}
                    onChange={handleChange}
                    placeholder="••••••••"
                    className="mt-1.5 bg-[#F0F4F8]"
                  />
                </div>
                <div>
                  <Label htmlFor="password">New Password</Label>
                  <Input
                    id="password"
                    type="password"
                    value={form.password}
                    onChange={handleChange}
                    placeholder="••••••••"
                    className="mt-1.5 bg-[#F0F4F8]"
                  />
                </div>
                <div>
                  <Label htmlFor="confirmedPassword">Confirm New Password</Label>
                  <Input
                    id="confirmedPassword"
                    type="password"
                    value={form.confirmedPassword}
                    onChange={handleChange}
                    placeholder="••••••••"
                    className="mt-1.5 bg-[#F0F4F8]"
                  />
                </div>
                <Button
                  onClick={handleChangePassword}
                  className="bg-[#56B2BB] hover:bg-[#56B2BB]/90"
                >
                  Change Password
                </Button>
              </div>
            </div> ) : 
            (
            <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30">
              <h3 className="font-bold text-[#1D2233] mb-6">Password</h3>
              <p className="text-sm text-[#717182]">
                You signed in with {authProvider === 'github' ? 'GitHub' : 'Google'}.
                Password management is handled by your connected account.
              </p>
            </div>
            ) } 

        </div>  
        </TabsContent>

        {/* ─── Notifications Tab ─── */}
        <TabsContent value="notifications">
          <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 max-w-2xl">
            <h3 className="font-bold text-[#1D2233] mb-6">Notification Preferences</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium text-[#1D2233]">Email Notifications</p>
                  <p className="text-sm text-[#717182]">Receive event and project updates via email</p>
                </div>
                <Switch defaultChecked />
              </div>
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium text-[#1D2233]">Team Messages</p>
                  <p className="text-sm text-[#717182]">Get notified when someone messages your team</p>
                </div>
                <Switch defaultChecked />
              </div>
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium text-[#1D2233]">Event Reminders</p>
                  <p className="text-sm text-[#717182]">Reminders for upcoming events</p>
                </div>
                <Switch defaultChecked />
              </div>
            </div>
          </div>
        </TabsContent>
        {/* ─── Privacy Tab ─── */}
    <TabsContent value="privacy">
      <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 max-w-2xl">
        <h3 className="font-bold text-[#1D2233] mb-6">Privacy Settings</h3>
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="font-medium text-[#1D2233]">Show Email</p>
              <p className="text-sm text-[#717182]">
                Display your email on your public profile
              </p>
            </div>
            <Switch
              checked={showEmail}
              onCheckedChange={async (checked) => {
                setShowEmail(checked)
                try {
                  await userService.updatePrivacy({ showEmail: checked })
                } catch (e) {
                  // revert on failure
                  setShowEmail(!checked)
                }
              }}
            />
          </div>
        </div>
      </div>
    </TabsContent>

    {/* ─── Connected Accounts Tab ─── */}
    <TabsContent value="connected">
      <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 max-w-2xl">
        <h3 className="font-bold text-[#1D2233] mb-6">Connected Accounts</h3>
        <div className="space-y-4">

          {/* GitHub */}
          <div className="flex items-center justify-between p-4 border border-[#BAC7CC]/30 rounded-lg">
            <div>
              <p className="font-medium text-[#1D2233]">GitHub</p>
              <p className="text-sm text-[#717182]">
                {authProvider === 'github'
                  ? `Connected as ${user?.name}`
                  : 'Not connected'}
              </p>
            </div>
            {authProvider === 'github'
              ? <span className="text-sm text-green-600 font-medium">Connected</span>
              : <Button
                  className="bg-[#56B2BB] hover:bg-[#56B2BB]/90"
                  onClick={() => window.location.href = 'http://localhost:8080/oauth2/authorization/github'}
                >
                  Connect
                </Button>
            }
          </div>

          {/* Google */}
          <div className="flex items-center justify-between p-4 border border-[#BAC7CC]/30 rounded-lg">
            <div>
              <p className="font-medium text-[#1D2233]">Google</p>
              <p className="text-sm text-[#717182]">
                {authProvider === 'google'
                  ? `Connected as ${user?.email}`
                  : 'Not connected'}
              </p>
            </div>
            {authProvider === 'google'
              ? <span className="text-sm text-green-600 font-medium">Connected</span>
              : <Button
                  className="bg-[#56B2BB] hover:bg-[#56B2BB]/90"
                  onClick={() => window.location.href = 'http://localhost:8080/oauth2/authorization/google'}
                >
                  Connect
                </Button>
            }
          </div>

        </div>
      </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}

