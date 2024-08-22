package licence.code.generator.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsernameDto(
        @NotNull @Size(min = 3, message = "Length must be greater than {min}") String username) {
}
