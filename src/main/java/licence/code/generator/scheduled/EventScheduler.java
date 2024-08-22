package licence.code.generator.scheduled;

import licence.code.generator.repositories.ResetPasswordTokenRepository;
import licence.code.generator.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
@Transactional
//@Profile("prod")
public class EventScheduler {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResetPasswordTokenRepository resetPasswordRepository;

    //@Scheduled(cron = "0 0 20 * * ?", zone = "Europe/Warsaw")//Every day at 20:00
    @Scheduled(cron = "0/20 * * * * ?", zone = "Europe/Warsaw")//Every day at 20:00
    public void expiredEntitiesRemover() {
        deleteExpiredResetPasswordTokens();
        removeExpiredUsers();
    }

    public void deleteExpiredResetPasswordTokens(){
        LOGGER.info("Removing expired Reset Password Tokens from the database");
        int deletedResetPasswordTokens = resetPasswordRepository.deleteExpiredTokens();
        LOGGER.info("Successfully removed {} Reset Password Tokens from the database", deletedResetPasswordTokens);
    }

    public void removeExpiredUsers() {
        LOGGER.info("Removing Users with expired Verification Token from the database");
        int deletedUsers = userRepository.deleteExpiredUsers();
        LOGGER.info("Successfully removed {} Users with expired Verification Token", deletedUsers);
    }




}
