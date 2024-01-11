package licence.code.generator.services;

import licence.code.generator.entities.User;
import licence.code.generator.entities.VerificationToken;

public interface IVerificationTokenService {
    VerificationToken createVerificationToken(User user);
}