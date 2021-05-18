package va.rit.teho.service.implementation.repairformation;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.labordistribution.WorkhoursDistributionInterval;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairStationType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairformation.RepairFormationUnitEquipmentStaffRepository;
import va.rit.teho.repository.repairformation.RepairFormationUnitRepository;
import va.rit.teho.service.repairformation.RepairFormationUnitService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class RepairFormationUnitServiceImpl implements RepairFormationUnitService {

    private final RepairFormationUnitEquipmentStaffRepository repairFormationUnitEquipmentStaffRepository;
    private final RepairFormationUnitRepository repairFormationUnitRepository;

    public RepairFormationUnitServiceImpl(
            RepairFormationUnitEquipmentStaffRepository repairFormationUnitEquipmentStaffRepository,
            RepairFormationUnitRepository repairFormationUnitRepository) {
        this.repairFormationUnitEquipmentStaffRepository = repairFormationUnitEquipmentStaffRepository;
        this.repairFormationUnitRepository = repairFormationUnitRepository;
    }

    @Override
    public Long count(List<Long> filterIds) {
        if (CollectionUtils.isEmpty(filterIds)) {
            return repairFormationUnitRepository.count();
        } else {
            return repairFormationUnitRepository.countByIdIn(filterIds);
        }
    }

    @Override
    public List<RepairFormationUnit> list(UUID sessionId, List<Long> filterIds, PageRequest pagination) {
        return repairFormationUnitRepository.findSorted(sessionId, filterIds, pagination);
    }

    @Override
    public List<RepairFormationUnit> list(UUID sessionId,
                                          RepairFormation repairFormation,
                                          List<Long> filterIds,
                                          PageRequest pagination) {
        return repairFormationUnitRepository.findSorted(sessionId,
                repairFormation.getId(),
                filterIds,
                pagination);
    }

    @Override
    public RepairFormationUnit get(Long id) {
        return getRepairFormationUnitOrThrow(id);
    }

    @Override
    public RepairFormationUnit add(String name,
                                   RepairFormation repairFormation,
                                   WorkhoursDistributionInterval interval,
                                   RepairType repairType,
                                   RepairStationType repairStationType,
                                   int stationAmount) {
        long newId = repairFormationUnitRepository.getMaxId() + 1;
        RepairFormationUnit repairFormationUnit = new RepairFormationUnit(
                newId, name, repairStationType, stationAmount, repairFormation, interval, repairType);
        return repairFormationUnitRepository.save(repairFormationUnit);
    }

    @Override
    public RepairFormationUnit update(Long id,
                                      String name,
                                      RepairFormation repairFormation,
                                      WorkhoursDistributionInterval interval,
                                      RepairType repairType,
                                      RepairStationType repairStationType,
                                      int amount) {
        RepairFormationUnit repairFormationUnit = getRepairFormationUnitOrThrow(id);

        repairFormationUnit.setRepairStationType(repairStationType);
        repairFormationUnit.setName(name);
        repairFormationUnit.setStationAmount(amount);
        repairFormationUnit.setRepairFormation(repairFormation);
        repairFormationUnit.setWorkhoursDistributionInterval(interval);
        repairFormationUnit.setRepairType(repairType);

        return repairFormationUnitRepository.save(repairFormationUnit);
    }

    private RepairFormationUnit getRepairFormationUnitOrThrow(Long id) {
        return repairFormationUnitRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("РВО с id = " + id + " не найден!"));
    }


    @Override
    public List<RepairFormationUnitEquipmentStaff> updateEquipmentStaff(
            List<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffList) {
        List<RepairFormationUnitEquipmentStaff> updatedData =
                repairFormationUnitEquipmentStaffList
                        .stream()
                        .map(repairFormationUnitEquipmentStaff ->
                                repairFormationUnitEquipmentStaffRepository
                                        .findById(repairFormationUnitEquipmentStaff.getEquipmentPerRepairFormationUnit())
                                        .map(rfues -> rfues
                                                .setAvailableStaff(repairFormationUnitEquipmentStaff.getAvailableStaff())
                                                .setTotalStaff(repairFormationUnitEquipmentStaff.getTotalStaff()))
                                        .orElse(repairFormationUnitEquipmentStaff))
                        .collect(Collectors.toList());

        Iterable<RepairFormationUnitEquipmentStaff> result = repairFormationUnitEquipmentStaffRepository
                .saveAll(updatedData);
        return StreamSupport
                .stream(result.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Map<EquipmentType, RepairFormationUnitEquipmentStaff> getEquipmentStaffPerType(UUID sessionId,
                                                                                          Long repairFormationUnitId,
                                                                                          List<Long> equipmentTypeIds) {
        RepairFormationUnit repairFormationUnit = getRepairFormationUnitOrThrow(repairFormationUnitId);
        return listEquipmentStaffPerType(sessionId, Collections.singletonList(repairFormationUnitId), equipmentTypeIds)
                .getOrDefault(repairFormationUnit, Collections.emptyMap());
    }

    @Override
    public Map<RepairFormationUnit, Map<EquipmentType, RepairFormationUnitEquipmentStaff>> listEquipmentStaffPerType(
            UUID sessionId,
            List<Long> repairFormationUnitIds,
            List<Long> equipmentTypeIds) {
        List<RepairFormationUnitEquipmentStaff> equipmentStaffList =
                repairFormationUnitEquipmentStaffRepository.findFiltered(sessionId,
                        repairFormationUnitIds,
                        equipmentTypeIds);

        Map<RepairFormationUnit, Map<EquipmentType, RepairFormationUnitEquipmentStaff>> result = new HashMap<>();
        for (RepairFormationUnitEquipmentStaff repairFormationUnitEquipmentStaff : equipmentStaffList) {
            RepairFormationUnit repairFormationUnit = repairFormationUnitEquipmentStaff.getRepairFormationUnit();
            result.computeIfAbsent(repairFormationUnit, rs -> new HashMap<>());
            result.get(repairFormationUnit).put(repairFormationUnitEquipmentStaff.getEquipmentType(),
                    repairFormationUnitEquipmentStaff);
        }
        return result;
    }

    @Override
    public List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(Long repairFormationUnitId) {
        return repairFormationUnitEquipmentStaffRepository.findAllByRepairFormationUnitId(repairFormationUnitId);
    }

    @Override
    public List<RepairFormationUnitEquipmentStaff> listEquipmentStaff(UUID sessionId) {
        return repairFormationUnitEquipmentStaffRepository.findAllByTehoSessionId(sessionId);
    }

    @Override
    public void copyRFUAndStaff(UUID originalSessionId, UUID newSessionId, RepairFormation originalRepairFormation, RepairFormation newRepairFormation) {
        List<RepairFormationUnit> originalRFUs = repairFormationUnitRepository.findAllByRepairFormationId(originalRepairFormation.getId());
        int counter = 1;
        for (RepairFormationUnit rfu : originalRFUs) {
            RepairFormationUnit newRFU = repairFormationUnitRepository.save(rfu.copy(repairFormationUnitRepository.getMaxId() + counter, newRepairFormation));

            List<RepairFormationUnitEquipmentStaff> rfuEquipmentStaff =
                    repairFormationUnitEquipmentStaffRepository.findAllByRepairFormationUnitId(rfu.getId());

            repairFormationUnitEquipmentStaffRepository.saveAll(rfuEquipmentStaff.stream().map(rfues -> rfues.copy(newRFU.getId(), newSessionId)).collect(Collectors.toList()));
            counter++;
        }
    }

    @Override
    public void delete(Long id) {
        repairFormationUnitRepository.deleteById(id);
    }

}
