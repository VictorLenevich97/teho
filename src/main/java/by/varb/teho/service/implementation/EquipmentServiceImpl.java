package by.varb.teho.service.implementation;

import by.varb.teho.entity.Equipment;
import by.varb.teho.entity.EquipmentType;
import by.varb.teho.repository.EquipmentRepository;
import by.varb.teho.repository.EquipmentTypeRepository;
import by.varb.teho.service.EquipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    public List<Equipment> list() {
        return (List<Equipment>) equipmentRepository.findAll();
    }

    @Override
    public Long add(String name, Long typeId) {
        LOGGER.debug(String.format("Добавление ВВСТ \"%s\", typeId = %d", name, typeId));
        Optional<EquipmentType> equipmentType = equipmentTypeRepository.findById(typeId);
        if (!equipmentType.isPresent()) {
            LOGGER.error("Неверный тип");
            return -1L;
        }

        if (equipmentRepository.findByName(name).isPresent()) {
            LOGGER.error("Уже существует");
            return -1L;
        }
        Equipment saved = equipmentRepository.save(new Equipment(name, equipmentType.get()));
        return saved.getId();
    }

    @Override
    public List<EquipmentType> listTypes() {
        return (List<EquipmentType>) equipmentTypeRepository.findAll();
    }

    @Override
    public Long addType(String shortName, String longName) {
        LOGGER.debug(String.format("Добавление типа ВВСТ: \"%s\" (\"%s\")", shortName, longName));
        EquipmentType equipmentType = new EquipmentType(shortName, longName);
        EquipmentType type = equipmentTypeRepository.save(equipmentType);
        return type.getId();
    }


}
