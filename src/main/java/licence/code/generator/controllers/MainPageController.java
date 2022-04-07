package licence.code.generator.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @GetMapping(value = {"/", "/mainPage"}) public String renderMainView() {
        return "mainPage";
    }

}
