package licence.code.generator.services;

import licence.code.generator.entities.User;
import licence.code.generator.entities.VerificationToken;
import licence.code.generator.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VerificationService implements IVerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired

    public VerificationService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public VerificationToken createVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken(user);
        verificationTokenRepository.save(verificationToken);
        return verificationToken;

    }
}
