import { Send, Search } from "lucide-react";
import { Input } from "../components/ui/input";
import { Button } from "../components/ui/button";

export function MessagingPage() {
  const conversations = [
    { name: "AI Study Buddy Team", lastMessage: "Let's discuss the next sprint", time: "10m ago", unread: 3 },
    { name: "Sarah Chen", lastMessage: "Thanks for the feedback!", time: "1h ago", unread: 0 },
    { name: "Hackathon Squad", lastMessage: "Who's bringing the energy drinks?", time: "2h ago", unread: 1 },
  ];

  return (
    <div className="p-8">
      <h1 className="text-4xl font-bold text-[#1D2233] mb-8">Messages</h1>

      <div className="grid lg:grid-cols-3 gap-6 h-[calc(100vh-16rem)]">
        {/* Conversations List */}
        <div className="bg-white rounded-xl border border-[#BAC7CC]/30 overflow-hidden flex flex-col">
          <div className="p-4 border-b border-[#BAC7CC]/20">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-[#717182]" />
              <Input placeholder="Search messages..." className="pl-9 bg-[#F0F4F8]" />
            </div>
          </div>
          <div className="flex-1 overflow-y-auto divide-y divide-[#BAC7CC]/20">
            {conversations.map((conv) => (
              <div key={conv.name} className="p-4 hover:bg-[#F0F4F8] cursor-pointer">
                <div className="flex items-start justify-between mb-1">
                  <h4 className="font-medium text-[#1D2233]">{conv.name}</h4>
                  {conv.unread > 0 && (
                    <span className="w-5 h-5 bg-[#56B2BB] text-white text-xs rounded-full flex items-center justify-center">
                      {conv.unread}
                    </span>
                  )}
                </div>
                <p className="text-sm text-[#717182] truncate">{conv.lastMessage}</p>
                <p className="text-xs text-[#717182] mt-1">{conv.time}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Chat Area */}
        <div className="lg:col-span-2 bg-white rounded-xl border border-[#BAC7CC]/30 flex flex-col">
          <div className="p-4 border-b border-[#BAC7CC]/20">
            <h3 className="font-bold text-[#1D2233]">AI Study Buddy Team</h3>
            <p className="text-sm text-[#717182]">5 members</p>
          </div>
          <div className="flex-1 p-6 overflow-y-auto">
            <div className="space-y-4">
              <div className="flex gap-3">
                <div className="w-8 h-8 bg-[#56B2BB]/10 rounded-full flex items-center justify-center">
                  <span className="text-xs font-bold text-[#56B2BB]">SC</span>
                </div>
                <div>
                  <p className="text-sm font-medium text-[#1D2233]">Sarah Chen</p>
                  <div className="bg-[#F0F4F8] rounded-lg p-3 mt-1">
                    <p className="text-sm">Let's discuss the next sprint planning</p>
                  </div>
                  <p className="text-xs text-[#717182] mt-1">10m ago</p>
                </div>
              </div>
            </div>
          </div>
          <div className="p-4 border-t border-[#BAC7CC]/20">
            <div className="flex gap-2">
              <Input placeholder="Type a message..." className="bg-[#F0F4F8]" />
              <Button className="bg-[#56B2BB] hover:bg-[#56B2BB]/90">
                <Send className="w-4 h-4" />
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
