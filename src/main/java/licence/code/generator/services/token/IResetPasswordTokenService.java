package licence.code.generator.services.token;

import licence.code.generator.entities.ResetPasswordToken;
import licence.code.generator.entities.User;

public interface IResetPasswordTokenService {
    ResetPasswordToken createVerificationToken(User user);

    ResetPasswordToken findByToken(String token);

    ResetPasswordToken findByUser(User user);
}
