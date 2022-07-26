package licence.code.generator.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ComponentsController {

    @GetMapping(value = {"/navbar"})
    public String renderNavbar() {
        return "components/navbar";
    }
}
