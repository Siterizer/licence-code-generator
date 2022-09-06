package licence.code.generator.dto.mapper;

import licence.code.generator.dto.LicenceDto;
import licence.code.generator.entities.Licence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class LicenceDtoMapper {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public LicenceDto toDto(Licence licence) {
        LOGGER.debug("Mapping Licence: {} to LicenceDto", licence);

        LicenceDto licenceDto = new LicenceDto(licence.getProduct().getName(), licence.getId());
        LOGGER.debug("Mapped LicenceDto: {}", licenceDto);

        return licenceDto;
    }
}
