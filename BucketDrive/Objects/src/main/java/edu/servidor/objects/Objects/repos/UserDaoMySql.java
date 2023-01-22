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
}
