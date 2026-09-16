import { Link } from "react-router";
import { Button } from "./ui/button";

export function Navbar() {
  return (
    <nav className="bg-[#1D2233] text-white">
      <div className="max-w-7xl mx-auto px-6 py-4">
        <div className="flex items-center justify-between">
          <Link to="/" className="flex items-center gap-2">
            <div className="w-10 h-10 bg-[#56B2BB] rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-lg">TH</span>
            </div>
            <span className="text-2xl font-bold">TechHub</span>
          </Link>
          
          <div className="hidden md:flex items-center gap-8">
            <Link to="/explore/events" className="hover:text-[#56B2BB] transition-colors">
              Events
            </Link>
            <Link to="/explore/projects" className="hover:text-[#56B2BB] transition-colors">
              Projects
            </Link>
            <Link to="/dashboard/communities" className="hover:text-[#56B2BB] transition-colors">
              Communities
            </Link>
          </div>
          
          <div className="flex items-center gap-4">
            <Link to="/auth/login">
              <Button variant="ghost" className="text-white hover:text-[#56B2BB]">
                Login
              </Button>
            </Link>
            <Link to="/auth/login">
              <Button className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white">
                Register
              </Button>
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
}
