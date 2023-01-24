package edu.servidor.objects.Objects.services;

import edu.servidor.objects.Objects.models.Bucket;
import edu.servidor.objects.Objects.models.User;
import edu.servidor.objects.Objects.repos.BucketDao;
import edu.servidor.objects.Objects.repos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyService {
    @Autowired
    UserDao userDao;

    @Autowired
    BucketDao bucketDao;

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
            return userDao.modifyPassword(password.hashCode(), username) == 0 ? "Unable to change password" : "password changed";
        }
    }

    public User getUserById(String username) {
        return userDao.getUsersByUsername(username).get(0);
    }

    public String createBucket(User currentUser, String uri) {
        if (bucketNameAvailable(currentUser, uri)) {
            return bucketDao.createBucket(currentUser, uri) == 0 ? "Database error" : "Success creating bucket";
        } else return "Bucket name already exists";
    }



    public List<Bucket> getBuckets(User user) {
        return bucketDao.getBucketsFromUser(user.getUsername());
    }

    public String deleteBucket(int id) {
        return bucketDao.deleteBucket(id) == 0 ? "Unable to delete bucket" : "Bucket deleted";
    }

    //Funciones extra
    private boolean bucketNameAvailable(User currentUser, String uri) {
        return bucketDao.getBucketFromUriAndUsername(uri, currentUser.getUsername()).size() == 0;
    }
}


