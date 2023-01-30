package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.ReferenceObjectToFile;

import java.sql.Ref;
import java.sql.Timestamp;
import java.util.List;

public interface ReferenceObjectToFileDao {
    int insertRowForNewObject(int objectId, int fileId, Timestamp currentTime);

    int deleteFromObjectId(int objectId);

    List<ReferenceObjectToFile> getRowsFromObjectId(int objectId);


    List<ReferenceObjectToFile> getRowFromFileId(int fileId);

    int insertRowForUpdate(int objectId, int fileId, Timestamp currentTime, int versionID);

    List<ReferenceObjectToFile> getRowFromObjectAndVersion(int objectId, int versionId);
}
