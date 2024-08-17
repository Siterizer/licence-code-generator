package licence.code.generator.services.user;

import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends UserDetailsService {

    void registerUser(RegisterUserDto userDto);

    void confirmRegistration(String token);

    void createResetUserPasswordToken(String username);

    void resetUserPassword(String token, String newPassword);

    List<User> getAllUsers();

    List<UserDto> getAllUsersDto(User admin);

    User findUserByEmail(String email);

    User loadUserWithRelatedEntitiesByUsername(String username);

    void blockUser(Long id, User admin);

    void unblockUser(Long id, User admin);

    void changeUserPassword(Long userId, String oldPassword, String newPassword);
}
