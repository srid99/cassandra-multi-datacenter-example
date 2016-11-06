package in.srid;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.Optional;

import static org.kohsuke.args4j.OptionHandlerFilter.ALL;

class Configuration {
    @Option(name = "-p", usage = "Port to be used to start this application", required = true, metaVar = "number")
    int port;

    @Option(name = "-h", usage = "Cassandra server hosts, comma separated value", required = true, metaVar = "string")
    String hosts;

    @Option(name = "-d", usage = "Cassandra server local datacenter name", required = true, metaVar = "string")
    String localDCName;

    @Option(name = "-m", usage = "Enable metrics to publish to graphite", metaVar = "boolean")
    boolean metricsEnabled;

    String keyspaceName = "test_keyspace";

    static Optional<Configuration> get(String... args) {
        Configuration configuration = new Configuration();
        CmdLineParser parser = new CmdLineParser(configuration);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar cassandra-multi-datacenter-example.jar [options...]");
            parser.printUsage(System.err);
            System.err.println();
            System.err.println("  Example: java -jar cassandra-multi-datacenter-example.jar" + parser.printExample(ALL));

            return Optional.empty();
        }

        return Optional.of(configuration);
    }
}
