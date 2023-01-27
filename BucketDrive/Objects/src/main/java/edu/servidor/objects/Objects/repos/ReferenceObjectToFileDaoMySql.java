package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.FileData;
import edu.servidor.objects.Objects.models.ObjectFile;
import edu.servidor.objects.Objects.models.ReferenceObjectToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public class ReferenceObjectToFileDaoMySql implements ReferenceObjectToFileDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insertRow(int objectId, int fileId, Timestamp currentTime) {
        return jdbcTemplate.update("INSERT INTO objectToFile (objectId, fileId, uploadDate, versionId) values (?, ?, ?, ?)"
                , objectId, fileId, currentTime, 1);
    }

    @Override
    public int deleteFromObjectId(int objectId) {
        return jdbcTemplate.update("DELETE FROM ObjectToFile WHERE objectId = ?;", objectId);
    }

    @Override
    public List<ReferenceObjectToFile> getRowFromObjectId(int objectId) {
        return jdbcTemplate.query("SELECT * FROM ObjectToFile WHERE objectId = ?", new BeanPropertyRowMapper<>(ReferenceObjectToFile.class), objectId);
    }

    @Override
    public List<ReferenceObjectToFile> getRowFromFileId(int fileId) {
        return jdbcTemplate.query("SELECT * FROM ObjectToFile WHERE fileId = ?", new BeanPropertyRowMapper<>(ReferenceObjectToFile.class), fileId);

    }


}