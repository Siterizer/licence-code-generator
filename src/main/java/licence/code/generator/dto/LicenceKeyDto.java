package licence.code.generator.dto;


import jakarta.validation.constraints.NotNull;
import licence.code.generator.security.validation.UUID;

public record LicenceKeyDto(@NotNull @UUID String key) {
}
