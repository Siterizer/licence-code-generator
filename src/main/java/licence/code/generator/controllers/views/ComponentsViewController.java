package licence.code.generator.controllers.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ComponentsViewController {

    @GetMapping(value = {"/navbar"})
    public String renderNavbar() {
        return "components/navbar";
    }
}
