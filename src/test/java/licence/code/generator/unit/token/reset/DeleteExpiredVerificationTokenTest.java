package licence.code.generator.unit.token.verification;

import licence.code.generator.helper.JpaResetPasswordEntityHelper;
import licence.code.generator.repositories.ResetPasswordTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DeleteExpiredPasswordTest {
    @Autowired
    private ResetPasswordTokenRepository tokenRepository;
    @Autowired
    private JpaResetPasswordEntityHelper jpaResetPasswordEntityHelper;


    @Test
    @Transactional
    public void deleteExpiredTokens_shouldDeleteMultipleExpiredTokens() {
        //given:
        jpaResetPasswordEntityHelper.createExpiredResetPasswordToken(null);
        jpaResetPasswordEntityHelper.createExpiredResetPasswordToken(null);
        jpaResetPasswordEntityHelper.createExpiredResetPasswordToken(null);

        //before:
        assertEquals(3, tokenRepository.findAll().size());

        //when:
        tokenRepository.deleteExpiredTokens();

        //then:
        assertEquals(0, tokenRepository.findAll().size());
    }

    @Test
    @Transactional
    public void deleteExpiredTokens_shouldDeleteOnlyExpiredTokens() {
        //given:
        jpaResetPasswordEntityHelper.createExpiredResetPasswordToken(null);
        jpaResetPasswordEntityHelper.createExpiredResetPasswordToken(null);
        jpaResetPasswordEntityHelper.createResetPasswordToken(null);
        jpaResetPasswordEntityHelper.createResetPasswordToken(null);
        jpaResetPasswordEntityHelper.createResetPasswordToken(null);

        //before:
        assertEquals(3, tokenRepository.findAll().size());

        //when:
        tokenRepository.deleteExpiredTokens();

        //then:
        assertEquals(3, tokenRepository.findAll().size());
    }
}
