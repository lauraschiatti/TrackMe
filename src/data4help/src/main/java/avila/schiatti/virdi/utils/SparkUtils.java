package avila.schiatti.virdi.utils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import spark.Spark;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;
import spark.embeddedserver.jetty.JettyServerFactory;


class EmbeddedJettyServerFactory implements JettyServerFactory {
    @Override
    public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
        Server server;
        if (maxThreads > 0) {
            int min = minThreads > 0 ? minThreads : 8;
            int idleTimeout = threadTimeoutMillis > 0 ? threadTimeoutMillis : '\uea60';
            server = new Server(new QueuedThreadPool(maxThreads, min, idleTimeout));
        } else {
            server = new Server();
        }
        return server;
    }

    @Override
    public Server create(ThreadPool threadPool) {
        return new Server(threadPool);
    }
}

public class SparkUtils {
    private final static String STATIC_FILES_PATH = "public";

    public static void createServerWithRequestLog() {
        Spark.staticFileLocation(STATIC_FILES_PATH);

        EmbeddedJettyFactory factory = createEmbeddedJettyFactory();
        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, factory);
    }

    private static EmbeddedJettyFactory createEmbeddedJettyFactory() {
        return new EmbeddedJettyFactory(new EmbeddedJettyServerFactory());
    }
}