package va.rit.teho.service.implementation.equipment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.EquipmentNotFoundException;
import va.rit.teho.repository.equipment.EquipmentLaborInputPerTypeRepository;
import va.rit.teho.repository.equipment.EquipmentRepository;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.equipment.EquipmentTypeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentTypeService equipmentTypeService;
    private final RepairTypeService repairTypeService;

    private final EquipmentRepository equipmentRepository;
    private final EquipmentLaborInputPerTypeRepository equipmentLaborInputPerTypeRepository;

    public EquipmentServiceImpl(EquipmentTypeService equipmentTypeService,
                                RepairTypeService repairTypeService,
                                EquipmentRepository equipmentRepository,
                                EquipmentLaborInputPerTypeRepository equipmentLaborInputPerTypeRepository) {
        this.equipmentTypeService = equipmentTypeService;
        this.repairTypeService = repairTypeService;
        this.equipmentRepository = equipmentRepository;
        this.equipmentLaborInputPerTypeRepository = equipmentLaborInputPerTypeRepository;
    }

    public List<Equipment> list() {
        return (List<Equipment>) equipmentRepository.findAll();
    }

    @Override
    public Map<Equipment, Map<RepairType, Integer>> listWithLaborInputPerType() {
        //TODO: LEFT OUTER JOIN и дополнить
        List<Equipment> equipmentList = list();
        List<RepairType> repairTypes = repairTypeService.list(true);
        List<EquipmentLaborInputPerType> laborInputPerTypeList =
                (List<EquipmentLaborInputPerType>) equipmentLaborInputPerTypeRepository.findAll();
        Map<Equipment, Map<RepairType, Integer>> grouped = new HashMap<>();
        for (EquipmentLaborInputPerType equipmentLaborInputPerType : laborInputPerTypeList) {
            grouped.computeIfAbsent(equipmentLaborInputPerType.getEquipment(), e -> new HashMap<>())
                   .put(equipmentLaborInputPerType.getRepairType(), equipmentLaborInputPerType.getAmount());
        }
        Map<RepairType, Integer> defaultLaborInputData =
                repairTypes.stream().collect(Collectors.groupingBy(rt -> rt, Collectors.summingInt(rt -> 0)));
        Map<Equipment, Map<RepairType, Integer>> result = new HashMap<>();
        for (Equipment equipment : equipmentList) {
            result.put(equipment, grouped.getOrDefault(equipment, defaultLaborInputData));
        }
        return result;
    }

    @Override
    public Equipment get(Long equipmentId) {
        return equipmentRepository.findById(equipmentId).orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
    }

    @Override
    public Equipment add(String name, Long subTypeId) {
        String logLine = String.format("Добавление ВВСТ \"%s\", subTypeId = %d", name, subTypeId);
        LOGGER.debug(logLine);
        EquipmentSubType equipmentSubType = equipmentTypeService.getSubType(subTypeId);
        if (equipmentRepository.findByName(name).isPresent()) {
            throw new AlreadyExistsException("ВВСТ", "имя", name);
        }
        long newId = equipmentRepository.getMaxId() + 1;
        return equipmentRepository.save(new Equipment(newId, name, equipmentSubType));
    }

    @Override
    @Transactional
    public Equipment add(String name,
                         Long subTypeId,
                         Map<Long, Integer> repairTypeIdLaborInputMap) {
        Equipment equipment = add(name, subTypeId);
        updateLaborInputData(repairTypeIdLaborInputMap, equipment);
        return equipment;
    }

    private void updateLaborInputData(Map<Long, Integer> repairTypeIdLaborInputMap, Equipment equipment) {
        List<EquipmentLaborInputPerType> equipmentLaborInputPerTypes = repairTypeIdLaborInputMap
                .entrySet()
                .stream()
                .map(repairTypeIdLaborInputEntry ->
                             new EquipmentLaborInputPerType(equipment.getId(),
                                                            repairTypeIdLaborInputEntry.getKey(),
                                                            repairTypeIdLaborInputEntry.getValue()))
                .collect(Collectors.toList());
        equipmentLaborInputPerTypeRepository.saveAll(equipmentLaborInputPerTypes);
    }

    @Override
    public Equipment update(Long id, String name, Long subTypeId, Map<Long, Integer> repairTypeIdLaborInputMap) {
        String logLine = String.format("Обновление ВВСТ (id = %d) \"%s\", subTypeId = %d", id, name, subTypeId);
        LOGGER.debug(logLine);
        equipmentRepository.findByName(name).ifPresent(e -> {
            if (!e.getId().equals(id)) {
                throw new AlreadyExistsException("ВВСТ", "имя", name);
            }
        });
        EquipmentSubType equipmentSubType = equipmentTypeService.getSubType(subTypeId);
        Equipment equipment = get(id);
        equipment.setName(name);
        equipment.setEquipmentSubType(equipmentSubType);

        equipmentRepository.save(equipment);
        updateLaborInputData(repairTypeIdLaborInputMap, equipment);
        return equipment;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        equipmentRepository.deleteById(id);
    }

    @Override
    public Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> listGroupedByTypes(List<Long> ids,
                                                                                         List<Long> subTypeIds,
                                                                                         List<Long> typeIds) {
        List<Equipment> equipmentList = equipmentRepository.findFiltered(ids, subTypeIds, typeIds);
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> result = new HashMap<>();
        for (Equipment equipment : equipmentList) {
            result
                    .computeIfAbsent(equipment.getEquipmentSubType().getEquipmentType(), k -> new HashMap<>())
                    .computeIfAbsent(equipment.getEquipmentSubType(), k -> new ArrayList<>())
                    .add(equipment);
        }
        return result;
    }


}
