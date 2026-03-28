import { Bell, Check } from "lucide-react";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";

export function NotificationsPage() {
  const notifications = [
    { type: "Team Invite", message: "Emma Wilson invited you to join the Web3 Workshop team", time: "2 hours ago", unread: true },
    { type: "Event Reminder", message: "AI Innovation Hackathon starts in 8 days", time: "1 day ago", unread: true },
    { type: "Project Update", message: "Sarah Chen joined your AI Study Buddy project", time: "2 days ago", unread: false },
    { type: "Comment", message: "Mike Johnson commented on your project", time: "3 days ago", unread: false },
  ];

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-4xl font-bold text-[#1D2233] mb-2">Notifications</h1>
          <p className="text-lg text-[#717182]">Stay updated with your activity</p>
        </div>
        <Button variant="outline" className="border-[#BAC7CC]/30">
          <Check className="w-4 h-4 mr-2" />
          Mark All as Read
        </Button>
      </div>

      <div className="bg-white rounded-xl border border-[#BAC7CC]/30 overflow-hidden">
        <div className="divide-y divide-[#BAC7CC]/20">
          {notifications.map((notif, index) => (
            <div key={index} className={`p-6 flex gap-4 ${notif.unread ? 'bg-[#56B2BB]/5' : ''}`}>
              <div className={`w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 ${notif.unread ? 'bg-[#56B2BB]' : 'bg-[#F0F4F8]'}`}>
                <Bell className={`w-5 h-5 ${notif.unread ? 'text-white' : 'text-[#717182]'}`} />
              </div>
              <div className="flex-1">
                <div className="flex items-start justify-between mb-1">
                  <Badge variant="outline" className="text-xs">{notif.type}</Badge>
                  {notif.unread && <div className="w-2 h-2 bg-[#56B2BB] rounded-full"></div>}
                </div>
                <p className="text-[#1D2233] mb-1">{notif.message}</p>
                <p className="text-xs text-[#717182]">{notif.time}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
