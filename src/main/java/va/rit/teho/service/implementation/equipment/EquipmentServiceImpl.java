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
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.equipment.EquipmentTypeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final EquipmentTypeService equipmentTypeService;

    private final EquipmentRepository equipmentRepository;
    private final EquipmentLaborInputPerTypeRepository equipmentLaborInputPerTypeRepository;

    public EquipmentServiceImpl(EquipmentTypeService equipmentTypeService,
                                EquipmentRepository equipmentRepository,
                                EquipmentLaborInputPerTypeRepository equipmentLaborInputPerTypeRepository) {
        this.equipmentTypeService = equipmentTypeService;
        this.equipmentRepository = equipmentRepository;
        this.equipmentLaborInputPerTypeRepository = equipmentLaborInputPerTypeRepository;
    }

    public List<Equipment> list() {
        return (List<Equipment>) equipmentRepository.findAll();
    }

    @Override
    public Map<Equipment, Map<RepairType, Integer>> listWithLaborInputPerType() {
        List<EquipmentLaborInputPerType> laborInputPerTypeList =
                (List<EquipmentLaborInputPerType>) equipmentLaborInputPerTypeRepository.findAll();
        Map<Equipment, Map<RepairType, Integer>> result = new HashMap<>();
        for (EquipmentLaborInputPerType equipmentLaborInputPerType : laborInputPerTypeList) {
            result.computeIfAbsent(equipmentLaborInputPerType.getEquipment(), e -> new HashMap<>())
                  .put(equipmentLaborInputPerType.getRepairType(), equipmentLaborInputPerType.getAmount());
        }
        return result;
    }

    @Override
    public Equipment get(Long equipmentId) {
        return equipmentRepository.findById(equipmentId).orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
    }

    @Override
    public Long add(String name, Long subTypeId) {
        String logLine = String.format("Добавление ВВСТ \"%s\", subTypeId = %d", name, subTypeId);
        LOGGER.debug(logLine);
        EquipmentSubType equipmentSubType = equipmentTypeService.getSubType(subTypeId);
        if (equipmentRepository.findByName(name).isPresent()) {
            throw new AlreadyExistsException("ВВСТ", "имя", name);
        }
        long newId = equipmentRepository.getMaxId() + 1;
        equipmentRepository.save(new Equipment(newId, name, equipmentSubType));
        return newId;
    }

    @Override
    public void update(Long id, String name, Long subTypeId) {
        String logLine = String.format("Обновление ВВСТ (id = %d) \"%s\", subTypeId = %d", id, name, subTypeId);
        LOGGER.debug(logLine);
        EquipmentSubType equipmentSubType = equipmentTypeService.getSubType(subTypeId);
        Equipment equipment = get(id);
        equipment.setName(name);
        equipment.setEquipmentSubType(equipmentSubType);

        equipmentRepository.save(equipment);
    }


    @Override
    public Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> listGroupedByTypes(List<Long> ids,
                                                                                         List<Long> subTypeIds,
                                                                                         List<Long> typeIds) {
        List<Equipment> equipmentList = equipmentRepository.findFiltered(ids, subTypeIds, typeIds);
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> result = new HashMap<>();
        for (Equipment equipment : equipmentList) {
            result
                    .computeIfAbsent(equipment.getEquipmentSubType().getEquipmentType(), (k) -> new HashMap<>())
                    .computeIfAbsent(equipment.getEquipmentSubType(), (k) -> new ArrayList<>())
                    .add(equipment);
        }
        return result;
    }


}
