package licence.code.generator.integration.rest;

import licence.code.generator.controllers.rest.AdminRestController;
import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.User;
import licence.code.generator.helper.JpaUserEntityHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AdminRestControllerTest {
    private final String ADMIN_USERNAME = "ADMIN_USERNAME";


    @Autowired
    private AdminRestController controller;

    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;

    @Test
    @WithMockUser(username = "admin")
    @WithUserDetails
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

}