package apm.maven.core;

import apm.maven.core.messages.ArtifactProcessRequest;
import apm.maven.core.messages.ArtifactProcessedResponse;

@FunctionalInterface
public interface Processor {
    ArtifactProcessedResponse process(ArtifactProcessRequest request);
}
