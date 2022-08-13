package licence.code.generator.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {

    @GetMapping(value = {"/user"})
    public String getCurrentUserEmail() {
        return "user";
    }

    @GetMapping(value = {"/admin"})
    public String showAllUsers() {
        return "admin";
    }
}

