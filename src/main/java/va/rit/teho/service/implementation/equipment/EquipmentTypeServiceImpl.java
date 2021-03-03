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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EquipmentTypeServiceImpl implements EquipmentTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentTypeServiceImpl.class);

    private final EquipmentTypeRepository equipmentTypeRepository;

    public EquipmentTypeServiceImpl(EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    @Override
    @Transactional
    public EquipmentType get(Long id) {
        return equipmentTypeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Тип ВВСТ не найден!"));
    }

    @Override
    @Transactional
    public List<EquipmentType> listTypes(List<Long> typeIds) {
        return (List<EquipmentType>) Optional.ofNullable(typeIds)
                                             .filter(l -> !l.isEmpty())
                                             .map(equipmentTypeRepository::findAllById)
                                             .orElse(equipmentTypeRepository.findAll());
    }

    @Override
    @Transactional
    public List<EquipmentType> listHighestLevelTypes(List<Long> typeIds) {
        return Optional.ofNullable(typeIds)
                       .filter(l -> !l.isEmpty())
                       .map(equipmentTypeRepository::findEquipmentTypeByParentTypeIsNullAndIdIn)
                       .orElse(equipmentTypeRepository.findEquipmentTypeByParentTypeIsNull())
                       .stream()
                       .sorted(Comparator.comparing(EquipmentType::getId))
                       .collect(Collectors.toList());
    }

    @Override
    public EquipmentType addType(String shortName, String fullName) {
        logEquipmentAdd(shortName, fullName, null);
        equipmentTypeRepository.findByFullName(fullName).ifPresent(et -> {
            throw new AlreadyExistsException("Тип ВВСТ", "название", fullName);
        });
        long newId = equipmentTypeRepository.getMaxId() + 1;
        EquipmentType equipmentType = new EquipmentType(newId, shortName, fullName);
        return equipmentTypeRepository.save(equipmentType);
    }

    @Override
    public EquipmentType addType(Long parentTypeId, String shortName, String fullName) {
        logEquipmentAdd(shortName, fullName, parentTypeId);

        equipmentTypeRepository.findByFullName(fullName).ifPresent(et -> {
            throw new AlreadyExistsException("Тип ВВСТ", "название", fullName);
        });

        long newId = equipmentTypeRepository.getMaxId() + 1;

        EquipmentType parentType = get(parentTypeId);
        EquipmentType equipmentType = new EquipmentType(newId, shortName, fullName, parentType);
        return equipmentTypeRepository.save(equipmentType);
    }

    @Override
    public EquipmentType updateType(Long id, String shortName, String fullName) {
        logEquipmentUpdate(id, shortName, fullName);

        EquipmentType equipmentType = get(id);

        equipmentTypeRepository.findByFullName(fullName).ifPresent(et -> {
            if (!et.getId().equals(id)) {
                throw new AlreadyExistsException("Тип ВВСТ", "название", fullName);
            }
        });

        equipmentType.setShortName(shortName);
        equipmentType.setFullName(fullName);
        return equipmentTypeRepository.save(equipmentType);
    }

    private void logEquipmentAdd(String shortName, String fullName, Long parentId) {
        String formatted = String.format("Добавление типа ВВСТ: \"%s\" (\"%s\", родительский тип: " + (parentId == null ? "отсутствует" : parentId),
                                         shortName,
                                         fullName,
                                         parentId);
        LOGGER.debug(formatted);
    }

    private void logEquipmentUpdate(Long id, String shortName, String fullName) {
        String formatted = String.format("Обновление типа ВВСТ (id = %d): \"%s\" (\"%s\")",
                                         id,
                                         shortName,
                                         fullName);
        LOGGER.debug(formatted);
    }

    @Override
    public EquipmentType updateType(Long id, Long parentTypeId, String shortName, String fullName) {
        logEquipmentUpdate(id, shortName, fullName);

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
