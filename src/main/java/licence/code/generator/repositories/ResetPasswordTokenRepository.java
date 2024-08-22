package licence.code.generator.repositories;

import licence.code.generator.entities.ResetPasswordToken;
import licence.code.generator.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    ResetPasswordToken findByToken(String token);

    ResetPasswordToken findByUser(User user);

    @Modifying
    @Query(value = """
              DELETE FROM ResetPasswordToken rps
              WHERE rps.expiryDate < CURRENT_TIMESTAMP
            """
    )
    int deleteExpiredTokens();
}
