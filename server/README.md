### Server setup

This example will help you to setup a cluster with 2 datacenters dc1 and dc2. And each datacenter will have 2 nodes.


#### Prerequisite

* Basic knowledge about Docker
* Docker installed
* A machine with more than 8GB memory (we need this since we will bootstrap 4 Cassandra servers)


#### Create the cluster

You can create the cluster by running `create_cluster.sh` script. This script creates a Docker network which will be used in the Cassandra containers
to communicate with each other. We will be using the official Cassandra Docker image.


#### Create keyspace and table

Once we have the cluster up and running, it is time to create our test keyspace and table. Run the below CQL command in one of the nodes.


```
CREATE KEYSPACE test_keyspace
	WITH REPLICATION = {
		'class' : 'NetworkTopologyStrategy',
		'dc1' : 2,
		'dc2' : 2
	};

CREATE TABLE test_table (
	key varchar PRIMARY KEY,
	value varchar
);
```
