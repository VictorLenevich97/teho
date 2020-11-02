package va.rit.teho.service.implementation.equipment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.equipment.EquipmentSubTypeRepository;
import va.rit.teho.repository.equipment.EquipmentTypeRepository;
import va.rit.teho.service.equipment.EquipmentTypeService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class EquipmentTypeServiceImpl implements EquipmentTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentTypeServiceImpl.class);

    private final EquipmentTypeRepository equipmentTypeRepository;
    private final EquipmentSubTypeRepository equipmentSubTypeRepository;

    public EquipmentTypeServiceImpl(EquipmentTypeRepository equipmentTypeRepository,
                                    EquipmentSubTypeRepository equipmentSubTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.equipmentSubTypeRepository = equipmentSubTypeRepository;
    }

    @Override
    public List<EquipmentType> listTypes(List<Long> typeIds) {
        Iterable<EquipmentType> result;
        if (typeIds.isEmpty()) {
            result = equipmentTypeRepository.findAll();
        } else {
            result = equipmentTypeRepository.findAllById(typeIds);
        }
        return (List<EquipmentType>) result;
    }

    @Override
    public Map<EquipmentType, List<EquipmentSubType>> listTypesWithSubTypes(List<Long> typeIds, List<Long> subTypeIds) {
        List<EquipmentSubType> equipmentSubTypes = equipmentSubTypeRepository.findByIds(subTypeIds, typeIds);
        return equipmentSubTypes.stream().collect(Collectors.groupingBy(EquipmentSubType::getEquipmentType));
    }

    @Override
    public Pair<EquipmentType, List<EquipmentSubType>> getTypeWithSubTypes(Long typeId) {
        return Pair.of(getEquipmentTypeById(typeId), equipmentSubTypeRepository.findByEquipmentTypeId(typeId));
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
        equipmentTypeRepository.findByFullName(fullName).ifPresent(et -> {
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
        equipmentSubTypeRepository.findByFullName(fullName).ifPresent(et -> {
            throw new AlreadyExistsException("Вид ВВСТ", "название", fullName);
        });
        return equipmentSubTypeRepository
                .save(new EquipmentSubType(shortName, fullName, getEquipmentTypeById(typeId)))
                .getId();
    }

    @Override
    public EquipmentSubType getSubType(Long subTypeId) {
        return equipmentSubTypeRepository.findById(subTypeId).orElseThrow(() -> new IncorrectParamException("subTypeId",
                                                                                                            subTypeId));
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
