### Kevoree Registry Web Server

#### Dev

**Start a PostgresSQL db:**
```sh
docker run --name kevoree-registry-db -p 5432:5432 -d postgres:9.4
```

Default configuration uses `kevoree_registry` as `user` and `db name`.
```sh
CREATE USER kevoree_registry; CREATE DATABASE kevoree_registry WITH OWNER = kevoree_registry;
```