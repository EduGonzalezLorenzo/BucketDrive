package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.Bucket;
import edu.servidor.objects.Objects.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BucketDaoMySql implements BucketDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int createBucket(User currentUser, String uri) {
        return jdbcTemplate.update("INSERT INTO bucket (uri, owner) values (?, ?)", uri, currentUser.getUsername());
    }

    @Override
    public List<Bucket> getBucketFromUri(String uri) {
        return jdbcTemplate.query("SELECT * from bucket where uri = ?", new BeanPropertyRowMapper<>(Bucket.class), uri);
    }

    @Override
    public List<Bucket> getBucketsFromUser(String username) {
        return jdbcTemplate.query("SELECT * from bucket where owner = ?", new BeanPropertyRowMapper<>(Bucket.class), username);
    }

    @Override
    public int deleteBucket(int id) {
        return jdbcTemplate.update("DELETE FROM bucket WHERE id = ?;", id);
    }

    @Override
    public List<Bucket> getBucketsById(int id) {
        return jdbcTemplate.query("SELECT * from bucket where id = ?", new BeanPropertyRowMapper<>(Bucket.class), id);
    }

    @Override
    public List<Bucket> getBucketByNameOwner(String name, String username) {
        return jdbcTemplate.query("SELECT * FROM bucket WHERE uri = ? AND owner = ?", new BeanPropertyRowMapper<>(Bucket.class),name, username );
    }

}
