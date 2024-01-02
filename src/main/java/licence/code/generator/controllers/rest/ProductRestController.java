package licence.code.generator.controllers.rest;

import licence.code.generator.dto.ProductDto;
import licence.code.generator.entities.Product;
import licence.code.generator.entities.User;
import licence.code.generator.repositories.ProductRepository;
import licence.code.generator.services.IProductService;
import licence.code.generator.services.IUserService;
import licence.code.generator.web.exception.UnauthorizedUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static licence.code.generator.util.GeneratorStringUtils.PRODUCT_GET_ALL_PATH;

@RestController
public class ProductRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final IProductService productService;
    private final IUserService userService;

    @Autowired
    ProductRestController(IProductService productService, IUserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping(value = {PRODUCT_GET_ALL_PATH})
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        User requester = getRequester();
        LOGGER.info("Showing all products for user with id: {}", requester.getId());
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    private User getRequester() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            //Note this is never going to happen in real-life usage case as Spring Security prevents any Unauthorized
            //entrance with RestAuthenticationEntryPoint class
            throw new UnauthorizedUserException("Unauthorized User wanted to access /admin endpoint");
        }
        return userService.findUserByUsername(authentication.getName());
    }

}
