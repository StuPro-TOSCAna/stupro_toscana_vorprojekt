#!/bin/bash  
[ "$EUID" -ne 0 ] && >&2 echo "Please run as root" && exit 1
systemctl stop mysql

