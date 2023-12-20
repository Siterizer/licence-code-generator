package licence.code.generator.integration.controller.rest;

import licence.code.generator.controllers.rest.UserRestController;
import licence.code.generator.dto.UpdatePasswordDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.User;
import licence.code.generator.helper.DtoHelper;
import licence.code.generator.helper.JpaUserEntityHelper;
import licence.code.generator.helper.SecurityHelper;
import licence.code.generator.repositories.UserRepository;
import licence.code.generator.web.exception.InvalidOldPasswordException;
import licence.code.generator.web.exception.UnauthorizedUserException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRestControllerTest {

    @Autowired
    private UserRestController controller;

    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DtoHelper dtoHelper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityHelper securityHelper;

    @Test
    @Transactional
    void getCurrentUserDetails_shouldReturnDetails() {
        //given:
        User user = jpaUserEntityHelper.createNotBlockedUser();
        securityHelper.setSecurityContextFromUser(user);

        //when:
        ResponseEntity<UserDto> response = controller.getCurrentUserDetails();

        //then:
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getUsername(), user.getUsername());
        assertEquals(Objects.requireNonNull(response.getBody()).getEmail(), user.getEmail());
    }

    @Test
    @Transactional
    void updateUserPassword_shouldChangePassword() {
        //given:
        User userToChangePassword = jpaUserEntityHelper.createRandomUser();
        UpdatePasswordDto requestDto = dtoHelper.createRandomUpdatePasswordDto(userToChangePassword.getPassword());
        userToChangePassword.setPassword(passwordEncoder.encode(userToChangePassword.getPassword()));
        securityHelper.setSecurityContextFromUser(userToChangePassword);

        //when:
        ResponseEntity<?> response = controller.updateUserPassword(requestDto);

        //then:
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        assertTrue(passwordEncoder.matches(requestDto.getNewPassword(), userRepository.getById(userToChangePassword.getId()).getPassword()));
    }

    @Test
    @Transactional
    void updateUserPassword_shouldThrowExceptionOnInvalidOldPassword() {
        //given:
        User userToChangePassword = jpaUserEntityHelper.createRandomUser();
        UpdatePasswordDto requestDto = dtoHelper.createRandomUpdatePasswordDto("123");
        userToChangePassword.setPassword("321");
        securityHelper.setSecurityContextFromUser(userToChangePassword);

        //when:
        assertThrows(InvalidOldPasswordException.class, () -> controller.updateUserPassword(requestDto));
    }

    @Test
    @Transactional
    void getCurrentUserDetails_shouldThrowExceptionOnNonLoggedInUse() {
        //when-then:
        assertThrows(UnauthorizedUserException.class, () -> controller.getCurrentUserDetails());
        assertThrows(UnauthorizedUserException.class, () -> controller.updateUserPassword(dtoHelper.createRandomUpdatePasswordDto("123")));
    }
}