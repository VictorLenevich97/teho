package va.rit.teho.service.implementation.repairformation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.repairformation.RepairFormation;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.RepairStationType;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.repository.repairformation.RepairFormationRepository;
import va.rit.teho.repository.repairformation.RepairFormationUnitEquipmentStaffRepository;
import va.rit.teho.repository.repairformation.RepairFormationUnitRepository;
import va.rit.teho.repository.repairformation.RepairStationTypeRepository;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class RepairFormationUnitServiceImpl implements RepairFormationUnitService {

    private final RepairFormationUnitEquipmentStaffRepository repairFormationUnitEquipmentStaffRepository;
    private final RepairFormationUnitRepository repairFormationUnitRepository;
    private final RepairStationTypeRepository repairStationTypeRepository;
    private final RepairFormationRepository repairFormationRepository;

    private final EquipmentTypeService equipmentTypeService;

    public RepairFormationUnitServiceImpl(
            RepairFormationUnitEquipmentStaffRepository repairFormationUnitEquipmentStaffRepository,
            RepairFormationUnitRepository repairFormationUnitRepository,
            RepairStationTypeRepository repairStationTypeRepository,
            RepairFormationRepository repairFormationRepository,
            EquipmentTypeService equipmentTypeService) {
        this.repairFormationUnitEquipmentStaffRepository = repairFormationUnitEquipmentStaffRepository;
        this.repairFormationUnitRepository = repairFormationUnitRepository;
        this.repairStationTypeRepository = repairStationTypeRepository;
        this.repairFormationRepository = repairFormationRepository;
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
    public RepairFormationUnit get(Long id) {
        return repairFormationUnitRepository.findById(id).orElseThrow(() -> new NotFoundException("РВО не найдено!"));
    }

    @Override
    public Pair<RepairFormationUnit, List<RepairFormationUnitEquipmentStaff>> getWithStaff(Long repairFormationUntId,
                                                                                           UUID sessionId) {
        return Pair.of(getRepairFormationUnitOrThrow(repairFormationUntId),
                       repairFormationUnitEquipmentStaffRepository.findAllByRepairFormationUnitIdAndTehoSessionId(
                               repairFormationUntId, sessionId));
    }

    @Override
    public RepairFormationUnit add(String name, Long repairFormationId, Long stationTypeId, int amount) {
        RepairStationType repairStationType = repairStationTypeRepository
                .findById(stationTypeId)
                .orElseThrow(() -> new NotFoundException("Тип мастерской не найден!"));
        RepairFormation repairFormation = repairFormationRepository
                .findById(repairFormationId)
                .orElseThrow(() -> new NotFoundException("Ремонтное формирование не найдено!"));
        repairFormationRepository.findByNameIgnoreCase(name).ifPresent(rfu -> {
            throw new AlreadyExistsException("РВО", "название", name);
        });
        long newId = repairFormationUnitRepository.getMaxId() + 1;
        RepairFormationUnit repairFormationUnit = new RepairFormationUnit(newId,
                                                                          name,
                                                                          repairStationType,
                                                                          amount,
                                                                          repairFormation);
        return repairFormationUnitRepository.save(repairFormationUnit);
    }

    @Override
    public RepairFormationUnit update(Long id, String name, Long repairFormationId, Long stationTypeId, int amount) {
        RepairFormationUnit repairFormationUnit = getRepairFormationUnitOrThrow(id);
        RepairFormation repairFormation = repairFormationRepository
                .findById(repairFormationId)
                .orElseThrow(() -> new NotFoundException("Ремонтное формирование не найдено!"));
        RepairStationType repairStationType = repairStationTypeRepository
                .findById(stationTypeId)
                .orElseThrow(() -> new NotFoundException(""));

        repairFormationUnit.setRepairStationType(repairStationType);
        repairFormationUnit.setName(name);
        repairFormationUnit.setStationAmount(amount);
        repairFormationUnit.setRepairFormation(repairFormation);

        return repairFormationUnitRepository.save(repairFormationUnit);
    }

    private RepairFormationUnit getRepairFormationUnitOrThrow(Long id) {
        return repairFormationUnitRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("РВО с id = " + id + " не найден!"));
    }

    private void checkEquipmentStaffPreconditions(RepairFormationUnitEquipmentStaff repairFormationUnitEquipmentStaff) {
        getRepairFormationUnitOrThrow(repairFormationUnitEquipmentStaff
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
    public List<RepairFormationUnitEquipmentStaff> updateEquipmentStaff(
            List<RepairFormationUnitEquipmentStaff> repairFormationUnitEquipmentStaffList) {
        repairFormationUnitEquipmentStaffList.forEach(this::checkEquipmentStaffPreconditions);

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
    public Map<EquipmentSubType, RepairFormationUnitEquipmentStaff> getEquipmentStaffPerSubType(UUID sessionId,
                                                                                                Long repairFormationUnitId,
                                                                                                List<Long> equipmentTypeIds,
                                                                                                List<Long> equipmentSubTypeIds) {
        RepairFormationUnit repairFormationUnit = getRepairFormationUnitOrThrow(repairFormationUnitId);
        return listEquipmentStaffPerSubType(sessionId,
                                            Collections.singletonList(repairFormationUnitId),
                                            equipmentTypeIds,
                                            equipmentSubTypeIds).getOrDefault(repairFormationUnit,
                                                                              Collections.emptyMap());
    }

    @Override
    public Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> listEquipmentStaffPerSubType(
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

    @Override
    public void delete(Long id) {
        repairFormationUnitRepository.deleteById(id);
    }

}
