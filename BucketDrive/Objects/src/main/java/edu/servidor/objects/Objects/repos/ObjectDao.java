package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.ObjectFile;

import java.util.List;

public interface ObjectDao {
    List<ObjectFile> getObjectsFromBucket(int bucketID);

    int createObject(ObjectFile objectFile);

    List<ObjectFile> getObjectsFromUri(String uri);
}
