package apm.maven.maven;

import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class MavenRepositoryClient {

    private static final Logger LOG = LoggerFactory.getLogger(MavenRepositoryClient.class);

    private final HttpClient client;

    private final ArtifactRepositoryLayout layout;
    private final URI repositoryUrl;

    public MavenRepositoryClient(URI repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                //.proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
                //.authenticator(Authenticator.getDefault())
                .build();
        layout = null;
    }


    public Metadata getMetadata(String groupId, String artifactId) {
        String path = RepositoryLayout.toPath(groupId, artifactId) + "/maven-metadata.xml";
        try (var stream = getFile(path)) {
            return new MetadataXpp3Reader().read(stream);
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        }

    }


    public InputStream getFile(MavenArtifact artifact, String extension) {
        return getFile(artifact.toPath(extension));
    }


    public InputStream getFile(String path) {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(this.repositoryUrl.resolve(path))
                    .GET()
                    .build();

            LOG.atDebug().addKeyValue("uri", request.uri()).log("fetching");

            var response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                throw new RuntimeException("unexpected status code fetching '%s': %d".formatted(response.uri().toString(), response.statusCode()));
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
