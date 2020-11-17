package va.rit.teho.service.implementation.base;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.base.Base;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.BaseNotFoundException;
import va.rit.teho.exception.EmptyFieldException;
import va.rit.teho.repository.base.BaseRepository;
import va.rit.teho.service.base.BaseService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BaseServiceImpl implements BaseService {

    private final BaseRepository baseRepository;

    public BaseServiceImpl(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    private void checkIfEmptyField(String field) {
        if (!Optional.ofNullable(field).map(s -> !s.isEmpty()).isPresent()) {
            throw new EmptyFieldException();
        }
    }

    @Override
    @Transactional
    public Long add(String shortName, String fullName) {
        checkIfEmptyField(shortName);
        checkIfEmptyField(fullName);
        baseRepository.findByFullName(fullName).ifPresent(b -> {
            throw new AlreadyExistsException("ВЧ", "название", fullName);
        });
        Base base = baseRepository.save(new Base(shortName, fullName));
        return base.getId();
    }

    @Override
    public void update(Long baseId, String shortName, String fullName) {
        Base base = getBaseOrThrow(baseId);
        base.setFullName(fullName);
        base.setShortName(shortName);
        baseRepository.save(base);
    }

    private Base getBaseOrThrow(Long baseId) {
        return baseRepository.findById(baseId).orElseThrow(() -> new BaseNotFoundException(baseId));
    }

    @Override
    public Base get(Long baseId) {
        return getBaseOrThrow(baseId);
    }

    @Override
    public List<Base> list() {
        return (List<Base>) baseRepository.findAll();
    }
}
