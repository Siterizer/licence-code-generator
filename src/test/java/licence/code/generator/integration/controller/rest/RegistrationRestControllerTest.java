package licence.code.generator.integration.controller.rest;

import licence.code.generator.controllers.rest.RegistrationRestController;
import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.helper.DtoHelper;
import licence.code.generator.repositories.UserRepository;
import licence.code.generator.web.exception.UserAlreadyExistException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegistrationRestControllerTest {

    @Autowired
    private RegistrationRestController controller;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DtoHelper dtoHelper;

    @Test
    @Transactional
    void registerUser_shouldCreateNewUser() {
        //given:
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();

        //when:
        ResponseEntity<?> response = controller.registerUser(userToRegister);

        //then:
        User registeredUser = userRepository.findByUsername(userToRegister.getUsername());

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(registeredUser.getEmail(), userToRegister.getEmail());
        assertEquals(registeredUser.getUsername(), userToRegister.getUsername());
        assertTrue(passwordEncoder.matches(userToRegister.getPassword(), registeredUser.getPassword()));
        assertTrue(registeredUser.hasRole(RoleName.ROLE_USER));
        assertFalse(registeredUser.isLocked());
    }

    @Test
    @Transactional
    void registerUser_shouldThrowExceptionOnTheSameEmailOrUsername() {
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();
        controller.registerUser(userToRegister);
        RegisterUserDto userWithDuplicatedEmail = dtoHelper.createRandomRegisterUserDto();
        userWithDuplicatedEmail.setEmail(userToRegister.getEmail());
        RegisterUserDto userWithDuplicatedUsername = dtoHelper.createRandomRegisterUserDto();
        userWithDuplicatedUsername.setUsername(userToRegister.getUsername());

        //when-then:
        assertThrows(UserAlreadyExistException.class, () -> controller.registerUser(userWithDuplicatedEmail));
        assertThrows(UserAlreadyExistException.class, () -> controller.registerUser(userWithDuplicatedUsername));
    }
}