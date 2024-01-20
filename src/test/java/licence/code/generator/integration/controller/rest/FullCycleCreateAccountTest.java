package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.entities.User;
import licence.code.generator.entities.VerificationToken;
import licence.code.generator.helper.DtoHelper;
import licence.code.generator.services.IUserService;
import licence.code.generator.services.IVerificationTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static licence.code.generator.util.GeneratorStringUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class FullCycleCreateAccountTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private DtoHelper dtoHelper;
    @Autowired
    private IUserService userService;
    @Autowired
    private IVerificationTokenService tokenService;
    @MockBean
    private JavaMailSender mailSender;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    @Transactional
    void userShouldBeAbleToPerformFullCycleCreateAccount() throws Exception {
        //given (register):
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();

        //when-then (register):
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        mvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToRegister)))
                .andExpect(status().isCreated());

        //given (registrationConfirm):
        User registeredUser = userService.findUserByUsername(userToRegister.username());
        VerificationToken verificationToken = tokenService.findByUser(registeredUser);

        //when-then (registrationConfirm):
        mvc.perform(get(REGISTRATION_CONFIRM_PATH + "?token=" + verificationToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //when-then (login)
        mvc.perform(post(LOGIN_PATH)
                        .param("username", userToRegister.username())
                        .param("password", userToRegister.password()))
                .andExpect(status().isOk());
    }
}
