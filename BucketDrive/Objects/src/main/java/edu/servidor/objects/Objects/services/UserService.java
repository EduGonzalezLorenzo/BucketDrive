package edu.servidor.objects.Objects.services;

import edu.servidor.objects.Objects.models.User;
import edu.servidor.objects.Objects.repos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {
    @Autowired
    UserDao userDao;

    public String addUser(String username, String name, String password) {
        if (userDao.getUsersByUsername(username).size() != 0) return "User name already exists";
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setPassword(String.valueOf(password.hashCode()));
        return userDao.addUser(user) == 0 ? "Database error" : "Sign up successfully!";
    }

    public User login(String username, String password) {
        List<User> users = userDao.getUsersByUsername(username);
        if (users.size() == 1) {
            User user = users.get(0);
            if (user.getPassword().equals(String.valueOf(password.hashCode()))) return user;
        }
        return null;
    }

    public String modifyUser(String name, String password, String username) {
        if (name != null) {
            return userDao.modifyName(name, username) == 0 ? "Unable to change name" : "Name changed";
        } else {
            return userDao.modifyPassword(password.hashCode(), username) == 0 ? "Unable to change password" : "Password changed";
        }
    }

    public User getUserById(String username) {
        return userDao.getUsersByUsername(username).get(0);
    }
}
