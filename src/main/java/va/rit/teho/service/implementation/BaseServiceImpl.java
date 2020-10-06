package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.Base;
import va.rit.teho.entity.EquipmentPerBase;
import va.rit.teho.entity.EquipmentPerBaseAmount;
import va.rit.teho.exception.*;
import va.rit.teho.repository.BaseRepository;
import va.rit.teho.repository.EquipmentPerBaseRepository;
import va.rit.teho.repository.EquipmentRepository;
import va.rit.teho.service.BaseService;

import java.util.List;
import java.util.Optional;

@Service
public class BaseServiceImpl implements BaseService {

    private final BaseRepository baseRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentPerBaseRepository equipmentPerBaseRepository;

    public BaseServiceImpl(
            BaseRepository baseRepository,
            EquipmentRepository equipmentRepository,
            EquipmentPerBaseRepository equipmentPerBaseRepository) {
        this.baseRepository = baseRepository;
        this.equipmentRepository = equipmentRepository;
        this.equipmentPerBaseRepository = equipmentPerBaseRepository;
    }

    private void checkIfEmptyField(String field) {
        if (!Optional.ofNullable(field).map(s -> !s.isEmpty()).isPresent()) {
            throw new EmptyFieldException();
        }
    }

    @Override
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

    @Override
    public void addEquipmentToBase(Long baseId, Long equipmentId, int intensity, int amount) {
        getBaseOrThrow(baseId);
        getEquipmentOrThrow(equipmentId);
        equipmentPerBaseRepository.findById(new EquipmentPerBaseAmount(baseId, equipmentId)).ifPresent(epb -> {
            throw new AlreadyExistsException("ВВСТ в ВЧ", "(id ВЧ, id ВВСТ)", "(" + baseId + ", " + equipmentId + ")");
        });

        this.equipmentPerBaseRepository.save(new EquipmentPerBase(baseId, equipmentId, intensity, amount));
    }

    private void getEquipmentOrThrow(Long equipmentId) {
        equipmentRepository
                .findById(equipmentId)
                .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
    }

    private Base getBaseOrThrow(Long baseId) {
        return baseRepository.findById(baseId).orElseThrow(() -> new BaseNotFoundException(baseId));
    }

    @Override
    public void updateEquipmentInBase(Long baseId, Long equipmentId, int intensity, int amount) {
        getBaseOrThrow(baseId);
        getEquipmentOrThrow(equipmentId);
        EquipmentPerBase epb =
                equipmentPerBaseRepository.findById(new EquipmentPerBaseAmount(baseId, equipmentId))
                                          .orElseThrow(() -> new NotFoundException("ВЧ (id = " + baseId + ") не содержит ВВСТ (id = " + equipmentId + ")!"));
        epb.setIntensity(intensity);
        epb.setAmount(amount);
        equipmentPerBaseRepository.save(epb);
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
