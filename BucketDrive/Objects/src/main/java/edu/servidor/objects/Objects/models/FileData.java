package edu.servidor.objects.Objects.models;

public class FileData {
    private int id;
    private byte[] body;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

}
