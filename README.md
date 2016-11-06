## Setting up Cassandra multi datacenter

This example helps you to setup Cassandra cluster with multi datacenter (in few minutes) and explore the client options to connect to the Cassandra server.
To achieve this we will be using Docker to setup the cluster and Datastax Java driver client to connect.

This example contains 4 modules, which are,

### Cassandra server setup

Assuming you have some basic knowledge of Docker, you can find the rest of the information [here](./server/).


### Client

There is a [simple Java app](./client/) which we will be using to connect to the server.


### Stress test (optional)

This [stress test](./stress-test) helps us to understand how Cassandra server/client works and what configuration is best for us. Sure this might not be enough but this is a good start.  


### Monitoring

And of course we need something to monitor our configuration and [this module](./monitor/) will help you out with that.
