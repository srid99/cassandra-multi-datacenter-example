#!/bin/bash

artillery run cassandra-multi-dc-stress-test.json -e dc1 &
artillery run cassandra-multi-dc-stress-test.json -e dc2 &

wait

