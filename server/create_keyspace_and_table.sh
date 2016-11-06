#!/bin/bash

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


