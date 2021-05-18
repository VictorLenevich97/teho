package va.rit.teho.service.implementation.repairformation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.labordistribution.WorkhoursDistributionIntervalService;
import va.rit.teho.service.repairformation.RepairFormationService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;
import va.rit.teho.service.repairformation.RepairFormationUnitServiceFacade;
import va.rit.teho.service.repairformation.RepairStationService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RepairFormationUnitServiceFacadeImpl implements RepairFormationUnitServiceFacade {

    private final RepairFormationService repairFormationService;
    private final RepairFormationUnitService repairFormationUnitService;
    private final EquipmentTypeService equipmentTypeService;
    private final RepairStationService repairStationService;
    private final WorkhoursDistributionIntervalService workhoursDistributionIntervalService;
    private final RepairTypeService repairTypeService;

    public RepairFormationUnitServiceFacadeImpl(RepairFormationService repairFormationService,
                                                RepairFormationUnitService repairFormationUnitService,
                                                EquipmentTypeService equipmentTypeService,
                                                RepairStationService repairStationService, WorkhoursDistributionIntervalService workhoursDistributionIntervalService, RepairTypeService repairTypeService) {
        this.repairFormationService = repairFormationService;
        this.repairFormationUnitService = repairFormationUnitService;
        this.equipmentTypeService = equipmentTypeService;
        this.repairStationService = repairStationService;
        this.workhoursDistributionIntervalService = workhoursDistributionIntervalService;
        this.repairTypeService = repairTypeService;
    }


    @Override
    public Long count(List<Long> filterIds) {
        return repairFormationUnitService.count(filterIds);
    }

    @Override
    public List<RepairFormationUnit> list(UUID sessionId, List<Long> filterIds, Integer pageNum, Integer pageSize) {
        return repairFormationUnitService.list(sessionId, filterIds, PageRequest.of(pageNum - 1, pageSize));
    }

    @Override
    public List<RepairFormationUnit> list(UUID sessionId, Long repairFormationId, List<Long> filterIds, Integer pageNum, Integer pageSize) {
        return repairFormationUnitService.list(sessionId, repairFormationService.get(repairFormationId), filterIds, PageRequest.of(pageNum - 1, pageSize));
    }

    @Override
    public RepairFormationUnit get(Long id) {
        return repairFormationUnitService.get(id);
    }

    @Override
    public Pair<RepairFormationUnit, List<RepairFormationUnitEquipmentStaff>> getWithStaff(UUID sessionId, Long repairFormationUntId) {
        return Pair.of(repairFormationUnitService.get(repairFormationUntId), repairFormationUnitService.getStaff(sessionId, repairFormationUntId));
    }

    @Override
    public RepairFormationUnit add(String name, Long repairFormationId, Long intervalId, Long repairTypeId, Long repairStationTypeId, int stationAmount) {
        return repairFormationUnitService.add(name,
                repairFormationService.get(repairFormationId),
                workhoursDistributionIntervalService.get(intervalId),
                repairTypeService.get(repairTypeId),
                repairStationService.get(repairStationTypeId),
                stationAmount);
    }

    @Override
    public RepairFormationUnit update(Long id, String name, Long repairFormationId, Long intervalId, Long repairTypeId, Long repairStationTypeId, int stationAmount) {
        return repairFormationUnitService.update(id, name, repairFormationService.get(repairFormationId),
                workhoursDistributionIntervalService.get(intervalId),
                repairTypeService.get(repairTypeId),
                repairStationService.get(repairStationTypeId),
                stationAmount);
    }

    private void checkEquipmentStaffPreconditions(RepairFormationUnitEquipmentStaff repairFormationUnitEquipmentStaff) {
        repairFormationUnitService.get(repairFormationUnitEquipmentStaff
                .getEquipmentPerRepairFormationUnit()
                .getRepairFormationUnitId());
        equipmentTypeService.get(repairFormationUnitEquipmentStaff
                .getEquipmentPerRepairFormationUnit()
                .getEquipmentTypeId()); //Проверка на существование
        if (repairFormationUnitEquipmentStaff.getTotalStaff() < repairFormationUnitEquipmentStaff.getAvailableStaff()) {
            throw new IncorrectParamException(
                    "Всего производственников < доступно производственников (" + repairFormationUnitEquipmentStaff.getTotalStaff() + " < " + repairFormationUnitEquipmentStaff
                            .getAvailableStaff() + ")");
        }
    }

    @Override
    public List<RepairFormationUnitEquipmentStaff> updateEquipmentStaff(List<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffList) {
        repairFormationUnitEquipmentStaffList.forEach(this::checkEquipmentStaffPreconditions);
        return repairFormationUnitService.updateEquipmentStaff(repairFormationUnitEquipmentStaffList);
    }

    @Override
    public Map<EquipmentType, RepairFormationUnitEquipmentStaff> getEquipmentStaffPerType(UUID sessionId, Long repairFormationUnitId, List<Long> equipmentTypeIds) {
        return repairFormationUnitService.getEquipmentStaffPerType(sessionId, repairFormationUnitId, equipmentTypeIds);
    }

    @Override
    public Map<RepairFormationUnit, Map<EquipmentType, RepairFormationUnitEquipmentStaff>> listEquipmentStaffPerType(UUID sessionId, List<Long> repairFormationUnitIds, List<Long> equipmentTypeIds) {
        return repairFormationUnitService.listEquipmentStaffPerType(sessionId, repairFormationUnitIds, equipmentTypeIds);
    }

    @Override
    public List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(UUID sessionId, Long repairFormationUnitId) {
        return repairFormationUnitService.listEquipmentStaff(sessionId, repairFormationUnitId);
    }

    @Override
    public List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(UUID sessionId) {
        return repairFormationUnitService.listEquipmentStaff(sessionId);
    }

    @Override
    public void copyEquipmentStaff(UUID originalSessionId, UUID newSessionId) {
        repairFormationUnitService.copyEquipmentStaff(originalSessionId, newSessionId);
    }

    @Override
    public void delete(Long id) {
        repairFormationUnitService.delete(id);
    }
}
