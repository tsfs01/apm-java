package apm.maven.core.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record Artifact(
        String id,
        String processor,
        String filter,
        ArtifactStatus status,
        boolean root,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        Map<String, ArtifactVersion> versions,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        Map<String, String> config,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        Set<ArtifactDependency> dependencies
) {
    public Artifact() {
        this("", "maven", "", ArtifactStatus.PROCESSING, true, new HashMap<>(), new HashMap<>(), new HashSet<>());
    }


}
