package apm.maven.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArtifactFile(
        String uri,
        String folder) {

    public ArtifactFile(String uri) {
        this(uri, null);
    }


}
