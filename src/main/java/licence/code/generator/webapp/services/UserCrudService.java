package licence.code.generator.webapp.services;

import licence.code.generator.webapp.entities.User;
import licence.code.generator.webapp.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserCrudService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserCrudService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(name);
        return user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + name));
    }

    public List<User> getAllUsers() {
        List<User> temp = new ArrayList<>();
        userRepository.findAll().forEach(temp::add);
        return temp;
    }
}
