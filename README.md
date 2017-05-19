Kevoree Registry
==========================
**Spring-Boot Server and Angular Client**


## Development
*Start the dev server at **localhost:8080***
``` bash
./mvnw spring-boot:run
```

## Production
*Create an executable **.war***
``` bash
./mvnw package -Pprod
```

## Start-up script using Docker
*All-in-one script to start a new Kevoree Registry*
``` bash
wget https://raw.githubusercontent.com/kevoree/kevoree-registry/master/src/main/docker/kevoree-registry.sh
export KREG_MAIL_USER="registry"
export KREG_MAIL_DOMAIN="kevoree.org"
export KREG_URL="https://registry.kevoree.org"
./kevoree-registry.sh
```
