package licence.code.generator.helper;

import licence.code.generator.entities.User;
import licence.code.generator.repositories.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class JpaUserEntityHelper {

    @Autowired
    UserRepository userRepository;
    Random random = new Random();

    public User createRandomUser() {
        User user = new User();
        user.setUsername(RandomString.make());
        user.setEmail(RandomString.make());
        user.setPassword(RandomString.make());
        user.setLocked(random.nextBoolean());
        userRepository.save(user);
        return user;
    }
}
