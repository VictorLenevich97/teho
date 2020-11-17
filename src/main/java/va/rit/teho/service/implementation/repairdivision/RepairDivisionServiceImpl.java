package va.rit.teho.service.implementation.repairdivision;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.repairdivision.RepairDivisionUnit;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitEquipmentStaff;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitType;
import va.rit.teho.entity.repairdivision.RepairStationType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairdivision.RepairDivisionUnitEquipmentStaffRepository;
import va.rit.teho.repository.repairdivision.RepairDivisionUnitRepository;
import va.rit.teho.repository.repairdivision.RepairStationTypeRepository;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.repairdivision.RepairDivisionService;
import va.rit.teho.service.repairdivision.RepairDivisionUnitTypeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RepairDivisionServiceImpl implements RepairDivisionService {

    private final RepairDivisionUnitEquipmentStaffRepository repairDivisionUnitEquipmentStaffRepository;
    private final RepairDivisionUnitRepository repairDivisionUnitRepository;
    private final RepairStationTypeRepository repairStationTypeRepository;

    private final RepairDivisionUnitTypeService repairDivisionUnitTypeService;
    private final EquipmentTypeService equipmentTypeService;

    public RepairDivisionServiceImpl(
            RepairDivisionUnitEquipmentStaffRepository repairDivisionUnitEquipmentStaffRepository,
            RepairDivisionUnitRepository repairDivisionUnitRepository,
            RepairStationTypeRepository repairStationTypeRepository,
            RepairDivisionUnitTypeService repairDivisionUnitTypeService,
            EquipmentTypeService equipmentTypeService) {
        this.repairDivisionUnitEquipmentStaffRepository = repairDivisionUnitEquipmentStaffRepository;
        this.repairDivisionUnitRepository = repairDivisionUnitRepository;
        this.repairStationTypeRepository = repairStationTypeRepository;
        this.repairDivisionUnitTypeService = repairDivisionUnitTypeService;
        this.equipmentTypeService = equipmentTypeService;
    }

    @Override
    public List<RepairDivisionUnit> listUnits(List<Long> filterIds) {
        return repairDivisionUnitRepository.findSorted(filterIds);
    }

    @Override
    public Pair<RepairDivisionUnit, List<RepairDivisionUnitEquipmentStaff>> getUnitWithStaff(Long repairDivisionUntId,
                                                                                             UUID sessionId) {
        return Pair.of(getRepairStationOrThrow(repairDivisionUntId),
                       repairDivisionUnitEquipmentStaffRepository.findAllByRepairDivisionUnitIdAndTehoSessionId(
                               repairDivisionUntId, sessionId));
    }

    @Override
    public Long addUnit(String name, Long typeId, Long stationTypeId, int amount) {
        RepairDivisionUnitType repairDivisionUnitType = repairDivisionUnitTypeService.get(typeId);
        RepairStationType repairStationType = repairStationTypeRepository
                .findById(stationTypeId)
                .orElseThrow(() -> new NotFoundException(""));
        repairStationTypeRepository.findByName(name).ifPresent(repairStation -> {
            throw new AlreadyExistsException("РВО", "название", name);
        });
        RepairDivisionUnit repairDivisionUnit = new RepairDivisionUnit(name,
                                                                       repairStationType,
                                                                       repairDivisionUnitType,
                                                                       amount);
        return repairDivisionUnitRepository.save(repairDivisionUnit).getId();
    }

    @Override
    public void updateUnit(Long id, String name, Long typeId, Long stationTypeId, int amount) {
        RepairDivisionUnit repairDivisionUnit = getRepairStationOrThrow(id);
        RepairDivisionUnitType repairDivisionUnitType = repairDivisionUnitTypeService.get(typeId);
        RepairStationType repairStationType = repairStationTypeRepository
                .findById(stationTypeId)
                .orElseThrow(() -> new NotFoundException(""));

        repairDivisionUnit.setRepairStationType(repairStationType);
        repairDivisionUnit.setRepairDivisionUnitType(repairDivisionUnitType);
        repairDivisionUnit.setName(name);
        repairDivisionUnit.setStationAmount(amount);

        repairDivisionUnitRepository.save(repairDivisionUnit);
    }

    private RepairDivisionUnit getRepairStationOrThrow(Long id) {
        return repairDivisionUnitRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("РВО с id = " + id + " не найден!"));
    }

    private void checkEquipmentStaffPreconditions(RepairDivisionUnitEquipmentStaff repairDivisionUnitEquipmentStaff) {
        getRepairStationOrThrow(repairDivisionUnitEquipmentStaff
                                        .getEquipmentPerRepairDivisionUnit()
                                        .getRepairDivisionUnitId());
        equipmentTypeService.getSubType(repairDivisionUnitEquipmentStaff
                                                .getEquipmentPerRepairDivisionUnit()
                                                .getEquipmentSubTypeId()); //Проверка на существование
        if (repairDivisionUnitEquipmentStaff.getTotalStaff() < repairDivisionUnitEquipmentStaff.getAvailableStaff()) {
            throw new IncorrectParamException(
                    "Всего производственников < доступно производственников (" + repairDivisionUnitEquipmentStaff.getTotalStaff() + " < " + repairDivisionUnitEquipmentStaff
                            .getAvailableStaff() + ")");
        }
    }

    @Override
    public void updateUnitEquipmentStaff(List<RepairDivisionUnitEquipmentStaff> repairDivisionUnitEquipmentStaffList) {
        repairDivisionUnitEquipmentStaffList.forEach(this::checkEquipmentStaffPreconditions);

        repairDivisionUnitEquipmentStaffRepository.saveAll(repairDivisionUnitEquipmentStaffList);
    }

    @Override
    public Map<RepairDivisionUnit, Map<EquipmentSubType, RepairDivisionUnitEquipmentStaff>> getRepairDivisionUnitEquipmentStaffGrouped(
            UUID sessionId,
            List<Long> repairDivisionUnitIds,
            List<Long> equipmentTypeIds,
            List<Long> equipmentSubTypeIds) {
        List<RepairDivisionUnitEquipmentStaff> equipmentStaffList =
                repairDivisionUnitEquipmentStaffRepository.findFiltered(sessionId,
                                                                        repairDivisionUnitIds,
                                                                        equipmentTypeIds,
                                                                        equipmentSubTypeIds);

        Map<RepairDivisionUnit, Map<EquipmentSubType, RepairDivisionUnitEquipmentStaff>> result = new HashMap<>();
        for (RepairDivisionUnitEquipmentStaff repairDivisionUnitEquipmentStaff : equipmentStaffList) {
            RepairDivisionUnit repairDivisionUnit = repairDivisionUnitEquipmentStaff.getRepairDivisionUnit();
            result.computeIfAbsent(repairDivisionUnit, rs -> new HashMap<>());
            result.get(repairDivisionUnit).put(repairDivisionUnitEquipmentStaff.getEquipmentSubType(),
                                               repairDivisionUnitEquipmentStaff);
        }
        return result;
    }

    @Override
    public List<RepairDivisionUnitEquipmentStaff> listRepairDivisionUnitEquipmentStaff(Long repairDivisionUnitId,
                                                                                       UUID sessionId) {
        return repairDivisionUnitEquipmentStaffRepository.findAllByRepairDivisionUnitIdAndTehoSessionId(
                repairDivisionUnitId, sessionId);
    }

    @Override
    public List<RepairDivisionUnitEquipmentStaff> listRepairDivisionUnitEquipmentStaff(UUID sessionId) {
        return repairDivisionUnitEquipmentStaffRepository.findAllByTehoSessionId(sessionId);
    }

    @Override
    public void copyEquipmentStaff(UUID originalSessionId, UUID newSessionId) {
        List<RepairDivisionUnitEquipmentStaff> equipmentStaffList =
                repairDivisionUnitEquipmentStaffRepository.findFiltered(originalSessionId,
                                                                        null,
                                                                        null,
                                                                        null);
        List<RepairDivisionUnitEquipmentStaff> updatedRepairDivisionUnitEquipmentStaffList =
                equipmentStaffList.stream().map(rses -> rses.copy(newSessionId)).collect(Collectors.toList());

        repairDivisionUnitEquipmentStaffRepository.saveAll(updatedRepairDivisionUnitEquipmentStaffList);
    }

}
