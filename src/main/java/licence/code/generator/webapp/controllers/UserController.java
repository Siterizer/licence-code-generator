package licence.code.generator.webapp.controllers;

import licence.code.generator.webapp.entities.User;
import licence.code.generator.webapp.services.UserCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {
    private final UserCrudService userCrudService;

    @Autowired
    public UserController(UserCrudService userCrudService) {
        this.userCrudService = userCrudService;
    }

    @GetMapping(value = {"/user"})
    public String renderMainView() {
        List<User> tmp = userCrudService.getAllUsers();
        for (User user : tmp) {
            System.out.println(user.getUsername());
        }
        return "helloWorld";
    }
}

