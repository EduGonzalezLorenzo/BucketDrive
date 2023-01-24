package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.Bucket;
import edu.servidor.objects.Objects.models.ObjectFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ObjectDaoMySql implements ObjectDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<ObjectFile> getObjectsFromBucket(int bucketID) {
        return jdbcTemplate.query("SELECT * FROM object WHERE bucketid = ?", new BeanPropertyRowMapper<>(ObjectFile.class), bucketID);
    }

    @Override
    public int createObject(ObjectFile objectFile) {
        return jdbcTemplate.update("INSERT INTO `object` (`uri`, `body`, `bucketId`, `versionId`, `owner`, `contentLength`, `contentType`, `lastModified`, `created`, `ETag`, `metadataId`)" +
                " VALUES (?,?,?,?,?,?,?,?,?,?,?)",objectFile.getUri(), objectFile.getBody(), objectFile.getBucketId(), objectFile.getVersionId(), objectFile.getOwner(), objectFile.getContentLength(), objectFile.getContentType(), objectFile.getLastModified(), objectFile.getCreated(), objectFile.getETag(), 0);
    }
}
