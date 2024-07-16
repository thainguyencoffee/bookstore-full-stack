#!/bin/bash

echo "🚀 Create a new Kubernetes cluster ... \n"
minikube start --cpus 2 --memory 4g --driver docker --profile bookstore

echo "🔌 Addons the Ingress controller ... \n"
minikube addons enable ingress --profile bookstore

sleep 30

echo "🔍 Checking the status of the cluster ... \n"
kubectl get nodes

echo "🚀 Deploy the applications ... \n"

echo "🚀 1. Deploying Postgresql ...\n"
kubectl apply -f service/postgresql.yml

sleep 5

echo "⌛ Waiting for Postgresql to be deployed ... \n"
while [ $(kubectl get pod -l db=bookstore-postgres | wc -l) -eq 0 ]; do
  sleep 5
done
echo "⌛ Waiting for Postgresql to be ready ... \n"
kubectl wait \
  --for=condition=ready pod \
  --selector=db=bookstore-postgresql \
  --timeout=300s

echo "🚀 2. Deploying Keycloak ...\n"
kubectl apply -f service/keycloak-config.yml
kubectl apply -f service/keycloak.yml

sleep 5

echo "⌛ Waiting for Keycloak to be deployed ... \n"
while [ $(kubectl get pod -l app=bookstore-keycloak | wc -l) -eq 0 ]; do
  sleep 5
done
echo "⌛ Waiting for Keycloak to be ready ... \n"
kubectl wait \
  --for=condition=ready pod \
  --selector=app=bookstore-keycloak \
  --timeout=300s

echo "🚀 3. Deploying Redis ...\n"
kubectl apply -f service/redis.yml
sleep 5
echo "⌛ Waiting for Redis to be deployed ... \n"
while [ $(kubectl get pod -l db=bookstore-redis | wc -l) -eq 0 ]; do
  sleep 5
done
echo "⌛ Waiting for Redis to be ready ... \n"
kubectl wait \
  --for=condition=ready pod \
  --selector=db=bookstore-redis \
  --timeout=300s

echo "🚀 4. Deploying the Bookstore UI ...\n"
kubectl apply -f service/bookstore-ui.yml
sleep 5
echo "⌛ Waiting for Bookstore UI to be deployed ... \n"
while [ $(kubectl get pod -l app=bookstore-ui | wc -l) -eq 0 ]; do
  sleep 5
done
echo "⌛ Waiting for Bookstore UI to be ready ... \n"
kubectl wait \
  --for=condition=ready pod \
  --selector=app=bookstore-ui \
  --timeout=300s

echo "⚓️ Happy coding! \n"
