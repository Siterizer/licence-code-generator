package licence.code.generator.services;

import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.entities.User;

import java.util.List;

public interface IUserService {

    void registerUser(RegisterUserDto userDto);

    List<User> getAllUsers();

    User findUserByEmail(String email);

    User findUserByUsername(String username);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void changeUserPassword(User user, String oldPassword, String newPassword);
}
