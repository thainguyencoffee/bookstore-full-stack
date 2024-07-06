#!/bin/bash

echo "Deleting full container ... \n"

docker rm -f bookstore-postgres bookstore-redis bookstore-rabbitmq