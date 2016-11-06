package in.srid;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Configuration.get(args).ifPresent(Application::start);
    }

    private static void start(final Configuration config) {
        CassandraClient client = new CassandraClient(config);
        Session session = client.getSession();

        if (config.metricsEnabled) MetricsReportor.report(config, client.getMetrics());

        port(config.port);

        get("/key/*", (request, response) -> {
            String key = request.splat()[0];

            Statement selectStatement = select().from("test_table").where(eq("key", key));

            ResultSet result = session.execute(selectStatement);
            Optional<Row> row = Optional.ofNullable(result.one());
            LOG.info("Result: {} for statement: {}", row, selectStatement);
            return row.toString();
        });

        post("/key/*/value/*", (request, response) -> {
            String key = request.splat()[0];
            String value = request.splat()[1];

            Statement insertStatement = insertInto("test_table").value("key", key).value("value", value);
            LOG.info("Insert statement: {}", insertStatement);
            return session.execute(insertStatement);
        });

        exception(Exception.class, (exception, request, response) -> {
            LOG.error("Request failed: ", exception);

            response.status(500);
            response.body(exception.getMessage());
        });

        Runtime.getRuntime().addShutdownHook(new Thread(client::close));
    }
}
