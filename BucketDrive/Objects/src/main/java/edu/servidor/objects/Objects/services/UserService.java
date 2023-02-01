package edu.servidor.objects.Objects.services;

import edu.servidor.objects.Objects.models.Bucket;
import edu.servidor.objects.Objects.models.User;
import edu.servidor.objects.Objects.repos.BucketDao;
import edu.servidor.objects.Objects.repos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static edu.servidor.objects.Objects.utils.GetHash.getHashSHA256;

@Component
public class UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    BucketService bucketService;

    public String addUser(String username, String name, String password) throws NoSuchAlgorithmException {
        if (userDao.getUsersByUsername(username).size() != 0) return "User name already exists";
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setPassword(getHashSHA256(password));
        return userDao.addUser(user) == 0 ? "Database error" : "Sign up successfully!";
    }

    public User login(String username, String password) throws NoSuchAlgorithmException {
        List<User> users = userDao.getUsersByUsername(username);
        if (users.size() == 1) {
            User user = users.get(0);
            if (user.getPassword().equals(getHashSHA256(password))) return user;
        }
        return null;
    }

    public String modifyUser(String name, String password, String username) throws NoSuchAlgorithmException {
        if (name != null) {
            return userDao.modifyName(name, username) == 0 ? "Unable to change name" : "Name changed";
        }
        if (password != null) {
            return userDao.modifyPassword(getHashSHA256(password), username) == 0 ? "Unable to change password" : "Password changed";
        } else {
            return deleteUser(username);
        }
    }

    private String deleteUser(String username) {
        User user = getUserByUserName(username);
        List<Bucket> userBuckets = bucketService.getBuckets(user);
        for (Bucket bucket : userBuckets) {
            bucketService.deleteBucket(bucket.getId());
        }
        return userDao.deleteUserByUserName(username) == 0 ? "Unable to delete user" : "User Deleted";
    }

    public User getUserByUserName(String username) {
        List<User> users = userDao.getUsersByUsername(username);
        if (users.size()==0) return null;
        return users.get(0);
    }
}
