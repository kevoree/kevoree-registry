#!/bin/bash
# Run this before docker-compose up
# It is needed for the mail container to work properly
docker run --rm -e MAIL_USER=registry@kevoree.org -e MAIL_PASS=foobar -ti tvial/docker-mailserver:v2 /bin/sh -c 'echo "$MAIL_USER|$(doveadm pw -s SHA512-CRYPT -u $MAIL_USER -p $MAIL_PASS)"' >> mail-config/postfix-accounts.cf && docker run --rm -v "$(pwd)/mail-config":/tmp/docker-mailserver -ti tvial/docker-mailserver:v2 generate-dkim-config
