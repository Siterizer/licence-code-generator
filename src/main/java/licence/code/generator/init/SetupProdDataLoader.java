package licence.code.generator.init;

import licence.code.generator.entities.*;
import licence.code.generator.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
@Profile("prod")
public class SetupProdDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SetupProdDataLoader(UserRepository userRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create initial privileges
        final Privilege mainPage = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege users = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        // == create initial roles
        final List<Privilege> userPrivileges = new ArrayList<>(Collections.singletonList(mainPage));
        final List<Privilege> adminPrivileges = new ArrayList<>(Collections.singletonList(users));
        final Role adminRole = createRoleIfNotFound(RoleName.ROLE_ADMIN, adminPrivileges);
        createRoleIfNotFound(RoleName.ROLE_USER, userPrivileges);

        // == create initial user
        final User admin = createUserIfNotFound("test@test.com", "test", "test", new ArrayList<>(Collections.singletonList(adminRole)), false);


        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege();
            privilege.setName(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(final RoleName name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
        }
        role.setPrivileges(privileges);
        role = roleRepository.save(role);
        return role;
    }

    @Transactional
    User createUserIfNotFound(final String email, final String password, final String username, final Collection<Role> roles, boolean locked) {
        User user = userRepository.findByEmail(email) == null ? userRepository.findByUsername(username) : userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setPassword(passwordEncoder.encode(password));
            user.setUsername(username);
            user.setEmail(email);
            user.setLocked(locked);
        }
        user.setRoles(roles);
        user = userRepository.save(user);
        return user;
    }
}