import { Calendar, MapPin, Users } from "lucide-react";
import { Badge } from "./ui/badge";
import { Button } from "./ui/button";
import { Link } from "react-router";

interface EventCardProps {
  id: string;
  title: string;
  organizer: string;
  date: string;
  location: string;
  participants: number;
  tags: string[];
  type: string;
}

export function EventCard({ id, title, organizer, date, location, participants, tags, type }: EventCardProps) {
  return (
    <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm hover:shadow-lg transition-all hover:-translate-y-1">
      <div className="flex items-start justify-between mb-3">
        <Badge className="bg-[#56B2BB]/10 text-[#56B2BB] hover:bg-[#56B2BB]/20">
          {type}
        </Badge>
        <span className="text-sm text-[#717182]">{organizer}</span>
      </div>
      
      <h3 className="text-xl font-bold text-[#1D2233] mb-3">{title}</h3>
      
      <div className="space-y-2 mb-4">
        <div className="flex items-center gap-2 text-sm text-[#717182]">
          <Calendar className="w-4 h-4" />
          <span>{date}</span>
        </div>
        <div className="flex items-center gap-2 text-sm text-[#717182]">
          <MapPin className="w-4 h-4" />
          <span>{location}</span>
        </div>
        <div className="flex items-center gap-2 text-sm text-[#717182]">
          <Users className="w-4 h-4" />
          <span>{participants} participants</span>
        </div>
      </div>
      
      <div className="flex flex-wrap gap-2 mb-4">
        {tags.map((tag) => (
          <Badge key={tag} variant="outline" className="text-xs">
            {tag}
          </Badge>
        ))}
      </div>
      
      <Link to={`/events/${id}`}>
        <Button className="w-full bg-[#56B2BB] hover:bg-[#56B2BB]/90 text-white">
          View Details
        </Button>
      </Link>
    </div>
  );
}
