[//]: # (![Production]&#40;https://github.com/thainguyencoffee/bookstore-full-stack/actions/workflows/production-stage-deployment.yml/badge.svg&#41;)
## Introduction
This is a full-stack project for managing a bookstore, including frontend, backend, and database integration.

## Features
- User authentication and authorization
- Book management (CRUD operations)
- Read book online on browser directly
- Review book
- Checkout order and shopping cart management
- Integration VNPay
- Track your purchase order
- Inventory management

## Setup
Instructions for setting up the project locally.

## Usage
Instructions on how to use the application.

## Contributing
### Initial setup
1. Thiết lập cơ sở dữ liệu và Keycloak (yêu cầu terminal đang ở thư mục gốc của project)
```bash
cd bookstore-dep/docker && docker compose up -d bookstore-keycloak
```
2. Run bookstore backend
```bash
cd bookstore-be && ./gradlew bootRun
```
3. Run bookstore fe
```bash
cd bookstore-fe && npm run dev
```
