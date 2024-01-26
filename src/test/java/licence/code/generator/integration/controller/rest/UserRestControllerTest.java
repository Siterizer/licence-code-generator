package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.UpdatePasswordDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.User;
import licence.code.generator.helper.DtoHelper;
import licence.code.generator.helper.JpaUserEntityHelper;
import licence.code.generator.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static licence.code.generator.util.GeneratorStringUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserRestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DtoHelper dtoHelper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void getCurrentUserDetails_shouldReturnDetails() throws Exception {
        //when:
        User user = jpaUserEntityHelper.createRandomUser();
        MvcResult result = mvc.perform(get(API_PATH + USER_INFO_PATH).with(user(user)).contentType(MediaType.APPLICATION_JSON)).andReturn();
        UserDto resultDto = mapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        //then:
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(user.getUsername(), resultDto.username());
        assertEquals(user.getEmail(), resultDto.email());
    }

    @Test
    @Transactional
    void updateUserPassword_shouldChangePassword() throws Exception {
        //given:
        User userToChangePassword = jpaUserEntityHelper.createRandomUser();
        UpdatePasswordDto requestDto = dtoHelper.createRandomUpdatePasswordDto(userToChangePassword.getPassword());
        userToChangePassword.setPassword(passwordEncoder.encode(userToChangePassword.getPassword()));

        //when:
        MvcResult result = mvc.perform(post(API_PATH + USER_UPDATE_PASSWORD_PATH).with(user(userToChangePassword))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andReturn();

        //then:
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
        assertTrue(passwordEncoder.matches(requestDto.newPassword(), userRepository.getReferenceById(userToChangePassword.getId()).getPassword()));
    }

    @Test
    void updateUserPassword_shouldThrowExceptionOnInvalidOldPassword() throws Exception {
        //given:
        User userToChangePassword = jpaUserEntityHelper.createRandomUser();
        UpdatePasswordDto requestDto = dtoHelper.createRandomUpdatePasswordDto("123");
        userToChangePassword.setPassword("321");

        //when-then:
        mvc.perform(post(API_PATH + USER_UPDATE_PASSWORD_PATH).with(user(userToChangePassword))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUserDetails_shouldThrowExceptionOnNonLoggedInUse() throws Exception {
        //when-then:
        mvc.perform(get(USER_INFO_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        mvc.perform(post(API_PATH + USER_UPDATE_PASSWORD_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dtoHelper.createRandomUpdatePasswordDto("123"))))
                .andExpect(status().isUnauthorized());
    }
}