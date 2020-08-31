package va.rit.teho.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.exception.EquipmentNotFoundException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.EquipmentRepository;
import va.rit.teho.repository.EquipmentSubTypeRepository;
import va.rit.teho.repository.EquipmentTypeRepository;
import va.rit.teho.service.EquipmentService;

import java.util.List;
import java.util.Map;
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
    public Equipment getEquipment(Long equipmentId) {
        return equipmentRepository.findById(equipmentId).orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
    }

    @Override
    public Long add(String name, Long subTypeId) {
        String logLine = String.format("Добавление ВВСТ \"%s\", subTypeId = %d", name, subTypeId);
        LOGGER.debug(logLine);
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
    public Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> listGroupedByTypes() {
        return equipmentRepository.getEquipmentGroupedByType();
    }

    @Override
    public Map<EquipmentType, List<EquipmentSubType>> listTypesWithSubTypes() {
        return equipmentSubTypeRepository.findAllGroupedByType();
    }

    @Override
    public Pair<EquipmentType, List<EquipmentSubType>> getTypeWithSubTypes(Long typeId) {
        return Pair.of(getEquipmentTypeById(typeId),
                       equipmentSubTypeRepository.findByEquipmentTypeId(typeId));
    }

    private EquipmentType getEquipmentTypeById(Long typeId) {
        return equipmentTypeRepository
                .findById(typeId)
                .orElseThrow(() -> new NotFoundException("Тип ВВСТ не найден!"));
    }

    @Override
    public Long addType(String shortName, String longName) {
        String logLine = String.format("Добавление типа ВВСТ: \"%s\" (\"%s\")", shortName, longName);
        LOGGER.debug(logLine);
        EquipmentType equipmentType = new EquipmentType(shortName, longName);
        EquipmentType type = equipmentTypeRepository.save(equipmentType);
        return type.getId();
    }

    @Override
    public Long addSubType(Long typeId, String shortName, String fullName) {
        return equipmentSubTypeRepository
                .save(new EquipmentSubType(shortName, fullName, getEquipmentTypeById(typeId)))
                .getId();
    }


}
