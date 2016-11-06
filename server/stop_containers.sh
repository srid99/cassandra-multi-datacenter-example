#!/bin/bash

for dc in dc1 dc2
do
  ./stop_datacenter.sh $dc
done

