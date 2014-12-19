### Kevoree Registry Web Server

#### Test db
```sh
sh start-dev-db.sh
mvn clean test
```

#### Compile & start
```sh
# compile
mvn clean install -DskipTests
# start a postgres db in a Docker container
sh start-db.sh
# run the server (don't forget to set the config.properties values properly)
java -jar target/org.kevoree.registry.server-5.0.0-SNAPSHOT.jar
```
