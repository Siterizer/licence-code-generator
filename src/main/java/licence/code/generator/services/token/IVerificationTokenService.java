package licence.code.generator.services.token;

import licence.code.generator.entities.User;
import licence.code.generator.entities.VerificationToken;

public interface IVerificationTokenService {
    VerificationToken createVerificationToken(User user);

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

    void delete(VerificationToken token);
}
