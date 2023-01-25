package edu.servidor.objects.Objects.models;

import java.sql.Timestamp;
import java.util.Map;

public class ObjectFile {
    private int id;
    private String uri;
    private int bucketId;
    private String owner;
    private String contentType;
    private Timestamp lastModified;
    private Timestamp created;
    private Map<String, String> metadataId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getBucketId() {
        return bucketId;
    }

    public void setBucketId(int bucketId) {
        this.bucketId = bucketId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Map<String, String> getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(Map<String, String> metadataId) {
        this.metadataId = metadataId;
    }
}
