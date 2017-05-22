#!/bin/bash
# Run this to start a fresh new Kevoree Registry using docker-compose
# eg. ./kevoree-registry.sh registry kevoree.org https://kevoree.registry.org
# ===================================================================
if ([ "$1" = "-h" ] || [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ]); then
  echo ""
  echo " Usage: $0 [mail_user] [mail_domain] [registry_fqdn]"
  echo ""
  echo "   [mail_user]     eg. \"registry\""
  echo "   [mail_domain]   eg. \"your-domain.tld\""
  echo "   [registry_fqdn] eg. \"https://registry.your-domain.tld\""
  echo ""
  exit 0
else
  echo "Downloading tvial/docker-mailserver setup script..."
  wget -q https://raw.githubusercontent.com/tomav/docker-mailserver/master/setup.sh
  chmod +x setup.sh

  # Read mail account password from command-line
  read -s -p "Define \"$1@$2\" password: " kregMailPwd
  echo ""

  # create the mail account
  echo "Creating \"$1@$2\" account..."
  ./setup.sh -i tvial/docker-mailserver:2.3 email add $1@$2 $kregMailPwd
  echo "Ok."
  echo ""

  # create DKIM
  echo "Generating DKIM keys..."
  ./setup.sh -i tvial/docker-mailserver:2.3 config dkim
  echo "Ok."
  echo ""

  echo "Downloading docker-compose app.yml..."
  wget -q https://raw.githubusercontent.com/kevoree/kevoree-registry/master/src/main/docker/app.yml
  echo "Ok."
  echo ""

  echo "Creating start-up script..."
  echo "#!/bin/bash" > start-kevoree-registry.sh
  echo "# AUTO-GENERATED FILE (by kevoree-registry.sh)" >> start-kevoree-registry.sh
  echo "KREG_MAIL_USER="$1" KREG_MAIL_DOMAIN="$2" KREG_URL="$3" docker-compose -f app.yml \$@" >> start-kevoree-registry.sh
  chmod +x start-kevoree-registry.sh
  echo "Ok."
  echo ""
  echo "You can now start your Kevoree Registry using:"
  echo ""
  echo " ./start-kevoree-registry.sh up -d"
  echo ""
fi
