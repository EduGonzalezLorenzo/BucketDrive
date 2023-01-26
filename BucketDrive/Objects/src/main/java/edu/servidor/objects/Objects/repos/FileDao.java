package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.FileData;

import java.util.List;

public interface FileDao {
    List<FileData> getFileByBody(byte[] body);

    int createFile(byte[] body);
}
