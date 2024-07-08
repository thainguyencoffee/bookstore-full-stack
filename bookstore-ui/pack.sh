#!/bin/sh

echo '\n ⌛ Building angular application'
ng build

sleep 15

echo '\n 📦 Package to image container'
pack build bookstore-ui \
--buildpack gcr.io/paketo-buildpacks/nginx \
--builder paketobuildpacks/builder:base \
-e BP_WEB_SERVER=nginx \
-e BP_WEB_SERVER_ROOT=bookstore-ui/browser \
-p dist
