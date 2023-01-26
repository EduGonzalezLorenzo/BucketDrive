package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.ReferenceObjectToFile;

import java.sql.Timestamp;
import java.util.List;

public interface ReferenceObjectToFileDao {
    int insertRow(int objectId, int fileId, Timestamp currentTime);

    int deleteFromObjectId(int objectId);

    List<ReferenceObjectToFile> getRowFromObjectId(int objectId);


    List<ReferenceObjectToFile> getRowFromFileId(int fileId);
}
