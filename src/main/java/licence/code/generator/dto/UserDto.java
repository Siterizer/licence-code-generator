package licence.code.generator.dto;

import java.util.List;

public record UserDto(
        Long id,
        String username,
        String email,
        List<String> roles,
        List<LicenceDto> licences,
        boolean isExpired,
        boolean isLocked) {
}
