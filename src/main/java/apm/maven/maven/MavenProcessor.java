package apm.maven.maven;

import apm.maven.core.Processor;
import apm.maven.core.messages.ArtifactCollectRequest;
import apm.maven.core.messages.ArtifactProcessRequest;
import apm.maven.core.messages.ArtifactProcessedResponse;
import apm.maven.core.model.ArtifactDependency;
import org.apache.maven.model.building.DefaultModelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;

public class MavenProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(MavenProcessor.class);

    private final MavenRepositoryClient client;

    public MavenProcessor() {
        this.client = new MavenRepositoryClient(URI.create("https://repo1.maven.org/maven2/"));
    }

    @Override
    public ArtifactProcessedResponse process(ArtifactProcessRequest request) {
        try {
            var artifact = request.artifact();

            LOG.atDebug().addKeyValue("artifact", artifact.id()).log("resolving artifact");

            var file = MavenArtifact.parse(artifact.id());

            if (!file.hasVersion()) {
                addVersionsAsDependencies(request, file);
            } else {


            }


            return new ArtifactProcessedResponse(request.ctx(), request.artifact(), null);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addVersionsAsDependencies(ArtifactProcessRequest request, MavenArtifact file) {
        var artifactMetadata = this.client.getMetadata(file.groupId(), file.artifactId());
        var versions = artifactMetadata.getVersioning().getVersions();

        LOG.atDebug()
                .addKeyValue("versions", versions.size())
                .log("got versions");


        request.artifact().dependencies().addAll(versions.stream()
                .map(v -> new ArtifactDependency(file.withVersion(v).toString(), "maven", null))
                .toList());
    }

}
