package licence.code.generator.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailService implements IEmailService {
    @Autowired
    JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailUsername;

    @Override
    public void sendRegistrationConfirmEmail(String setTo, String verificationToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername);
        message.setTo(setTo);
        message.setSubject("Confirm your address email for generator app");
        message.setText("Token: bla bla bla");
        mailSender.send(message);
    }
}
