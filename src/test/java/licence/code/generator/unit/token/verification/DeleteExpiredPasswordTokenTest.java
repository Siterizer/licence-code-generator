package licence.code.generator.unit.token.verification;

import licence.code.generator.entities.User;
import licence.code.generator.helper.JpaLicenceEntityHelper;
import licence.code.generator.helper.JpaResetPasswordEntityHelper;
import licence.code.generator.helper.JpaUserEntityHelper;
import licence.code.generator.helper.JpaVerificationTokenEntityHelper;
import licence.code.generator.repositories.*;
import licence.code.generator.services.token.IVerificationTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DeleteExpiredPasswordTokenTest {
    @Autowired
    private JpaResetPasswordEntityHelper jpaResetPasswordEntityHelper;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
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
    @Autowired
    private JpaLicenceEntityHelper jpaLicenceEntityHelper;
    @Autowired
    private LicenceRepository licenceRepository;
    @Autowired
    private ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Test
    @Transactional
    public void deleteExpiredTokens_shouldDeleteMultipleExpiredTokens() {
        //given:
        jpaResetPasswordEntityHelper.createExpiredResetPasswordToken(null);
        jpaResetPasswordEntityHelper.createExpiredResetPasswordToken(null);
        jpaResetPasswordEntityHelper.createExpiredResetPasswordToken(null);

        //before:
        assertEquals(3, resetPasswordTokenRepository.findAll().size());

        //when:
        int deletedTokens = resetPasswordTokenRepository.deleteExpiredTokens();

        //then:
        assertEquals(0, resetPasswordTokenRepository.findAll().size());
        assertEquals(3, deletedTokens);
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
        assertEquals(5, resetPasswordTokenRepository.findAll().size());

        //when:
        int deletedTokens = resetPasswordTokenRepository.deleteExpiredTokens();

        //then:
        assertEquals(3, resetPasswordTokenRepository.findAll().size());
        assertEquals(2, deletedTokens);
    }

    @Test
    @Transactional
    public void deleteExpiredTokens_shouldNotDeleteUserRelatedEntities() {
        //given:
        User expiredUser = userEntityHelper.createExpiredUser();
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(expiredUser));

        jpaLicenceEntityHelper.addRandomLicenceToExistingUser(expiredUser);
        jpaLicenceEntityHelper.addRandomLicenceToExistingUser(expiredUser);
        jpaLicenceEntityHelper.addRandomLicenceToExistingUser(expiredUser);
        jpaResetPasswordEntityHelper.createExpiredResetPasswordToken(expiredUser);

        //before:
        int initialUserCount = userRepository.findAll().size();
        assertEquals(1, verificationTokenRepository.findAll().size());
        assertEquals(3, licenceRepository.findAll().size());
        assertEquals(1, resetPasswordTokenRepository.findAll().size());
        assertEquals(1, roleRepository.findRolesByUsername(expiredUser.getUsername()).size());

        //when:
        int deletedRows = resetPasswordTokenRepository.deleteExpiredTokens();

        //then:
        assertEquals(1, verificationTokenRepository.findAll().size());
        assertEquals(0, initialUserCount - userRepository.findAll().size());
        assertEquals(3, licenceRepository.findAll().size());
        assertEquals(0, resetPasswordTokenRepository.findAll().size());
        assertEquals(1, roleRepository.findRolesByUsername(expiredUser.getUsername()).size());
        assertEquals(1, deletedRows);
    }
}
