package licence.code.generator.webapp.controllers;

import licence.code.generator.webapp.services.UserCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {
    private final UserCrudService userCrudService;

    @Autowired
    public UserController(UserCrudService userCrudService) {
        this.userCrudService = userCrudService;
    }

    @GetMapping(value = {"/users"})
    public String renderMainView(Model model) {
        model.addAttribute("users", userCrudService.getAllUsers());
        return "users";
    }
}

