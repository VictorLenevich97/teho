package va.rit.teho.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.repository.EquipmentRepository;
import va.rit.teho.repository.EquipmentSubTypeRepository;
import va.rit.teho.repository.EquipmentTypeRepository;
import va.rit.teho.service.EquipmentService;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentRepository equipmentRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final EquipmentSubTypeRepository equipmentSubTypeRepository;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository,
                                EquipmentTypeRepository equipmentTypeRepository,
                                EquipmentSubTypeRepository equipmentSubTypeRepository) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.equipmentSubTypeRepository = equipmentSubTypeRepository;
    }

    public List<Equipment> list() {
        return (List<Equipment>) equipmentRepository.findAll();
    }

    @Override
    public Long add(String name, Long subTypeId) {
        LOGGER.debug(String.format("Добавление ВВСТ \"%s\", subTypeId = %d", name, subTypeId));
        Optional<EquipmentSubType> equipmentSubType = equipmentSubTypeRepository.findById(subTypeId);
        if (!equipmentSubType.isPresent()) {
            LOGGER.error("Неверный тип");
            return -1L;
        }

        if (equipmentRepository.findByName(name).isPresent()) {
            LOGGER.error("Уже существует");
            return -1L;
        }
        Equipment s = new Equipment(name, equipmentSubType.get());
        Equipment saved = equipmentRepository.save(s);
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
