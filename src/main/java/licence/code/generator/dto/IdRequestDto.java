package licence.code.generator.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IdRequestDto(@NotNull @Positive Long id) {
}
