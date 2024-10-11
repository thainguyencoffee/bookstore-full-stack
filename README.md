# Bookstore project

[![Backend CI with Gradle](https://github.com/thainguyencoffee/bookstore-full-stack/actions/workflows/commit-stage.yml/badge.svg)](https://github.com/thainguyencoffee/bookstore-full-stack/actions/workflows/commit-stage.yml)

## Techs used & Design patterns
- Java 17
- Docker
- Docker compose plugin
- Bash

## Features
- Backend with book management, backend for frontend, OAuth2 Keycloak with OIDC Flow

### Check out sources
```bash
https://github.com/thainguyencoffee/bookstore-full-stack.git
```

### Compile and test; build all JARs, build image container, start docker compose

1. Without angular (recommend when you want to develop frontend)
```bash
./build.sh without-angular
```
2. With angular (recommend when you want to deploy)
```bash
./build.sh
```

3. If you want to build image
```bash
./build.sh native
```