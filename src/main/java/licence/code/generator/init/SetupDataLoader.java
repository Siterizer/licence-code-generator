package licence.code.generator.init;

import licence.code.generator.entities.*;
import licence.code.generator.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private LicenceRepository licenceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // API

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
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(mainPage));
        final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(users));
        final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        final Role userRole = createRoleIfNotFound("ROLE_USER", userPrivileges);

        // == create initial products
        final Product fishBot = createProductIfNotFound("Fish Bot");
        final Product gatheringBot = createProductIfNotFound("Gathering Bot");
        final Product fightingBot = createProductIfNotFound("Fighting Bot");

        // == create initial user
        final User locked = createUserIfNotFound("locked@test.com", "locked", "locked", new ArrayList<>(Arrays.asList(userRole)), true);
        final User user1 = createUserIfNotFound("asd1@test.com", "asd", "asd1", new ArrayList<>(Arrays.asList(userRole)), false);
        final User user2 = createUserIfNotFound("asd2@test.com", "asd", "asd2", new ArrayList<>(Arrays.asList(userRole)), false);
        final User admin = createUserIfNotFound("test@test.com", "test", "test", new ArrayList<>(Arrays.asList(adminRole)), false);

        // == create initial licences
        createLicenceIfNotFound(user1, fishBot);
        createLicenceIfNotFound(user1, gatheringBot);
        createLicenceIfNotFound(user2, gatheringBot);
        createLicenceIfNotFound(user2, fightingBot);

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
        }
        role.setPrivileges(privileges);
        role = roleRepository.save(role);
        return role;
    }

    @Transactional
    Product createProductIfNotFound(final String name) {
        Product product = productRepository.findByName(name);
        if (product == null) {
            product = new Product(name);
            product = productRepository.save(product);
        }
        return product;
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

    @Transactional
    Licence createLicenceIfNotFound(final User user, Product product) {
        Licence licence = new Licence(user, product);
        licence = licenceRepository.save(licence);

        return licence;
    }


}