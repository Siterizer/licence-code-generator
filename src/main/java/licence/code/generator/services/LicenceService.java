package licence.code.generator.services;

import licence.code.generator.entities.Licence;
import licence.code.generator.repositories.LicenceRepository;
import licence.code.generator.repositories.ProductRepository;
import licence.code.generator.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class LicenceService implements ILicenceService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final LicenceRepository licenceRepository;

    @Autowired
    public LicenceService(ProductRepository productRepository, UserRepository userRepository, LicenceRepository licenceRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.licenceRepository = licenceRepository;
    }

    @Override
    public Licence createLicence(Long userId, Long productId) {
        Licence newLicence = new Licence();
        newLicence.setUser(userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(
                "Unable to create licence for User with id: " + userId + " User does not exists!")));
        newLicence.setProduct(productRepository.findById(productId).orElseThrow(() -> new NoSuchElementException(
                "Unable to create licence for User with id: " + userId + " Product with id: " + productId + " does not exists!")));
        licenceRepository.save(newLicence);
        return newLicence;
    }

    @Override
    public Boolean checkLicenceAccordance(String id) {
        Licence licence = licenceRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
                "Unable to create licence with id: " + id));
        //null check for now. Later will come ip check or Browser Fingerprint or Device Information.
        return Objects.nonNull(licence);
    }
}
