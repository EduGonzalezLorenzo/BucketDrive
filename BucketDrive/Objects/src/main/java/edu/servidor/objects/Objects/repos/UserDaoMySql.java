package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDaoMySql implements UserDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int addUser(User user) {
        return jdbcTemplate.update("INSERT INTO user (username, name, password) values (?, ?, ?)", user.getUsername(), user.getName(), user.getPassword());
    }

    @Override
    public int modifyName(String name, String username) {
        return jdbcTemplate.update("UPDATE user SET name=? WHERE username = ?", name, username);
    }

    @Override
    public int modifyPassword(int password, String username) {
        return jdbcTemplate.update("UPDATE user SET password=? WHERE username = ?", password, username);
    }

    @Override
    public List<User> getUsersByUsername(String username) {
        return jdbcTemplate.query("SELECT * from user WHERE username = ?", new BeanPropertyRowMapper<>(User.class), username);
    }
}
