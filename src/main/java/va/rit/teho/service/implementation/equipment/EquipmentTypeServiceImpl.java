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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public List<EquipmentSubType> listSubTypes(List<Long> typeIds) {
        return equipmentSubTypeRepository.findByIds(null, typeIds);
    }

    @Override
    public Map<EquipmentType, List<EquipmentSubType>> listTypesWithSubTypes(List<Long> typeIds, List<Long> subTypeIds) {
        List<EquipmentSubType> equipmentSubTypes = equipmentSubTypeRepository.findByIds(subTypeIds, typeIds);
        Map<EquipmentType, List<EquipmentSubType>> map = new LinkedHashMap<>();
        for (EquipmentSubType equipmentSubType : equipmentSubTypes) {
            map.computeIfAbsent(equipmentSubType.getEquipmentType(), k -> new ArrayList<>()).add(equipmentSubType);
        }
        return map;
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
    public EquipmentType addType(String shortName, String fullName) {
        String logLine = String.format("Добавление типа ВВСТ: \"%s\" (\"%s\")", shortName, fullName);
        LOGGER.debug(logLine);
        equipmentTypeRepository.findByFullName(fullName).ifPresent(et -> {
            throw new AlreadyExistsException("Тип ВВСТ", "название", fullName);
        });
        EquipmentType equipmentType = new EquipmentType(shortName, fullName);
        return equipmentTypeRepository.save(equipmentType);
    }

    @Override
    public EquipmentType updateType(Long id, String shortName, String fullName) {
        String logLine = String.format("Обновление типа ВВСТ (id = %d): \"%s\" (\"%s\")", id, shortName, fullName);
        LOGGER.debug(logLine);
        EquipmentType equipmentType = equipmentTypeRepository.findById(id)
                                                             .orElseThrow(() -> new NotFoundException(
                                                                     "Тип ВВСТ с id = " + id + " не найден!"));
        equipmentType.setShortName(shortName);
        equipmentType.setFullName(fullName);
        return equipmentTypeRepository.save(equipmentType);
    }

    @Override
    public EquipmentType deleteType(Long id) {
        Pair<EquipmentType, List<EquipmentSubType>> typeWithSubTypes = getTypeWithSubTypes(id);
        if (!typeWithSubTypes.getSecond().isEmpty()) {
            throw new IncorrectParamException("У типа ВВСТ существуют подтипы, удаление невозможно!");
        }
        equipmentTypeRepository.deleteById(id);
        return typeWithSubTypes.getFirst();
    }

    @Override
    public EquipmentSubType addSubType(Long typeId, String shortName, String fullName) {
        if (!equipmentTypeRepository.findById(typeId).isPresent()) {
            throw new IncorrectParamException("typeId", typeId);
        }
        equipmentSubTypeRepository.findByFullName(fullName).ifPresent(et -> {
            throw new AlreadyExistsException("Вид ВВСТ", "название", fullName);
        });
        return equipmentSubTypeRepository
                .save(new EquipmentSubType(shortName, fullName, getEquipmentTypeById(typeId)));
    }

    @Override
    public EquipmentSubType getSubType(Long subTypeId) {
        return equipmentSubTypeRepository.findById(subTypeId).orElseThrow(() -> new IncorrectParamException("subTypeId",
                                                                                                            subTypeId));
    }

    @Override
    public EquipmentSubType updateSubType(Long id, Long typeId, String shortName, String fullName) {
        EquipmentType equipmentType = equipmentTypeRepository.findById(typeId)
                                                             .orElseThrow(() -> new IncorrectParamException("typeId",
                                                                                                            typeId));
        EquipmentSubType equipmentSubType = equipmentSubTypeRepository.findById(id)
                                                                      .orElseThrow(() -> new NotFoundException(
                                                                              "Подтип ВВСТ с id = " + id + "  найден!"));

        equipmentSubType.setEquipmentType(equipmentType);
        equipmentSubType.setShortName(shortName);
        equipmentSubType.setFullName(fullName);
        return equipmentSubTypeRepository.save(equipmentSubType);
    }

    @Override
    public void deleteSubType(Long id) {
        equipmentSubTypeRepository.deleteById(id);
    }
}
