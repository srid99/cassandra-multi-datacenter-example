package in.srid;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.datastax.driver.core.Metrics;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class MetricsReportor {
    static void report(Configuration conf, Metrics metrics) {
        Graphite graphite = new Graphite(new InetSocketAddress("localhost", 2003));
        GraphiteReporter reporter = GraphiteReporter.forRegistry(metrics.getRegistry())
                .prefixedWith(conf.localDCName)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        reporter.start(10, TimeUnit.SECONDS);
    }
}
