import { Tabs, TabsContent, TabsList, TabsTrigger } from "../components/ui/tabs";
import { Label } from "../components/ui/label";
import { Input } from "../components/ui/input";
import { Button } from "../components/ui/button";
import { Switch } from "../components/ui/switch";

export function SettingsPage() {
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

        <TabsContent value="account">
          <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 max-w-2xl">
            <h3 className="font-bold text-[#1D2233] mb-6">Account Information</h3>
            <div className="space-y-4">
              <div>
                <Label htmlFor="name">Full Name</Label>
                <Input id="name" defaultValue="Alex Davis" className="mt-1.5 bg-[#F0F4F8]" />
              </div>
              <div>
                <Label htmlFor="email">Email</Label>
                <Input id="email" type="email" defaultValue="alex@example.com" className="mt-1.5 bg-[#F0F4F8]" />
              </div>
              <div>
                <Label htmlFor="password">New Password</Label>
                <Input id="password" type="password" placeholder="••••••••" className="mt-1.5 bg-[#F0F4F8]" />
              </div>
              <Button className="bg-[#56B2BB] hover:bg-[#56B2BB]/90">Save Changes</Button>
            </div>
          </div>
        </TabsContent>

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

        <TabsContent value="privacy">
          <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 max-w-2xl">
            <h3 className="font-bold text-[#1D2233] mb-6">Privacy Settings</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium text-[#1D2233]">Public Profile</p>
                  <p className="text-sm text-[#717182]">Make your profile visible to everyone</p>
                </div>
                <Switch defaultChecked />
              </div>
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium text-[#1D2233]">Show Email</p>
                  <p className="text-sm text-[#717182]">Display your email on your public profile</p>
                </div>
                <Switch />
              </div>
            </div>
          </div>
        </TabsContent>

        <TabsContent value="connected">
          <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 max-w-2xl">
            <h3 className="font-bold text-[#1D2233] mb-6">Connected Accounts</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-4 border border-[#BAC7CC]/30 rounded-lg">
                <div>
                  <p className="font-medium text-[#1D2233]">GitHub</p>
                  <p className="text-sm text-[#717182]">Connected as @alexdavis</p>
                </div>
                <Button variant="outline">Disconnect</Button>
              </div>
              <div className="flex items-center justify-between p-4 border border-[#BAC7CC]/30 rounded-lg">
                <div>
                  <p className="font-medium text-[#1D2233]">Google</p>
                  <p className="text-sm text-[#717182]">Not connected</p>
                </div>
                <Button className="bg-[#56B2BB] hover:bg-[#56B2BB]/90">Connect</Button>
              </div>
            </div>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}
