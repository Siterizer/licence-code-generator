package licence.code.generator;

import licence.code.generator.entities.User;
import licence.code.generator.repositories.PrivilegeRepository;
import licence.code.generator.services.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class GeneratorApplicationTests {
    @Autowired
    private IUserService userService;

    @Autowired
    private PrivilegeRepository privilegeRepository;


    @Test
    @Transactional
    void fetchUserByUsername() {
        User user = userService.findUserByUsername("user1");
        assert (user.getEmail().equals("user1@email.com"));
    }
}
