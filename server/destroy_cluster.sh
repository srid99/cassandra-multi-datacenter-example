#!/bin/bash

echo "Stop all running nodes"
docker stop cassandra-dc2-node1
docker stop cassandra-dc2-seed1
docker stop cassandra-dc1-node1
docker stop cassandra-dc1-seed1

echo "Let's give some time to stop Cassandra nodes"
sleep 10

echo "Remove all containers"
docker rm cassandra-dc2-node1
docker rm cassandra-dc2-seed1
docker rm cassandra-dc1-node1
docker rm cassandra-dc1-seed1

echo "Remove the network"
docker network rm cassandra_network

