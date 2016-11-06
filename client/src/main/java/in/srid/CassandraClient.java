package in.srid;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.Metrics;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CassandraClient {

    private static final Logger LOG = LoggerFactory.getLogger(CassandraClient.class);

    private final Cluster cluster;
    private final Session session;
    private final Metrics metrics;

    CassandraClient(Configuration configuration) {
        cluster = cluster(configuration);
        session = cluster.connect(configuration.keyspaceName);
        metrics = cluster.getMetrics();

        LOG.info("Cassandra keyspace [{}] configured", configuration.keyspaceName);
    }

    Session getSession() {
        return session;
    }

    Metrics getMetrics() {
        return metrics;
    }

    void close() {
        LOG.info("Close all Cassandra related resources");
        session.close();
        cluster.close();
    }

    private Cluster cluster(Configuration config) {
        SocketOptions socketOptions = new SocketOptions()
                .setConnectTimeoutMillis(500)
                .setReadTimeoutMillis(500);

        PoolingOptions poolingOptions = new PoolingOptions()
                .setCoreConnectionsPerHost(HostDistance.LOCAL, 4)
                .setMaxConnectionsPerHost(HostDistance.LOCAL, 8)
                .setCoreConnectionsPerHost(HostDistance.REMOTE, 2)
                .setMaxConnectionsPerHost(HostDistance.REMOTE, 4);

        DCAwareRoundRobinPolicy loadBalancingPolicy = DCAwareRoundRobinPolicy.builder()
                .withLocalDc(config.localDCName)
                .withUsedHostsPerRemoteDc(2)
                .allowRemoteDCsForLocalConsistencyLevel()
                .build();

        Cluster.Builder builder = Cluster.builder()
                .withPoolingOptions(poolingOptions)
                .withSocketOptions(socketOptions)
                .withLoadBalancingPolicy(loadBalancingPolicy)
                .withPort(9042);

        for (String hostPart : config.hosts.split(",")) {
            builder.addContactPoint(hostPart.trim());
        }

        return builder.build();
    }
}
