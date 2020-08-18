package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.Base;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentPerBase;
import va.rit.teho.exception.BaseNotFoundException;
import va.rit.teho.exception.EquipmentNotFoundException;
import va.rit.teho.repository.BaseRepository;
import va.rit.teho.repository.EquipmentPerBaseRepository;
import va.rit.teho.repository.EquipmentRepository;
import va.rit.teho.service.BaseService;

import java.util.List;

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

    @Override
    public Long add(String shortName, String fullName) {
        Base base = this.baseRepository.save(new Base(shortName, fullName));
        return base.getId();
    }

    @Override
    public void addEquipmentToBase(Long baseId, Long equipmentId, int intensity, int amount) {
        Base b = baseRepository.findById(baseId).orElseThrow(() -> new BaseNotFoundException(baseId));
        Equipment e =
                equipmentRepository
                        .findById(equipmentId)
                        .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
        this.equipmentPerBaseRepository.save(new EquipmentPerBase(b, e, intensity, amount));
    }

    @Override
    public Base get(Long baseId) {
        return baseRepository.findById(baseId).orElseThrow(() -> new BaseNotFoundException(baseId));
    }

    @Override
    public List<Base> list() {
        return (List<Base>) this.baseRepository.findAll();
    }
}
