package apm.maven.core.model;

import java.util.Map;

public record ArtifactDependency(
        String id,
        String processor,
        Map<String, String> config
) {
}
