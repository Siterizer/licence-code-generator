package licence.code.generator.repositories;

import licence.code.generator.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query(value = """
              SELECT distinct u
              FROM User u
              INNER JOIN FETCH u.roles r
              INNER JOIN FETCH r.privileges p
              WHERE u.username = ?1
            """
    )
    User findByUsername(String username);

    @Query(value = """
              SELECT distinct u
              FROM User u
              INNER JOIN FETCH u.roles r
              INNER JOIN FETCH r.privileges p
              WHERE u.id = ?1
            """
    )
    Optional<User> findById(Long id);

    @Query(value = """
              SELECT distinct u
              FROM User u
              INNER JOIN FETCH u.roles r
              INNER JOIN FETCH r.privileges priv
              LEFT JOIN FETCH u.licences l
              LEFT JOIN FETCH l.product prod
              WHERE u.username = ?1
            """
    )
    User findByUsernameWithRelatedEntities(String username);
}
