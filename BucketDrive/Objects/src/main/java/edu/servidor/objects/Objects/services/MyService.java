package edu.servidor.objects.Objects.services;


import com.google.common.hash.Hashing;
import edu.servidor.objects.Objects.models.User;
import edu.servidor.objects.Objects.repos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class MyService {
    @Autowired
    UserDao userDao;

    public String addUser(String username, String name, String password) {
        List<User> userList = userDao.getAllUsers();
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return "User name already exists";
            }
        }
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setPassword(String.valueOf(password.hashCode()));
        return userDao.addUser(user) == 0 ? "Database error" : "Sign up successfully!";
    }

    public User login(String username, String password) {
        List<User> userList = userDao.getAllUsers();
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
               if(user.getPassword().equals(String.valueOf(password.hashCode()))) return user;
            }
        }
        return null;
    }

    public String modifyUser(String username, String name, String password, int userId) {
        if (username!=null){
            List<User> userList = userDao.getAllUsers();
            for (User user : userList) {
                if (user.getUsername().equals(username)) {
                    return "Username already exists";
                }
            }
            return userDao.modifyUsername(username, userId) == 0 ? "Unable to change username" : "Username changed";
        }else if (name != null){
            return userDao.modifyName(name, userId) == 0 ? "Unable to change name" : "Name changed";
        } else {
            return userDao.modifyPassword(password.hashCode(), userId) == 0 ? "Unable to change password" : "password changed";
        }
    }

    public User getUserById(int id) {
        return userDao.getUserById(id).get(0);
    }
}
