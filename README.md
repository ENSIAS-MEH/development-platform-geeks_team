# TechHub – Collaborative Tech Community Platform

## Theme

**Applications sociales et interactives pour les jeunes**

TechHub est une plateforme communautaire destinée aux étudiants, développeurs et passionnés de technologie afin de connecter **événements tech, projets collaboratifs et communautés** dans un même espace.

---
# Architecture Globale

![Architecture](https://github.com/ENSIAS-MEH/development-platform-geeks_team/blob/6c262ec4757055c7f0639911067b52cc620ccc98/Global%20Project%20Architecture.png)


# 1. Introduction

Aujourd’hui, l’écosystème tech pour les jeunes est **fragmenté entre plusieurs plateformes** :

* les hackathons sont publiés sur Devpost
* les conférences sur LinkedIn ou Eventbrite
* les projets sur GitHub
* les discussions sur Discord ou WhatsApp

Cette dispersion rend difficile :

* la **découverte d’événements**
* la **formation d’équipes pour des projets**
* la **continuité des collaborations après un hackathon**
* la **visibilité des communautés tech locales**

TechHub propose donc **une plateforme centralisée** permettant de connecter les **événements, les projets et les membres d’une communauté technologique** afin de favoriser la collaboration, le networking et l’innovation.

---

# 2. Objectif du projet

Le projet consiste à **concevoir et développer une plateforme communautaire permettant aux jeunes développeurs et étudiants de découvrir des événements tech, partager des projets, trouver des collaborateurs et interagir au sein d’une communauté technologique active.**

La plateforme permettra notamment :

* la publication et la gestion d’événements technologiques
* la publication d’idées et de projets collaboratifs
* la mise en relation de profils techniques
* la création d’une communauté active autour de la technologie

---

# 3. Acteurs du système

### 1. Utilisateur (Developer / Student)

Peut :

* créer un profil technique
* publier des projets ou idées
* rejoindre des projets
* participer à des événements
* créer ou rejoindre des équipes
* commenter et interagir dans la communauté

### 2. Organisateur d’événements

Peut :

* publier des événements (hackathons, conférences, workshops)
* gérer les inscriptions
* suivre les participants
* gérer la formation des équipes

### 3. Administrateur

Responsable de :

* la modération de la plateforme
* la gestion des utilisateurs
* la supervision des contenus et événements

---

# 4. Fonctionnalités principales

## 4.1 Gestion des utilisateurs

Chaque utilisateur possède :

* un **profil technique**
* une liste de **compétences**
* un **portfolio**
* un historique de **participation aux événements**

Fonctionnalités :

* inscription / authentification
* gestion du profil
* ajout de compétences
* suivi d’activités

---

## 4.2 Gestion des événements

Les organisateurs peuvent publier :

* hackathons
* conférences
* workshops
* compétitions
* meetups tech

Fonctionnalités :

* publication d’événements
* inscription des participants
* formation d’équipes
* gestion d’agenda
* notifications aux participants

---

## 4.3 Projets et idées

Les utilisateurs peuvent publier :

* idées de startup
* projets open source
* projets étudiants
* projets hackathon

Chaque projet peut contenir :

* description
* technologies utilisées
* compétences recherchées
* statut du projet
* lien GitHub

Les autres utilisateurs peuvent :

* rejoindre un projet
* proposer des contributions
* discuter autour du projet

---

## 4.4 Recherche de collaborateurs

La plateforme propose un **système de matching entre profils et projets**.

Exemples :

* *Cherche développeur Frontend React pour projet EdTech*
* *Cherche Data Scientist pour hackathon AI*

La plateforme peut suggérer :

* des profils compatibles
* des projets correspondant aux compétences

---

## 4.5 Communauté et networking

Les utilisateurs peuvent :

* créer des **groupes thématiques**
* commenter projets et idées
* suivre des événements
* interagir avec des mentors
* développer leur réseau professionnel

---

# 5. Architecture du système

Le système est conçu selon une **architecture microservices** afin d'assurer la scalabilité, la modularité et la maintenabilité.

Les principaux services sont :

* **User Service**
  gestion des utilisateurs et authentification

* **Event Service**
  gestion des événements et inscriptions

* **Project Service**
  gestion des projets et collaborations

* **Team Service**
  gestion des équipes pour les projets et événements

* **Community Service**
  gestion des groupes et interactions communautaires

* **Notification Service**
  gestion des notifications (emails, push)

Un **API Gateway** centralise les requêtes provenant du frontend.

La communication entre services utilise un **message broker (Kafka / RabbitMQ)**.

L'infrastructure peut être déployée avec **Docker et Kubernetes**.

*(Insérer ici le diagramme d’architecture du projet)*

---

# 6. Technologies utilisées

## Backend

* **Spring Boot**
* Spring Security
* Spring Data JPA
* REST APIs
* Kafka / RabbitMQ
* Docker

## Frontend

* **React.js / Next.js**
* Tailwind CSS ou Material UI
* Axios / Fetch API

## Base de données

* PostgreSQL ou MySQL

## Infrastructure

* Docker
* Kubernetes
* API Gateway
* CI/CD (GitHub Actions ou GitLab CI)

---

# 7. Structure globale du projet

```
techhub
│
├── frontend (Next.js / React)
│
├── api-gateway
│
├── user-service
│
├── event-service
│
├── project-service
│
├── team-service
│
├── community-service
│
├── notification-service
│
├── docker
│
└── docs
```

---

# 8. Vision du projet

TechHub vise à devenir **une plateforme centrale pour l’écosystème tech étudiant**, permettant :

* de découvrir des événements technologiques
* de transformer des idées en projets réels
* de connecter des profils complémentaires
* de créer une communauté tech durable

---

# 9. Équipe du projet

Équipe :

* Alae LABHAL
* Hafsa ABBAR
* Halima ANEJARI
* Kawtar LAMEGHAIZI

Encadrant :

* Pr. Mahmoud El Hamlaoui
---
