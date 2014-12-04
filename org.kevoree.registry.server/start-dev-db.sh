#! /bin/sh
docker run --name kevoree-registry-db-dev -p 5432:5432 -d kevoree/registry-db:dev
