package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.LoginDto;
import licence.code.generator.dto.ResetPasswordDto;
import licence.code.generator.dto.UsernameDto;
import licence.code.generator.entities.ResetPasswordToken;
import licence.code.generator.entities.User;
import licence.code.generator.helper.DtoHelper;
import licence.code.generator.helper.JpaUserEntityHelper;
import licence.code.generator.repositories.ResetPasswordTokenRepository;
import licence.code.generator.services.email.IEmailService;
import licence.code.generator.services.token.IResetPasswordTokenService;
import licence.code.generator.services.user.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

import static licence.code.generator.util.GeneratorStringUtils.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ResetPasswordRestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;
    @Autowired
    private DtoHelper dtoHelper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JavaMailSender mailSender;
    @Autowired
    private IUserService userService;
    @Autowired
    private ResetPasswordTokenRepository resetPasswordRepository;
    @Autowired
    private IResetPasswordTokenService tokenService;
    @MockBean
    private IEmailService emailService;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    @Transactional
    void userShouldBeAbleToPerformFullCycleResetPassword() throws Exception {
        //given:
        User userToResetPassword = jpaUserEntityHelper.createRandomUser();
        UsernameDto usernameDto = new UsernameDto(userToResetPassword.getUsername());

        //when (reset password token send):
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        mvc.perform(post(API_PATH + USER_SEND_RESET_PASSWORD_EMAIL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(usernameDto)))
                .andExpect(status().isCreated());

        //given:
        ResetPasswordToken resetPasswordToken = tokenService.findByUser(
                userService.loadUserWithRelatedEntitiesByUsername(userToResetPassword.getUsername()));
        ResetPasswordDto passwordDto = dtoHelper.createResetPasswordDto();

        //when-then (resetPassword):
        mvc.perform(post(API_PATH + USER_RESET_PASSWORD_PATH + "?token=" + resetPasswordToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordDto)))
                .andExpect(status().isCreated());

        //when-then (login with new password)
        mvc.perform(post(API_PATH + LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new LoginDto(userToResetPassword.getUsername(), passwordDto.newPassword()))))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void resetUserPasswordEmail_shouldSendEmailOnceOnMultipleCalls() throws Exception {
        //given:
        User userToResetPassword = jpaUserEntityHelper.createRandomUser();
        UsernameDto usernameDto = new UsernameDto(userToResetPassword.getUsername());

        //when:
        for (int i = 0; i < 3; i++) {
            doNothing().when(mailSender).send(any(SimpleMailMessage.class));
            mvc.perform(post(API_PATH + USER_SEND_RESET_PASSWORD_EMAIL_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(usernameDto)))
                    .andExpect(status().isCreated());
        }
        //then:
        verify(emailService, times(1)).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    @Transactional
    void resetUserPasswordEmail_shouldNotSendAnEmailWithInvalidUser() throws Exception {
        //given:
        String username = "123456";

        //when:
        mvc.perform(post(API_PATH + USER_SEND_RESET_PASSWORD_EMAIL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UsernameDto(username))))
                .andExpect(status().isCreated());

        //then:
        verify(emailService, times(0)).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    @Transactional
    void resetPassword_shouldResetPassword() throws Exception {
        //given:
        User userToResetPassword = jpaUserEntityHelper.createRandomUser();
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(userToResetPassword);
        resetPasswordRepository.save(resetPasswordToken);
        ResetPasswordDto passwordDto = dtoHelper.createResetPasswordDto();

        //when:
        mvc.perform(post(API_PATH + USER_RESET_PASSWORD_PATH + "?token=" + resetPasswordToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordDto)))
                .andExpect(status().isCreated());

        //then:
        assertTrue(passwordEncoder.matches(passwordDto.newPassword(),
                userService.loadUserWithRelatedEntitiesByUsername(userToResetPassword.getUsername()).getPassword()));
    }

    @Test
    @Transactional
    void resetPassword_shouldReturn404WhenTokenDoesNotExists() throws Exception {
        //when-when:
        mvc.perform(post(API_PATH + USER_RESET_PASSWORD_PATH + "?token=123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dtoHelper.createResetPasswordDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void resetPassword_shouldReturn410OnExpiredToken() throws Exception {
        //given:
        //given:
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.DATE, -1);
        User userToResetPassword = jpaUserEntityHelper.createRandomUser();
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(userToResetPassword);
        resetPasswordToken.setExpiryDate(cal.getTime());
        resetPasswordRepository.save(resetPasswordToken);

        //when-when:
        mvc.perform(post(API_PATH + USER_RESET_PASSWORD_PATH + "?token=" + resetPasswordToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dtoHelper.createResetPasswordDto())))
                .andExpect(status().isGone());
    }

}