#!/bin/bash

set -euo pipefail

echo "📦 Deploying bookstore-ui to production..."
kubectl apply -f resources

echo "⌛ Waiting for bookstore-ui to be deployed..."
while [ $(kubectl get pod -l app=bookstore-ui | wc -l) -eq 0 ]; do
    sleep 5
done

echo "⌛ Waiting for bookstore-ui to be ready..."
kubectl wait \
  --for=condition=ready pod \
  --selector=app=bookstore-ui \
  --timeout=180s

echo "🚀 bookstore-ui has been successfully deployed to production!"

