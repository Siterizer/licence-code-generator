package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.helper.DtoHelper;
import licence.code.generator.repositories.UserRepository;
import licence.code.generator.services.email.IEmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static licence.code.generator.util.GeneratorStringUtils.REGISTER_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
    @MockBean
    private IEmailService emailService;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void registerRequest_shouldCreateNewUser() throws Exception {
        //given:
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();

        //when:
        doNothing().when(emailService).sendRegistrationConfirmEmail(anyString(), anyString());
        MvcResult result = mvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToRegister)))
                .andReturn();

        //then:
        User registeredUser = userRepository.findByUsername(userToRegister.username());

        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals(userToRegister.email(), registeredUser.getEmail());
        assertEquals(userToRegister.username(), registeredUser.getUsername());
        assertTrue(passwordEncoder.matches(userToRegister.password(), registeredUser.getPassword()));
        assertTrue(registeredUser.hasRole(RoleName.ROLE_USER));
        assertTrue(registeredUser.isLocked());
    }

    @Test
    void registerRequest_shouldReturnConflictOnTheSameEmailOrUsername() throws Exception {
        //given:
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();
        RegisterUserDto userWithDuplicatedEmail = dtoHelper.createRandomRegisterUserDtoFromEmail(userToRegister.email());
        RegisterUserDto userWithDuplicatedUsername = dtoHelper.createRandomRegisterUserDtoFromUsername(userToRegister.username());

        //when-then:
        doNothing().when(emailService).sendRegistrationConfirmEmail(anyString(), anyString());
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