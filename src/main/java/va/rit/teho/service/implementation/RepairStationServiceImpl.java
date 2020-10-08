package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.*;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.RepairStationEquipmentStaffRepository;
import va.rit.teho.repository.RepairStationRepository;
import va.rit.teho.service.BaseService;
import va.rit.teho.service.EquipmentTypeService;
import va.rit.teho.service.RepairStationService;
import va.rit.teho.service.RepairStationTypeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RepairStationServiceImpl implements RepairStationService {

    private final RepairStationEquipmentStaffRepository repairStationEquipmentStaffRepository;
    private final RepairStationRepository repairStationRepository;

    private final RepairStationTypeService repairStationTypeService;
    private final BaseService baseService;
    private final EquipmentTypeService equipmentTypeService;

    public RepairStationServiceImpl(
            RepairStationEquipmentStaffRepository repairStationEquipmentStaffRepository,
            RepairStationRepository repairStationRepository,
            RepairStationTypeService repairStationTypeService,
            BaseService baseService,
            EquipmentTypeService equipmentTypeService) {
        this.repairStationEquipmentStaffRepository = repairStationEquipmentStaffRepository;
        this.repairStationRepository = repairStationRepository;
        this.repairStationTypeService = repairStationTypeService;
        this.baseService = baseService;
        this.equipmentTypeService = equipmentTypeService;
    }

    @Override
    public List<RepairStation> list(List<Long> filterIds) {
        return repairStationRepository.findSorted(filterIds);
    }

    @Override
    public Pair<RepairStation, List<RepairStationEquipmentStaff>> get(Long repairStationId) {
        return Pair.of(getRepairStationOrThrow(repairStationId),
                       repairStationEquipmentStaffRepository.findAllByRepairStationId(repairStationId));
    }

    @Override
    public Long add(String name, Long baseId, Long typeId, int amount) {
        Base base = baseService.get(baseId);
        RepairStationType repairStationType = repairStationTypeService.get(typeId);
        repairStationRepository.findByName(name).ifPresent(repairStation -> {
            throw new AlreadyExistsException("РВО", "название", name);
        });
        RepairStation repairStation = new RepairStation(name, repairStationType, base, amount);
        return repairStationRepository.save(repairStation).getId();
    }

    @Override
    public void update(Long id, String name, Long baseId, Long typeId, int amount) {
        RepairStation repairStation = getRepairStationOrThrow(id);
        Base base = baseService.get(baseId);
        RepairStationType repairStationType = repairStationTypeService.get(typeId);

        repairStation.setBase(base);
        repairStation.setRepairStationType(repairStationType);
        repairStation.setName(name);
        repairStation.setStationAmount(amount);

        repairStationRepository.save(repairStation);
    }

    private RepairStation getRepairStationOrThrow(Long id) {
        return repairStationRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("РВО с id = " + id + " не найден!"));
    }

    private void checkEquipmentStaffPreconditions(RepairStationEquipmentStaff repairStationEquipmentStaff) {
        getRepairStationOrThrow(repairStationEquipmentStaff.getEquipmentPerRepairStation().getRepairStationId());
        equipmentTypeService.getSubType(repairStationEquipmentStaff
                                                .getEquipmentPerRepairStation()
                                                .getEquipmentSubTypeId()); //Проверка на существование
        if (repairStationEquipmentStaff.getTotalStaff() < repairStationEquipmentStaff.getAvailableStaff()) {
            throw new IncorrectParamException(
                    "Всего производственников < доступно производственников (" + repairStationEquipmentStaff.getTotalStaff() + " < " + repairStationEquipmentStaff
                            .getAvailableStaff() + ")");
        }
    }

    @Override
    public void setEquipmentStaff(List<RepairStationEquipmentStaff> repairStationEquipmentStaffList) {
        repairStationEquipmentStaffList.forEach(this::checkEquipmentStaffPreconditions);

        repairStationEquipmentStaffRepository.saveAll(repairStationEquipmentStaffList);
    }

    @Override
    public void updateEquipmentStaff(List<RepairStationEquipmentStaff> repairStationEquipmentStaffList) {
        List<RepairStationEquipmentStaff> stationEquipmentStaffList =
                repairStationEquipmentStaffList
                        .stream()
                        .map(this::getRepairStationEquipmentStaff)
                        .collect(Collectors.toList());
        repairStationEquipmentStaffRepository.saveAll(stationEquipmentStaffList);
    }

    private RepairStationEquipmentStaff getRepairStationEquipmentStaff(RepairStationEquipmentStaff repairStationEquipmentStaff) {
        checkEquipmentStaffPreconditions(repairStationEquipmentStaff);
        RepairStationEquipmentStaff stationEquipmentStaff =
                repairStationEquipmentStaffRepository
                        .findById(repairStationEquipmentStaff.getEquipmentPerRepairStation())
                        .orElseThrow(() -> new NotFoundException("Прозиводственные возможности РВО с id = " + repairStationEquipmentStaff
                                .getEquipmentPerRepairStation()
                                .getRepairStationId() + " по виду ВВСТ с id = " + repairStationEquipmentStaff
                                .getEquipmentPerRepairStation()
                                .getEquipmentSubTypeId() + " не найдены!"));

        stationEquipmentStaff.setAvailableStaff(repairStationEquipmentStaff.getAvailableStaff());
        stationEquipmentStaff.setTotalStaff(repairStationEquipmentStaff.getTotalStaff());
        return stationEquipmentStaff;
    }

    @Override
    public Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> getRepairStationEquipmentStaff(UUID sessionId,
                                                                                                                 List<Long> repairStationIds,
                                                                                                                 List<Long> equipmentTypeIds,
                                                                                                                 List<Long> equipmentSubTypeIds) {
        List<RepairStationEquipmentStaff> equipmentStaffList =
                repairStationEquipmentStaffRepository.findFiltered(sessionId,
                                                                   repairStationIds,
                                                                   equipmentTypeIds,
                                                                   equipmentSubTypeIds);

        Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> result = new HashMap<>();
        for (RepairStationEquipmentStaff repairStationEquipmentStaff : equipmentStaffList) {
            RepairStation repairStation = repairStationEquipmentStaff.getRepairStation();
            result.computeIfAbsent(repairStation, rs -> new HashMap<>());
            result.get(repairStation).put(repairStationEquipmentStaff.getEquipmentSubType(),
                                          repairStationEquipmentStaff);
        }
        return result;
    }

    @Override
    public void copyEquipmentStaff(UUID originalSessionId, UUID newSessionId) {
        List<RepairStationEquipmentStaff> equipmentStaffList =
                repairStationEquipmentStaffRepository.findFiltered(originalSessionId,
                                                                   null,
                                                                   null,
                                                                   null);
        List<RepairStationEquipmentStaff> updatedRepairStationEquipmentStaffList =
                equipmentStaffList.stream().map(rses -> rses.copy(newSessionId)).collect(Collectors.toList());

        repairStationEquipmentStaffRepository.saveAll(updatedRepairStationEquipmentStaffList);
    }

}
