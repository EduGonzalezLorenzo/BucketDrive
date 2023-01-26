package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.ObjectFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class ReferenceObjectToFileDaoMySql implements ReferenceObjectToFileDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insertRow(int objectId, int fileId, Timestamp currentTime) {
        return jdbcTemplate.update("INSERT INTO objectToFile (objectId, fileId, uploadDate, versionId) values (?, ?, ?)"
                , objectId, fileId, currentTime, 1);
    }
}
