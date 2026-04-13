import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "../components/ui/tabs";
import { Github, Mail } from "lucide-react";
import { authService } from "../services/authService";
 //added some modifications here 
export function LoginPage() {
  const navigate = useNavigate()

  // Login form state
  const [loginEmail, setLoginEmail] = useState("")
  const [loginPassword, setLoginPassword] = useState("")
  const [loginError, setLoginError] = useState("")
  const [loginLoading, setLoginLoading] = useState(false)

  // Register form state
  const [name, setName] = useState("")
  const [registerEmail, setRegisterEmail] = useState("")
  const [registerPassword, setRegisterPassword] = useState("")
  const [role, setRole] = useState("Developer")
  const [registerError, setRegisterError] = useState("")
  const [registerLoading, setRegisterLoading] = useState(false)

  // ── Login handler ──────────────────────────────────────────────────────
  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()          // prevent page refresh
    setLoginError("")
    setLoginLoading(true)

    try {
      await authService.login({ email: loginEmail, password: loginPassword })
      navigate("/dashboard")    // redirect on success
    } catch (error: any) {
      // Show the message from your ErrorResponse DTO
      setLoginError(
        error.response?.data?.message || "Invalid email or password"
      )
    } finally {
      setLoginLoading(false)
    }
  }

  // ── Register handler ───────────────────────────────────────────────────
  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault()
    setRegisterError("")
    setRegisterLoading(true)

    try {
      await authService.register({ name, email: registerEmail, password: registerPassword, role })
      navigate("/dashboard")
    } catch (error: any) {
      setRegisterError(
        error.response?.data?.message || "Registration failed. Please try again."
      )
    } finally {
      setRegisterLoading(false)
    }
  }

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

        {/* ── Login Tab ── */}
        <TabsContent value="login">
          <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-lg">
            <form className="space-y-4" onSubmit={handleLogin}>
              <div>
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="your.email@example.com"
                  value={loginEmail}
                  onChange={(e) => setLoginEmail(e.target.value)}
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                  required
                />
              </div>

              <div>
                <Label htmlFor="password">Password</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="••••••••"
                  value={loginPassword}
                  onChange={(e) => setLoginPassword(e.target.value)}
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                  required
                />
              </div>

              {/* Error message from backend */}
              {loginError && (
                <p className="text-red-500 text-sm">{loginError}</p>
              )}

              <Button
                type="submit"
                disabled={loginLoading}
                className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white"
              >
                {loginLoading ? "Signing in..." : "Sign In"}
              </Button>
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
                  <Github className="w-5 h-5 mr-2" />GitHub
                </Button>
                <Button variant="outline" className="border-[#BAC7CC]/30">
                  <Mail className="w-5 h-5 mr-2" />Google
                </Button>
              </div>
            </div>
          </div>
        </TabsContent>

        {/* ── Register Tab ── */}
        <TabsContent value="register">
          <div className="bg-white rounded-xl p-8 border border-[#BAC7CC]/30 shadow-lg">
            <form className="space-y-4" onSubmit={handleRegister}>
              <div>
                <Label htmlFor="name">Full Name</Label>
                <Input
                  id="name"
                  type="text"
                  placeholder="John Doe"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                  required
                />
              </div>

              <div>
                <Label htmlFor="reg-email">Email</Label>
                <Input
                  id="reg-email"
                  type="email"
                  placeholder="your.email@example.com"
                  value={registerEmail}
                  onChange={(e) => setRegisterEmail(e.target.value)}
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                  required
                />
              </div>

              <div>
                <Label htmlFor="reg-password">Password</Label>
                <Input
                  id="reg-password"
                  type="password"
                  placeholder="••••••••"
                  value={registerPassword}
                  onChange={(e) => setRegisterPassword(e.target.value)}
                  className="mt-1.5 bg-[#F0F4F8] border-[#BAC7CC]/30"
                  required
                />
              </div>

              <div>
                <Label htmlFor="role">I am a...</Label>
                <select
                  id="role"
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
                  className="mt-1.5 w-full rounded-md border border-[#BAC7CC]/30 bg-[#F0F4F8] px-3 py-2"
                >
                  <option>Student</option>
                  <option>Developer</option>
                  <option>Event Organizer</option>
                  <option>Company</option>
                </select>
              </div>

              {/* Error message from backend */}
              {registerError && (
                <p className="text-red-500 text-sm">{registerError}</p>
              )}

              <Button
                type="submit"
                disabled={registerLoading}
                className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white"
              >
                {registerLoading ? "Creating account..." : "Create Account"}
              </Button>
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
                  <Github className="w-5 h-5 mr-2" />GitHub
                </Button>
                <Button variant="outline" className="border-[#BAC7CC]/30">
                  <Mail className="w-5 h-5 mr-2" />Google
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
  )
}