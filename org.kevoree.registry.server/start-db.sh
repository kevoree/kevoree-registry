#! /bin/sh
docker run --name kevoree-registry-db -p 5432:5432 -d kevoree/registry-db
