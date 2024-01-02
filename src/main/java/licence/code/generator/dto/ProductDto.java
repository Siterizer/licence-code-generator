package licence.code.generator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;

    @Override
    public String toString() {
        return "ProductDto [id=" +
                id +
                ", name=" +
                name +
                "]";
    }
}
