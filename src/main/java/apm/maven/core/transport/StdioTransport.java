package apm.maven.core.transport;

import apm.maven.core.Transport;
import apm.maven.core.messages.ArtifactProcessRequest;
import apm.maven.core.messages.ArtifactProcessedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class StdioTransport implements Transport {

    private final ObjectMapper mapper;

    public StdioTransport(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    @Override
    public ArtifactProcessRequest receive() throws IOException {
        return this.mapper.readValue(System.in, ArtifactProcessRequest.class);
    }

    @Override
    public void send(ArtifactProcessedResponse response) throws IOException {
        System.out.flush();
        this.mapper.writeValue(System.out, response);
        System.out.println();
        System.out.flush();
    }
}
