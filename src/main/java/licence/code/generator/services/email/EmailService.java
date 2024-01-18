package licence.code.generator.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

import static licence.code.generator.util.GeneratorStringUtils.REGISTRATION_CONFIRM_PATH;


@Service("emailService")
public class EmailService implements IEmailService {
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    Environment environment;
    @Value("${spring.mail.username}")
    private String mailUsername;
    @Value("${server.port}")
    int port;

    @Override
    public void sendRegistrationConfirmEmail(String setTo, String verificationToken) {
        String url = String.format("%s:%d%s?token=%s", InetAddress.getLoopbackAddress().getHostAddress(),
                port, REGISTRATION_CONFIRM_PATH, verificationToken);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername);
        message.setTo(setTo);
        message.setSubject("Confirm your address email for generator app");
        message.setText("Click the link and confirm your address: " + url);
        mailSender.send(message);
    }
}
