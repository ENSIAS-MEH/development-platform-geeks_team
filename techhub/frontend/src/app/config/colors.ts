// TechHub Color Palette
// Based on design specifications in techhub-platform-design.md

export const colors = {
  // Primary Colors
  background: {
    light: '#F0F4F8',
    dark: '#0A0F22',
  },
  
  accent: {
    muted: '#BAC7CC',
  },
  
  primary: {
    teal: '#56B2BB',
  },
  
  navy: {
    dark: '#1D2233',
  },
  
  // Text Colors
  text: {
    primary: '#1D2233',
    secondary: '#717182',
    muted: '#BAC7CC',
    white: '#FFFFFF',
  },
  
  // Semantic Colors
  status: {
    success: '#10b981',
    warning: '#f59e0b',
    error: '#ef4444',
    info: '#3b82f6',
  },
} as const;

// Tailwind-friendly color exports
export const bgLight = 'bg-[#F0F4F8]';
export const bgDark = 'bg-[#0A0F22]';
export const bgNavy = 'bg-[#1D2233]';
export const bgTeal = 'bg-[#56B2BB]';
export const textNavy = 'text-[#1D2233]';
export const textMuted = 'text-[#717182]';
export const borderMuted = 'border-[#BAC7CC]';
