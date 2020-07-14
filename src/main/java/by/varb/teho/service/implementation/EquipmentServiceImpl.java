package by.varb.teho.service.implementation;

import by.varb.teho.entity.Equipment;
import by.varb.teho.entity.EquipmentType;
import by.varb.teho.repository.EquipmentRepository;
import by.varb.teho.repository.EquipmentTypeRepository;
import by.varb.teho.service.EquipmentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    private final EquipmentTypeRepository equipmentTypeRepository;

    public static final Logger log = LogManager.getLogger(EquipmentServiceImpl.class);

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    public List<Equipment> getAll() {
        return (List<Equipment>) equipmentRepository.findAll();
    }

    @Override
    public void add(String name, Long typeId) throws ChangeSetPersister.NotFoundException {
        Optional<EquipmentType> equipmentType = equipmentTypeRepository.findById(typeId);
        if (!equipmentType.isPresent()) {
            log.error("Неверный тип");
            return;
        }

        if (equipmentRepository.findByName(name).isPresent()) {
            log.error("Уже существует");
            return;
        }
        equipmentRepository.save(new Equipment(name, equipmentType.get()));
    }

    @Override
    public List<EquipmentType> getAllTypes() {
        return (List<EquipmentType>) equipmentTypeRepository.findAll();
    }

    @Override
    public void addType(String shortName, String longName) {
        EquipmentType equipmentType = new EquipmentType(shortName, longName);
        equipmentTypeRepository.save(equipmentType);
    }


}
