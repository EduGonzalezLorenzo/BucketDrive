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
        if (!userDao.checkUserName(username)) return "User name already exists";
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setPassword(String.valueOf(password.hashCode()));
        return userDao.addUser(user) == 0 ? "Database error" : "Sign up successfully!";
    }

    public User login(String username, String password) {
        User user = userDao.getUserByUserName(username);
        if (user.getPassword().equals(String.valueOf(password.hashCode()))) return user;
        return null;
    }

    public String modifyUser(String name, String password, int userId) {
        if (name != null) {
            return userDao.modifyName(name, userId) == 0 ? "Unable to change name" : "Name changed";
        } else {
            return userDao.modifyPassword(password.hashCode(), userId) == 0 ? "Unable to change password" : "password changed";
        }
    }

    public User getUserById(int id) {
        return userDao.getUserById(id).get(0);
    }

    public String createBucket(User currentUser, String uri) {
        if (bucketDao.checkUri(uri, currentUser.getId())) {
            return bucketDao.createBucket(currentUser, uri) == 0 ? "Database error" : "Success creating bucket";
        } else return "Bucket name already exists";
    }

    public List<Bucket> getBuckets(User user) {
        return bucketDao.getBucketsFromUser(user.getId());
    }

    public String deleteBucket(int id) {
        Bucket bucketToDelete = bucketDao.getBucketById(id);
        if (bucketToDelete == null) return "The bucket that you are trying to delete doesn't exists";
        String bucketName = bucketToDelete.getUri();
        return bucketDao.deleteBucket(id) == 0 ? "Unable to delete bucket" : "Bucket " + bucketName + " deleted";
    }
}
