package licence.code.generator.repositories;

import licence.code.generator.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByName(String name);

    @Override
    void delete(Product role);

}
