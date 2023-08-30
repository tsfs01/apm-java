package apm.maven.core.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ArtifactStatus {
    PROCESSING;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
