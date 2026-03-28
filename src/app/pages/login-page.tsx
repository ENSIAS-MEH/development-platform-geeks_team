import { useState } from "react";
import { Link } from "react-router";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "../components/ui/tabs";
import { Github, Mail } from "lucide-react";

export function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");

  return (
    <div className="w-full max-w-md">
      <div className="text-center mb-8">
        <Link to="/" className="inline-flex items-center gap-2 mb-6">
          <div className="w-12 h-12 bg-[#56B2BB] rounded-lg flex items-center justify-center">
            <span className="text-white font-bold text-xl">TH</span>
          </div>
          <span className="text-3xl font-bold text-[#1D2233]">TechHub</span>
        </Link>
        <h1 className="text-3xl font-bold text-[#1D2233] mt-4 mb-2">Welcome Back</h1>
        <p className="text-[#717182]">Sign in to continue your journey</p>
      </div>

      <Tabs defaultValue="login" className="w-full">
        <TabsList className="grid w-full grid-cols-2 mb-6">
          <TabsTrigger value="login">Sign In</TabsTrigger>
          <TabsTrigger value="register">Register</TabsTrigger>
        </TabsList>

        <TabsContent value="login">
          <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-lg">
            <form className="space-y-4">
              <div>
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="your.email@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                />
              </div>
              
              <div>
                <Label htmlFor="password">Password</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                />
              </div>

              <div className="flex items-center justify-between">
                <label className="flex items-center gap-2 text-sm">
                  <input type="checkbox" className="rounded border-[#BAC7CC]" />
                  <span className="text-[#717182]">Remember me</span>
                </label>
                <a href="#" className="text-sm text-[#56B2BB] hover:underline">
                  Forgot password?
                </a>
              </div>

              <Link to="/dashboard">
                <Button className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white">
                  Sign In
                </Button>
              </Link>
            </form>

            <div className="mt-6">
              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-[#BAC7CC]/30"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-2 bg-white text-[#717182]">Or continue with</span>
                </div>
              </div>

              <div className="mt-6 grid grid-cols-2 gap-4">
                <Button variant="outline" className="border-[#BAC7CC]/30">
                  <Github className="w-5 h-5 mr-2" />
                  GitHub
                </Button>
                <Button variant="outline" className="border-[#BAC7CC]/30">
                  <Mail className="w-5 h-5 mr-2" />
                  Google
                </Button>
              </div>
            </div>
          </div>
        </TabsContent>

        <TabsContent value="register">
          <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-lg">
            <form className="space-y-4">
              <div>
                <Label htmlFor="name">Full Name</Label>
                <Input
                  id="name"
                  type="text"
                  placeholder="John Doe"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                />
              </div>

              <div>
                <Label htmlFor="reg-email">Email</Label>
                <Input
                  id="reg-email"
                  type="email"
                  placeholder="your.email@example.com"
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                />
              </div>
              
              <div>
                <Label htmlFor="reg-password">Password</Label>
                <Input
                  id="reg-password"
                  type="password"
                  placeholder="••••••••"
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                />
              </div>

              <div>
                <Label htmlFor="role">I am a...</Label>
                <select 
                  id="role"
                  className="mt-1.5 w-full rounded-md border border-[#BAC7CC]/30 bg-[#F0F4F8] px-3 py-2"
                >
                  <option>Student</option>
                  <option>Developer</option>
                  <option>Event Organizer</option>
                </select>
              </div>

              <Link to="/dashboard">
                <Button className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white">
                  Create Account
                </Button>
              </Link>
            </form>

            <div className="mt-6">
              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-[#BAC7CC]/30"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-2 bg-white text-[#717182]">Or register with</span>
                </div>
              </div>

              <div className="mt-6 grid grid-cols-2 gap-4">
                <Button variant="outline" className="border-[#BAC7CC]/30">
                  <Github className="w-5 h-5 mr-2" />
                  GitHub
                </Button>
                <Button variant="outline" className="border-[#BAC7CC]/30">
                  <Mail className="w-5 h-5 mr-2" />
                  Google
                </Button>
              </div>
            </div>
          </div>
        </TabsContent>
      </Tabs>

      <p className="text-center text-sm text-[#717182] mt-6">
        By continuing, you agree to TechHub's{" "}
        <a href="#" className="text-[#56B2BB] hover:underline">Terms of Service</a>
        {" "}and{" "}
        <a href="#" className="text-[#56B2BB] hover:underline">Privacy Policy</a>
      </p>
    </div>
  );
}
