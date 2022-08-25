package licence.code.generator.controllers.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginViewController {

    @GetMapping(value = {"/login"})
    public String renderLoginPage() {
        return "login";
    }

}
