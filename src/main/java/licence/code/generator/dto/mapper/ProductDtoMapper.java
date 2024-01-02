package licence.code.generator.dto.mapper;

import licence.code.generator.dto.ProductDto;
import licence.code.generator.entities.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ProductDto toDto(Product product) {
        LOGGER.debug("Mapping product: {} to ProductDto", product);

        ProductDto productDto = new ProductDto(product.getId(), product.getName());
        LOGGER.debug("Mapped ProductDto: {}", productDto);

        return productDto;
    }
}
