package licence.code.generator.controllers.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageViewController {

    @GetMapping(value = {"/", "/mainPage"}) public String renderMainView() {
        return "mainPage";
    }

}
