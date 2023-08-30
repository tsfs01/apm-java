package apm.maven.core.messages;

import apm.maven.core.model.Artifact;

import java.util.UUID;

public record ArtifactProcessRequest(UUID ctx, Artifact artifact) {

}
