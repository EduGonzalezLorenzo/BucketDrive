package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.FileData;
import edu.servidor.objects.Objects.models.ObjectFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileDaoMySql implements FileDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public List<FileData> getFileByBody(byte[] body) {
        return jdbcTemplate.query("SELECT * FROM file WHERE body = ?", new BeanPropertyRowMapper<>(FileData.class), body);
    }

    @Override
    public int createFile(byte[] body) {
        return jdbcTemplate.update("INSERT INTO file (body) values (?)", body);
    }

    @Override
    public int removeFile(int fileId) {
        return jdbcTemplate.update("DELETE FROM file WHERE id = ?;", fileId);
    }
}
