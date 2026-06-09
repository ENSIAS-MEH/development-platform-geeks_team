import { defineConfig } from 'vite'
import path from 'path'
import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [
    // The React and Tailwind plugins are both required for Make, even if
    // Tailwind is not being actively used – do not remove them
    react(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      // Alias @ to the src directory
      '@': path.resolve(__dirname, './src'),
    },
  },

  // File types to support raw imports. Never add .css, .tsx, or .ts files to this.
  assetsInclude: ['**/*.svg', '**/*.csv'],

  // ── Dev proxy ─────────────────────────────────────────────────────
  server: {
    proxy: {
      // ── User service ───────────────────────────────────────────────
      '/api/auth': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        secure: false,
      },
      '/api/users': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        secure: false,
      },
      // ── Event service ──────────────────────────────────────────────
      '/api/events': {
        target: 'http://127.0.0.1:8082',
        changeOrigin: true,
        secure: false,
      },
      // ── Community service ──────────────────────────────────────────
      '/api/groups': {
        target: 'http://127.0.0.1:8085',
        changeOrigin: true,
        secure: false,
      },
      '/api/posts': {
        target: 'http://127.0.0.1:8085',
        changeOrigin: true,
        secure: false,
      },
      // ── Notification service — prefix stripped before forwarding ───
      '/notifications': {
        target: 'http://127.0.0.1:8086',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/notifications/, ''),
      },
      // ── Project service ────────────────────────────────────────────
      '/api/projects': {
        target: 'http://127.0.0.1:8084',
        changeOrigin: true,
        secure: false,
      },
      // ── Team service (catch-all for /api/teams, /api/invitations…) ─
      '/api': {
        target: 'http://127.0.0.1:8083',
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
