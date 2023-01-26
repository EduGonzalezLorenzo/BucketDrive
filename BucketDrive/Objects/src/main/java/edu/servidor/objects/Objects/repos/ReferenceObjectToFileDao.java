package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.ObjectFile;

import java.sql.Timestamp;

public interface ReferenceObjectToFileDao {
    int insertRow(int objectId, int fileId, Timestamp currentTime);
}
