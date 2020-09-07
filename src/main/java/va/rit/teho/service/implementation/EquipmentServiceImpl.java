package va.rit.teho.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.EquipmentNotFoundException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.EquipmentRepository;
import va.rit.teho.repository.EquipmentSubTypeRepository;
import va.rit.teho.repository.EquipmentTypeRepository;
import va.rit.teho.service.EquipmentService;

import java.util.List;
import java.util.Map;

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
        EquipmentSubType equipmentSubType = equipmentSubTypeRepository
                .findById(subTypeId).orElseThrow(() -> new IncorrectParamException("subTypeId", subTypeId));
        if (equipmentRepository.findByName(name).isPresent()) {
            throw new AlreadyExistsException("ВВСТ", "имя", name);
        }
        Equipment s = new Equipment(name, equipmentSubType);
        Equipment saved = equipmentRepository.save(s);
        return saved.getId();
    }

    @Override
    public void update(Long id, String name, Long subTypeId) {
        String logLine = String.format("Обновление ВВСТ (id = %d) \"%s\", subTypeId = %d", id, name, subTypeId);
        LOGGER.debug(logLine);
        EquipmentSubType equipmentSubType =
                equipmentSubTypeRepository.findById(subTypeId)
                                          .orElseThrow(() -> new IncorrectParamException("subTypeId", subTypeId));
        Equipment equipment = getEquipment(id);
        equipment.setName(name);
        equipment.setEquipmentSubType(equipmentSubType);

        equipmentRepository.save(equipment);
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
    public Long addType(String shortName, String fullName) {
        String logLine = String.format("Добавление типа ВВСТ: \"%s\" (\"%s\")", shortName, fullName);
        LOGGER.debug(logLine);
        equipmentTypeRepository.findByFullName(fullName).ifPresent((et) -> {
            throw new AlreadyExistsException("Тип ВВСТ", "название", fullName);
        });
        EquipmentType equipmentType = new EquipmentType(shortName, fullName);
        EquipmentType type = equipmentTypeRepository.save(equipmentType);
        return type.getId();
    }

    @Override
    public void updateType(Long id, String shortName, String fullName) {
        String logLine = String.format("Обновление типа ВВСТ (id = %d): \"%s\" (\"%s\")", id, shortName, fullName);
        LOGGER.debug(logLine);
        EquipmentType equipmentType = equipmentTypeRepository.findById(id)
                                                             .orElseThrow(() -> new NotFoundException(
                                                                     "Тип ВВСТ с id = " + id + " не найден!"));
        equipmentType.setShortName(shortName);
        equipmentType.setFullName(fullName);
        equipmentTypeRepository.save(equipmentType);
    }

    @Override
    public Long addSubType(Long typeId, String shortName, String fullName) {
        if (!equipmentTypeRepository.findById(typeId).isPresent()) {
            throw new IncorrectParamException("typeId", typeId);
        }
        equipmentSubTypeRepository.findByFullName(fullName).ifPresent((et) -> {
            throw new AlreadyExistsException("Вид ВВСТ", "название", fullName);
        });
        return equipmentSubTypeRepository
                .save(new EquipmentSubType(shortName, fullName, getEquipmentTypeById(typeId)))
                .getId();
    }

    @Override
    public void updateSubType(Long id, Long typeId, String shortName, String fullName) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(typeId)
                                                             .orElseThrow(() -> new IncorrectParamException("typeId",
                                                                                                            typeId));
        EquipmentSubType equipmentSubType = equipmentSubTypeRepository.findById(id)
                                                                      .orElseThrow(() -> new NotFoundException(
                                                                              "Подтип ВВСТ с id = " + id + "  найден!"));

        equipmentSubType.setEquipmentType(equipmentType);
        equipmentSubType.setShortName(shortName);
        equipmentSubType.setFullName(fullName);
        equipmentSubTypeRepository.save(equipmentSubType);
    }


}
