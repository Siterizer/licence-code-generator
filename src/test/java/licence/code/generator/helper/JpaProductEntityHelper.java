package licence.code.generator.helper;

import licence.code.generator.entities.Product;
import licence.code.generator.repositories.ProductRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

@Component
public class JpaProductEntityHelper {

    @Autowired
    ProductRepository productRepository;

    public Product createRandomProduct() {
        Product product = new Product();
        product.setName(RandomString.make());
        product.setPrice(new BigDecimal(BigInteger.valueOf(new Random().nextInt(10001)), 2));
        productRepository.save(product);
        return product;
    }
}
