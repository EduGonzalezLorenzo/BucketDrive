package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.User;

import java.util.List;

public interface UserDao {
    int addUser(User user);

    List<User> getAllUsers();

    int modifyUsername(String username, int id);

    int modifyName(String name, int id);

    int modifyPassword(int password, int id);

    List<User> getUserById(int id);
}
