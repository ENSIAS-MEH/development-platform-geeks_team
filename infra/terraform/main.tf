terraform {
  required_providers {
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.12.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.25.0"
    }
  }
}

# ==========================================
# AWS EKS Configuration (from main branch)
# ==========================================
provider "aws" {
  region = var.aws_region
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "cluster_name" {
  type    = string
  default = "devconnect-eks"
}

# Example EKS module structure for production-style deployment
# module "eks" {
#   source          = "terraform-aws-modules/eks/aws"
#   version         = "~> 20.0"
#   cluster_name    = var.cluster_name
#   cluster_version = "1.29"
#   subnet_ids      = ["subnet-abcde012", "subnet-bcde012a", "subnet-cde012ab"]
#   vpc_id          = "vpc-abcde012"
#
#   eks_managed_node_groups = {
#     default = {
#       min_size     = 1
#       max_size     = 3
#       desired_size = 2
#       instance_types = ["t3.medium"]
#     }
#   }
# }

output "cluster_endpoint" {
  value       = "https://eks-endpoint-placeholder.amazonaws.com"
  description = "EKS Cluster Endpoint"
}

# ==========================================
# Local Monitoring Configuration (our branch)
# ==========================================
provider "kubernetes" {
  config_path = "~/.kube/config"
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

# Create a namespace for monitoring
resource "kubernetes_namespace" "monitoring" {
  metadata {
    name = "monitoring"
  }
}

# Deploy kube-prometheus-stack (Prometheus + Grafana)
resource "helm_release" "prometheus_stack" {
  name       = "kube-prometheus-stack"
  repository = "https://prometheus-community.github.io/helm-charts"
  chart      = "kube-prometheus-stack"
  namespace  = kubernetes_namespace.monitoring.metadata[0].name
  version    = "56.6.2"

  values = [
    file("../monitoring/prometheus/values.yaml")
  ]

  depends_on = [kubernetes_namespace.monitoring]
}

# Deploy Loki for Log Aggregation
resource "helm_release" "loki_stack" {
  name       = "loki-stack"
  repository = "https://grafana.github.io/helm-charts"
  chart      = "loki-stack"
  namespace  = kubernetes_namespace.monitoring.metadata[0].name
  version    = "2.10.2"

  set {
    name  = "promtail.enabled"
    value = "true"
  }

  set {
    name  = "grafana.enabled"
    value = "false" # Use the Grafana from kube-prometheus-stack
  }

  depends_on = [kubernetes_namespace.monitoring]
}
