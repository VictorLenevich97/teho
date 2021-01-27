package va.rit.teho.service.implementation.equipment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentLaborInputPerType;
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
    public Map<Equipment, Map<RepairType, Integer>> listWithLaborInputPerType(List<Long> ids,
                                                                              List<Long> typeIds) {
        List<Equipment> equipmentList = equipmentRepository.findFiltered(ids, typeIds);

        Map<RepairType, Integer> defaultLaborInputData =
                repairTypeService.list(true).stream().collect(Collectors.toMap(rt -> rt, rt -> 0));

        return equipmentList
                .stream()
                .collect(Collectors.toMap(e -> e,
                                          e -> e.getLaborInputPerTypes().isEmpty() ? defaultLaborInputData :
                                                  e.getLaborInputPerTypes()
                                                   .stream()
                                                   .collect(Collectors.toMap(
                                                           EquipmentLaborInputPerType::getRepairType,
                                                           EquipmentLaborInputPerType::getAmount))));
    }

    @Override
    public Equipment get(Long equipmentId) {
        return equipmentRepository.findById(equipmentId).orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
    }

    @Override
    public Equipment add(String name, Long typeId) {
        String logLine = String.format("Добавление ВВСТ \"%s\", typeId = %d", name, typeId);
        LOGGER.debug(logLine);
        EquipmentType equipmentType = equipmentTypeService.get(typeId);
        if (equipmentRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new AlreadyExistsException("ВВСТ", "имя", name);
        }
        long newId = equipmentRepository.getMaxId() + 1;
        return equipmentRepository.save(new Equipment(newId, name, equipmentType));
    }

    @Override
    @Transactional
    public Equipment add(String name,
                         Long typeId,
                         Map<Long, Integer> repairTypeIdLaborInputMap) {
        Equipment equipment = add(name, typeId);
        updateLaborInputData(repairTypeIdLaborInputMap, equipment);
        return equipment;
    }

    private void updateLaborInputData(Map<Long, Integer> repairTypeIdLaborInputMap, Equipment equipment) {
        List<EquipmentLaborInputPerType> equipmentLaborInputPerTypes = repairTypeIdLaborInputMap
                .entrySet()
                .stream()
                .map(repairTypeIdLaborInputEntry ->
                             equipmentLaborInputPerTypeRepository
                                     .findByEquipmentIdAndRepairTypeId(equipment.getId(),
                                                                       repairTypeIdLaborInputEntry.getKey())
                                     .map(eliptr -> eliptr.setAmount(repairTypeIdLaborInputEntry.getValue()))
                                     .orElse(new EquipmentLaborInputPerType(equipment.getId(),
                                                                            repairTypeIdLaborInputEntry.getKey(),
                                                                            repairTypeIdLaborInputEntry.getValue())))
                .collect(Collectors.toList());
        equipmentLaborInputPerTypeRepository.saveAll(equipmentLaborInputPerTypes);
    }

    @Override
    public Equipment update(Long id, String name, Long typeId, Map<Long, Integer> repairTypeIdLaborInputMap) {
        String logLine = String.format("Обновление ВВСТ (id = %d) \"%s\", typeId = %d", id, name, typeId);
        LOGGER.debug(logLine);
        equipmentRepository.findByNameIgnoreCase(name).ifPresent(e -> {
            if (!e.getId().equals(id)) {
                throw new AlreadyExistsException("ВВСТ", "имя", name);
            }
        });

        EquipmentType equipmentType = equipmentTypeService.get(typeId);
        Equipment equipment = get(id);
        equipment.setName(name);
        equipment.setEquipmentType(equipmentType);

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
    public Map<EquipmentType, List<Equipment>> listGroupedByTypes(List<Long> ids,
                                                                  List<Long> typeIds) {
        List<Equipment> equipmentList = equipmentRepository.findFiltered(ids, typeIds);
        Map<EquipmentType, List<Equipment>> result = new HashMap<>();
        for (Equipment equipment : equipmentList) {
            result.computeIfAbsent(equipment.getEquipmentType(), k -> new ArrayList<>()).add(equipment);
        }
        return result;
    }


}
