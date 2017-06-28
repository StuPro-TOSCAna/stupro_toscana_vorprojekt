package de.toscana.transformator.model;

/**
 * This class represents the path of a artifact in the TOSCAlite archive
 */
public class ArtifactPath {
    private String absoultePath;

    public ArtifactPath(String path, Node node) {
        if (node == null) {
            throw new IllegalArgumentException("A node has to be supplied," +
                    " when creating a ArtifactPath");
        }
        if (path.startsWith("/")) {
            this.absoultePath = path;
            return;
        }
        absoultePath = "/" + node.name + "/" + path;
    }

    public ArtifactPath(String path, Relationship relationship) {
        if (relationship == null) {
            throw new IllegalArgumentException("A relationship has to be supplied," +
                    " when creating a ArtifactPath");
        }
        if (path.startsWith("/")) {
            this.absoultePath = path;
            return;
        }
        absoultePath = "/relationships/" + path;
    }

    public String getAbsoultePath() {
        return absoultePath;
    }

    @Override
    public String toString() {
        return absoultePath;
    }
}
