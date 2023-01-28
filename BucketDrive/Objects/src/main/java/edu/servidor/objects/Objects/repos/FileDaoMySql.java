package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.FileData;
import edu.servidor.objects.Objects.models.ObjectFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class FileDaoMySql implements FileDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<FileData> getFileByHash(int hash) {
        return jdbcTemplate.query("SELECT * FROM file WHERE hash = ?", new BeanPropertyRowMapper<>(FileData.class), hash);
    }

    @Override
    public int createFile(byte[] body) {
        return jdbcTemplate.update("INSERT INTO file (body, hash, ref) values (?, ?, 1)", body, Arrays.hashCode(body));
    }

    @Override
    public int removeFile(int fileId) {
        return jdbcTemplate.update("DELETE FROM file WHERE id = ?;", fileId);
    }

    @Override
    public void modifyRefCounter(int id, int ref) {
        jdbcTemplate.update("UPDATE File set ref = ? WHERE id = ?", ref, id);
    }

    @Override
    public List<FileData> getFileById(int fileId) {
        return jdbcTemplate.query("SELECT * FROM File WHERE id = ?", new BeanPropertyRowMapper<>(FileData.class), fileId);
    }
}
