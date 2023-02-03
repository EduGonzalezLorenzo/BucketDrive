package edu.servidor.objects.Objects.repos;

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
        return jdbcTemplate.update("INSERT INTO `object` (`uri`, `bucketId`, `owner`, `contentType`, `lastModified`, `created`, `metadataId`) VALUES (?,?,?,?,?,?,?)"
                , objectFile.getUri(), objectFile.getBucketId(), objectFile.getOwner(), objectFile.getContentType(), objectFile.getLastModified(), objectFile.getCreated(), 0);
    }

    @Override
    public List<ObjectFile> getObjectsFromUri(String uri) {
        return jdbcTemplate.query("SELECT * FROM object WHERE uri = ?", new BeanPropertyRowMapper<>(ObjectFile.class), uri);
    }

    @Override
    public int deleteFromId(int objectId) {
        return jdbcTemplate.update("DELETE FROM object WHERE id = ?;", objectId);
    }

    @Override
    public List<ObjectFile> getObjectFromId(int objectId) {
        return jdbcTemplate.query("SELECT * FROM object WHERE id = ?", new BeanPropertyRowMapper<>(ObjectFile.class), objectId);
    }

//    @Override
//    public List<ObjectFile> getObjectsAndVersionsFromUri(String substring) {
//        return jdbcTemplate.query("SELECT * FROM ");
//    }
}
