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

    public String addUser(String name, String password) {
        List<User> userList = userDao.getAllUsers();
        for (User user : userList) {
            if (user.getName().equals(name)) {
                return "User name already exists";
            }
        }
        User user = new User();
        user.setName(name);
        String hashPass = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
        user.setPassword(hashPass);
        return userDao.addUser(user) == 0 ? "Database error" : "Success user created!";
    }
}
