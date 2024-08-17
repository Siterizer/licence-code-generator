package licence.code.generator.services.user;

import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.entities.ResetPasswordToken;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.entities.VerificationToken;
import licence.code.generator.repositories.RoleRepository;
import licence.code.generator.repositories.UserRepository;
import licence.code.generator.services.email.IEmailService;
import licence.code.generator.services.token.IResetPasswordTokenService;
import licence.code.generator.services.token.IVerificationTokenService;
import licence.code.generator.web.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserDtoMapper userDtoMapper;
    private final IEmailService emailService;
    private final IVerificationTokenService verificationService;
    private final IResetPasswordTokenService passwordChangeService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                       UserDtoMapper userDtoMapper, IEmailService emailService, IVerificationTokenService verificationService,
                       IResetPasswordTokenService passwordChangeService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userDtoMapper = userDtoMapper;
        this.emailService = emailService;
        this.verificationService = verificationService;
        this.passwordChangeService = passwordChangeService;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @Override
    public User loadUserWithRelatedEntitiesByUsername(String username) {
        return userRepository.findByUsernameWithRelatedEntities(username);
    }


    /**
     * Keep in mind that User account is locked by default. It is unlocked until User confirms his email.
     */
    @Override
    public void registerUser(RegisterUserDto userDto) throws UserAlreadyExistException {
        if (userExists(userDto.email(), userDto.username())) {
            throw new UserAlreadyExistException("There is an account with that username/email: " + userDto.email());
        }
        User user = new User();
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setRoles(Collections.singleton(roleRepository.findByName(RoleName.ROLE_USER)));
        user.setLocked(true);
        userRepository.save(user);

        VerificationToken generatedToken = verificationService.createVerificationToken(user);//TODO purge tokens after 24h
        emailService.sendRegistrationConfirmEmail(userDto.email(), generatedToken.getToken());
    }

    @Override
    public void confirmRegistration(String token) {
        VerificationToken verificationToken = verificationService.findByToken(token);
        if (Objects.isNull(verificationToken)) {
            throw new NoSuchElementException("Provided Verification Token does not exists: " + token);
        }
        User user = verificationToken.getUser();
        if (!user.isLocked()) {
            throw new EmailAlreadyConfirmedException("For User with id:" + user.getId() + " email is already confirmed");
        }
        if (verificationToken.isExpired()) {
            throw new VerificationTokenExpiredException("Verification Token has Expired for User: " + user.getId());
        }
        user.setLocked(false);
    }

    @Override
    public void createResetUserPasswordToken(String username) {
        User user = userRepository.findByUsername(username);
        if (Objects.isNull(user)) {
            //note we do not want to know the requester if the username exists and email was sent. That's why it not produces Exception
            return;
        }
        if (passwordChangeService.findByUser(user) != null) {
            //user clicked again on reset password. Only one can exist in a moment  //TODO purge tokens after 24h
            return;
        }

        ResetPasswordToken passwordToken = passwordChangeService.createVerificationToken(user);
        emailService.sendPasswordResetEmail(user.getEmail(), passwordToken.getToken());
    }

    @Override
    public void resetUserPassword(String token, String newPassword) {
        ResetPasswordToken resetPasswordToken = passwordChangeService.findByToken(token);
        if (Objects.isNull(resetPasswordToken)) {
            throw new NoSuchElementException("Provided Password Change Token does not exists: " + token);
        }
        User user = resetPasswordToken.getUser();
        if (resetPasswordToken.isExpired()) {
            throw new PasswordChangeTokenExpiredException("Password Change Token has Expired for User: " + user.getId());
        }
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    public void changeUserPassword(Long userId, final String oldPassword, final String newPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        if (!validCurrentPassword(oldPassword, user.getPassword())) {
            throw new InvalidOldPasswordException("Invalid Old Password for user with id: " + user.getId());
        }

        user.setPassword(passwordEncoder.encode(newPassword));
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
    }

    private boolean userExists(String email, String username) {
        return userRepository.findByEmail(email) != null || userRepository.findByUsername(username) != null;
    }

    private boolean validCurrentPassword(String providedPassword, final String currentPassword) {
        return passwordEncoder.matches(providedPassword, currentPassword);
    }
}
