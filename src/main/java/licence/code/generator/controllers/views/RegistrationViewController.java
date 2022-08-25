package licence.code.generator.controllers.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistrationViewController {

    @GetMapping(value = {"/register"})
    public String renderRegisterPage() {
        return "register";
    }
}
