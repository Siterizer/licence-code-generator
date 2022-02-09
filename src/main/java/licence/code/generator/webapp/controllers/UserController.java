package licence.code.generator.webapp.controllers;

import licence.code.generator.webapp.entities.User;
import licence.code.generator.webapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = {"/users"})
    public String renderMainView(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping(value = {"/tmp/register"})
    public String registerUser() {
        User user = new User();
        user.setUsername("tak");
        userService.registerUser(user);
        return "users";
    }
}

