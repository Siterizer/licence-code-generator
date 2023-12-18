package licence.code.generator.integration.rest;

import licence.code.generator.controllers.rest.AdminRestController;
import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.User;
import licence.code.generator.helper.JpaUserEntityHelper;
import licence.code.generator.web.exception.InsufficientPrivilegesException;
import licence.code.generator.web.exception.UnauthorizedUserException;
import licence.code.generator.web.exception.UserAlreadyBlockedException;
import licence.code.generator.web.exception.UserNotBlockedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AdminRestControllerTest {

    @Autowired
    private AdminRestController controller;

    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;

    @Test
    @WithMockUser(username = "admin")
    @Transactional
    void showAllUsers_shouldReturnMultipleUsers() {
        //given:
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
    @WithMockUser(username = "admin")
    @Transactional
    void blockUser_shouldSuccessfullyBlockUserOnly() {
        //given:
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
    @WithMockUser(username = "admin")
    @Transactional
    void blockUser_shouldThrowExceptionOnNotBlockedUser() {
        //given:
        User userToBeBlocked = jpaUserEntityHelper.createBlockedUser();

        //when-then:
        assertThrows(UserAlreadyBlockedException.class, () -> controller.blockUser(userToBeBlocked.getId()));
    }

    @Test
    @WithMockUser(username = "admin")
    @Transactional
    void blockUser_shouldThrowExceptionOnAdminBlockTry() {
        //given:
        User adminToBeBlocked = jpaUserEntityHelper.createNotBlockedAdmin();

        //when-then:
        assertThrows(InsufficientPrivilegesException.class, () -> controller.blockUser(adminToBeBlocked.getId()));
    }

    @Test
    @WithMockUser(username = "admin")
    @Transactional
    void unblockUser_shouldSuccessfullyUnblockUserOnly() {
        //given:
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
    @WithMockUser(username = "admin")
    @Transactional
    void unblockUser_shouldThrowExceptionOnNotBlockedUser() {
        //given:
        User userToBeUnblocked = jpaUserEntityHelper.createNotBlockedUser();

        //when-then:
        assertThrows(UserNotBlockedException.class, () -> controller.unblockUser(userToBeUnblocked.getId()));
    }

    @Test
    @WithMockUser(username = "admin")
    @Transactional
    void unblockUser_shouldThrowExceptionOnAdminBlockTry() {
        //given:
        User adminToBeUnblocked = jpaUserEntityHelper.createBlockedAdmin();

        //when-then:
        assertThrows(InsufficientPrivilegesException.class, () -> controller.unblockUser(adminToBeUnblocked.getId()));
    }


    @Test
    @WithMockUser(username = "user1")
    @Transactional
    void adminRestControllerMethods_shouldThrowExceptionOnUserAttempt() {
        //given:
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