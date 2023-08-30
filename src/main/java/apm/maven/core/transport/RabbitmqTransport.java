package apm.maven.core.transport;

import apm.maven.core.Transport;
import apm.maven.core.messages.ArtifactProcessRequest;
import apm.maven.core.messages.ArtifactProcessedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class RabbitmqTransport implements Transport {

    private final static Logger LOG = LoggerFactory.getLogger(RabbitmqTransport.class);

    private final ObjectMapper objectMapper;
    private final Connection connection;
    private final Channel channel;
    private final ArrayBlockingQueue<ArtifactProcessRequest> requestQueue;

    public RabbitmqTransport(ObjectMapper objectMapper) throws Exception {
        var factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        this.objectMapper = objectMapper;
        this.requestQueue = new ArrayBlockingQueue<>(32);

        try {
            this.connection = factory.newConnection();
            this.channel = this.connection.createChannel();
            this.channel.addReturnListener(this::onMessageReturned);
        } catch (IOException e) {
            this.close();
            throw e;
        }

    }

    @Override
    public void close() throws Exception {
        if (this.channel != null) {
            this.channel.close();
        }
        if (this.connection != null) {
            this.connection.close();
        }
    }

    @Override
    public ArtifactProcessRequest receive() throws IOException, InterruptedException {
        LOG.atDebug().log("receiving request");

        this.channel.basicConsume("apm-maven", true, this::onMessageReceived, this::onMessageCanceled);

        return this.requestQueue.take();
    }

    @Override
    public void send(ArtifactProcessedResponse response) throws IOException {
        LOG.atDebug().log("sending response");

        var body = this.objectMapper.writeValueAsBytes(response);

        this.channel.basicPublish("", "apc-ingest-processed", true, null, body);
    }

    private void onMessageReceived(String consumerTag, Delivery delivery) throws IOException {
        var request = this.objectMapper.readValue(delivery.getBody(), ArtifactProcessRequest.class);

        try {
            LOG.debug("received message: {}", delivery.getProperties().getMessageId());
            this.requestQueue.put(request);
        } catch (InterruptedException e) {
            this.channel.abort();
        }
    }

    private void onMessageCanceled(String consumerTag) {
        LOG.atError()
                .addKeyValue("consumerTag", consumerTag)
                .log("message canceled");
    }

    private void onMessageReturned(Return r) {
        LOG.atError()
                .addKeyValue("reply", r.getReplyText())
                .addKeyValue("exchange", r.getExchange())
                .addKeyValue("routingKey", r.getRoutingKey())
                .log("message returned");
    }


}
