package licence.code.generator.security.configuration.scheduled;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class EventScheduler {

    @Scheduled(cron = "0 20 * * * ?", zone = "Europe/Warsaw")//Every day at 20:00
    public void removeExpiredTokens() {

    }


}
