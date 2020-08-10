package by.varb.teho.service.implementation;

import by.varb.teho.entity.Base;
import by.varb.teho.entity.Equipment;
import by.varb.teho.entity.EquipmentPerBase;
import by.varb.teho.repository.BaseRepository;
import by.varb.teho.repository.EquipmentPerBaseRepository;
import by.varb.teho.repository.EquipmentRepository;
import by.varb.teho.service.BaseService;
import org.springframework.stereotype.Service;

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
    public void add(String shortName, String fullName) {
        this.baseRepository.save(new Base(shortName, fullName));
    }

    @Override
    public void addEquipmentToBase(Long baseId, Long equipmentId, int intensity, int amount) {
        //TODO: Throw exceptions on .orElseThrow
        Base b = baseRepository.findById(baseId).get();
        Equipment e = equipmentRepository.findById(equipmentId).get();
        this.equipmentPerBaseRepository.save(new EquipmentPerBase(b, e, intensity, amount));
    }
}
