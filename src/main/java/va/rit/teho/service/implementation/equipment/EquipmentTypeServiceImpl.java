package va.rit.teho.service.implementation.equipment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.equipment.EquipmentTypeRepository;
import va.rit.teho.service.equipment.EquipmentTypeService;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class EquipmentTypeServiceImpl implements EquipmentTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentTypeServiceImpl.class);

    private final EquipmentTypeRepository equipmentTypeRepository;

    public EquipmentTypeServiceImpl(EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    @Override
    public EquipmentType get(Long id) {
        return equipmentTypeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Тип ВВСТ не найден!"));
    }

    @Override
    public List<EquipmentType> listTypes(List<Long> typeIds) {
        Iterable<EquipmentType> result;
        if (typeIds == null || typeIds.isEmpty()) {
            result = equipmentTypeRepository.findAll();
        } else {
            result = equipmentTypeRepository.findAllById(typeIds);
        }
        return (List<EquipmentType>) result;
    }

    @Override
    public List<EquipmentType> listHighestLevelTypes(List<Long> typeIds) {
        return equipmentTypeRepository.findEquipmentTypeByParentTypeIsNullAndIdIn(typeIds == null ? Collections.emptyList() : typeIds);
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
    public EquipmentType addType(Long parentTypeId, String shortName, String fullName) {
        String logLine = String.format("Добавление типа ВВСТ: \"%s\" (\"%s\")", shortName, fullName);
        LOGGER.debug(logLine);

        equipmentTypeRepository.findByFullName(fullName).ifPresent(et -> {
            throw new AlreadyExistsException("Тип ВВСТ", "название", fullName);
        });

        EquipmentType parentType = get(parentTypeId);
        EquipmentType equipmentType = new EquipmentType(shortName, fullName, parentType);
        return equipmentTypeRepository.save(equipmentType);
    }

    @Override
    public EquipmentType updateType(Long id, String shortName, String fullName) {
        String logLine = String.format("Обновление типа ВВСТ (id = %d): \"%s\" (\"%s\")", id, shortName, fullName);
        LOGGER.debug(logLine);

        equipmentTypeRepository.findByFullName(fullName).ifPresent(et -> {
            if (!et.getId().equals(id)) {
                throw new AlreadyExistsException("Тип ВВСТ", "название", fullName);
            }
        });

        EquipmentType equipmentType = get(id);
        equipmentType.setShortName(shortName);
        equipmentType.setFullName(fullName);
        return equipmentTypeRepository.save(equipmentType);
    }

    @Override
    public EquipmentType updateType(Long id, Long parentTypeId, String shortName, String fullName) {
        String logLine = String.format("Обновление типа ВВСТ (id = %d): \"%s\" (\"%s\")", id, shortName, fullName);
        LOGGER.debug(logLine);

        equipmentTypeRepository.findByFullName(fullName).ifPresent(et -> {
            if (!et.getId().equals(id)) {
                throw new AlreadyExistsException("Тип ВВСТ", "название", fullName);
            }
        });

        EquipmentType equipmentType = get(id);
        EquipmentType parentType = get(parentTypeId);
        equipmentType.setShortName(shortName);
        equipmentType.setFullName(fullName);
        equipmentType.setParentType(parentType);
        return equipmentTypeRepository.save(equipmentType);
    }

    @Override
    public EquipmentType deleteType(Long id) {
        EquipmentType equipmentType = get(id);
        if (!equipmentType.getEquipmentTypes().isEmpty()) {
            throw new IncorrectParamException("У типа ВВСТ существуют подтипы, удаление невозможно!");
        }
        equipmentTypeRepository.deleteById(id);
        return equipmentType;
    }

}
