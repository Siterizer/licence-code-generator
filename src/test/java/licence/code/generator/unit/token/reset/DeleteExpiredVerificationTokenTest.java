package licence.code.generator.unit.token.reset;

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
    @Autowired
    private JpaLicenceEntityHelper jpaLicenceEntityHelper;
    @Autowired
    private LicenceRepository licenceRepository;
    @Autowired
    private ResetPasswordTokenRepository resetPasswordTokenRepository;
    @Autowired
    private JpaResetPasswordEntityHelper jpaResetPasswordEntityHelper;


    @Test
    @Transactional
    public void deleteExpiredUsers_shouldDeleteMultipleExpiredUsersAndTokens() {
        //given:
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(userEntityHelper.createExpiredUser()));
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(userEntityHelper.createExpiredUser()));
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(userEntityHelper.createExpiredUser()));

        //before:
        int initialUserCount = userRepository.findAll().size();
        assertEquals(3, tokenRepository.findAll().size());

        //when:
        int deletedRows = userRepository.deleteExpiredUsers();

        //then:
        assertEquals(0, tokenRepository.findAll().size());
        assertEquals(3, initialUserCount - userRepository.findAll().size());
        assertEquals(3, deletedRows);
    }

    @Test
    @Transactional
    public void deleteExpiredUsers_shouldDeleteOnlyExpiredEntities() {
        //given:
        verificationTokenService.createVerificationToken(userEntityHelper.createExpiredUser());
        verificationTokenService.createVerificationToken(userEntityHelper.createExpiredUser());
        verificationTokenService.createVerificationToken(userEntityHelper.createExpiredUser());
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(userEntityHelper.createExpiredUser()));
        jpaVerificationTokenEntityHelper.expireVerificationToken(
                verificationTokenService.createVerificationToken(userEntityHelper.createExpiredUser()));

        //before
        int initialUserCount = userRepository.findAll().size();
        assertEquals(5, tokenRepository.findAll().size());

        //when:
        int deletedRows = userRepository.deleteExpiredUsers();

        //then:
        assertEquals(3, tokenRepository.findAll().size());
        assertEquals(2, initialUserCount - userRepository.findAll().size());
        assertEquals(2, deletedRows);
    }

    @Test
    @Transactional
    public void deleteExpiredUsers_shouldDeleteAllUserRelatedEntities() {
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
        assertEquals(3, licenceRepository.findAll().size());
        assertEquals(1, resetPasswordTokenRepository.findAll().size());
        assertEquals(1, roleRepository.findRolesByUsername(expiredUser.getUsername()).size());

        //when:
        int deletedRows = userRepository.deleteExpiredUsers();

        //then:
        assertEquals(0, tokenRepository.findAll().size());
        assertEquals(1, initialUserCount - userRepository.findAll().size());
        assertEquals(0, licenceRepository.findAll().size());
        assertEquals(0, resetPasswordTokenRepository.findAll().size());
        assertEquals(0, roleRepository.findRolesByUsername(expiredUser.getUsername()).size());
        assertEquals(1, deletedRows);
    }
}
