package apm.maven.maven;

import apm.maven.core.model.Artifact;
import com.beust.jcommander.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record MavenArtifact(String groupId, String artifactId, String version, String classifier) {

    public static MavenArtifact parse(String spec) {
        // <groupId>:<artifactId>:<version>:<classifier>?
        var parts = spec.split(":");

        return switch (parts.length) {
            case 1 -> throw new IllegalStateException("invalid artifact spec: %s".formatted(spec));
            case 2 -> new MavenArtifact(parts[0], parts[1], null, null);
            case 3 -> new MavenArtifact(parts[0], parts[1], parts[2], null);
            case 4 -> new MavenArtifact(parts[0], parts[1], parts[2], parts[3]);
            default -> throw new IllegalStateException("invalid artifact spec: %s".formatted(spec));
        };
    }

    public MavenArtifact withVersion(String version) {
        return new MavenArtifact(groupId, artifactId, version, null);
    }

    public boolean hasVersion() {
        return version != null && !version.isEmpty();
    }
    public boolean hasClassifier() {
        return classifier != null && !classifier.isEmpty();
    }

    public String toPath(String extension) {
        var path = new ArrayList<>(Arrays.stream(groupId.split("\\.")).toList());
        path.add(artifactId);
        path.add(version);

        var filenameParts = new ArrayList<String>();
        filenameParts.add(artifactId);
        filenameParts.add(version);

        if (hasClassifier()) {
            filenameParts.add(this.classifier);
        }

        var filename = String.join("-", filenameParts) + "." + extension;

        path.add(filename);

        return String.join("/", path);
    }

    @Override
    public String toString() {
        return Stream.of(groupId, artifactId, version, classifier)
                .filter(v -> v != null && !v.isEmpty())
                .collect(Collectors.joining(":"));
    }
}
