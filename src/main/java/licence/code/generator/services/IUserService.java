package licence.code.generator.services;

import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.User;

import java.util.List;

public interface IUserService {

    void registerUser(UserDto userDto);

    List<User> getAllUsers();

    void saveRegisteredUser(User user);

    void deleteUser(User user);
}
