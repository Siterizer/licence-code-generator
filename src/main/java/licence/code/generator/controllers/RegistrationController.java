package licence.code.generator.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistrationController {

    @GetMapping(value = {"/register"})
    public String renderRegisterPage() {
        return "register";
    }
}
