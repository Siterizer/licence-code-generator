package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.LoginDto;
import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.entities.VerificationToken;
import licence.code.generator.helper.DtoHelper;
import licence.code.generator.helper.JpaUserEntityHelper;
import licence.code.generator.repositories.VerificationTokenRepository;
import licence.code.generator.services.email.IEmailService;
import licence.code.generator.services.token.IVerificationTokenService;
import licence.code.generator.services.user.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import static licence.code.generator.util.GeneratorStringUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SessionManagementRestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private DtoHelper dtoHelper;
    @Autowired
    private IUserService userService;
    @Autowired
    private IVerificationTokenService verificationTokenService;
    @MockBean
    private JavaMailSender mailSender;
    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Value("${generator.app.jwtCookieName}")
    private String jwtCookie;
    @MockBean
    private IEmailService emailService;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void registerRequest_shouldCreateNewUser() throws Exception {
        //given:
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();

        //when:
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        MvcResult result = mvc.perform(post(API_PATH + REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToRegister)))
                .andReturn();

        //then:
        User registeredUser = userService.loadUserWithRelatedEntitiesByUsername(userToRegister.username());

        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals(userToRegister.email(), registeredUser.getEmail());
        assertEquals(userToRegister.username(), registeredUser.getUsername());
        assertTrue(passwordEncoder.matches(userToRegister.password(), registeredUser.getPassword()));
        assertTrue(registeredUser.hasRole(RoleName.ROLE_USER));
        assertTrue(registeredUser.isAccountExpired());
    }

    @Test
    @Transactional
    void registerRequest_shouldSendAnEmailWithNewVerificationToken() throws Exception {
        //given:
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();

        //when:
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        mvc.perform(post(API_PATH + REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToRegister)))
                .andReturn();

        //then:
        User registeredUser = userService.loadUserWithRelatedEntitiesByUsername(userToRegister.username());
        VerificationToken verificationToken = verificationTokenService.findByUser(registeredUser);

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
        mvc.perform(post(API_PATH + REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToRegister)))
                .andExpect(status().isCreated());

        mvc.perform(post(API_PATH + REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userWithDuplicatedEmail)))
                .andExpect(status().isConflict());

        mvc.perform(post(API_PATH + REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userWithDuplicatedUsername)))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    void registrationConfirm_shouldUnExpireUserAndDeleteVerificationToken() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createExpiredUser();
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(user);

        //when:
        MvcResult result = mvc.perform(get(API_PATH + REGISTRATION_CONFIRM_PATH + "?token=" + verificationToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //then:
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
        assertFalse(user.isAccountExpired());
        assertNull(verificationTokenRepository.findByToken(verificationToken.getToken()));
    }

    @Test
    @Transactional
    void registrationConfirm_shouldReturn400CodeForNullToken() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createExpiredUser();
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(user);

        //when-then:
        mvc.perform(get(API_PATH + REGISTRATION_CONFIRM_PATH + "?toke=" //Typo
                + verificationToken.getToken())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void registrationConfirm_shouldReturn404CodeForNonExistingLicence() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createExpiredUser();
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(user);

        //when-then:
        mvc.perform(get(API_PATH + REGISTRATION_CONFIRM_PATH + "?token=" + verificationToken.getToken() + "123")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

    }

    @Test
    @Transactional
    void registrationConfirm_shouldReturn409CodeForAlreadyConfirmedUser() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createExpiredUser();
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(user);
        user.setAccountExpired(false);

        //when-then:
        mvc.perform(get(API_PATH + REGISTRATION_CONFIRM_PATH + "?token=" + verificationToken.getToken())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());

    }

    @Test
    @Transactional
    void registrationConfirm_shouldReturn410CodeForExpiredToken() throws Exception {
        //given:
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.DATE, -1);
        User user = jpaUserEntityHelper.createExpiredUser();
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(user);
        verificationToken.setExpiryDate(cal.getTime());

        //when-then:
        mvc.perform(get(API_PATH + REGISTRATION_CONFIRM_PATH + "?token=" + verificationToken.getToken())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isGone());

    }

    @Test
    @Transactional
    void loginUser_shouldReturnJWTTokenOnSuccess() throws Exception {
        //given:
        User notBlockedUser = jpaUserEntityHelper.createNotBlockedUser();
        LoginDto loginDto = new LoginDto(notBlockedUser.getUsername(), notBlockedUser.getPassword());
        notBlockedUser.setPassword(passwordEncoder.encode(notBlockedUser.getPassword()));

        //when-then:
        mvc.perform(post(API_PATH + LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(cookie().exists(jwtCookie))
                .andExpect(status().isOk());

    }

    @Test
    @Transactional
    void loginUser_shouldReturn400WhenValidationFails() throws Exception {
        //given:
        User notBlockedUser = jpaUserEntityHelper.createNotBlockedUser();
        LoginDto loginDto = new LoginDto(null, null);
        notBlockedUser.setPassword(passwordEncoder.encode(notBlockedUser.getPassword()));

        //when-then:
        mvc.perform(post(API_PATH + LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(cookie().doesNotExist(jwtCookie))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void loginUser_shouldReturn401CodeForBadCredentials() throws Exception {
        //given:
        User notBlockedUser = jpaUserEntityHelper.createNotBlockedUser();
        LoginDto loginDto = new LoginDto(notBlockedUser.getUsername(), notBlockedUser.getPassword() + "error");
        notBlockedUser.setPassword(passwordEncoder.encode(notBlockedUser.getPassword()));

        //when-then:
        mvc.perform(post(API_PATH + LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(cookie().doesNotExist(jwtCookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void userShouldBeAbleToPerformFullCycleCreateAccount() throws Exception {
        //given (register):
        RegisterUserDto userToRegister = dtoHelper.createRandomRegisterUserDto();

        //when-then (register):
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        mvc.perform(post(API_PATH + REGISTER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToRegister)))
                .andExpect(status().isCreated());

        //given (registrationConfirm):
        User registeredUser = userService.loadUserWithRelatedEntitiesByUsername(userToRegister.username());
        VerificationToken verificationToken = verificationTokenService.findByUser(registeredUser);

        //when-then (registrationConfirm):
        mvc.perform(get(API_PATH + REGISTRATION_CONFIRM_PATH + "?token=" + verificationToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //when-then (login)
        mvc.perform(post(API_PATH + LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new LoginDto(userToRegister.username(), userToRegister.password()))))
                .andExpect(status().isOk());
    }
}
