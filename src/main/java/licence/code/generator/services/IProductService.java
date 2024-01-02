package licence.code.generator.services;

import licence.code.generator.dto.ProductDto;

import java.util.List;

public interface IProductService {
    List<ProductDto> getAllProducts();
}
