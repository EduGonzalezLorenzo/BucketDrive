package edu.servidor.objects.Objects.services;

import edu.servidor.objects.Objects.models.*;
import edu.servidor.objects.Objects.repos.BucketDao;
import edu.servidor.objects.Objects.repos.FileDao;
import edu.servidor.objects.Objects.repos.ObjectDao;
import edu.servidor.objects.Objects.repos.ReferenceObjectToFileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
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
        return bucketDao.getBucketFromUriAndUsername(uri, currentUser.getUsername()).size() == 0;
    }

    public List<Bucket> getBuckets(User user) {
        return bucketDao.getBucketsFromUser(user.getUsername());
    }

    public String deleteBucket(int id) {
        return bucketDao.deleteBucket(id) == 0 ? "Unable to delete bucket" : "Bucket deleted";
    }

    public int getBucketID(String name, String username) {
        List<Bucket> buckets = bucketDao.getBucketByNameOwner(name, username);
        if (buckets.size() == 1) return buckets.get(0).getId();
        return 0;
    }

    public List<ObjectFile> getObjectsFromBucket(int bucketID) {
        return objectDao.getObjectsFromBucket(bucketID);
    }

    public String createObject(MultipartFile file, String path, Bucket bucket, User user) throws IOException {
        Timestamp currentTime = new Timestamp(new Date().getTime());
        String uri = generateUri(bucket, path, file.getOriginalFilename());
        ObjectFile objectFile = generateObject(currentTime, bucket, user, file, uri);
        byte[] body = file.getBytes();
        List<FileData> files = fileDao.getFileByBody(body);
        if (files.size() == 0) fileDao.createFile(body);
        files = fileDao.getFileByBody(body);
        int fileId = files.get(0).getId();

        if (objectDao.createObject(objectFile) == 0) return "Error creating object";

        objectFile = objectDao.getObjectsFromUri(uri).get(0);

        referenceObjectToFileDao.insertRow(objectFile.getId(), fileId, currentTime);

        return "Object created successfully";
    }

    private String generateUri(Bucket bucket, String path, String fileName) {
        if (!path.startsWith("/")) path = "/" + path;
        if (!path.endsWith("/")) path = path + "/";
        return bucket.getUri() + path + fileName;
    }

    private ObjectFile generateObject(Timestamp currentTime, Bucket bucket, User user, MultipartFile file, String uri) {
        ObjectFile objectFile = new ObjectFile();
        objectFile.setMetadataId(new HashMap<>());
        objectFile.setCreated(currentTime);
        objectFile.setLastModified(currentTime);
        objectFile.setBucketId(bucket.getId());
        objectFile.setUri(uri);
        objectFile.setOwner(user.getUsername());
        objectFile.setContentType(file.getContentType());
        return objectFile;
    }

    public Bucket getBucketByNameOwner(String bucket, String username) {
        List<Bucket> buckets = bucketDao.getBucketByNameOwner(bucket, username);
        if (buckets.size() == 1) return buckets.get(0);
        return null;
    }
}


