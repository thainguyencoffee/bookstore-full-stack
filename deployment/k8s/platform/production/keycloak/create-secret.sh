#!/bin/bash

set -euo pipefail

echo "🗝️ Create secret for OAuth2 client."
kubectl delete secret keycloak-issuer-client-secret || true

kubectl create secret generic keycloak-issuer-client-secret \
  --from-literal=spring.security.oauth2.client.provider.keycloak.issuer-uri=$1

echo "\n🗝️ Create secret for OAuth2 Resource Server."
kubectl delete secret keycloak-issuer-resourceserver-secret || true
kubectl create secret generic keycloak-issuer-resourceserver-secret \
  --from-literal=spring.security.oauth2.resourceserver.jwt.issuer-uri=$1

echo "\n🗝️  Secret generation completed successfully.\n"