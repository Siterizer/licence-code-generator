package licence.code.generator.controllers;

import licence.code.generator.entities.User;
import licence.code.generator.dto.UserDto;
import licence.code.generator.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Controller
public class RegistrationRestController {
    private final UserService userService;

    @Autowired
    public RegistrationRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = {"/register"})
    public RedirectView registerUser(@Valid final UserDto userDto) {
        User user = new User();
        user.setUsername("tak");
        userService.registerUser(user);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("users");
        return redirectView;
    }

    @PostMapping(value = {"/user/updatePassword"})
    public String updateUserPassword() {
        User user = new User();
        user.setUsername("tak");
        userService.registerUser(user);
        return "users";
    }

    @PostMapping("/user/resetPassword")
    public String resetUserPassword() {
        User user = new User();
        user.setUsername("tak");
        userService.registerUser(user);
        return "users";
    }
}
