package apm.maven.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Engine {

    private final static Logger LOG = LoggerFactory.getLogger(Engine.class);

    private final Transport transport;
    private final Processor processor;

    public Engine(Transport transport, Processor processor) {
        this.transport = transport;
        this.processor = processor;
    }

    public Thread start() {
        var thread = new Thread(this::run, "Processor");
        thread.setDaemon(false);
        thread.start();
        return thread;
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                // receive request
                LOG.atDebug().log("receiving request");
                var request = transport.receive();

                if (request == null) {
                    LOG.atDebug().log("transport completed");
                    break;
                }


                var artifactId = request.artifact().id();

                LOG.atDebug()
                        .addKeyValue("ctx", request.ctx())
                        .addKeyValue("artifact", artifactId)
                        .log("received request");


                // process
                LOG.atDebug()
                        .addKeyValue("ctx", request.ctx())
                        .addKeyValue("artifact", artifactId)
                        .log("processing request");

                var response = this.processor.process(request);

                LOG.atDebug()
                        .addKeyValue("ctx", request.ctx())
                        .addKeyValue("artifact", artifactId)
                        .log("processed request");

                // send response
                LOG.atDebug()
                        .addKeyValue("ctx", request.ctx())
                        .addKeyValue("artifact", artifactId)
                        .log("sending response");

                transport.send(response);

                LOG.atDebug()
                        .addKeyValue("ctx", request.ctx())
                        .addKeyValue("artifact", artifactId)
                        .log("sent response");

            } catch (IOException e) {

                LOG.atError().addKeyValue("exception", e).log("unrecoverable exception");
                break;
            } catch (InterruptedException e) {
                LOG.info("thread interrupted");
                break;
            }
        }
        LOG.debug("returning");
    }

}
