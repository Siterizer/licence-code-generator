package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.entities.VerificationToken;
import licence.code.generator.helper.DtoHelper;
import licence.code.generator.helper.JpaUserEntityHelper;
import licence.code.generator.repositories.UserRepository;
import licence.code.generator.services.IVerificationTokenService;
import licence.code.generator.services.email.IEmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

import static licence.code.generator.util.GeneratorStringUtils.REGISTER_PATH;
import static licence.code.generator.util.GeneratorStringUtils.REGISTRATION_CONFIRM_PATH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @Autowired
    private IVerificationTokenService tokenService;
    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;
    @MockBean
    private JavaMailSender mailSender;
    @MockBean
    private IEmailService emailService;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void registerRequest_shouldCreateNewUser() throws Exception {
        //given:
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();

        //when:
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
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
    @Transactional
    void registerRequest_shouldSendAnEmailWithNewVerificationToken() throws Exception {
        //given:
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();

        //when:
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        mvc.perform(post(REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToRegister)))
                .andReturn();

        //then:
        User registeredUser = userRepository.findByUsername(userToRegister.username());
        VerificationToken verificationToken = tokenService.findByUser(registeredUser);

        assertNotNull(verificationToken.getToken());
        assertFalse(verificationToken.isExpired());
        verify(emailService, times(1)).sendRegistrationConfirmEmail(userToRegister.email(), verificationToken.getToken());
    }

    @Test
    void registerRequest_shouldReturnConflictOnTheSameEmailOrUsername() throws Exception {
        //given:
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();
        RegisterUserDto userWithDuplicatedEmail = dtoHelper.createRandomRegisterUserDtoFromEmail(userToRegister.email());
        RegisterUserDto userWithDuplicatedUsername = dtoHelper.createRandomRegisterUserDtoFromUsername(userToRegister.username());

        //when-then:
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
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

    @Test
    @Transactional
    void registrationConfirm_shouldUnBlockUser() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createBlockedUser();
        VerificationToken verificationToken = tokenService.createVerificationToken(user);

        //when:
        MvcResult result = mvc.perform(get(REGISTRATION_CONFIRM_PATH + "?token=" + verificationToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //then:
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
        assertFalse(user.isLocked());
    }

    @Test
    void registrationConfirm_shouldReturn400CodeForNullToken() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createBlockedUser();
        VerificationToken verificationToken = tokenService.createVerificationToken(user);

        //when-then:
        mvc.perform(get(REGISTRATION_CONFIRM_PATH + "?toke=" //Typo
                + verificationToken.getToken())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void registrationConfirm_shouldReturn404CodeForNonExistingLicence() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createBlockedUser();
        VerificationToken verificationToken = tokenService.createVerificationToken(user);

        //when-then:
        mvc.perform(get(REGISTRATION_CONFIRM_PATH + "?token=" + verificationToken.getToken() + "123")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

    }

    @Test
    void registrationConfirm_shouldReturn409CodeForAlreadyConfirmedUser() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createNotBlockedUser();//note User is not blocked
        VerificationToken verificationToken = tokenService.createVerificationToken(user);

        //when-then:
        mvc.perform(get(REGISTRATION_CONFIRM_PATH + "?token=" + verificationToken.getToken())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());

    }

    @Test
    @Transactional
    void registrationConfirm_shouldReturn410CodeForExpiredToken() throws Exception {
        //given:
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.DATE, -1);
        User user = jpaUserEntityHelper.createBlockedUser();
        VerificationToken verificationToken = tokenService.createVerificationToken(user);
        verificationToken.setExpiryDate(cal.getTime());

        //when-then:
        mvc.perform(get(REGISTRATION_CONFIRM_PATH + "?token=" + verificationToken.getToken())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isGone());

    }
}