package de.toscana.transformator.model;

/**
 * This class represents the path of a artifact in the TOSCAlite archive
 */
public class ArtifactPath {
    private static final String PATH_SEPERATOR = "/";
    private String absolutePath;

    public ArtifactPath(String path, Node node) {
        if (node == null) {
            throw new IllegalArgumentException("A node has to be supplied," +
                    " when creating a ArtifactPath");
        }
        if (path.startsWith(PATH_SEPERATOR)) {
            this.absolutePath = path;
            return;
        }
        absolutePath = PATH_SEPERATOR + node.name + PATH_SEPERATOR + path;
    }

    public ArtifactPath(String path, Relationship relationship) {
        if (relationship == null) {
            throw new IllegalArgumentException("A relationship has to be supplied," +
                    " when creating a ArtifactPath");
        }
        if (path.startsWith(PATH_SEPERATOR)) {
            this.absolutePath = path;
            return;
        }
        absolutePath = PATH_SEPERATOR + "relationships" + PATH_SEPERATOR + path;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public String toString() {
        return absolutePath;
    }
}
