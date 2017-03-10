#!/bin/bash

while IFS='' read -r line || [[ -n "$line" ]]
do
    echo "Attempting graceful shutdown of openconnect with pid: $line"
    if ps -p $line > /dev/null; then
	sudo kill -SIGINT $line
    else
	echo "Pid $line does not exist!"
    fi
done < "$1"
