#!/bin/bash

datacenters=("dc1" "dc2")

for i in "${!datacenters[@]}"; do
  dc=${datacenters[$i]}
  if ! [  $i == 0 ]; then
    echo "Wait sometime before we start next datacenter $dc"
    sleep 20 
  fi

  ./start_datacenter.sh $dc
done

