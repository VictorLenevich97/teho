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
import va.rit.teho.repository.EquipmentRepository;
import va.rit.teho.repository.EquipmentSubTypeRepository;
import va.rit.teho.service.EquipmentService;

import java.util.*;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentRepository equipmentRepository;
    private final EquipmentSubTypeRepository equipmentSubTypeRepository;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository,
                                EquipmentSubTypeRepository equipmentSubTypeRepository) {
        this.equipmentRepository = equipmentRepository;
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
    public Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> listGroupedByTypes(List<Long> ids,
                                                                                         List<Long> subTypeIds,
                                                                                         List<Long> typeIds) {
        List<Equipment> equipmentList = equipmentRepository.findFiltered(ids, subTypeIds, typeIds);
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> result =
                new TreeMap<>(Comparator.comparing(EquipmentType::getId));
        for (Equipment equipment : equipmentList) {
            result
                    .computeIfAbsent(equipment.getEquipmentSubType().getEquipmentType(),
                                     (k) -> new TreeMap<>(Comparator.comparing(EquipmentSubType::getId)))
                    .computeIfAbsent(equipment.getEquipmentSubType(), (k) -> new ArrayList<>())
                    .add(equipment);
        }
        return result;
    }


}
