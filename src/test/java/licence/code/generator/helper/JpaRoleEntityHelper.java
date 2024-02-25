package licence.code.generator.helper;

import licence.code.generator.entities.Role;
import licence.code.generator.entities.RoleName;
import licence.code.generator.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class JpaRoleEntityHelper {

    @Autowired
    RoleRepository roleRepository;

    public Set<Role> getUserRole() {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_USER));
        return roles;
    }

    public Set<Role> getAdminRole() {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN));
        return roles;
    }
}
