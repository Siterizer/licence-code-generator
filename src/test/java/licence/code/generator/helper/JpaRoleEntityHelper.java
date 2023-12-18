package licence.code.generator.helper;

import licence.code.generator.entities.Role;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.repositories.RoleRepository;
import licence.code.generator.repositories.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class JpaRoleEntityHelper {

    @Autowired
    RoleRepository roleRepository;

    public List<Role> getUserRole() {
        List<Role> userList = new ArrayList<>();
        userList.add(roleRepository.findByName(RoleName.ROLE_USER));
        return userList;
    }

    public List<Role> getAdminRole() {
        List<Role> adminList = new ArrayList<>();
        adminList.add(roleRepository.findByName(RoleName.ROLE_ADMIN));
        return adminList;
    }
}
