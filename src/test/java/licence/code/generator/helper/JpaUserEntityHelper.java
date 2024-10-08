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
    private UserRepository userRepository;
    @Autowired
    JpaRoleEntityHelper roleEntityHelper;
    Random random = new Random();

    public User createRandomUser() {
        User user = new User();
        user.setUsername(RandomString.make());
        user.setEmail(RandomString.make());
        user.setPassword(RandomString.make());
        user.setLocked(random.nextBoolean());
        user.setAccountExpired(random.nextBoolean());
        user.setRoles(roleEntityHelper.getUserRole());
        userRepository.save(user);
        return user;
    }

    public User createNotBlockedUser() {
        User user = new User();
        user.setUsername(RandomString.make());
        user.setEmail(RandomString.make());
        user.setPassword(RandomString.make());
        user.setLocked(false);
        user.setAccountExpired(false);
        user.setRoles(roleEntityHelper.getUserRole());
        userRepository.save(user);
        return user;
    }

    public User createBlockedUser() {
        User user = new User();
        user.setUsername(RandomString.make());
        user.setEmail(RandomString.make());
        user.setPassword(RandomString.make());
        user.setLocked(true);
        user.setAccountExpired(false);
        user.setRoles(roleEntityHelper.getUserRole());
        userRepository.save(user);
        return user;
    }

    public User createExpiredUser() {
        User user = new User();
        user.setUsername(RandomString.make());
        user.setEmail(RandomString.make());
        user.setPassword(RandomString.make());
        user.setLocked(false);
        user.setAccountExpired(true);
        user.setRoles(roleEntityHelper.getUserRole());
        userRepository.save(user);

        return user;
    }

    public User createNotBlockedAdmin() {
        User user = new User();
        user.setUsername(RandomString.make());
        user.setEmail(RandomString.make());
        user.setPassword(RandomString.make());
        user.setLocked(false);
        user.setAccountExpired(false);
        user.setRoles(roleEntityHelper.getAdminRole());
        userRepository.save(user);
        return user;
    }

    public User createBlockedAdmin() {
        User user = new User();
        user.setUsername(RandomString.make());
        user.setEmail(RandomString.make());
        user.setPassword(RandomString.make());
        user.setLocked(true);
        user.setAccountExpired(false);
        user.setRoles(roleEntityHelper.getAdminRole());
        userRepository.save(user);
        return user;
    }
}
