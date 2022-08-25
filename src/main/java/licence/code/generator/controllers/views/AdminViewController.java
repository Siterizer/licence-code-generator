package licence.code.generator.controllers.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdminViewController {

    @GetMapping(value = {"/admin"})
    public String showAllUsers() {
        return "admin";
    }
}

