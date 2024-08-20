package licence.code.generator.unit.token.reset;

import licence.code.generator.entities.Role;
import licence.code.generator.entities.User;
import licence.code.generator.entities.VerificationToken;
import licence.code.generator.helper.JpaResetPasswordEntityHelper;
import licence.code.generator.helper.JpaUserEntityHelper;
import licence.code.generator.helper.JpaVerificationTokenEntityHelper;
import licence.code.generator.repositories.ResetPasswordTokenRepository;
import licence.code.generator.repositories.RoleRepository;
import licence.code.generator.repositories.UserRepository;
import licence.code.generator.repositories.VerificationTokenRepository;
import licence.code.generator.services.token.IVerificationTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DeleteExpiredVerificationTokenTest {
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JpaVerificationTokenEntityHelper jpaVerificationTokenEntityHelper;
    @Autowired
    private JpaUserEntityHelper userEntityHelper;
    @Autowired
    private IVerificationTokenService verificationTokenService;
    @Autowired
    private RoleRepository roleRepository;


    @Test
    @Transactional
    public void deleteExpiredTokens_shouldDeleteMultipleExpiredTokens() {
        //given:
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(userEntityHelper.createExpireddUser()));
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(userEntityHelper.createExpireddUser()));
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(userEntityHelper.createExpireddUser()));

        //before:
        assertEquals(3, tokenRepository.findAll().size());

        //when:
        int deletedRows = userRepository.deleteExpiredUsers();

        //then:
        assertEquals(0, tokenRepository.findAll().size());
        assertEquals(3, deletedRows);
    }

    @Test
    @Transactional
    public void deleteExpiredTokens_shouldDeleteOnlyExpiredTokens() {
        //given:
        verificationTokenService.createVerificationToken(userEntityHelper.createExpireddUser());
        verificationTokenService.createVerificationToken(userEntityHelper.createExpireddUser());
        verificationTokenService.createVerificationToken(userEntityHelper.createExpireddUser());
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(userEntityHelper.createExpireddUser()));
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(userEntityHelper.createExpireddUser()));

        //before
        assertEquals(5, tokenRepository.findAll().size());

        //when:
        int deletedRows = userRepository.deleteExpiredUsers();

        //then:
        assertEquals(3, tokenRepository.findAll().size());
        assertEquals(2, deletedRows);
    }
}
