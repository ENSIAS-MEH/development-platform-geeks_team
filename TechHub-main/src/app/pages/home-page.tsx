import { Link } from "react-router";
import { Navbar } from "../components/navbar";
import { Button } from "../components/ui/button";
import { EventCard } from "../components/event-card";
import { motion } from "motion/react";
import { Calendar, FolderGit2, Users, Rocket, Code, Trophy } from "lucide-react";

export function HomePage() {
  const stats = [
    { icon: Calendar, label: "Active Events", value: "150+" },
    { icon: FolderGit2, label: "Open Projects", value: "500+" },
    { icon: Users, label: "Community Members", value: "10K+" },
    { icon: Trophy, label: "Hackathons", value: "75+" },
  ];

  const featuredEvents = [
    {
      id: "1",
      title: "AI Innovation Hackathon 2026",
      organizer: "TechCorp",
      date: "March 25-27, 2026",
      location: "Online",
      participants: 250,
      tags: ["AI/ML", "Python", "TensorFlow"],
      type: "Hackathon"
    },
    {
      id: "2",
      title: "Web3 Workshop Series",
      organizer: "Blockchain Academy",
      date: "April 5, 2026",
      location: "San Francisco, CA",
      participants: 80,
      tags: ["Blockchain", "Solidity", "Web3"],
      type: "Workshop"
    },
    {
      id: "3",
      title: "Mobile Dev Conference",
      organizer: "DevCommunity",
      date: "April 15-16, 2026",
      location: "New York, NY",
      participants: 500,
      tags: ["React Native", "Flutter", "Mobile"],
      type: "Conference"
    },
  ];

  return (
    <div className="min-h-screen">
      <Navbar />
      
      {/* Hero Section */}
      <section className="bg-[#1D2233] text-white py-20 px-6">
        <div className="max-w-7xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="text-center max-w-4xl mx-auto"
          >
            <h1 className="text-5xl md:text-6xl font-bold mb-6">
              Connect. Collaborate. Create.
            </h1>
            <p className="text-xl text-[#BAC7CC] mb-8">
              TechHub is your centralized community hub for student developers and tech enthusiasts. 
              Join events, collaborate on projects, and connect with innovators worldwide.
            </p>
            <div className="flex flex-wrap gap-4 justify-center">
              <Link to="/auth/login">
                <Button size="lg" className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white px-8 py-6 text-lg">
                  Get Started
                  <Rocket className="w-5 h-5 ml-2" />
                </Button>
              </Link>
              <Link to="/explore/events">
                <Button size="lg" variant="outline" className="border-[#56B2BB] text-[#56B2BB] hover:bg-[#56B2BB]/10 px-8 py-6 text-lg">
                  Explore Events
                </Button>
              </Link>
            </div>
          </motion.div>

          {/* Animated Stats */}
          <motion.div 
            initial={{ opacity: 0, y: 40 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.3 }}
            className="grid grid-cols-2 md:grid-cols-4 gap-6 mt-16"
          >
            {stats.map((stat, index) => {
              const Icon = stat.icon;
              return (
                <motion.div
                  key={stat.label}
                  initial={{ opacity: 0, scale: 0.8 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ duration: 0.4, delay: 0.5 + index * 0.1 }}
                  className="bg-[#0A0F22] rounded-xl p-6 text-center border border-[#BAC7CC]/20"
                >
                  <Icon className="w-8 h-8 text-[#56B2BB] mx-auto mb-3" />
                  <p className="text-3xl font-bold mb-1">{stat.value}</p>
                  <p className="text-sm text-[#BAC7CC]">{stat.label}</p>
                </motion.div>
              );
            })}
          </motion.div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 px-6 bg-white">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-12">
            <h2 className="text-4xl font-bold text-[#1D2233] mb-4">
              Why Choose TechHub?
            </h2>
            <p className="text-lg text-[#717182] max-w-2xl mx-auto">
              Everything you need to grow as a developer, all in one place
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            <div className="bg-[#F0F4F8] rounded-xl p-8 border border-[#BAC7CC]/30">
              <div className="w-12 h-12 bg-[#56B2BB] rounded-lg flex items-center justify-center mb-4">
                <Calendar className="w-6 h-6 text-white" />
              </div>
              <h3 className="text-2xl font-bold text-[#1D2233] mb-3">Discover Events</h3>
              <p className="text-[#717182]">
                Find hackathons, workshops, conferences, and meetups tailored to your interests and skill level.
              </p>
            </div>

            <div className="bg-[#F0F4F8] rounded-xl p-8 border border-[#BAC7CC]/30">
              <div className="w-12 h-12 bg-[#56B2BB] rounded-lg flex items-center justify-center mb-4">
                <Code className="w-6 h-6 text-white" />
              </div>
              <h3 className="text-2xl font-bold text-[#1D2233] mb-3">Collaborate on Projects</h3>
              <p className="text-[#717182]">
                Join open-source projects or start your own. Find contributors with the exact skills you need.
              </p>
            </div>

            <div className="bg-[#F0F4F8] rounded-xl p-8 border border-[#BAC7CC]/30">
              <div className="w-12 h-12 bg-[#56B2BB] rounded-lg flex items-center justify-center mb-4">
                <Users className="w-6 h-6 text-white" />
              </div>
              <h3 className="text-2xl font-bold text-[#1D2233] mb-3">Build Your Network</h3>
              <p className="text-[#717182]">
                Connect with like-minded developers, join communities, and grow your professional network.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Events Section */}
      <section className="py-20 px-6 bg-[#F0F4F8]">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center justify-between mb-12">
            <div>
              <h2 className="text-4xl font-bold text-[#1D2233] mb-2">
                Featured Events
              </h2>
              <p className="text-lg text-[#717182]">
                Don't miss out on these upcoming opportunities
              </p>
            </div>
            <Link to="/explore/events">
              <Button variant="outline" className="border-[#56B2BB] text-[#56B2BB] hover:bg-[#56B2BB]/10">
                View All Events
              </Button>
            </Link>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            {featuredEvents.map((event, index) => (
              <motion.div
                key={event.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: index * 0.1 }}
              >
                <EventCard {...event} />
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 px-6 bg-[#1D2233] text-white">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-4xl font-bold mb-6">
            Ready to Start Your Journey?
          </h2>
          <p className="text-xl text-[#BAC7CC] mb-8">
            Join thousands of developers already building the future on TechHub
          </p>
          <Link to="/auth/login">
            <Button size="lg" className="bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white px-8 py-6 text-lg">
              Create Your Account
            </Button>
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-[#0A0F22] text-[#BAC7CC] py-12 px-6">
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-4 gap-8 mb-8">
            <div>
              <h4 className="text-white font-bold mb-4">TechHub</h4>
              <p className="text-sm">
                Connecting developers and tech enthusiasts worldwide.
              </p>
            </div>
            <div>
              <h4 className="text-white font-bold mb-4">Platform</h4>
              <ul className="space-y-2 text-sm">
                <li><Link to="/explore/events" className="hover:text-[#56B2BB]">Events</Link></li>
                <li><Link to="/explore/projects" className="hover:text-[#56B2BB]">Projects</Link></li>
                <li><Link to="/dashboard/communities" className="hover:text-[#56B2BB]">Communities</Link></li>
              </ul>
            </div>
            <div>
              <h4 className="text-white font-bold mb-4">Resources</h4>
              <ul className="space-y-2 text-sm">
                <li><a href="#" className="hover:text-[#56B2BB]">Help Center</a></li>
                <li><a href="#" className="hover:text-[#56B2BB]">Blog</a></li>
                <li><a href="#" className="hover:text-[#56B2BB]">API Docs</a></li>
              </ul>
            </div>
            <div>
              <h4 className="text-white font-bold mb-4">Company</h4>
              <ul className="space-y-2 text-sm">
                <li><a href="#" className="hover:text-[#56B2BB]">About Us</a></li>
                <li><a href="#" className="hover:text-[#56B2BB]">Contact</a></li>
                <li><a href="#" className="hover:text-[#56B2BB]">Privacy</a></li>
              </ul>
            </div>
          </div>
          <div className="border-t border-[#BAC7CC]/20 pt-8 text-center text-sm">
            <p>&copy; 2026 TechHub. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
