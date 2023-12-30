package licence.code.generator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IdRequestDto {
    private Long id;

    @Override
    public String toString() {
        return "IdRequestDto [id=" + id + "]";
    }
}
