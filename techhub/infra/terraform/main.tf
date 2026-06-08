terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.0"
    }
  }
}

provider "kubernetes" {
  config_path    = "~/.kube/config"
  config_context = "minikube"
}

provider "helm" {
  kubernetes {
    config_path    = "~/.kube/config"
    config_context = "minikube"
  }
}

# ─── Namespace ───────────────────────────────────────────────
resource "kubernetes_namespace" "techhub" {
  metadata {
    name = var.namespace
  }
}

# ─── Secrets ─────────────────────────────────────────────────
resource "kubernetes_secret" "userservice_secrets" {
  metadata {
    name      = "userservice-secrets"
    namespace = kubernetes_namespace.techhub.metadata[0].name
  }

  data = {
    DB_PASSWORD          = var.db_password
    JWT_SECRET           = var.jwt_secret
    CLIENT_ID            = var.github_client_id
    CLIENT_SECRET        = var.github_client_secret
    GOOGLE_CLIENT_ID     = var.google_client_id
    GOOGLE_CLIENT_SECRET = var.google_client_secret
  }
}

# ─── ConfigMap ───────────────────────────────────────────────
resource "kubernetes_config_map" "userservice_config" {
  metadata {
    name      = "userservice-config"
    namespace = kubernetes_namespace.techhub.metadata[0].name
  }

  data = {
    SPRING_DATASOURCE_URL          = "jdbc:postgresql://postgres:5432/usersdb"
    SPRING_DATASOURCE_USERNAME     = "postgres"
    SPRING_DATA_REDIS_HOST         = "redis-master"
    SPRING_DATA_REDIS_PORT         = "6379"
    SPRING_KAFKA_BOOTSTRAP_SERVERS = "kafka:9092"
    SERVER_PORT                    = "8080"
  }
}
