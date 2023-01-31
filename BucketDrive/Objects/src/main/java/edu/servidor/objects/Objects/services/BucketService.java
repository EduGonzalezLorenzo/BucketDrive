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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    public List<ReferenceObjectToFile> getObjectVersions(int objectId) {
        return referenceObjectToFileDao.getRowsFromObjectId(objectId);
    }

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

    public String deleteBucket(int bucketId) {
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

    private void checkFilesToDelete(List<ReferenceObjectToFile> references) {
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

    public List<String> getObjectsFromBucketFromUri(int bucketID, String uri) {
        List<ObjectFile> allBucketObjects = objectDao.getObjectsFromBucket(bucketID);

        return getObjectsPaths(allBucketObjects, uri);
    }

    private List<String> getObjectsPaths(List<ObjectFile> allBucketObjects, String uri) {
        int uriPosition = uri.split("/").length;
        List<String> objectsPaths = new ArrayList<>();
        for (ObjectFile objectFile : allBucketObjects) {
            String objectUri = objectFile.getUri();
            objectUri = getCurrentUri(objectUri, uriPosition, uri);
            if (objectUri.equals("")) continue;
            if (objectUri.contains("/")) objectUri = (objectUri.substring(0, objectUri.indexOf("/") + 1));
            if (!objectsPaths.contains(objectUri)) objectsPaths.add(objectUri);
        }
        return objectsPaths;
    }

    public String getCurrentUri(String uri, int position, String currentFolder) {
        String[] uriSplit = uri.split("/");
        String[] currentFolderSplit = currentFolder.split("/");
        if (uriSplit.length <= position) return "";
        for (int i = 0; i < position; i++) {
            if (!uriSplit[i].equals(currentFolderSplit[i])) return "";
            uri = uri.substring(uri.indexOf("/") + 1);
        }
        return uri;
    }

    public String createObject(MultipartFile file, String path, Bucket bucket, User user) throws IOException {
        Timestamp currentTime = new Timestamp(new Date().getTime());
        String uri = generateUri(bucket, path, file.getOriginalFilename());
        ObjectFile objectFile = generateObject(currentTime, bucket, user, file, uri);
        byte[] body = file.getBytes();
        int bodyHash = Arrays.hashCode(body);
        List<FileData> files = fileDao.getFileByHash(bodyHash);
        if (files.size() == 0) fileDao.createFile(body);
        else {
            FileData fileData = files.get(0);
            fileDao.modifyRefCounter(fileData.getId(), fileData.getRef() + 1);
        }
        files = fileDao.getFileByHash(bodyHash);
        int fileId = files.get(0).getId();

        List<ObjectFile> objects = objectDao.getObjectsFromUri(uri);
        if (objects.size() == 0) {
            objectDao.createObject(objectFile);
            objects = objectDao.getObjectsFromUri(uri);
            referenceObjectToFileDao.insertRowForNewObject(objects.get(0).getId(), fileId, currentTime);
            return "Object created successfully";
        } else {
            int versionId = getVersionId(objects.get(0).getId());
            referenceObjectToFileDao.insertRowForUpdate(objects.get(0).getId(), fileId, currentTime, versionId);
            return "Object updated successfully";
        }
    }

    private int getVersionId(int objectId) {
        List<ReferenceObjectToFile> referenceObjectToFiles = referenceObjectToFileDao.getRowsFromObjectId(objectId);
        return referenceObjectToFiles.size() + 1;
    }

    private String generateUri(Bucket bucket, String path, String fileName) {
        if (!path.startsWith("/")) path = "/" + path;
        if (!path.endsWith("/")) path = path + "/";
        return bucket.getUri() + path + fileName;
    }

    private ObjectFile generateObject(Timestamp currentTime, Bucket bucket, User user, MultipartFile file, String uri) {
        ObjectFile objectFile = new ObjectFile();
        objectFile.setMetadataId(0);
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

    public List<ObjectFile> getObjectsFromBucket(int id) {
        return objectDao.getObjectsFromBucket(id);
    }

    public int getObjectId(String path) {
        return objectDao.getObjectsFromUri(path).get(0).getId();
    }

    public ReferenceObjectToFile getObjectFromVersion(int objectId, int versionId) {
        return referenceObjectToFileDao.getRowFromObjectAndVersion(objectId, versionId).get(0);
    }

    public FileData getFileById(int fileId) {
        return fileDao.getFileById(fileId).get(0);
    }

    public ObjectFile getObjectFromId(int objectId) {
        return objectDao.getObjectFromId(objectId).get(0);
    }

    public String getFileName(ObjectFile objectFile) {
        String[] objectFileUriSplit = objectFile.getUri().split("/");
        return objectFileUriSplit[objectFileUriSplit.length - 1];
    }

    public Object deleteObject(int objectId) {
        List<ReferenceObjectToFile> referenceObjectToFile = referenceObjectToFileDao.getRowsFromObjectId(objectId);
        referenceObjectToFileDao.deleteFromObjectId(objectId);
        int deletedObjects = objectDao.deleteFromId(objectId);
        checkFilesToDelete(referenceObjectToFile);

        return deletedObjects == 0 ? "Unable to delete object" : "Object deleted";
    }

    public boolean checkOwner(String bucketName, User user) {
        Bucket bucket = bucketDao.getBucketFromUri(bucketName).get(0);
        return bucket.getOwner().equals(user.getUsername());
    }
}


