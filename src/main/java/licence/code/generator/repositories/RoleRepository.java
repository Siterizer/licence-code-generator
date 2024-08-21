package licence.code.generator.repositories;

import licence.code.generator.entities.Role;
import licence.code.generator.entities.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = """
              SELECT distinct r
              FROM Role r
              INNER JOIN FETCH r.privileges p
              WHERE r.name = ?1
            """
    )
    Role findByName(RoleName name);

    @Query(value = """
              SELECT distinct r
              FROM User u
              INNER JOIN u.roles r
              WHERE u.username = ?1
            """
    )
    List<Role> findRolesByUsername(String username);

    @Override
    void delete(Role role);

}
