# TechHub — Plateforme Collaborative Microservices pour la Communauté Tech

> **Projet de fin de module — 2ème année Génie Logiciel**
> École Nationale Supérieure d'Informatique et d'Analyse des Systèmes (ENSIAS)
> Encadrant : **Pr. Mahmoud El Hamlaoui**

[![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-7.6-231F20?logo=apachekafka)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)](https://www.docker.com/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-K8s-326CE5?logo=kubernetes)](https://kubernetes.io/)
[![ArgoCD](https://img.shields.io/badge/ArgoCD-GitOps-EF7B4D?logo=argo)](https://argo-cd.readthedocs.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## 1. Présentation du Projet

### Contexte

L'écosystème technologique étudiant souffre d'une fragmentation chronique : les hackathons sont annoncés sur Devpost, les conférences sur LinkedIn ou Eventbrite, les projets sur GitHub, et les discussions communautaires sur Discord ou WhatsApp. Cette dispersion nuit à la visibilité des opportunités, à la formation d'équipes compétentes et à la pérennité des collaborations post-événement.

TechHub est une **plateforme communautaire centralisée**, développée dans le cadre du module Développement Logiciel Avancé à l'ENSIAS. Elle cible les étudiants ingénieurs, les jeunes développeurs et les organisateurs d'événements tech qui cherchent un espace unique pour découvrir des opportunités, collaborer sur des projets et s'organiser en équipes.

### Problématique

> Comment concevoir une plateforme scalable, résiliente et maintenable permettant de centraliser les événements tech, les projets collaboratifs et la gestion d'équipes, tout en garantissant une communication en temps réel entre des services indépendants et un déploiement continu en production ?

### Objectifs du Projet

- Concevoir une architecture microservices robuste avec Spring Boot 3 et Java 21.
- Implémenter une authentification stateless par JWT, partagée entre tous les services.
- Assurer la communication asynchrone inter-services via Apache Kafka.
- Optimiser les lectures fréquentes avec un cache distribué Redis.
- Conteneuriser chaque service avec Docker en suivant les bonnes pratiques de multi-stage build.
- Orchestrer les déploiements avec Kubernetes dans un namespace dédié `techhub`.
- Automatiser le pipeline CI/CD via GitHub Actions avec gate de couverture JaCoCo à 80 %.
- Appliquer le paradigme GitOps avec ArgoCD pour la synchronisation continue.

### Valeur Ajoutée de la Solution

| Dimension | Apport TechHub |
|-----------|----------------|
| **Centralisation** | Un seul point d'entrée pour événements, projets, équipes et communauté |
| **Temps réel** | Notifications asynchrones par Kafka dès qu'un événement métier survient |
| **Scalabilité** | Architecture microservices — chaque service se déploie et monte en charge indépendamment |
| **Résilience** | Chaque service possède sa propre base de données, aucun single point of failure applicatif |
| **DevOps Mature** | Pipeline CI/CD complet + GitOps ArgoCD + observabilité Prometheus/Grafana |
| **Sécurité** | JWT stateless, BCrypt, CORS configuré, method-level `@PreAuthorize` |

---

## 2. Équipe Projet

| Membre | GitHub | Rôle | Responsabilités principales |
|--------|--------|------|-----------------------------|
| **Alae LABHAL** | [@Alae-eng](https://github.com/Alae-eng) | Lead Backend — User Service | Authentification JWT, gestion des profils, OAuth2 GitHub/Google, intégration Redis |
| **Hafsa ABBAR** | [@Hafsaabbar](https://github.com/Hafsaabbar) | Backend — Community Service | Groupes thématiques, interactions communautaires, API REST, cache Redis |
| **Halima ANEJARI** | [@Hali24-tech](https://github.com/Hali24-tech) | Backend — Event & Project Service | Gestion des événements, des projets collaboratifs, pipeline CI/CD event-service |
| **Kawtar LAMEGHAIZI** | [@KLdevs007](https://github.com/KLdevs007) | Backend — Team & Notification Service | Équipes, invitations, expiration scheduler, Kafka producer/consumer, emails MailHog |
| **Pr. Mahmoud El Hamlaoui** | — | Encadrant | Supervision académique, validation architecturale, évaluation |

---

## 3. Table des Matières

<details>
<summary>Développer la table des matières complète</summary>

### PARTIE I — DÉVELOPPEMENT
- [4. Analyse des Besoins](#4-analyse-des-besoins)
- [5. User Stories](#5-user-stories)
- [6. Architecture Fonctionnelle](#6-architecture-fonctionnelle)
- [7. Conception UML](#7-conception-uml)
  - [7.1 Diagramme de Cas d'Utilisation](#71-diagramme-de-cas-dutilisation)
  - [7.2 Diagramme de Classes](#72-diagramme-de-classes)
  - [7.3 Diagrammes de Séquence](#73-diagrammes-de-séquence)
  - [7.4 Diagramme de Déploiement](#74-diagramme-de-déploiement)
- [8. Architecture Logicielle](#8-architecture-logicielle)
- [9. Choix Technologiques](#9-choix-technologiques)
- [10. Implémentation Backend](#10-implémentation-backend)
- [11. Base de Données](#11-base-de-données)
- [12. Sécurité](#12-sécurité)
- [13. Documentation API](#13-documentation-api)
- [14. Tests](#14-tests)

### PARTIE II — DEVOPS
- [15. Containerisation](#15-containerisation)
- [16. Orchestration Kubernetes](#16-orchestration-kubernetes)
- [17. CI/CD](#17-cicd)
- [18. GitOps avec ArgoCD](#18-gitops-avec-argocd)
- [19. Monitoring et Observabilité](#19-monitoring-et-observabilité)
- [20. Gestion de la Configuration](#20-gestion-de-la-configuration)
- [21. Guide de Déploiement](#21-guide-de-déploiement)
- [22. Difficultés Rencontrées](#22-difficultés-rencontrées)
- [23. Perspectives d'Amélioration](#23-perspectives-damélioration)
- [24. Conclusion](#24-conclusion)

</details>

---

# PARTIE I — DÉVELOPPEMENT

---

## 4. Analyse des Besoins

### Besoins Fonctionnels

**Module Utilisateur**
- Inscription et connexion via email/mot de passe avec hachage BCrypt.
- Authentification OAuth2 sociale via GitHub et Google.
- Gestion du profil technique : compétences, biographie, portfolio, liens GitHub/LinkedIn.
- Accès sécurisé par token JWT avec durée de vie configurable.
- Changement de mot de passe avec notification automatique par email.

**Module Équipe**
- Création d'une équipe avec nom, description, capacité maximale et statut (`OPEN` / `FULL`).
- Invitation de membres par UUID d'utilisateur avec une durée de validité paramétrable (défaut : 72 h).
- Acceptation ou refus d'une invitation depuis le tableau de bord.
- Expiration automatique des invitations non traitées via un scheduler Spring (`@Scheduled`).
- Départ volontaire ou exclusion d'un membre par le propriétaire de l'équipe.
- Recherche paginée d'équipes publiques par mots-clés.

**Module Notification**
- Envoi d'email transactionnel à chaque événement métier critique (inscription, changement de mot de passe, invitation, intégration à une équipe).
- Persistance des notifications dans PostgreSQL avec statut (`SENT` / `FAILED`) et horodatage.
- Lecture des événements Kafka de manière asynchrone avec acquittement manuel.
- Exposition d'une API REST pour la consultation et le marquage "lu" des notifications.

**Module Événement**
- Publication d'événements (hackathon, conférence, workshop, meetup) avec dates, lieu et capacité.
- Inscription et désinscription des participants.
- Statistiques d'audience et suivi de remplissage.

**Module Projet**
- Soumission d'idées et de projets collaboratifs avec description et stack technique.
- Recherche de collaborateurs selon compétences.
- Gestion du statut des projets (idée, en cours, terminé).

**Module Communauté**
- Création et gestion de groupes thématiques.
- Publication de posts et commentaires.
- Système de membres et modération.

---

### Besoins Non Fonctionnels

| Critère | Exigence | Mesure / Mécanisme |
|---------|----------|--------------------|
| **Performance** | Temps de réponse < 200 ms sur les endpoints GET les plus fréquents | Cache Redis avec TTL configuré par service |
| **Disponibilité** | Uptime cible ≥ 99,5 % | Health checks K8s (`/actuator/health`), probes liveness/readiness |
| **Scalabilité** | Montée en charge horizontale par service | Kubernetes Deployments avec réplication indépendante |
| **Maintenabilité** | Couverture de tests ≥ 80 % (gate JaCoCo en CI) | JUnit 5, Mockito, Spring Boot Test |
| **Sécurité** | Authentification stateless sans session serveur | JWT HS256, Spring Security, CORS strict |
| **Fiabilité** | Pas de perte de message sur les événements Kafka critiques | Acquittement manuel Kafka, `AckMode.MANUAL`, skip sur exception non bloquante |
| **Observabilité** | Métriques exposées pour Prometheus | Spring Actuator + Micrometer + Grafana |
| **Portabilité** | Build reproductible dans n'importe quel environnement | Docker multi-stage build, images Alpine JRE |
| **Conformité** | Exécution en utilisateur non-root dans les conteneurs | Dockerfile crée `techhub` group + utilisateur système dédié par service |

---

## 5. User Stories

| ID | En tant que | Je veux | Afin de | Priorité |
|----|-------------|---------|---------|----------|
| US-01 | Visiteur | M'inscrire avec mon email et un mot de passe | Créer mon compte sur TechHub | Haute |
| US-02 | Visiteur | Me connecter via GitHub OAuth2 | Éviter de gérer un nouveau mot de passe | Haute |
| US-03 | Utilisateur authentifié | Créer une équipe avec un nom et une capacité max | Trouver des coéquipiers pour mon hackathon | Haute |
| US-04 | Propriétaire d'équipe | Inviter un utilisateur dans mon équipe | Constituer mon équipe avant la deadline | Haute |
| US-05 | Utilisateur invité | Accepter ou refuser une invitation | Rejoindre l'équipe qui m'intéresse | Haute |
| US-06 | Utilisateur | Recevoir un email lors de mon inscription | Être confirmé que mon compte est actif | Haute |
| US-07 | Utilisateur | Recevoir un email quand je rejoins une équipe | Être notifié de l'événement en temps réel | Moyenne |
| US-08 | Utilisateur | Consulter la liste des équipes disponibles | Trouver une équipe qui me correspond | Moyenne |
| US-09 | Propriétaire d'équipe | Exclure un membre de mon équipe | Maintenir la qualité et la cohésion | Moyenne |
| US-10 | Utilisateur | Quitter une équipe dont je suis membre | Me désengager d'une collaboration | Moyenne |
| US-11 | Utilisateur | Consulter mes notifications passées | Retracer les actions importantes | Basse |
| US-12 | Organisateur | Publier un événement tech avec date et lieu | Annoncer mon hackathon à la communauté | Haute |
| US-13 | Participant | M'inscrire à un événement | Réserver ma place avant saturation | Haute |
| US-14 | Utilisateur | Publier un projet avec sa stack technique | Recruter des collaborateurs compétents | Moyenne |
| US-15 | Administrateur | Modérer les contenus publiés | Maintenir la qualité de la communauté | Basse |
| US-16 | Utilisateur | Changer mon mot de passe | Sécuriser mon compte | Haute |
| US-17 | Utilisateur | Rechercher des équipes par mots-clés | Trouver rapidement une équipe pertinente | Moyenne |
| US-18 | Système | Expirer automatiquement les invitations périmées | Éviter les invitations fantômes en base | Haute |

---

## 6. Architecture Fonctionnelle

TechHub est organisé autour de six domaines métier indépendants, chacun encapsulé dans un microservice autonome. Le frontend React.js communique exclusivement avec un **API Gateway** qui route les requêtes vers le service approprié. La communication entre services est **asynchrone via Kafka** pour les événements métier, et **synchrone via REST** pour les appels nécessitant une réponse immédiate.

### Vue d'Ensemble des Interactions

```mermaid
graph TD
    FE["Frontend React.js\n(localhost:3000)"]
    GW["API Gateway\n(port 8080)"]

    subgraph "Services Metier"
        US["User Service\n(port 8080)"]
        TS["Team Service\n(port 8083)"]
        ES["Event Service\n(port 8082)"]
        PS["Project Service\n(port 8084)"]
        CS["Community Service\n(port 8085)"]
        NS["Notification Service\n(port 8086)"]
    end

    subgraph "Infrastructure"
        PG[("PostgreSQL 16\nBDD par service")]
        RD[("Redis 7\nCache distribue")]
        KF["Apache Kafka\n+ Zookeeper"]
        MH["MailHog\nSMTP Dev"]
    end

    FE --> GW
    GW --> US
    GW --> TS
    GW --> ES
    GW --> PS
    GW --> CS

    US --> PG
    US --> RD
    US --> KF

    TS --> PG
    TS --> RD
    TS -->|"team-invited / team-joined / team-created"| KF

    ES --> PG
    ES --> RD
    ES --> KF

    PS --> PG
    PS --> RD
    PS --> KF

    CS --> PG
    CS --> RD
    CS --> KF

    KF -->|"user-registered / user-password-changed / team-invited / team-joined"| NS
    NS --> PG
    NS --> MH
```

### Description des Flux Principaux

**Inscription utilisateur :** L'utilisateur soumet son formulaire → User Service valide, hache le mot de passe, persiste → publie `user-registered` sur Kafka → Notification Service consomme l'événement → envoie l'email de bienvenue via MailHog → persiste la notification avec statut `SENT`.

**Invitation dans une équipe :** Le propriétaire invite un utilisateur → Team Service vérifie la capacité et l'absence de doublon → crée l'invitation en base (statut `PENDING`, TTL 72h) → publie `team-invited` sur Kafka → Notification Service envoie l'email d'invitation → le scheduler `InvitationExpirationScheduler` expire les invitations à chaque minute.

**Acceptation d'invitation :** L'invité accepte → Team Service marque l'invitation `ACCEPTED` → incrémente `currentMembers` → si `isFull()` : statut équipe passe à `FULL` → publie `team-joined` → Notification Service notifie le propriétaire.

---

## 7. Conception UML

### 7.1 Diagramme de Cas d'Utilisation

#### Acteurs du Système

| Acteur | Description |
|--------|-------------|
| **Visiteur** | Utilisateur non authentifié pouvant consulter les événements publics |
| **Membre** | Utilisateur authentifié avec accès complet aux fonctionnalités sociales |
| **Manager / Propriétaire** | Membre pouvant créer et gérer une équipe ou un événement |
| **Administrateur** | Accès à la modération et aux fonctions d'administration système |
| **Système (Kafka)** | Acteur interne déclenchant des notifications automatiques |

```mermaid
graph LR
    subgraph "Acteurs"
        V["Visiteur"]
        M["Membre"]
        MG["Manager"]
        AD["Admin"]
        SYS["Systeme Kafka"]
    end

    subgraph "Authentification"
        UC1["S inscrire"]
        UC2["Se connecter JWT"]
        UC3["OAuth2 GitHub/Google"]
        UC4["Changer mot de passe"]
    end

    subgraph "Equipe"
        UC5["Creer une equipe"]
        UC6["Inviter un membre"]
        UC7["Accepter/Refuser invitation"]
        UC8["Quitter une equipe"]
        UC9["Exclure un membre"]
        UC10["Rechercher des equipes"]
    end

    subgraph "Notification"
        UC11["Recevoir email"]
        UC12["Consulter notifications"]
        UC13["Marquer comme lu"]
    end

    subgraph "Evenement"
        UC14["Publier un evenement"]
        UC15["S inscrire a un evenement"]
    end

    V --> UC1
    V --> UC2
    V --> UC3
    M --> UC4
    M --> UC7
    M --> UC8
    M --> UC10
    M --> UC12
    M --> UC13
    M --> UC15
    MG --> UC5
    MG --> UC6
    MG --> UC9
    MG --> UC14
    AD --> UC1
    SYS --> UC11
```

---

### 7.2 Diagramme de Classes

#### Entités Principales du Team Service

```mermaid
classDiagram
    class Team {
        +UUID id
        +String name
        +String description
        +Integer maxMembers
        +Integer currentMembers
        +TeamStatus status
        +UUID ownerId
        +Instant createdAt
        +Instant updatedAt
        +isFull() boolean
        +hasCapacity() boolean
        +incrementMembers() void
        +decrementMembers() void
    }

    class TeamMember {
        +UUID id
        +UUID teamId
        +UUID userId
        +MemberRole role
        +Instant joinedAt
        +isOwner() boolean
    }

    class TeamInvitation {
        +UUID id
        +UUID teamId
        +UUID senderId
        +UUID receiverId
        +InvitationStatus status
        +Instant expirationTime
        +Instant createdAt
        +Instant updatedAt
        +isActionable() boolean
        +isExpired() boolean
        +accept() void
        +decline() void
        +expire() void
    }

    class TeamStatus {
        <<enumeration>>
        OPEN
        FULL
    }

    class MemberRole {
        <<enumeration>>
        OWNER
        MEMBER
    }

    class InvitationStatus {
        <<enumeration>>
        PENDING
        ACCEPTED
        DECLINED
        EXPIRED
    }

    Team "1" --> "0..*" TeamMember : contient
    Team "1" --> "0..*" TeamInvitation : genere
    TeamMember --> MemberRole
    TeamInvitation --> InvitationStatus
    Team --> TeamStatus
```

#### Entité du Notification Service

```mermaid
classDiagram
    class Notification {
        +UUID id
        +UUID userId
        +String recipientEmail
        +String displayName
        +NotificationType type
        +NotificationStatus status
        +String failureReason
        +String title
        +String message
        +boolean read
        +String eventId
        +String eventType
        +LocalDateTime createdAt
        +LocalDateTime sentAt
    }

    class NotificationType {
        <<enumeration>>
        USER_REGISTERED
        USER_PASSWORD_CHANGED
        TEAM_INVITED
        TEAM_JOINED
    }

    class NotificationStatus {
        <<enumeration>>
        SENT
        FAILED
    }

    Notification --> NotificationType
    Notification --> NotificationStatus
```

#### Couches Applicatives

```mermaid
classDiagram
    class TeamController {
        -TeamService teamService
        +createTeam(TeamRequest) ResponseEntity
        +getTeam(UUID) ResponseEntity
        +updateTeam(UUID, TeamRequest) ResponseEntity
        +deleteTeam(UUID) ResponseEntity
        +getMyTeams(int, int) ResponseEntity
        +searchTeams(String, int, int) ResponseEntity
        +getTeamMembers(UUID, int, int) ResponseEntity
        +leaveTeam(UUID) ResponseEntity
        +removeMember(UUID, UUID) ResponseEntity
    }

    class TeamService {
        <<interface>>
        +createTeam(TeamRequest) TeamResponse
        +getTeam(UUID) TeamResponse
        +updateTeam(UUID, TeamRequest) TeamResponse
        +deleteTeam(UUID) void
        +getMyTeams(int, int) Page
        +searchTeams(String, int, int) Page
        +getTeamMembers(UUID, int, int) Page
        +leaveTeam(UUID) void
        +removeMember(UUID, UUID) void
    }

    class TeamServiceImpl {
        -TeamRepository teamRepository
        -TeamMemberRepository memberRepository
        -TeamEventProducer eventProducer
        -TeamMapper mapper
    }

    class TeamRepository {
        <<JpaRepository>>
        +findByOwnerId(UUID, Pageable) Page
        +findByNameContainingIgnoreCase(String, Pageable) Page
    }

    class TeamMapper {
        +toResponse(Team) TeamResponse
        +toEntity(TeamRequest) Team
    }

    class TeamEventProducer {
        -KafkaTemplate kafkaTemplate
        +sendTeamCreated(Object) void
        +sendTeamInvited(Object) void
        +sendTeamJoined(Object) void
        +sendTeamFull(Object) void
        +sendTeamLeft(Object) void
    }

    TeamController --> TeamService
    TeamService <|.. TeamServiceImpl
    TeamServiceImpl --> TeamRepository
    TeamServiceImpl --> TeamMapper
    TeamServiceImpl --> TeamEventProducer
```

---

### 7.3 Diagrammes de Séquence

#### Authentification JWT

```mermaid
sequenceDiagram
    actor Client
    participant GW as API Gateway
    participant US as User Service
    participant PG as PostgreSQL
    participant RD as Redis

    Client->>GW: POST /api/auth/login {email, password}
    GW->>US: Forward request
    US->>PG: SELECT * FROM users WHERE email = ?
    PG-->>US: User entity
    US->>US: BCrypt.verify(password, hashedPassword)
    alt Mot de passe incorrect
        US-->>GW: 401 Unauthorized
        GW-->>Client: 401 Unauthorized
    else Authentification reussie
        US->>US: JwtUtil.generateToken(userId, roles)
        US->>RD: SET session:userId token TTL=24h
        US-->>GW: 200 OK {accessToken, refreshToken}
        GW-->>Client: 200 OK {accessToken, refreshToken}
    end

    Note over Client: Requetes suivantes avec Bearer token
    Client->>GW: GET /teams {Authorization: Bearer token}
    GW->>US: Forward + validate token
    US->>US: JwtAuthenticationFilter.doFilterInternal()
    US->>US: JwtUtil.extractUserId(token)
    US-->>GW: SecurityContext.setAuthentication()
    GW->>GW: Route vers Team Service port 8083
```

#### Création et Invitation dans une Équipe

```mermaid
sequenceDiagram
    actor Owner as Proprietaire
    participant TS as Team Service
    participant PG as PostgreSQL team_db
    participant KF as Apache Kafka
    participant NS as Notification Service
    participant MH as MailHog SMTP

    Owner->>TS: POST /teams {name, description, maxMembers}
    TS->>TS: JwtAuthenticationFilter extrait ownerId du JWT
    TS->>PG: INSERT INTO teams (id, owner_id, status=OPEN, current_members=1)
    PG-->>TS: Team persisted
    TS->>PG: INSERT INTO team_members (teamId, userId=ownerId, role=OWNER)
    TS->>KF: send(topic=team-created, payload=TeamCreatedEvent)
    TS-->>Owner: 201 Created {teamId, name, status}

    Owner->>TS: POST /teams/{id}/invitations {receiverId}
    TS->>PG: SELECT * FROM teams WHERE id = ?
    TS->>TS: team.hasCapacity() et pas de doublon ?
    TS->>PG: INSERT INTO team_invitations (status=PENDING, expirationTime=now+72h)
    TS->>KF: send(topic=team-invited, payload=TeamInvitedEvent)
    TS-->>Owner: 201 Created {invitationId, status=PENDING}

    KF-->>NS: consume(topic=team-invited)
    NS->>NS: EventMapper.toTeamInvitedEvent(record.value())
    NS->>MH: sendEmail(to=receiver, template=invitation)
    NS->>PG: INSERT INTO notifications (type=TEAM_INVITED, status=SENT)
```

#### Envoi d'une Notification Kafka

```mermaid
sequenceDiagram
    participant Producer as Service Producteur
    participant KF as Apache Kafka Broker
    participant CG as Consumer Group notification-group
    participant NS as Notification Service
    participant ES as EmailService
    participant MH as MailHog SMTP
    participant PG as PostgreSQL notification_db

    Producer->>KF: kafkaTemplate.send(topic, payload)
    KF-->>Producer: CompletableFuture offset partition

    KF->>CG: Poll - nouveau message disponible
    CG->>NS: UserEventListener.onUserRegistered(ConsumerRecord)
    NS->>NS: EventMapper.toUserRegisteredEvent(record.value())
    NS->>ES: sendWelcomeEmail(event.email, event.displayName)
    ES->>MH: JavaMailSender.send(MimeMessage)
    MH-->>ES: 250 OK SMTP accepted

    alt Email envoye avec succes
        NS->>PG: save Notification status=SENT sentAt=now
    else Erreur d envoi
        NS->>PG: save Notification status=FAILED failureReason=message
        NS->>NS: log.error - ne rethrow pas pour ne pas arreter le consumer
    end

    NS->>CG: acknowledgment.acknowledge() - commit offset manuel
```

---

### 7.4 Diagramme de Déploiement

```mermaid
graph TB
    subgraph "Machine Client"
        BR["Navigateur Web"]
    end

    subgraph "Kubernetes Cluster namespace techhub"
        subgraph "Ingress"
            ING["Ingress Controller NGINX"]
        end

        subgraph "Application Pods"
            US_POD["Pod: userservice\nPort 8080"]
            TS_POD["Pod: team-service\nPort 8083"]
            ES_POD["Pod: event-service\nPort 8082"]
            PS_POD["Pod: project-service\nPort 8084"]
            CS_POD["Pod: community-service\nPort 8085"]
            NS_POD["Pod: notification-service\nPort 8086"]
        end

        subgraph "Infrastructure Pods"
            PG_POD[("PostgreSQL 16-alpine\nPort 5432\n6 databases")]
            RD_POD[("Redis 7-alpine\nPort 6379")]
            KF_POD["Kafka KRaft\nPort 9092"]
        end

        subgraph "Monitoring"
            PROM["Prometheus"]
            GRAF["Grafana"]
        end
    end

    subgraph "GitOps"
        ARGO["ArgoCD\nsync depuis main"]
        GH["GitHub\nmanifests K8s"]
        GHCR["GHCR\nimages Docker"]
    end

    BR --> ING
    ING --> US_POD
    ING --> TS_POD
    ING --> ES_POD
    ING --> PS_POD
    ING --> CS_POD

    US_POD --> PG_POD
    US_POD --> RD_POD
    US_POD --> KF_POD

    TS_POD --> PG_POD
    TS_POD --> RD_POD
    TS_POD --> KF_POD

    ES_POD --> PG_POD
    ES_POD --> RD_POD
    ES_POD --> KF_POD

    PS_POD --> PG_POD
    PS_POD --> RD_POD

    CS_POD --> PG_POD
    CS_POD --> RD_POD
    CS_POD --> KF_POD

    KF_POD --> NS_POD
    NS_POD --> PG_POD

    PROM --> US_POD
    PROM --> TS_POD
    PROM --> NS_POD
    GRAF --> PROM

    GH --> ARGO
    ARGO --> KF_POD
    ARGO --> PG_POD
    GHCR --> US_POD
    GHCR --> TS_POD
```

---

## 8. Architecture Logicielle

TechHub repose sur une architecture microservices au sens strict : chaque service possède **sa propre base de données**, son propre cycle de déploiement et sa propre configuration. Il n'existe aucune dépendance de compilation entre services.

### Cartographie des Services

```mermaid
graph LR
    subgraph "Ports exposition Docker Compose"
        P1["User Service :8080"]
        P2["Event Service :8082"]
        P3["Team Service :8083"]
        P4["Project Service :8084"]
        P5["Community Service :8085"]
        P6["Notification Service :8086"]
    end

    subgraph "Bases de donnees dedies"
        DB1[("user_db")]
        DB2[("event_db")]
        DB3[("team_db")]
        DB4[("project_db")]
        DB5[("community_db")]
        DB6[("notification_db")]
    end

    P1 --> DB1
    P2 --> DB2
    P3 --> DB3
    P4 --> DB4
    P5 --> DB5
    P6 --> DB6
```

### Description des Services

#### User Service (`userservice`, port 8080)

Point d'entrée unique pour l'authentification. Gère les comptes utilisateurs, le hachage des mots de passe (BCrypt), l'émission des tokens JWT et l'intégration OAuth2 (GitHub, Google). Publie les événements `user-registered` et `user-password-changed` sur Kafka. Utilise Redis pour le cache de sessions et les données de profil fréquemment lues.

**Technologies clés :** Spring Security, Spring OAuth2 Client, JJWT, Hibernate, Redis

#### Team Service (`team-service`, port 8083)

Gère le cycle de vie complet des équipes : création, mise à jour, suppression, gestion des membres et des invitations. Implémente un scheduler Spring (`@Scheduled`) pour l'expiration automatique des invitations. Publie cinq types d'événements Kafka : `team-created`, `team-invited`, `team-joined`, `team-full`, `team-left`. Utilise le second-level cache Hibernate via Redisson pour la mise en cache des entités `Team` (stratégie `READ_WRITE`).

**Technologies clés :** Spring Data JPA, Spring Kafka, Redisson, Spring Scheduler

#### Event Service (`event-service`, port 8082)

Gère la publication et la gestion d'événements technologiques. Permet l'inscription des participants avec contrôle de capacité. Expose des statistiques de participation. Cache les événements populaires dans Redis.

#### Project Service (`project-service`, port 8084)

Gère les projets collaboratifs avec leur cycle de vie (idée → en cours → terminé). Offre un système de recherche par compétences et de mise en relation entre porteurs de projets et contributeurs potentiels.

#### Community Service (`communityservice`, port 8085)

Gère les groupes thématiques, les publications et les interactions communautaires. Supporte la pagination des flux de contenu avec cache Redis.

#### Notification Service (`notification-service`, port 8086)

Service purement réactif : il ne reçoit que des messages Kafka et ne produit aucun appel REST sortant vers d'autres services. Consomme quatre topics : `user-registered`, `user-password-changed`, `team-invited`, `team-joined`. Utilise MailHog en développement comme serveur SMTP local. Chaque consommateur est configuré avec acquittement manuel (`AckMode.MANUAL`) et ne rethrow jamais d'exceptions pour éviter l'arrêt du consumer group.

**Technologies clés :** Spring Kafka, Spring Mail, Spring Data JPA

### Communication Inter-Services

| Mode | Technologie | Quand l'utiliser |
|------|-------------|-----------------|
| **Synchrone** | REST/HTTP + JWT | Requêtes client → service avec besoin de réponse immédiate |
| **Asynchrone** | Apache Kafka | Événements métier découplés (notification, audit, réaction) |
| **Cache** | Redis | Éviter les appels répétés à PostgreSQL sur données stables |

### Topics Kafka du Projet

| Topic | Producteur | Consommateur | Déclencheur |
|-------|------------|--------------|-------------|
| `user-registered` | User Service | Notification Service | Nouvel utilisateur créé |
| `user-password-changed` | User Service | Notification Service | Changement de mot de passe |
| `team-created` | Team Service | — | Nouvelle équipe créée |
| `team-invited` | Team Service | Notification Service | Invitation envoyée |
| `team-joined` | Team Service | Notification Service | Invitation acceptée |
| `team-full` | Team Service | — | Équipe atteint sa capacité max |
| `team-left` | Team Service | — | Membre quitte l'équipe |

---

## 9. Choix Technologiques

| Technologie | Version | Utilisation dans TechHub | Justification |
|-------------|---------|--------------------------|---------------|
| **Java** | 21 (LTS) | Langage principal de tous les microservices | Dernière LTS avec support Temurin garanti jusqu'en 2030. Records, sealed classes, performance améliorée. |
| **Spring Boot** | 3.x | Framework applicatif de chaque microservice | Autoconfiguration, Actuator intégré, compatibilité Jakarta EE 10. Réduction drastique du boilerplate. |
| **Spring Security** | 6.x | Authentification JWT, CORS, `@PreAuthorize` | Intégration native avec Spring Boot 3, SecurityFilterChain fluent API, method security sans XML. |
| **Spring Data JPA** | 3.x | ORM et accès aux données PostgreSQL | Repositories génériques, queries JPQL, pagination intégrée. Évite le SQL manuel répétitif. |
| **PostgreSQL** | 16 | Base de données relationnelle par service | ACID complet, support UUID natif, index partiels, types JSON. 6 bases isolées via `postgres-init.sql`. |
| **Apache Kafka** | 7.6.1 (Confluent) | Bus de messages asynchrone inter-services | Rétention configurable, consumer groups, acquittement manuel. Découple totalement producteurs et consommateurs. |
| **Redis** | 7 | Cache distribué, cache Hibernate L2 | Ultra-faible latence (<1 ms), TTL natif, support Redisson pour second-level cache. |
| **Redisson** | — | Second-level cache Hibernate (Team Service) | Intégration Hibernate 6, stratégie `READ_WRITE` sur entités `Team` mise en cache dans région nommée `teams`. |
| **Docker** | — | Conteneurisation multi-stage | Image minimale Alpine JRE + non-root user + Spring Boot layered JAR = images < 200 MB. |
| **Kubernetes** | — | Orchestration en cluster | Déploiements reproductibles, probes de santé, ConfigMaps/Secrets, namespace isolé `techhub`. |
| **ArgoCD** | — | GitOps Continuous Delivery | Synchronisation automatique depuis le dépôt Git, réconciliation de l'état déclaré. |
| **GitHub Actions** | — | CI/CD automatisé | Pipeline test → build → push GHCR déclenché sur push/PR. Cache Maven GHA. Gate JaCoCo 80 %. |
| **GHCR** | — | Registry d'images Docker | Intégré à GitHub, authentification via `GITHUB_TOKEN`, tags SHA immutables (`sha-a1b2c3d`). |
| **Swagger / OpenAPI** | 3.0 | Documentation API REST | Interface interactive, génération auto depuis annotations Spring. URL : `/swagger-ui.html`. |
| **JUnit 5** | — | Tests unitaires | Annotations déclaratives, paramétrage, modèle d'extension. |
| **Mockito** | — | Mocking dans les tests unitaires | Stubbing précis, vérification des interactions entre couches. |
| **JaCoCo** | — | Couverture de code | Gate CI à 80 % sur les lignes — bloque le merge si non atteint. |
| **Lombok** | — | Réduction du boilerplate Java | `@Builder`, `@Getter`, `@RequiredArgsConstructor` — élimine ~60 % du code répétitif. |
| **MailHog** | 1.0.1 | SMTP de développement | Interface web sur `:8025`, intercepte tous les emails sans envoi réel. Remplacé par un SMTP réel en production. |
| **React.js** | — | Interface utilisateur frontend | Composants réutilisables, routing SPA, consommation des APIs REST. |
| **Tailwind CSS** | — | Styling utilitaire | Design system cohérent via classes utilitaires, compatible avec shadcn/ui. |
| **Maven** | 3.9 | Gestion de build et dépendances | Phases lifecycle (clean/verify/package), plugins Surefire/Failsafe/JaCoCo, cache GHA. |

---

## 10. Implémentation Backend

### Organisation du Code — Structure Type par Service

Chaque microservice TechHub respecte la même organisation en couches verticales, isolant clairement les responsabilités selon le principe de **Single Responsibility (SOLID)** :

```
src/main/java/com/techhub/{service-name}/
│
├── controller/          ← Couche présentation : reçoit HTTP, valide @Valid, délègue
│   ├── TeamController.java
│   └── InvitationController.java
│
├── service/             ← Interface du contrat métier (Dependency Inversion)
│   ├── TeamService.java
│   └── InvitationService.java
│
├── service/impl/        ← Implémentation : logique métier, transactions, orchestration
│   ├── TeamServiceImpl.java
│   └── InvitationServiceImpl.java
│
├── repository/          ← Couche accès données : Spring Data JPA
│   ├── TeamRepository.java
│   ├── TeamMemberRepository.java
│   └── InvitationRepository.java
│
├── entity/              ← Entités JPA persistées en base
│   ├── Team.java
│   ├── TeamMember.java
│   ├── TeamInvitation.java
│   └── enums/
│       ├── TeamStatus.java      (OPEN, FULL)
│       ├── MemberRole.java      (OWNER, MEMBER)
│       └── InvitationStatus.java (PENDING, ACCEPTED, DECLINED, EXPIRED)
│
├── dto/                 ← Data Transfer Objects : découplage API / modèle interne
│   ├── TeamRequest.java
│   ├── TeamResponse.java
│   ├── InvitationRequest.java
│   ├── InvitationResponse.java
│   └── TeamMemberResponse.java
│
├── mapper/              ← Conversion Entity <-> DTO
│   ├── TeamMapper.java
│   └── InvitationMapper.java
│
├── event/               ← Kafka Producer : publication des événements métier
│   └── TeamEventProducer.java
│
├── kafka/               ← Kafka Consumer (Notification Service)
│   ├── consumer/
│   │   ├── UserEventListener.java
│   │   └── TeamEventListener.java
│   └── event/
│       ├── UserRegisteredEvent.java
│       ├── TeamInvitedEvent.java
│       └── TeamJoinedEvent.java
│
├── config/              ← Configuration Spring : Security, Kafka, Redis, OpenAPI
│   ├── SecurityConfig.java
│   ├── KafkaConfig.java
│   ├── RedisConfig.java
│   └── OpenApiConfig.java
│
├── exception/           ← Gestion centralisée des erreurs (@RestControllerAdvice)
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── DuplicateInvitationException.java
│   ├── TeamFullException.java
│   ├── InvitationExpiredException.java
│   ├── ForbiddenException.java
│   └── UnauthorizedException.java
│
├── scheduler/           ← Tâches planifiées (@Scheduled)
│   └── InvitationExpirationScheduler.java
│
└── util/                ← Utilitaires transversaux
    ├── JwtUtil.java
    └── SecurityUtils.java
```

### Design Patterns Appliqués

| Pattern | Localisation dans TechHub | Rôle |
|---------|--------------------------|------|
| **Repository** | `TeamRepository`, `InvitationRepository`, `NotificationRepository` | Abstraction de l'accès aux données, queries JPQL paginées |
| **Builder** | Toutes les entités via `@Builder` Lombok | Construction d'objets complexes sans constructeur surchargé |
| **Strategy** | `EmailService` (interface) / `EmailServiceImpl` (implémentation concrète SMTP) | Séparation du contrat d'envoi d'email de son implémentation |
| **Observer** | `TeamEventProducer` → Kafka → `UserEventListener` / `TeamEventListener` | Découplage événementiel : le producteur ne connaît pas ses consommateurs |
| **Facade** | `TeamServiceImpl` orchestre `TeamRepository`, `TeamMemberRepository`, `InvitationRepository` et `TeamEventProducer` | Interface unique masquant la complexité de coordination |
| **Template Method** | `TemplateService` (Notification Service) | Algorithme d'envoi commun, corps d'email varié par sous-type d'événement |
| **Factory** | `KafkaConfig.ConcurrentKafkaListenerContainerFactory` | Création configurée des conteneurs Kafka listener |

### Gestion des Exceptions

Le pattern `@RestControllerAdvice` via `GlobalExceptionHandler` centralise la transformation des exceptions métier en réponses HTTP normalisées :

| Exception | Code HTTP | Déclencheur |
|-----------|-----------|-------------|
| `ResourceNotFoundException` | 404 | Équipe, invitation ou membre inexistant |
| `DuplicateInvitationException` | 409 | Invitation doublon (même team + receiver en PENDING) |
| `TeamFullException` | 409 | Invitation alors que `currentMembers >= maxMembers` |
| `InvitationExpiredException` | 410 | Action sur invitation post-expiration |
| `ForbiddenException` | 403 | Action réservée au propriétaire tentée par un membre |
| `UnauthorizedException` | 401 | Token JWT absent ou invalide |

### Scheduler d'Expiration des Invitations

Le `InvitationExpirationScheduler` s'exécute toutes les 60 secondes (configurable via `app.invitation.scheduler-rate-ms`) et appelle `InvitationService.expirePendingInvitations()`. Seul le Team Service est responsable de la transition d'état `PENDING → EXPIRED`, évitant toute redondance entre services.

```java
@Scheduled(fixedRateString = "${app.invitation.scheduler-rate-ms:60000}")
public void expireInvitations() {
    invitationService.expirePendingInvitations();
}
```

---

## 11. Base de Données

### Modèle de Données

TechHub adopte le principe **Database-per-Service** : chaque microservice possède une base PostgreSQL strictement isolée. Il n'existe aucune jointure SQL cross-service. L'intégrité référentielle entre services (ex. `owner_id` dans `teams` référençant un utilisateur dans `user_db`) est garantie **par la couche service** et les claims JWT, et non par des foreign keys inter-bases.

### Initialisation — `postgres-init.sql`

```sql
-- Création des 6 bases au démarrage du conteneur PostgreSQL
CREATE DATABASE user_db;
CREATE DATABASE event_db;
CREATE DATABASE community_db;
CREATE DATABASE notification_db;
CREATE DATABASE project_db;
CREATE DATABASE team_db;
```

### Schéma ERD — Team Service (`team_db`)

```mermaid
erDiagram
    teams {
        UUID id PK
        VARCHAR_120 name
        TEXT description
        INTEGER max_members
        INTEGER current_members
        VARCHAR_10 status
        UUID owner_id
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    team_members {
        UUID id PK
        UUID team_id FK
        UUID user_id
        VARCHAR_10 role
        TIMESTAMP joined_at
    }

    team_invitations {
        UUID id PK
        UUID team_id FK
        UUID sender_id
        UUID receiver_id
        VARCHAR_10 status
        TIMESTAMP expiration_time
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    teams ||--o{ team_members : contient
    teams ||--o{ team_invitations : genere
```

### Schéma ERD — Notification Service (`notification_db`)

```mermaid
erDiagram
    notifications {
        UUID id PK
        UUID user_id
        VARCHAR recipient_email
        VARCHAR display_name
        VARCHAR type
        VARCHAR status
        VARCHAR_500 failure_reason
        VARCHAR title
        VARCHAR_1000 message
        BOOLEAN read
        VARCHAR event_id
        VARCHAR event_type
        TIMESTAMP created_at
        TIMESTAMP sent_at
    }
```

### Contraintes et Index

**Table `teams`**
- `idx_teams_owner_id` sur `owner_id` — accélère `GET /teams/my-teams` (filtre par propriétaire)
- `idx_teams_status` sur `status` — accélère les recherches d'équipes `OPEN`
- DDL : `validate` en production, `create-drop` en développement Docker

**Table `team_members`**
- `idx_team_members_team_id` sur `team_id` — accélère `GET /teams/{id}/members`
- `idx_team_members_user_id` sur `user_id` — accélère la recherche des équipes d'un utilisateur
- `UNIQUE(team_id, user_id)` — empêche les doublons de membres

**Table `team_invitations`**
- `idx_invitations_receiver_id` — accélère la récupération des invitations reçues
- `idx_invitations_team_id` — accélère les requêtes par équipe
- `idx_invitations_status` — accélère le scheduler d'expiration (filtre `PENDING`)

### Redis — Cache Distribué

Redis 7 est utilisé à deux niveaux distincts dans TechHub :

**1. Cache applicatif (Spring Cache)**
- TTL configuré par service via `@Cacheable`, `@CacheEvict`
- Clés préfixées par service pour éviter les collisions entre services partageant la même instance Redis

**2. Second-level cache Hibernate (Team Service)**
- Entités `Team` mises en cache avec stratégie `READ_WRITE` (cohérente avec les updates transactionnels)
- Région nommée `teams` — isolée des autres caches
- Désactivé en mode Docker dev (`application-docker.yml`) pour simplifier la configuration locale

```yaml
# application.yml — activation Hibernate L2 via Redisson
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_second_level_cache: true
          region.factory_class: org.redisson.hibernate.RedissonRegionFactory
```

---

## 12. Sécurité

### Spring Security

Chaque microservice configure sa propre `SecurityFilterChain` en mode **stateless** — aucune session HTTP n'est créée ou maintenue côté serveur. La configuration suit le principe de moindre privilège :

```
Endpoints publics (sans JWT) :
  GET  /teams/{id}              — Consultation publique d'une équipe
  GET  /actuator/health/**      — Probes K8s liveness/readiness
  GET  /actuator/prometheus     — Scraping Prometheus
  GET  /swagger-ui/**           — Documentation API (dev uniquement)
  GET  /v3/api-docs/**          — Spec OpenAPI

Endpoints protégés (JWT obligatoire) :
  Toutes les autres routes → anyRequest().authenticated()
```

### JWT — Flux d'Authentification

```mermaid
sequenceDiagram
    participant REQ as Requete HTTP
    participant FILTER as JwtAuthenticationFilter
    participant UTIL as JwtUtil
    participant CTX as SecurityContext

    REQ->>FILTER: OncePerRequestFilter.doFilterInternal()
    FILTER->>FILTER: Extraire header Authorization
    alt Header absent ou malformé
        FILTER->>FILTER: chain.doFilter() sans authentification
    else Bearer token présent
        FILTER->>UTIL: extractUserId(token)
        UTIL->>UTIL: Jwts.parserBuilder().setSigningKey(secret).build()
        alt Token invalide ou expiré
            UTIL-->>FILTER: JwtException
            FILTER->>REQ: 401 via JwtAuthenticationEntryPoint
        else Token valide
            UTIL-->>FILTER: UUID userId
            FILTER->>CTX: SecurityContextHolder.setAuthentication()
            FILTER->>FILTER: chain.doFilter() - acces autorisé
        end
    end
```

**Structure du payload JWT :**
```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "roles": ["ROLE_USER"],
  "iat": 1717920000,
  "exp": 1718006400
}
```

Le secret JWT (`JWT_SECRET`) est injecté par variable d'environnement. En production Kubernetes, il est stocké dans un `Secret` K8s monté en variable d'environnement dans chaque pod.

### BCrypt

Les mots de passe ne sont jamais stockés en clair. BCrypt avec un facteur de coût de 10 rounds (défaut Spring Security) est utilisé. La vérification s'effectue via `PasswordEncoder.matches()` sans déchiffrer le hash.

### Contrôle d'Accès par Rôle (RBAC)

`@EnableMethodSecurity(prePostEnabled = true)` est activé sur chaque service. Les vérifications d'autorisation au niveau métier utilisent `SecurityUtils.getCurrentUserId()` pour comparer l'identité du demandeur avec le propriétaire de la ressource.

| Rôle | Permissions |
|------|-------------|
| **OWNER** | Créer, modifier, supprimer son équipe ; inviter et exclure des membres |
| **MEMBER** | Consulter les équipes, accepter/décliner une invitation, quitter une équipe |
| **ADMIN** | Modération globale, supervision des contenus et des utilisateurs |

### CORS

Configuration centralisée dans `SecurityConfig.corsConfigurationSource()`. Les origines autorisées sont injectées via `app.cors.allowed-origins` (défaut dev : `http://localhost:3000,http://localhost:5173`). Méthodes autorisées : `GET, POST, PUT, PATCH, DELETE, OPTIONS`.

---

### Patterns de Sécurité JWT — Comparaison et Choix Architectural

TechHub utilise deux approches de validation JWT selon les services. Ce choix est **délibéré** et reflète des niveaux de maturité et de sécurité différents.

#### Pattern A — Validation JWT in-service (Team Service) ✅ Recommandé

Le Team Service valide lui-même chaque token JWT entrant via un filtre dédié :

```
Requête HTTP → JwtAuthenticationFilter → JwtUtil.isTokenValid(token)
                                       ↓
                              Vérification cryptographique HMAC-HS512
                              avec secret partagé JWT_SECRET
                                       ↓
                     OK → SecurityContext.setAuthentication(userId, roles)
                     KO → 401 Unauthorized (JwtAuthenticationEntryPoint)
```

**Implémentation clé :**

```java
// JwtUtil.java — Team Service
public String extractUserId(String token) {
    // Lit le claim "userId" (UUID) injecté par User Service au moment de l'émission
    Object userId = parseClaims(token).get("userId");
    if (userId != null) return userId.toString();
    return parseClaims(token).getSubject(); // fallback
}

// SecurityUtils.java — utilisé dans la couche service
public static UUID getCurrentUserId() {
    // Principal = UUID extrait du JWT vérifié — impossible à forger sans le secret
    return UUID.fromString(authentication.getPrincipal().toString());
}
```

**Structure complète du payload JWT (depuis User Service) :**
```json
{
  "sub":    "test@techhub.dev",
  "userId": "4ceef1f6-ecc0-47e9-bb67-bc93f3a1d83c",
  "iat":    1780987534,
  "exp":    1781073934
}
```

> Le claim `userId` (UUID) est injecté par `AuthService.buildAuthResponse()` dans User Service :
> ```java
> Map<String, Object> extraClaims = new HashMap<>();
> extraClaims.put("userId", user.getId().toString());
> String accessToken = jwtService.generateAccessToken(extraClaims, userDetails);
> ```

**Avantages :**
- Un attaquant **ne peut pas usurper une identité** en envoyant un header arbitraire `X-User-Id`
- Fonctionne de manière sécurisée **sans API Gateway** (environnement dev, tests d'intégration)
- Chaque service est **autonome** — défense en profondeur (defense in depth)
- Correspond à l'architecture cible Kubernetes où chaque pod doit se défendre indépendamment

**Contrainte de coordination :** Le secret `JWT_SECRET` doit être **identique** dans tous les services qui valident les tokens. En développement, il est injecté via `docker-compose.yml`. En production K8s, il est stocké dans un `Secret` Kubernetes monté en variable d'environnement dans chaque `Deployment`.

```yaml
# Kubernetes Secret (production) — jamais committé en clair
apiVersion: v1
kind: Secret
metadata:
  name: jwt-secret
  namespace: techhub
type: Opaque
stringData:
  JWT_SECRET: <valeur générée avec openssl rand -hex 32>
```

---

#### Pattern B — Délégation à un API Gateway (Community Service) ⚠️ Dev uniquement

Le Community Service adopte une approche simplifiée en déléguant la validation JWT à un composant amont (API Gateway) :

```java
// SecurityConfig.java — Community Service
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/**").permitAll()  // Gateway valide le JWT avant de router
    .anyRequest().authenticated()
)
```

**Principe :** L'API Gateway intercepte chaque requête, valide le token JWT, puis ajoute le header `X-User-Id` avant de transmettre la requête au service. Le service fait confiance à ce header sans re-vérifier la signature.

**Risque en environnement sans gateway :**

| Scénario | Pattern A (Team Service) | Pattern B (Community Service) |
|----------|--------------------------|-------------------------------|
| Requête directe sans token | ❌ 401 rejeté | ✅ Accepté (permissif) |
| Header `X-User-Id` forgé | ❌ Ignoré — UUID vient du JWT vérifié | ⚠️ Accepté sans vérification |
| Secret JWT différent | ❌ 401 — validation échoue | N/A — pas de validation |
| Sans API Gateway | ✅ Sécurisé | ⚠️ Tout utilisateur peut forger son identité |

> **Conclusion :** Le Pattern B n'est sûr qu'en production avec un API Gateway (NGINX, Spring Cloud Gateway, Kong) configuré pour bloquer tout accès direct aux services. En développement sans gateway, **n'importe qui peut appeler le Community Service avec un UUID arbitraire**. Le Pattern A (Team Service) est sécurisé dans tous les environnements et constitue la **référence architecturale** pour les nouveaux services TechHub.

---

## 13. Documentation API

### Swagger / OpenAPI 3.0

Chaque service expose sa documentation interactive via **SpringDoc OpenAPI**. L'interface Swagger UI est accessible en développement :

| Service | URL Swagger UI | Port |
|---------|---------------|------|
| User Service | `http://localhost:8080/swagger-ui.html` | 8080 |
| Event Service | `http://localhost:8082/swagger-ui.html` | 8082 |
| Team Service | `http://localhost:8083/swagger-ui.html` | 8083 |
| Project Service | `http://localhost:8084/swagger-ui.html` | 8084 |
| Community Service | `http://localhost:8085/swagger-ui.html` | 8085 |
| Notification Service | `http://localhost:8086/swagger-ui.html` | 8086 |

### Référentiel des Endpoints — Team Service

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| `POST` | `/teams` | JWT | Créer une nouvelle équipe |
| `GET` | `/teams/{id}` | Public | Obtenir les détails d'une équipe |
| `PUT` | `/teams/{id}` | JWT (owner) | Mettre à jour une équipe |
| `DELETE` | `/teams/{id}` | JWT (owner) | Supprimer une équipe |
| `GET` | `/teams/my-teams` | JWT | Lister ses propres équipes (paginé) |
| `GET` | `/teams/search` | JWT | Rechercher des équipes par mots-clés |
| `GET` | `/teams/{id}/members` | JWT | Lister les membres d'une équipe (paginé) |
| `POST` | `/teams/{id}/leave` | JWT | Quitter une équipe |
| `DELETE` | `/teams/{id}/members/{memberId}` | JWT (owner) | Exclure un membre |
| `POST` | `/teams/{id}/invitations` | JWT (owner) | Envoyer une invitation |
| `POST` | `/invitations/{id}/accept` | JWT | Accepter une invitation |
| `POST` | `/invitations/{id}/decline` | JWT | Refuser une invitation |
| `GET` | `/invitations/received` | JWT | Lister les invitations reçues |

### Référentiel des Endpoints — Notification Service

| Méthode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| `GET` | `/notifications` | JWT | Lister les notifications de l'utilisateur |
| `GET` | `/notifications/{id}` | JWT | Obtenir une notification par ID |
| `PATCH` | `/notifications/{id}/read` | JWT | Marquer une notification comme lue |

### Exemples de Requêtes

**Créer une équipe :**
```http
POST /teams
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "name": "Team Phoenix",
  "description": "Équipe pour le hackathon IA de juin",
  "maxMembers": 5
}
```

```http
HTTP/1.1 201 Created
Location: /teams/550e8400-e29b-41d4-a716-446655440000

{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Team Phoenix",
  "description": "Équipe pour le hackathon IA de juin",
  "maxMembers": 5,
  "currentMembers": 1,
  "status": "OPEN",
  "ownerId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "createdAt": "2026-06-09T10:00:00Z"
}
```

**Inviter un membre :**
```http
POST /teams/550e8400-e29b-41d4-a716-446655440000/invitations
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "receiverId": "b2c3d4e5-f6a7-8901-bcde-f12345678901"
}
```

```http
HTTP/1.1 201 Created

{
  "id": "c3d4e5f6-a7b8-9012-cdef-123456789012",
  "teamId": "550e8400-e29b-41d4-a716-446655440000",
  "receiverId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "status": "PENDING",
  "expirationTime": "2026-06-12T10:00:00Z",
  "createdAt": "2026-06-09T10:00:00Z"
}
```

---

## 14. Tests

### Stratégie de Test

TechHub adopte une pyramide de tests à trois niveaux, couvrant la logique de domaine (entités/mappers), l'accès aux données (repositories) et l'intégration Spring Boot complète.

### Tests Unitaires — JUnit 5 & Mockito

Les tests unitaires ciblent la logique de domaine dans les entités et les mappers, sans aucune infrastructure externe.

**Team Service — tests d'entités :**
- `TeamTest` — `isFull()`, `hasCapacity()`, `incrementMembers()`, `decrementMembers()`
- `TeamMemberTest` — `isOwner()`, cycle de vie `@PrePersist`
- `TeamInvitationTest` — `isActionable()`, `isExpired()`, `accept()`, `decline()`, `expire()`

**Team Service — tests de mappers :**
- `TeamMapperTest` — conversion `TeamRequest → Team`, `Team → TeamResponse`
- `InvitationMapperTest` — conversion `TeamInvitation → InvitationResponse`

```java
// Exemple — test logique de domaine Team
@Test
void team_should_become_FULL_when_capacity_reached() {
    Team team = Team.builder()
        .maxMembers(3)
        .currentMembers(2)
        .status(TeamStatus.OPEN)
        .build();

    team.incrementMembers();

    assertThat(team.isFull()).isTrue();
    assertThat(team.getStatus()).isEqualTo(TeamStatus.FULL);
}
```

### Tests d'Intégration — Spring Boot Test

Les tests de repository utilisent `@DataJpaTest` avec une base H2 in-memory. Le profil `test` active `application-test.yml` qui configure H2, exclut Redis et utilise `@EmbeddedKafka` pour les tests Kafka.

**Team Service — tests de repositories :**
- `TeamRepositoryTest` — findByOwnerId, recherche textuelle paginée
- `TeamMemberRepositoryTest` — findByTeamId, contrainte unique team+user
- `InvitationRepositoryTest` — findPendingExpired, findByReceiverIdAndStatus

```java
// Exemple — test repository
@DataJpaTest
class InvitationRepositoryTest {

    @Autowired
    InvitationRepository repository;

    @Test
    void should_find_pending_invitations_past_expiration() {
        TeamInvitation expired = TeamInvitation.builder()
            .expirationTime(Instant.now().minusSeconds(3600))
            .status(InvitationStatus.PENDING)
            .build();
        repository.save(expired);

        List<TeamInvitation> result =
            repository.findExpiredPendingInvitations(Instant.now());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(InvitationStatus.PENDING);
    }
}
```

### Couverture de Code — JaCoCo

Le plugin JaCoCo est configuré avec une **gate de couverture de 80 % sur les lignes**, vérifiée lors de la phase `mvn verify`. Le CI bloque le merge si le seuil n'est pas atteint. Les rapports HTML sont publiés en artifact GitHub Actions (rétention 7 jours).

```xml
<!-- Extrait pom.xml Team Service — gate JaCoCo -->
<execution>
  <id>jacoco-check</id>
  <goals><goal>check</goal></goals>
  <configuration>
    <rules>
      <rule>
        <element>BUNDLE</element>
        <limits>
          <limit>
            <counter>LINE</counter>
            <value>COVEREDRATIO</value>
            <minimum>0.80</minimum>
          </limit>
        </limits>
      </rule>
    </rules>
  </configuration>
</execution>
```

### Tableau Récapitulatif des Tests

| Service | Tests unitaires | Tests repository | Couverture cible | Outils |
|---------|----------------|------------------|-----------------|--------|
| Team Service | Entités (Team, TeamMember, TeamInvitation), Mappers | TeamRepo, MemberRepo, InvitationRepo | ≥ 80 % lignes | JUnit 5, Mockito, `@DataJpaTest` |
| Notification Service | EventMapper, NotificationMapper | NotificationRepository | ≥ 80 % lignes | JUnit 5, Mockito |
| Event Service | Entités métier, Mappers | EventRepository | ≥ 80 % lignes | JUnit 5, Spring Boot Test |
| Project Service | Entités métier, Mappers | ProjectRepository | ≥ 80 % lignes | JUnit 5, Mockito |
| User Service | JwtUtil, PasswordEncoder | UserRepository | ≥ 80 % lignes | JUnit 5, `@WebMvcTest` |

---
 lien partie 2 :
 [README_DEVOPS.md](README_DEVOPS.md)
 