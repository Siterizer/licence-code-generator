package licence.code.generator.repositories;

import licence.code.generator.entities.User;
import licence.code.generator.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    @Query(value = """
              SELECT vt
              FROM VerificationToken vt
              INNER JOIN FETCH vt.user
              WHERE vt.token = ?1
            """
    )
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
