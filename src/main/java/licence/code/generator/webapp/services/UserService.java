package licence.code.generator.webapp.services;

import licence.code.generator.webapp.entities.User;
import licence.code.generator.webapp.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void saveRegisteredUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }
}
