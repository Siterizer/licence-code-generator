package licence.code.generator.integration.controller.rest;

import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.helper.DtoHelper;
import licence.code.generator.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static licence.code.generator.util.GeneratorStringUtils.LOCALHOST_URL;
import static licence.code.generator.util.GeneratorStringUtils.REGISTER_PATH;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegistrationRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DtoHelper dtoHelper;

    @Test
    void registerRequest_shouldCreateNewUser() {
        //given:
        String registerFullUrl = LOCALHOST_URL + port + REGISTER_PATH;
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();

        //when:
        ResponseEntity<?> response = restTemplate.postForEntity(registerFullUrl, userToRegister, String.class);

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
    void registerRequest_shouldReturnConflictOnTheSameEmailOrUsername() {
        //given:
        String registerFullUrl = LOCALHOST_URL + port + REGISTER_PATH;
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();
        RegisterUserDto userWithDuplicatedEmail = dtoHelper.createRandomRegisterUserDto();
        userWithDuplicatedEmail.setEmail(userToRegister.getEmail());
        RegisterUserDto userWithDuplicatedUsername = dtoHelper.createRandomRegisterUserDto();
        userWithDuplicatedUsername.setUsername(userToRegister.getUsername());

        //when:
        ResponseEntity<?> response = restTemplate.postForEntity(registerFullUrl, userToRegister, String.class);
        ResponseEntity<?> responseDuplicatedEmail = restTemplate.postForEntity(registerFullUrl, userWithDuplicatedEmail, String.class);
        ResponseEntity<?> responseDuplicatedUsername = restTemplate.postForEntity(registerFullUrl, userWithDuplicatedUsername, String.class);

        //then:
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(responseDuplicatedEmail.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(responseDuplicatedUsername.getStatusCode(), HttpStatus.CONFLICT);
    }
}