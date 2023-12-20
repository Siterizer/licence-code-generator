package licence.code.generator.integration.controller.rest;

import licence.code.generator.controllers.rest.AdminRestController;
import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.User;
import licence.code.generator.helper.JpaUserEntityHelper;
import licence.code.generator.helper.SecurityHelper;
import licence.code.generator.web.exception.InsufficientPrivilegesException;
import licence.code.generator.web.exception.UnauthorizedUserException;
import licence.code.generator.web.exception.UserAlreadyBlockedException;
import licence.code.generator.web.exception.UserNotBlockedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
class AdminRestControllerTest {

    @Autowired
    private AdminRestController controller;
    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;
    @Autowired
    SecurityHelper securityHelper;

    @Test
    @Transactional
    void showAllUsers_shouldReturnMultipleUsers() {
        //given:
        securityHelper.setSecurityContextFromRandomAdmin();
        List<User> expected = List.of(
                jpaUserEntityHelper.createRandomUser(),
                jpaUserEntityHelper.createRandomUser(),
                jpaUserEntityHelper.createRandomUser());
        List<Long> expectedIds = expected.stream().map(User::getId).collect(Collectors.toList());


        //when:
        ResponseEntity<List<UserDto>> result = controller.showAllUsers();
        List<Long> resultIds = Objects.requireNonNull(result.getBody()).stream().map(UserDto::getId).collect(Collectors.toList());

        //then:
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertTrue(resultIds.containsAll(expectedIds));
    }

    @Test
    @Transactional
    void blockUser_shouldSuccessfullyBlockUserOnly() {
        //given:
        securityHelper.setSecurityContextFromRandomAdmin();
        User userToBeBlocked = jpaUserEntityHelper.createNotBlockedUser();
        List<User> usersToNotBeBlocked = List.of(
                jpaUserEntityHelper.createNotBlockedUser(),
                jpaUserEntityHelper.createNotBlockedUser(),
                jpaUserEntityHelper.createNotBlockedUser());

        //when:
        ResponseEntity<?> response = controller.blockUser(userToBeBlocked.getId());

        //then:
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        assertTrue(userToBeBlocked.isLocked());
        usersToNotBeBlocked.forEach(e -> assertFalse(e.isLocked()));
    }

    @Test
    @Transactional
    void blockUser_shouldThrowExceptionOnNotBlockedUser() {
        //given:
        securityHelper.setSecurityContextFromRandomAdmin();
        User userToBeBlocked = jpaUserEntityHelper.createBlockedUser();

        //when-then:
        assertThrows(UserAlreadyBlockedException.class, () -> controller.blockUser(userToBeBlocked.getId()));
    }

    @Test
    @Transactional
    void blockUser_shouldThrowExceptionOnAdminBlockTry() {
        //given:
        securityHelper.setSecurityContextFromRandomAdmin();
        User adminToBeBlocked = jpaUserEntityHelper.createNotBlockedAdmin();

        //when-then:
        assertThrows(InsufficientPrivilegesException.class, () -> controller.blockUser(adminToBeBlocked.getId()));
    }

    @Test
    @Transactional
    void unblockUser_shouldSuccessfullyUnblockUserOnly() {
        //given:
        securityHelper.setSecurityContextFromRandomAdmin();
        User userToBeUnblocked = jpaUserEntityHelper.createBlockedUser();
        List<User> usersToNotBeBlocked = List.of(
                jpaUserEntityHelper.createBlockedUser(),
                jpaUserEntityHelper.createBlockedUser(),
                jpaUserEntityHelper.createBlockedUser());

        //when:
        ResponseEntity<?> response = controller.unblockUser(userToBeUnblocked.getId());

        //then:
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        assertFalse(userToBeUnblocked.isLocked());
        usersToNotBeBlocked.forEach(e -> assertTrue(e.isLocked()));
    }

    @Test
    @Transactional
    void unblockUser_shouldThrowExceptionOnNotBlockedUser() {
        //given:
        securityHelper.setSecurityContextFromRandomAdmin();
        User userToBeUnblocked = jpaUserEntityHelper.createNotBlockedUser();

        //when-then:
        assertThrows(UserNotBlockedException.class, () -> controller.unblockUser(userToBeUnblocked.getId()));
    }

    @Test
    @Transactional
    void unblockUser_shouldThrowExceptionOnAdminBlockTry() {
        //given:
        securityHelper.setSecurityContextFromRandomAdmin();
        User adminToBeUnblocked = jpaUserEntityHelper.createBlockedAdmin();

        //when-then:
        assertThrows(InsufficientPrivilegesException.class, () -> controller.unblockUser(adminToBeUnblocked.getId()));
    }


    @Test
    @Transactional
    void adminRestControllerMethods_shouldThrowExceptionOnUserAttempt() {
        //given:
        securityHelper.setSecurityContextFromRandomUser();
        jpaUserEntityHelper.createRandomUser();
        User userToBeBlocked = jpaUserEntityHelper.createNotBlockedUser();
        User userToBeUnblocked = jpaUserEntityHelper.createBlockedUser();

        //when-then:
        assertThrows(InsufficientPrivilegesException.class, () -> controller.showAllUsers());
        assertThrows(InsufficientPrivilegesException.class, () -> controller.blockUser(userToBeBlocked.getId()));
        assertThrows(InsufficientPrivilegesException.class, () -> controller.unblockUser(userToBeUnblocked.getId()));
    }

    @Test
    @Transactional
    void adminRestControllerMethods_shouldThrowExceptionOnNonLoggedInUser() {
        //given:
        jpaUserEntityHelper.createRandomUser();
        User userToBeBlocked = jpaUserEntityHelper.createNotBlockedUser();
        User userToBeUnblocked = jpaUserEntityHelper.createBlockedUser();

        //when-then:
        assertThrows(UnauthorizedUserException.class, () -> controller.showAllUsers());
        assertThrows(UnauthorizedUserException.class, () -> controller.blockUser(userToBeBlocked.getId()));
        assertThrows(UnauthorizedUserException.class, () -> controller.unblockUser(userToBeUnblocked.getId()));
    }
}