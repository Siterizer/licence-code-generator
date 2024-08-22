package licence.code.generator.services.token;

import licence.code.generator.entities.ResetPasswordToken;
import licence.code.generator.entities.User;
import licence.code.generator.repositories.ResetPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResetPasswordService implements IResetPasswordTokenService {
    private final ResetPasswordTokenRepository resetPasswordRepository;

    @Autowired
    public ResetPasswordService(ResetPasswordTokenRepository resetPasswordRepository) {
        this.resetPasswordRepository = resetPasswordRepository;
    }

    public ResetPasswordToken createVerificationToken(User user) {
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(user);
        resetPasswordRepository.save(resetPasswordToken);
        return resetPasswordToken;

    }

    @Override
    public ResetPasswordToken findByToken(String token) {
        return resetPasswordRepository.findByToken(token);
    }

    @Override
    public ResetPasswordToken findByUser(User user) {
        return resetPasswordRepository.findByUser(user);
    }
}
