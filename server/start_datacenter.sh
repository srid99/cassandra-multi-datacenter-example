#!/bin/bash

if [ -z "$1" ]
then
  echo "No 'datacenter' supplied!"
  echo "Usage: /start_datacenter.sh <datacenter_name>"
  exit -1
fi

DC=$1

echo "Starting all nodes in datacenter $DC"
docker start "cassandra-${DC}-seed1"
docker start "cassandra-${DC}-node1"

