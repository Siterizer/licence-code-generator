package licence.code.generator.controllers;

import licence.code.generator.entities.User;
import licence.code.generator.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = {"/user"})
    public String getCurrentUserEmail(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("email", user.getEmail());
        return "user";
    }

    @GetMapping(value = {"/admin"})
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }
}

