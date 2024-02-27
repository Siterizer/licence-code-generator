package licence.code.generator.services;

import licence.code.generator.entities.Licence;

public interface ILicenceService {
    Licence createLicence(Long userId, Long productId);

    Boolean checkLicenceAccordance(String id);
}
