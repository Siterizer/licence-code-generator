package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.IdRequestDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.User;
import licence.code.generator.helper.JpaUserEntityHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static licence.code.generator.util.GeneratorStringUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AdminRestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void showAllUsers_shouldReturnMultipleUsers() throws Exception {
        //given:
        List<User> expected = List.of(
                jpaUserEntityHelper.createRandomUser(),
                jpaUserEntityHelper.createRandomUser(),
                jpaUserEntityHelper.createRandomUser());
        List<Long> expectedIds = expected.stream().map(User::getId).collect(Collectors.toList());

        //when:
        MvcResult result = mvc.perform(get(ADMIN_INFO_PATH).with(user(jpaUserEntityHelper.createNotBlockedAdmin()))
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<UserDto> resultDtoList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        List<Long> resultIds = Objects.requireNonNull(resultDtoList).stream().map(UserDto::getId).collect(Collectors.toList());

        //then:
        assertTrue(resultIds.containsAll(expectedIds));
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }


    @Test
    @Transactional
    void blockUser_shouldSuccessfullyBlockUserOnly() throws Exception {
        //given:
        User userToBeBlocked = jpaUserEntityHelper.createNotBlockedUser();
        List<User> usersToNotBeBlocked = List.of(
                jpaUserEntityHelper.createNotBlockedUser(),
                jpaUserEntityHelper.createNotBlockedUser(),
                jpaUserEntityHelper.createNotBlockedUser());

        //when:
        MvcResult result = mvc.perform(post(ADMIN_BLOCK_PATH).with(user(jpaUserEntityHelper.createNotBlockedAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IdRequestDto(userToBeBlocked.getId()))))
                .andReturn();

        //then:
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
        assertTrue(userToBeBlocked.isLocked());
        usersToNotBeBlocked.forEach(e -> assertFalse(e.isLocked()));
    }

    @Test
    void blockUser_shouldThrowExceptionOnAlreadyBlockedUser() throws Exception {
        //given:
        User userToBeBlocked = jpaUserEntityHelper.createBlockedUser();

        //when-then:
        mvc.perform(post(ADMIN_BLOCK_PATH)
                        .contentType(MediaType.APPLICATION_JSON).with(user(jpaUserEntityHelper.createNotBlockedAdmin()))
                        .content(mapper.writeValueAsString(new IdRequestDto(userToBeBlocked.getId()))))
                .andExpect(status().isConflict());
    }

    @Test
    void blockUser_shouldThrowExceptionOnAdminBlockTry() throws Exception {
        //given:
        User adminToBeBlocked = jpaUserEntityHelper.createNotBlockedAdmin();

        //when-then:
        mvc.perform(post(ADMIN_BLOCK_PATH)
                        .contentType(MediaType.APPLICATION_JSON).with(user(jpaUserEntityHelper.createNotBlockedAdmin()))
                        .content(mapper.writeValueAsString(new IdRequestDto(adminToBeBlocked.getId()))))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void unblockUser_shouldSuccessfullyUnblockUserOnly() throws Exception {
        //given:
        User userToBeUnblocked = jpaUserEntityHelper.createBlockedUser();
        List<User> usersToNotBeBlocked = List.of(
                jpaUserEntityHelper.createBlockedUser(),
                jpaUserEntityHelper.createBlockedUser(),
                jpaUserEntityHelper.createBlockedUser());

        //when:
        MvcResult result = mvc.perform(post(ADMIN_UNBLOCK_PATH).with(user(jpaUserEntityHelper.createNotBlockedAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IdRequestDto(userToBeUnblocked.getId()))))
                .andReturn();

        //then:
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
        assertFalse(userToBeUnblocked.isLocked());
        usersToNotBeBlocked.forEach(e -> assertTrue(e.isLocked()));
    }

    @Test
    void unblockUser_shouldThrowExceptionOnNotBlockedUser() throws Exception {
        //given:
        User userToBeUnblocked = jpaUserEntityHelper.createNotBlockedUser();

        //when-then:
        mvc.perform(post(ADMIN_UNBLOCK_PATH).with(user(jpaUserEntityHelper.createNotBlockedAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IdRequestDto(userToBeUnblocked.getId()))))
                .andExpect(status().isConflict());
    }

    @Test
    void unblockUser_shouldThrowExceptionOnAdminBlockTry() throws Exception {
        //given:
        User adminToBeUnblocked = jpaUserEntityHelper.createBlockedAdmin();

        //when-then:
        mvc.perform(post(ADMIN_UNBLOCK_PATH).with(user(jpaUserEntityHelper.createNotBlockedAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IdRequestDto(adminToBeUnblocked.getId()))))
                .andExpect(status().isForbidden());
    }


    @Test
    void adminRestControllerMethods_shouldThrowExceptionOnUserAttempt() throws Exception {
        //given:
        User requester = jpaUserEntityHelper.createNotBlockedUser();
        jpaUserEntityHelper.createRandomUser();
        User userToBeBlocked = jpaUserEntityHelper.createNotBlockedUser();
        User userToBeUnblocked = jpaUserEntityHelper.createBlockedUser();

        //when-then:
        mvc.perform(get(ADMIN_INFO_PATH).with(user(requester))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        mvc.perform(post(ADMIN_BLOCK_PATH).with(user(requester))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IdRequestDto(userToBeBlocked.getId()))))
                .andExpect(status().isForbidden());

        mvc.perform(post(ADMIN_UNBLOCK_PATH).with(user(requester))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IdRequestDto(userToBeUnblocked.getId()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminRestControllerMethods_shouldThrowExceptionOnNonLoggedInUser() throws Exception {
        //given:
        jpaUserEntityHelper.createRandomUser();
        User userToBeBlocked = jpaUserEntityHelper.createNotBlockedUser();
        User userToBeUnblocked = jpaUserEntityHelper.createBlockedUser();

        //when-then:
        mvc.perform(get(ADMIN_INFO_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        mvc.perform(post(ADMIN_BLOCK_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IdRequestDto(userToBeBlocked.getId()))))
                .andExpect(status().isUnauthorized());

        mvc.perform(post(ADMIN_UNBLOCK_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IdRequestDto(userToBeUnblocked.getId()))))
                .andExpect(status().isUnauthorized());
    }
}