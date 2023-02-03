package edu.servidor.objects.Objects.utils;

import edu.servidor.objects.Objects.models.Bucket;
import edu.servidor.objects.Objects.models.ObjectFile;
import edu.servidor.objects.Objects.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

public class ObjectUtils {
    public static ObjectFile generateObject(Timestamp currentTime, Bucket bucket, User user, MultipartFile file, String uri) {
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

    public static String generateUri(Bucket bucket, String path, String fileName) {
        if (!path.startsWith("/")) path = "/" + path;
        if (!path.endsWith("/")) path = path + "/";
        return bucket.getUri() + path + fileName;
    }


}
