package apm.maven.core.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.Map;

public record ArtifactVersion(
        String version,
        ArtifactVersionStatus status,

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        Map<String, ArtifactFile> files) {
}
