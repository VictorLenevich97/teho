package by.varb.teho.service.implementation;

import by.varb.teho.entity.Base;
import by.varb.teho.entity.Equipment;
import by.varb.teho.entity.EquipmentPerBase;
import by.varb.teho.exception.BaseNotFoundException;
import by.varb.teho.exception.EquipmentNotFoundException;
import by.varb.teho.repository.BaseRepository;
import by.varb.teho.repository.EquipmentPerBaseRepository;
import by.varb.teho.repository.EquipmentRepository;
import by.varb.teho.service.BaseService;
import org.springframework.stereotype.Service;

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
