package edu.servidor.objects.Objects.services;

import edu.servidor.objects.Objects.models.*;
import edu.servidor.objects.Objects.repos.BucketDao;
import edu.servidor.objects.Objects.repos.FileDao;
import edu.servidor.objects.Objects.repos.ObjectDao;
import edu.servidor.objects.Objects.repos.ReferenceObjectToFileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BucketService {

    @Autowired
    BucketDao bucketDao;

    @Autowired
    ObjectDao objectDao;

    @Autowired
    FileDao fileDao;

    @Autowired
    ReferenceObjectToFileDao referenceObjectToFileDao;

    public String createBucket(User currentUser, String uri) {
        if (bucketNameAvailable(currentUser, uri)) {
            return bucketDao.createBucket(currentUser, uri) == 0 ? "Database error" : "Success creating bucket";
        } else return "Bucket name already exists";
    }

    private boolean bucketNameAvailable(User currentUser, String uri) {
        return bucketDao.getBucketFromUri(uri).size() == 0;
    }

    public List<Bucket> getBuckets(User user) {
        return bucketDao.getBucketsFromUser(user.getUsername());
    }

    public String deleteBucket(int bucketId, String userName) {
        if (bucketDao.getBucketByNameOwner(bucketDao.getBucketsById(bucketId).get(0).getUri(), userName).size() == 0){
            return "You are not the owner of this bucket";
        }
        List<ObjectFile> objects = objectDao.getObjectsFromBucket(bucketId);
        for (ObjectFile objectFile : objects) {
            int objectId = objectFile.getId();
            List<ReferenceObjectToFile> referenceObjectToFile = referenceObjectToFileDao.getRowsFromObjectId(objectId);
            referenceObjectToFileDao.deleteFromObjectId(objectId);
            objectDao.deleteFromId(objectId);
            checkFilesToDelete(referenceObjectToFile);
        }
        return bucketDao.deleteBucket(bucketId) == 0 ? "Unable to delete bucket" : "Bucket deleted";
    }

    public void checkFilesToDelete(List<ReferenceObjectToFile> references) {
        for (ReferenceObjectToFile reference : references) {
            int fileId = reference.getFileId();
            List<FileData> filesToObject = fileDao.getFileById(fileId);
            if (filesToObject.get(0).getRef() == 1) fileDao.removeFile(fileId);
            else fileDao.modifyRefCounter(fileId, filesToObject.get(0).getRef() - 1);
        }
    }

    public int getBucketID(String name, String username) {
        List<Bucket> buckets = bucketDao.getBucketByNameOwner(name, username);
        if (buckets.size() == 1) return buckets.get(0).getId();
        return 0;
    }

    public Bucket getBucketByNameOwner(String bucket, String username) {
        List<Bucket> buckets = bucketDao.getBucketByNameOwner(bucket, username);
        if (buckets.size() == 1) return buckets.get(0);
        return null;
    }

    public boolean checkBucketOwner(String bucketName, User user) {
        List<Bucket> buckets = bucketDao.getBucketFromUri(bucketName);
        if (buckets.size() == 0) return false;
        Bucket bucket = buckets.get(0);
        return bucket.getOwner().equals(user.getUsername());
    }
}


