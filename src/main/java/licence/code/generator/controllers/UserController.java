package licence.code.generator.controllers;

import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.entities.User;
import licence.code.generator.services.UserService;
import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static java.util.stream.Collectors.toList;


@Controller
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserDtoMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping(value = {"/user"})
    public String getCurrentUserEmail(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("email", user.getEmail());
        return "user";
    }

    @GetMapping(value = {"/admin"})
    public String showAllUsers(Model model) {
        model.addAttribute("users",
                userService.getAllUsers()
                        .stream()
                        .map(userMapper::toDto)
                        .collect(toList()));
        return "admin";
    }
}

