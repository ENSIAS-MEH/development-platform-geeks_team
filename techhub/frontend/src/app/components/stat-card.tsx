import { LucideIcon } from "lucide-react";

interface StatCardProps {
  icon: LucideIcon;
  label: string;
  value: string | number;
  trend?: string;
  trendUp?: boolean;
}

export function StatCard({ icon: Icon, label, value, trend, trendUp }: StatCardProps) {
  return (
    <div className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <p className="text-sm text-[#717182] mb-1">{label}</p>
          <p className="text-3xl font-bold text-[#1D2233] mb-2">{value}</p>
          {trend && (
            <p className={`text-sm ${trendUp ? 'text-green-600' : 'text-red-600'}`}>
              {trend}
            </p>
          )}
        </div>
        <div className="w-12 h-12 bg-[#56B2BB]/10 rounded-lg flex items-center justify-center">
          <Icon className="w-6 h-6 text-[#56B2BB]" />
        </div>
      </div>
    </div>
  );
}
