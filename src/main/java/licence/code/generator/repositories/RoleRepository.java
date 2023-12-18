package licence.code.generator.repositories;

import licence.code.generator.entities.Role;
import licence.code.generator.entities.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(RoleName name);

    @Override
    void delete(Role role);

}
