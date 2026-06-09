output "namespace" {
  description = "Techhub namespace"
  value       = kubernetes_namespace.techhub.metadata[0].name
}

output "argocd_namespace" {
  description = "ArgoCD namespace"
  value       = "argocd"
}