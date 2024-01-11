package licence.code.generator.services.email;

public interface IEmailService {
    void sendRegistrationConfirmEmail(String setTo, String verificationToken);
}
