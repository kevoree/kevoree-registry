FROM ibmjava:jre-alpine

ENV JHIPSTER_SLEEP 0

# add directly the war
ADD *.war /app.war

# add prod config
ADD config/ /

RUN sh -c 'touch /app.war'

VOLUME /tmp
EXPOSE 8080
CMD echo "The application will start in ${JHIPSTER_SLEEP}s..." && \
    sleep ${JHIPSTER_SLEEP} && \
    java -Djava.security.egd=file:/dev/./urandom -jar /app.war --spring.profiles.active=prod
