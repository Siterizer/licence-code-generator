package licence.code.generator.controllers;

import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.entities.User;
import licence.code.generator.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static java.util.stream.Collectors.toList;


@Controller
public class UserController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserService userService;
    private final UserDtoMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserDtoMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping(value = {"/user"})
    public String getCurrentUserEmail(Model model) {
        final User user = userService.findUserByUsername(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        LOGGER.debug("Showing email for user with id: {}", user.getId());

        model.addAttribute("email", user.getEmail());
        return "user";
    }

    @GetMapping(value = {"/admin"})
    public String showAllUsers(Model model) {
        final User user = userService.findUserByUsername(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        LOGGER.debug("Showing all users for admin with id: {}", user.getId());
        model.addAttribute("users",
                userService.getAllUsers()
                        .stream()
                        .map(userMapper::toDto)
                        .collect(toList()));
        return "admin";
    }
}

