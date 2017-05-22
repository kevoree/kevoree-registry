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
# download the script
wget https://raw.githubusercontent.com/kevoree/kevoree-registry/master/src/main/docker/kevoree-registry.sh
# make it executable
chmod +x kevoree-registry.sh
# execute the script using your own data
./kevoree-registry.sh [mail_user] [mail_domain] [registry_fqdn]
```
*eg.* `./kevoree-registry.sh registry kevoree.org https://registry.kevoree.org`  
*with:*
 - `registry` being the mail user name
 - `kevoree.org` being the mail server domain
 - `https://registry.kevoree.org` being the fqdn of your registry
