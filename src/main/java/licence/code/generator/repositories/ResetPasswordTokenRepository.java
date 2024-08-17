package licence.code.generator.repositories;

import licence.code.generator.entities.ResetPasswordToken;
import licence.code.generator.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    ResetPasswordToken findByToken(String token);

    ResetPasswordToken findByUser(User user);
}
