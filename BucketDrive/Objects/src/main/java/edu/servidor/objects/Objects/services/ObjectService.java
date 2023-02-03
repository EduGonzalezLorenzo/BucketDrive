package edu.servidor.objects.Objects.services;

import edu.servidor.objects.Objects.models.*;
import edu.servidor.objects.Objects.repos.FileDao;
import edu.servidor.objects.Objects.repos.ObjectDao;
import edu.servidor.objects.Objects.repos.ReferenceObjectToFileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static edu.servidor.objects.Objects.utils.HashUtils.getHashSHA256;
import static edu.servidor.objects.Objects.utils.ObjectUtils.generateObject;
import static edu.servidor.objects.Objects.utils.ObjectUtils.generateUri;

@Controller
public class ObjectService {
    @Autowired
    ObjectDao objectDao;
    @Autowired
    ReferenceObjectToFileDao referenceObjectToFileDao;
    @Autowired
    FileDao fileDao;

    public List<ReferenceObjectToFile> getObjectVersions(int objectId) {
        return referenceObjectToFileDao.getRowsFromObjectId(objectId);
    }

    public List<String> getObjectsFromBucketAndPath(int bucketID, String uri) {
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

    public String createObject(MultipartFile file, String path, Bucket bucket, User user) throws IOException, NoSuchAlgorithmException {
        Timestamp currentTime = new Timestamp(new Date().getTime());
        String uri = generateUri(bucket, path, file.getOriginalFilename());
        ObjectFile objectFile = generateObject(currentTime, bucket, user, file, uri);
        byte[] body = file.getBytes();
        String bodyHash = getHashSHA256(Arrays.toString(body));
        List<FileData> files = fileDao.getFileByHash(bodyHash);
        if (files.size() == 0) fileDao.createFile(body, bodyHash);
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

    public String deleteObject(int objectId, User user) {
        List<ObjectFile> objectFile = objectDao.getObjectFromId(objectId);
        if (objectFile.size() == 0) return "Unable to delete object";
        if (!objectFile.get(0).getOwner().equals(user.getUsername())) return "You are not the owner of this object";
        List<ReferenceObjectToFile> referenceObjectToFile = referenceObjectToFileDao.getRowsFromObjectId(objectId);
        referenceObjectToFileDao.deleteFromObjectId(objectId);
        int deletedObjects = objectDao.deleteFromId(objectId);
        checkFilesToDelete(referenceObjectToFile);

        return deletedObjects == 0 ? "Unable to delete object" : "Object deleted";
    }

    public void checkFilesToDelete(List<ReferenceObjectToFile> references) {
        for (ReferenceObjectToFile reference : references) {
            int fileId = reference.getFileId();
            List<FileData> filesToObject = fileDao.getFileById(fileId);
            if (filesToObject.get(0).getRef() == 1) fileDao.removeFile(fileId);
            else fileDao.modifyRefCounter(fileId, filesToObject.get(0).getRef() - 1);
        }
    }

    private int getVersionId(int objectId) {
        List<ReferenceObjectToFile> referenceObjectToFiles = referenceObjectToFileDao.getRowsFromObjectId(objectId);
        return referenceObjectToFiles.size() + 1;
    }
}
