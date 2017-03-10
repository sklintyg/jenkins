#!/bin/bash

[[ -n "$1" ]] && user="$1" || { echo "No username provided!"; exit 1; }
[[ -n "$2" ]] && pwdfile="$2" || { echo "No password file provided!"; exit 1; }

pidfile="/tmp/oc.pid"

echo "Starting openconnect, pid in file $pidfile"$'\n'"Use 'kill_openconnect $pidfile' to close the connection."

sudo openconnect -u $user --no-cert-check --passwd-on-stdin -b --pid-file=$pidfile https://ssl-vpn.sth.basefarm.net/ssl < $pwdfile
