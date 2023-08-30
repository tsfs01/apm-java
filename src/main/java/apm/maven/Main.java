package apm.maven;

import apm.maven.core.Engine;
import apm.maven.core.Transport;
import apm.maven.core.transport.RabbitmqTransport;
import apm.maven.core.transport.StdioTransport;
import apm.maven.maven.MavenProcessor;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private final JCommander parser;

    enum TransportType {
        stdio, rabbitmq
    }

    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help = false;

    @Parameter(names={"--transport", "-t"})
    private TransportType transportType = TransportType.rabbitmq;

    public static void main(String[] args) {
        new Main(args).run();
    }

    public Main(String[] args) {
        this.parser = JCommander.newBuilder()
                .addObject(this)
                .build();

        parser.setProgramName("apm-maven");
        parser.parse(args);
    }

    public void run() {
        if (this.help) {
            this.parser.usage();
            System.exit(0);
        }

        LOG.info("starting");

        var objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);;

        try (var transport = createTransport(objectMapper)) {

            var resolver = new MavenProcessor();
            var processor = new Engine(transport, resolver);

            var procesorThread = processor.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    LOG.info("shutting down");
                    procesorThread.interrupt();
                    procesorThread.join();
                } catch (Exception e) {
                    LOG.error("shutdown interrupted", e);
                }
            }));

            LOG.info("waiting for signal");
            procesorThread.join();


        } catch (Exception e) {
            LOG.atError().log("exception", e);
        }

        LOG.info("exiting");
    }

    private Transport createTransport(ObjectMapper objectMapper) throws Exception {
        return switch (this.transportType) {
            case stdio -> new StdioTransport(objectMapper);
            case rabbitmq -> new RabbitmqTransport(objectMapper);
        };
    }


}
