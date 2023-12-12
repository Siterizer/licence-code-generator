package licence.code.generator.services;

import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.entities.Role;
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
import java.util.List;

@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
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
        if (userExists(userDto.getEmail(), userDto.getUsername())) {
            throw new UserAlreadyExistException("There is an account with that username/email: " + userDto.getEmail());
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));
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
        User user = userRepository.findById(id).orElseThrow();
        if (!user.isAccountNonLocked()) {
            throw new UserAlreadyBlockedException("User with id:" + user.getId() + " is already blocked");
        }

        if (user.getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch(e -> e.equals("ROLE_ADMIN"))) {
            throw new InsufficientPrivilegesException("Admin with id: " + admin.getId() + " tried to block another admin with id:" + id);
        }
        user.setLocked(true);
        userRepository.save(user);
    }

    @Override
    public void unblockUser(Long id, User admin) {
        User user = userRepository.findById(id).orElseThrow();
        if (user.isAccountNonLocked()) {
            throw new UserNotBlockedException("User with id:" + user.getId() + " is not blocked");
        }

        if (user.getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch(e -> e.equals("ROLE_ADMIN"))) {
            throw new InsufficientPrivilegesException("Admin with id: " + admin.getId() + " tried to unblock another admin with id:" + id);
        }
        user.setLocked(false);
        userRepository.save(user);
    }

    private boolean userExists(String email, String username) {
        return userRepository.findByEmail(email) != null || userRepository.findByUsername(username) != null;
    }

    public boolean validCurrentPassword(String providedPassword, final String currentPassword) {
        return passwordEncoder.matches(providedPassword, currentPassword);
    }
}
