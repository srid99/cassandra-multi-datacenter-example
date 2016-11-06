#!/bin/bash

docker network create cassandra_network

docker run -d --name cassandra-dc1-seed1 \
	--net=cassandra_network \
	-e CASSANDRA_CLUSTER_NAME=cassandra-cluster \
	-e CASSANDRA_DC=dc1 \
	-e CASSANDRA_ENDPOINT_SNITCH=GossipingPropertyFileSnitch \
	-e CASSANDRA_SEEDS=cassandra-dc1-seed1,cassandra-dc2-seed1 \
	cassandra:latest

docker run -d --name cassandra-dc2-seed1 \
	--net=cassandra_network \
	-e CASSANDRA_CLUSTER_NAME=cassandra-cluster \
	-e CASSANDRA_DC=dc2 \
	-e CASSANDRA_ENDPOINT_SNITCH=GossipingPropertyFileSnitch \
	-e CASSANDRA_SEEDS=cassandra-dc1-seed1,cassandra-dc2-seed1 \
	cassandra:latest

docker run -d --name cassandra-dc1-node1 \
	--net=cassandra_network \
	-e CASSANDRA_CLUSTER_NAME=cassandra-cluster \
	-e CASSANDRA_DC=dc1 \
	-e CASSANDRA_ENDPOINT_SNITCH=GossipingPropertyFileSnitch \
	-e CASSANDRA_SEEDS=cassandra-dc1-seed1,cassandra-dc2-seed1 \
	cassandra:latest

docker run -d --name cassandra-dc2-node1 \
	--net=cassandra_network \
	-e CASSANDRA_CLUSTER_NAME=cassandra-cluster \
	-e CASSANDRA_DC=dc2 \
	-e CASSANDRA_ENDPOINT_SNITCH=GossipingPropertyFileSnitch \
	-e CASSANDRA_SEEDS=cassandra-dc1-seed1,cassandra-dc2-seed1 \
	cassandra:latest

