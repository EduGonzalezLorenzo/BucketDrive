package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.User;

import java.util.List;

public interface UserDao {
    int addUser(User user);

    List<User> getAllUsers();
}
