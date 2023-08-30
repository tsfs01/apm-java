package apm.maven.core;

import apm.maven.core.messages.ArtifactProcessRequest;
import apm.maven.core.messages.ArtifactProcessedResponse;

import java.io.IOException;

public interface Transport extends AutoCloseable {
    ArtifactProcessRequest receive() throws IOException, InterruptedException;
    void send(ArtifactProcessedResponse response) throws IOException;

    @Override
    default void close() throws Exception {

    }
}
