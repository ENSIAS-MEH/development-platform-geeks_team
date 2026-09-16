# TechHub Development Guide - Version 1

## 🚀 Quick Start

### Installation
```bash
# Les dépendances sont déjà installées
# Si nécessaire, réinstaller avec :
pnpm install
```

### Démarrage
```bash
pnpm run dev
```

L'application sera disponible sur `http://localhost:5173`

## 📂 Structure du Projet

```
/src/
├── app/
│   ├── components/          # Composants réutilisables
│   │   ├── ui/             # Composants shadcn/ui
│   │   ├── navbar.tsx      # Navigation principale
│   │   ├── footer.tsx      # Footer global
│   │   ├── event-card.tsx  # Carte d'événement
│   │   ├── project-card.tsx # Carte de projet
│   │   └── stat-card.tsx   # Carte de statistiques
│   ├── config/             # Configuration
│   │   ├── colors.ts       # Palette de couleurs
│   │   └── navigation.ts   # Routes de navigation
│   ├── data/               # Données mockées
│   │   └── mock-data.ts    # Événements, projets, utilisateurs
│   ├── layouts/            # Layouts de page
│   │   ├── root-layout.tsx
│   │   ├── auth-layout.tsx
│   │   └── dashboard-layout.tsx
│   ├── pages/              # 26 pages de l'application
│   ├── App.tsx             # Point d'entrée
│   └── routes.tsx          # Configuration React Router
├── styles/
│   ├── fonts.css           # Import Google Fonts (Inter)
│   ├── theme.css           # Variables CSS et thème
│   ├── tailwind.css        # Configuration Tailwind
│   └── index.css           # Imports globaux
└── main.tsx                # Entry point Vite
```

## 🎨 Design System

### Couleurs Principales
```typescript
// Importez depuis /src/app/config/colors.ts
import { colors, bgTeal, textNavy } from '@/config/colors';

// Utilisation en Tailwind
className="bg-[#56B2BB] text-[#1D2233]"
```

### Composants UI
Tous les composants UI sont dans `/src/app/components/ui/` et suivent le pattern shadcn/ui.

```tsx
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card } from '@/components/ui/card';
```

### Composants Personnalisés
```tsx
// Import groupé depuis index
import { Navbar, Footer, EventCard, ProjectCard, StatCard } from '@/components';

// Ou import individuel
import { EventCard } from '@/components/event-card';
```

## 🧭 Navigation

### React Router v7 (Data Mode)
L'application utilise React Router v7 avec le pattern Data Mode.

```tsx
// Configuration dans /src/app/routes.tsx
import { createBrowserRouter } from "react-router";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: RootLayout,
    children: [...]
  }
]);
```

### Navigation dans les composants
```tsx
import { Link, useNavigate } from "react-router";

// Avec Link
<Link to="/dashboard">Dashboard</Link>

// Avec useNavigate
const navigate = useNavigate();
navigate('/dashboard');
```

### Routes Disponibles
Consultez `/src/app/config/navigation.ts` pour la liste complète.

## 🎭 Animations

L'application utilise **Motion** (anciennement Framer Motion) pour les animations.

```tsx
import { motion } from "motion/react";

<motion.div
  initial={{ opacity: 0, y: 20 }}
  animate={{ opacity: 1, y: 0 }}
  transition={{ duration: 0.6 }}
>
  Contenu animé
</motion.div>
```

## 📊 Charts & Analytics

Utilise **Recharts** pour les graphiques.

```tsx
import { LineChart, Line, XAxis, YAxis, Tooltip } from 'recharts';

<LineChart data={data}>
  <XAxis dataKey="month" />
  <YAxis />
  <Tooltip />
  <Line type="monotone" dataKey="users" stroke="#56B2BB" />
</LineChart>
```

## 📋 Bonnes Pratiques

### 1. Imports Relatifs
```tsx
// Préférez les imports relatifs clairs
import { Button } from "../components/ui/button";
import { EventCard } from "../components/event-card";
```

