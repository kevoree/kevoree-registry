#!/bin/bash
# Run this to start a fresh new Kevoree Registry using docker-compose
# eg. ./kevoree-registry.sh registry kevoree.org https://kevoree.registry.org
# ===================================================================
KREG_MAIL_USER=$1
KREG_MAIL_DOMAIN=$2
KREG_URL=$3

echo "Downloading tvial/docker-mailserver setup script..."
wget https://raw.githubusercontent.com/tomav/docker-mailserver/master/setup.sh
chmod +x setup.sh

# Read mail account password from command-line
read -s -p "Define mail account password: " kregMailPwd

# create the mail account
echo "Creating mail server account..."
./setup.sh -i tvial/docker-mailserver:2.3 email add $KREG_MAIL_USER@$KREG_MAIL_DOMAIN $kregMailPwd
echo "Ok."

# create DKIM
echo "Generating DKIM keys..."
./setup.sh -i tvial/docker-mailserver:2.3 config dkim
echo "Ok."

echo "Downloading docker-compose app.yml..."
wget https://raw.githubusercontent.com/kevoree/kevoree-registry/master/src/main/docker/app.yml
echo "Starting Kevoree Registry using docker-compose..."
docker-compose -f app.yml up
