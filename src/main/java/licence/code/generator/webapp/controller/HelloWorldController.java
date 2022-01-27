package licence.code.generator.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloWorldController {

    @GetMapping(value = {"/*"}) public String renderMainView() {
        return "helloWorld";
    }

}
