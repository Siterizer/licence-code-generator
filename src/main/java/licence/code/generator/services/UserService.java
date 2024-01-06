package licence.code.generator.services;

import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.repositories.RoleRepository;
import licence.code.generator.repositories.UserRepository;
import licence.code.generator.web.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserDtoMapper userDtoMapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userDtoMapper = userDtoMapper;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    @Override
    public List<UserDto> getAllUsersDto(User admin) {
        if (!admin.hasRole(RoleName.ROLE_ADMIN)) {
            throw new InsufficientPrivilegesException("User with id: " + admin.getId() + " tried to get all UsersDto:");
        }
        return userRepository.findAll().stream()
                .map(userDtoMapper::toDto)
                .sorted((Comparator.comparing(UserDto::id)))
                .collect(Collectors.toList());
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void registerUser(RegisterUserDto userDto) throws UserAlreadyExistException {
        if (userExists(userDto.email(), userDto.username())) {
            throw new UserAlreadyExistException("There is an account with that username/email: " + userDto.email());
        }

        User user = new User();
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setRoles(Collections.singletonList(roleRepository.findByName(RoleName.ROLE_USER)));
        user.setLocked(false);
        userRepository.save(user);
    }

    @Override
    public void changeUserPassword(final User user, final String oldPassword, final String newPassword) {
        if (!validCurrentPassword(oldPassword, user.getPassword())) {
            throw new InvalidOldPasswordException("Invalid Old Password for user with id: " + user.getId());
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void blockUser(Long id, User admin) {
        if (!admin.hasRole(RoleName.ROLE_ADMIN)) {
            throw new InsufficientPrivilegesException("User with id: " + admin.getId() + " tried to block another User");
        }
        User user = userRepository.findById(id).orElseThrow();
        if (!user.isAccountNonLocked()) {
            throw new UserAlreadyBlockedException("User with id:" + user.getId() + " is already blocked");
        }
        if (user.hasRole(RoleName.ROLE_ADMIN)) {
            throw new InsufficientPrivilegesException("Admin with id: " + admin.getId() + " tried to block another admin with id:" + id);
        }
        user.setLocked(true);
        userRepository.save(user);
    }

    @Override
    public void unblockUser(Long id, User admin) {
        if (!admin.hasRole(RoleName.ROLE_ADMIN)) {
            throw new InsufficientPrivilegesException("User with id: " + admin.getId() + " tried to unblock another User");
        }
        User user = userRepository.findById(id).orElseThrow();
        if (user.isAccountNonLocked()) {
            throw new UserNotBlockedException("User with id:" + user.getId() + " is not blocked");
        }
        if (user.hasRole(RoleName.ROLE_ADMIN)) {
            throw new InsufficientPrivilegesException("Admin with id: " + admin.getId() + " tried to unblock another admin with id:" + id);
        }
        user.setLocked(false);
        userRepository.save(user);
    }

    private boolean userExists(String email, String username) {
        return userRepository.findByEmail(email) != null || userRepository.findByUsername(username) != null;
    }

    private boolean validCurrentPassword(String providedPassword, final String currentPassword) {
        return passwordEncoder.matches(providedPassword, currentPassword);
    }
}
