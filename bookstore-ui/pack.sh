#!/bin/sh

echo '\n ⌛ Building angular application'
ng build

sleep 15

echo "\n Copy files from nginx to dist"
cp -r nginx/* dist/

echo '\n 📦 Package to image container'
pack build bookstore-ui \
--buildpack gcr.io/paketo-buildpacks/nginx \
--builder paketobuildpacks/builder:base \
-p dist
