package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.helper.DtoHelper;
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

import static licence.code.generator.util.GeneratorStringUtils.REGISTER_PATH;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RegistrationRestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DtoHelper dtoHelper;
    @Autowired
    private UserRepository userRepository;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void registerRequest_shouldCreateNewUser() throws Exception {
        //given:
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();

        //when:
        MvcResult result = mvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToRegister)))
                .andReturn();

        //then:
        User registeredUser = userRepository.findByUsername(userToRegister.getUsername());

        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals(userToRegister.getEmail(), registeredUser.getEmail());
        assertEquals(userToRegister.getUsername(), registeredUser.getUsername());
        assertTrue(passwordEncoder.matches(userToRegister.getPassword(), registeredUser.getPassword()));
        assertTrue(registeredUser.hasRole(RoleName.ROLE_USER));
        assertFalse(registeredUser.isLocked());
    }

    @Test
    void registerRequest_shouldReturnConflictOnTheSameEmailOrUsername() throws Exception {
        //given:
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();
        RegisterUserDto userWithDuplicatedEmail = dtoHelper.createRandomRegisterUserDto();
        userWithDuplicatedEmail.setEmail(userToRegister.getEmail());
        RegisterUserDto userWithDuplicatedUsername = dtoHelper.createRandomRegisterUserDto();
        userWithDuplicatedUsername.setUsername(userToRegister.getUsername());

        //when-then:
        mvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToRegister)))
                .andExpect(status().isCreated());

        mvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userWithDuplicatedEmail)))
                .andExpect(status().isConflict());

        mvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userWithDuplicatedUsername)))
                .andExpect(status().isConflict());
    }
}