package apm.maven.maven;

import java.util.ArrayList;
import java.util.Arrays;

public class RepositoryLayout {

    public static String toPath(String groupId, String artifactId) {
        var path = new ArrayList<>(Arrays.stream(groupId.split("\\.")).toList());
        path.add(artifactId);

        return String.join("/", path);
    }

}
