#!/bin/bash  
# requires: $password   - password of mysql root user
[ "$EUID" -ne 0 ] && >&2 echo "Please run as root" && exit 1
[ -z $password ] && >&2 echo "Variable 'password' not set, aborting" && exit 1
debconf-set-selections <<< "mysql-server mysql-server/root_password password $password"
debconf-set-selections <<< "mysql-server mysql-server/root_password_again password $password"
apt -y install mysql-server
systemctl stop mysql

