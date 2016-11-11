package in.srid;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.JdkSSLOptions;
import com.datastax.driver.core.Metrics;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.SSLOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

class CassandraClient {

    private static final Logger LOG = LoggerFactory.getLogger(CassandraClient.class);

    private static final String[] DEFAULT_SSL_CIPHER_SUITES = new String[]{
            "TLS_RSA_WITH_NULL_SHA256",
            "SSL_RSA_WITH_NULL_MD5",
            "SSL_RSA_WITH_NULL_SHA"};

    private static final String DEFAULT_SSL_PROTOCOL = "TLS";
    private static final String DEFAULT_KEYSTORE_TYPE = "JKS";

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

        if (config.sslEnabled) {
            builder.withSSL(createSslOptions(config));
        }

        for (String hostPart : config.hosts.split(",")) {
            builder.addContactPoint(hostPart.trim());
        }

        return builder.build();
    }

    private SSLOptions createSslOptions(final Configuration configuration) {
        try {
            final SSLContext context = SSLContext.getInstance(DEFAULT_SSL_PROTOCOL);
            final KeyManager[] keyManagers = loadKeyStore(configuration);
            final TrustManager[] trustManagers = loadTrustStore(configuration);
            context.init(keyManagers, trustManagers, null);
            return JdkSSLOptions.builder() //
                    .withSSLContext(context) //
                    .withCipherSuites(DEFAULT_SSL_CIPHER_SUITES) //
                    .build();
        } catch (Exception e) {
            LOG.error("Error creating SSLOptions", e);
            throw new RuntimeException(e);
        }
    }

    private KeyManager[] loadKeyStore(final Configuration configuration) throws Exception {
        final char[] keyStorePass = configuration.keystorePassword.toCharArray();
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(configuration.keystorePath);
            final KeyStore ks = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
            ks.load(fileInputStream, keyStorePass);
            kmf.init(ks, keyStorePass);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }

        return kmf.getKeyManagers();
    }

    private TrustManager[] loadTrustStore(final Configuration configuration) throws Exception {
        final char[] trustStorePass = configuration.truststorePassword.toCharArray();
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(configuration.truststorePath);
            final KeyStore ts = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
            ts.load(fileInputStream, trustStorePass);
            tmf.init(ts);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }

        return tmf.getTrustManagers();
    }
}
