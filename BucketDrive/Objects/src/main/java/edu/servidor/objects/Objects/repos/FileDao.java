package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.FileData;

import java.util.List;

public interface FileDao {
    List<FileData> getFileByHash(int hash);

    int createFile(byte[] body);

    int removeFile(int fileId);

    void modifyRefCounter(int fileID, int ref);

    List<FileData> getFileById(int fileId);
}
