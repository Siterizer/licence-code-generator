package licence.code.generator.webapp.services;

import licence.code.generator.webapp.entities.User;

import java.util.List;

public interface IUserService {

    User registerUser(User user);

    List<User> getAllUsers();

    void saveRegisteredUser(User user);

    void deleteUser(User user);
}
