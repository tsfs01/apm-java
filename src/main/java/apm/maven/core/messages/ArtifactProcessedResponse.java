package apm.maven.core.messages;

import apm.maven.core.model.Artifact;

import java.util.List;
import java.util.UUID;

public record ArtifactProcessedResponse(
        UUID context,
        Artifact artifact,
        List<ArtifactCollectRequest> CollectRequests) {
}
