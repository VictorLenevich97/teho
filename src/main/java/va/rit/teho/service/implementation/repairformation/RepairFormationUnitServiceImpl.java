package va.rit.teho.service.implementation.repairformation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairStationType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairformation.RepairFormationUnitEquipmentStaffRepository;
import va.rit.teho.repository.repairformation.RepairFormationUnitRepository;
import va.rit.teho.repository.repairformation.RepairStationTypeRepository;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;
import va.rit.teho.service.repairformation.RepairFormationTypeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RepairFormationUnitServiceImpl implements RepairFormationUnitService {

    private final RepairFormationUnitEquipmentStaffRepository repairFormationUnitEquipmentStaffRepository;
    private final RepairFormationUnitRepository repairFormationUnitRepository;
    private final RepairStationTypeRepository repairStationTypeRepository;

    private final EquipmentTypeService equipmentTypeService;

    public RepairFormationUnitServiceImpl(
            RepairFormationUnitEquipmentStaffRepository repairFormationUnitEquipmentStaffRepository,
            RepairFormationUnitRepository repairFormationUnitRepository,
            RepairStationTypeRepository repairStationTypeRepository,
            EquipmentTypeService equipmentTypeService) {
        this.repairFormationUnitEquipmentStaffRepository = repairFormationUnitEquipmentStaffRepository;
        this.repairFormationUnitRepository = repairFormationUnitRepository;
        this.repairStationTypeRepository = repairStationTypeRepository;
        this.equipmentTypeService = equipmentTypeService;
    }

    @Override
    public List<RepairFormationUnit> list(List<Long> filterIds, Integer pageNum, Integer pageSize) {
        return repairFormationUnitRepository.findSorted(filterIds, PageRequest.of(pageNum - 1, pageSize));
    }

    @Override
    public List<RepairFormationUnit> list(Long repairFormationId,
                                          List<Long> filterIds,
                                          Integer pageNum,
                                          Integer pageSize) {
        return repairFormationUnitRepository.findSorted(repairFormationId,
                                                        filterIds,
                                                        PageRequest.of(pageNum - 1, pageSize));
    }

    @Override
    public Pair<RepairFormationUnit, List<RepairFormationUnitEquipmentStaff>> getWithStaff(Long repairFormationUntId,
                                                                                           UUID sessionId) {
        return Pair.of(getRepairStationOrThrow(repairFormationUntId),
                       repairFormationUnitEquipmentStaffRepository.findAllByRepairFormationUnitIdAndTehoSessionId(
                               repairFormationUntId, sessionId));
    }

    @Override
    public Long add(String name, Long repairFormationId, Long stationTypeId, int amount) {
        RepairStationType repairStationType = repairStationTypeRepository
                .findById(stationTypeId)
                .orElseThrow(() -> new NotFoundException(""));
        repairStationTypeRepository.findByName(name).ifPresent(repairStation -> {
            throw new AlreadyExistsException("РВО", "название", name);
        });
        RepairFormationUnit repairFormationUnit = new RepairFormationUnit(name,
                                                                          repairStationType,
                                                                          amount);
        return repairFormationUnitRepository.save(repairFormationUnit).getId();
    }

    @Override
    public void update(Long id, String name, Long repairFormationId, Long stationTypeId, int amount) {
        RepairFormationUnit repairFormationUnit = getRepairStationOrThrow(id);
        RepairStationType repairStationType = repairStationTypeRepository
                .findById(stationTypeId)
                .orElseThrow(() -> new NotFoundException(""));

        repairFormationUnit.setRepairStationType(repairStationType);
        repairFormationUnit.setName(name);
        repairFormationUnit.setStationAmount(amount);

        repairFormationUnitRepository.save(repairFormationUnit);
    }

    private RepairFormationUnit getRepairStationOrThrow(Long id) {
        return repairFormationUnitRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("РВО с id = " + id + " не найден!"));
    }

    private void checkEquipmentStaffPreconditions(RepairFormationUnitEquipmentStaff repairFormationUnitEquipmentStaff) {
        getRepairStationOrThrow(repairFormationUnitEquipmentStaff
                                        .getEquipmentPerRepairFormationUnit()
                                        .getRepairFormationUnitId());
        equipmentTypeService.getSubType(repairFormationUnitEquipmentStaff
                                                .getEquipmentPerRepairFormationUnit()
                                                .getEquipmentSubTypeId()); //Проверка на существование
        if (repairFormationUnitEquipmentStaff.getTotalStaff() < repairFormationUnitEquipmentStaff.getAvailableStaff()) {
            throw new IncorrectParamException(
                    "Всего производственников < доступно производственников (" + repairFormationUnitEquipmentStaff.getTotalStaff() + " < " + repairFormationUnitEquipmentStaff
                            .getAvailableStaff() + ")");
        }
    }

    @Override
    public void updateEquipmentStaff(List<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffList) {
        repairFormationUnitEquipmentStaffList.forEach(this::checkEquipmentStaffPreconditions);

        repairFormationUnitEquipmentStaffRepository.saveAll(repairFormationUnitEquipmentStaffList);
    }

    @Override
    public Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> getWithEquipmentStaffGrouped(
            UUID sessionId,
            List<Long> repairFormationUnitIds,
            List<Long> equipmentTypeIds,
            List<Long> equipmentSubTypeIds) {
        List<RepairFormationUnitEquipmentStaff> equipmentStaffList =
                repairFormationUnitEquipmentStaffRepository.findFiltered(sessionId,
                                                                         repairFormationUnitIds,
                                                                         equipmentTypeIds,
                                                                         equipmentSubTypeIds);

        Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> result = new HashMap<>();
        for (RepairFormationUnitEquipmentStaff repairFormationUnitEquipmentStaff : equipmentStaffList) {
            RepairFormationUnit repairFormationUnit = repairFormationUnitEquipmentStaff.getRepairFormationUnit();
            result.computeIfAbsent(repairFormationUnit, rs -> new HashMap<>());
            result.get(repairFormationUnit).put(repairFormationUnitEquipmentStaff.getEquipmentSubType(),
                                                repairFormationUnitEquipmentStaff);
        }
        return result;
    }

    @Override
    public List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(Long repairFormationUnitId,
                                                                      UUID sessionId) {
        return repairFormationUnitEquipmentStaffRepository.findAllByRepairFormationUnitIdAndTehoSessionId(
                repairFormationUnitId, sessionId);
    }

    @Override
    public List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(UUID sessionId) {
        return repairFormationUnitEquipmentStaffRepository.findAllByTehoSessionId(sessionId);
    }

    @Override
    public void copyEquipmentStaff(UUID originalSessionId, UUID newSessionId) {
        List<RepairFormationUnitEquipmentStaff> equipmentStaffList =
                repairFormationUnitEquipmentStaffRepository.findFiltered(originalSessionId,
                                                                         null,
                                                                         null,
                                                                         null);
        List<RepairFormationUnitEquipmentStaff> updatedRepairFormationUnitEquipmentStaffList =
                equipmentStaffList.stream().map(rses -> rses.copy(newSessionId)).collect(Collectors.toList());

        repairFormationUnitEquipmentStaffRepository.saveAll(updatedRepairFormationUnitEquipmentStaffList);
    }

}
