package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDaoMySql  implements UserDao{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * from user", new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public int addUser(User user) {
        return jdbcTemplate.update("INSERT INTO user (username, name, password) values (?, ?, ?)", user.getUsername(), user.getName(), user.getPassword());
    }

    @Override
    public int modifyUsername(String username, int id) {
        return jdbcTemplate.update("UPDATE user SET username=? WHERE id = ?", username, id);
    }

    @Override
    public int modifyName(String name, int id) {
        return jdbcTemplate.update("UPDATE user SET name=? WHERE id = ?", name, id);
    }

    @Override
    public int modifyPassword(int password, int id) {
        return jdbcTemplate.update("UPDATE user SET password=? WHERE id = ?", password, id);
    }

    @Override
    public List<User> getUserById(int id) {
        return jdbcTemplate.query("SELECT * from user WHERE id = ?", new BeanPropertyRowMapper<>(User.class), id);
    }
}
