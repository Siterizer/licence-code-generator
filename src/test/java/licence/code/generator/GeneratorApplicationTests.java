package licence.code.generator;

import licence.code.generator.entities.User;
import licence.code.generator.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class GeneratorApplicationTests {
    @Autowired
    private UserService userService;


    @Test
    @Transactional
    void fetchUserByUsername() {
        User user = userService.findUserByUsername("user1");
        assert (user.getEmail().equals("user1@email.com"));
    }
}
