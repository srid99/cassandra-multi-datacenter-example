#!/bin/bash

if [ -z "$1" ]
then
  echo "No 'datacenter' supplied! Usage: /stop_datacenter.sh <datacenter_name>"
  exit -1
fi

dc=$1

echo "Stopping all nodes in datacenter $dc"
docker stop "cassandra-${dc}-seed1"
docker stop "cassandra-${dc}-node1"