### 2. Typage TypeScript
```tsx
// Définissez les interfaces pour les props
interface EventCardProps {
  id: string;
  title: string;
  organizer: string;
  // ...
}

export function EventCard({ id, title, organizer }: EventCardProps) {
  // ...
}
```

### 3. Composants Fonctionnels
Utilisez toujours des composants fonctionnels avec hooks.

```tsx
import { useState } from "react";

export function MyComponent() {
  const [state, setState] = useState("");
  
  return <div>{state}</div>;
}
```

### 4. Styling avec Tailwind
```tsx
// Classes TechHub standards
className="bg-white rounded-xl p-6 border border-[#BAC7CC]/30 shadow-sm"

// Responsive
className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"

// Hover states
className="hover:shadow-lg transition-all hover:-translate-y-1"
```

## 🗃️ Données Mockées

Toutes les données sont dans `/src/app/data/mock-data.ts`

```tsx
import { mockEvents, mockProjects, mockUsers, mockCommunities } from "../data/mock-data";

// Utilisation
const events = mockEvents;
```

## 🎯 Fonctionnalités par Page

### Pages Publiques
- **HomePage** : Hero animé, stats, événements featured
- **ExploreEventsPage** : Filtres, recherche, grille de cartes
- **ExploreProjectsPage** : Navigation de projets avec filtres
- **LoginPage** : Tabs Sign In/Register, OAuth buttons

### Pages Dashboard
- **DashboardPage** : Vue d'ensemble personnalisée
- **MyProfilePage** : Profil éditable, compétences, historique
- **CreateProjectPage** : Formulaire avec tags dynamiques

### Pages Organisateur
- **OrganizerDashboardPage** : KPIs, liste d'événements
- **CreateEventPage** : Formulaire multi-étapes
- **EventAnalyticsPage** : Graphiques Recharts

### Pages Admin
- **AdminDashboardPage** : KPIs globaux, alertes
- **PlatformAnalyticsPage** : Analytics avancées

## 🛠️ Dépannage

### Problème : Erreur d'import
Vérifiez que le chemin d'import est correct et relatif au fichier actuel.

### Problème : Styles ne s'appliquent pas
1. Vérifiez que les classes Tailwind sont correctes
2. Regardez `/src/styles/theme.css` pour les variables CSS
3. Utilisez les couleurs TechHub : `#56B2BB`, `#1D2233`, etc.

### Problème : Route ne fonctionne pas
Vérifiez `/src/app/routes.tsx` et assurez-vous que la route est définie.

## 📝 Conventions de Code

### Nommage des Fichiers
- Components : `kebab-case.tsx` (ex: `event-card.tsx`)
- Pages : `kebab-case-page.tsx` (ex: `home-page.tsx`)
- Layouts : `kebab-case-layout.tsx`

### Nommage des Composants
- PascalCase pour les composants (ex: `EventCard`)
- camelCase pour les fonctions et variables

### Structure des Composants
```tsx
// 1. Imports
import { useState } from "react";
import { Button } from "../components/ui/button";

// 2. Interfaces/Types
interface MyComponentProps {
  title: string;
}

// 3. Composant
export function MyComponent({ title }: MyComponentProps) {
  // 3a. Hooks
  const [state, setState] = useState("");
  
  // 3b. Handlers
  const handleClick = () => {
    // ...
  };
  
  // 3c. Render
  return (
    <div>
      <h1>{title}</h1>
    </div>
  );
}
```

## 🔍 Ressources Utiles

- **Tailwind CSS**: https://tailwindcss.com/docs
- **React Router**: https://reactrouter.com
- **Motion**: https://motion.dev
- **Recharts**: https://recharts.org
- **Lucide Icons**: https://lucide.dev
- **Radix UI**: https://radix-ui.com

## 📞 Support

Pour toute question sur le développement :
1. Consultez ce guide
2. Regardez les exemples dans `/src/app/pages/`
3. Vérifiez `/TECHHUB_VERSION1.md` pour la documentation complète

---

**Happy Coding!** 🚀
