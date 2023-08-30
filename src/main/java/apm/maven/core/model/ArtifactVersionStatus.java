package apm.maven.core.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ArtifactVersionStatus {
    SENT_FOR_COLLECTION;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
