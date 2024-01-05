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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
@Profile("local")
public class SetupLocalDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final LicenceRepository licenceRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SetupLocalDataLoader(UserRepository userRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, LicenceRepository licenceRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.licenceRepository = licenceRepository;
        this.productRepository = productRepository;
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
        final Role userRole = createRoleIfNotFound(RoleName.ROLE_USER, userPrivileges);

        // == create initial products
        final Product fishBot = createProductIfNotFound("Fish Bot", new BigDecimal("0.50"));
        final Product gatheringBot = createProductIfNotFound("Gathering Bot", new BigDecimal("1.00"));
        final Product fightingBot = createProductIfNotFound("Fighting Bot", new BigDecimal("2.50"));

        // == create initial user
        final User locked = createUserIfNotFound("locked@test.com", "locked", "locked", new ArrayList<>(Collections.singletonList(userRole)), true);
        final User user1 = createUserIfNotFound("asd1@test.com", "asd", "asd1", new ArrayList<>(Collections.singletonList(userRole)), false);
        final User user2 = createUserIfNotFound("asd2@test.com", "asd", "asd2", new ArrayList<>(Collections.singletonList(userRole)), false);
        final User admin = createUserIfNotFound("test@test.com", "test", "test", new ArrayList<>(List.of(adminRole, userRole)), false);

        // == create initial licences
        createLicenceIfNotFound(user1, fishBot);
        createLicenceIfNotFound(user1, gatheringBot);
        createLicenceIfNotFound(user2, gatheringBot);
        createLicenceIfNotFound(user2, fightingBot);
        createLicenceIfNotFound(admin, fightingBot);

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
    Product createProductIfNotFound(final String name, final BigDecimal price) {
        Product product = productRepository.findByName(name);
        if (product == null) {
            product = new Product();
            product.setName(name);
            product.setPrice(price);
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
        for (Licence userLicence : licenceRepository.findByUser(user)) {
            if (userLicence.getProduct().getName().equals(product.getName())) {
                return userLicence;
            }
        }
        Licence licence = new Licence();
        licence.setUser(user);
        licence.setProduct(product);
        licence = licenceRepository.save(licence);
        return licence;
    }


}