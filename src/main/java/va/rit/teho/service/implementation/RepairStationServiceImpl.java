package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.*;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.RepairStationEquipmentCapabilitiesRepository;
import va.rit.teho.repository.RepairStationRepository;
import va.rit.teho.service.BaseService;
import va.rit.teho.service.EquipmentTypeService;
import va.rit.teho.service.RepairStationService;
import va.rit.teho.service.RepairStationTypeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RepairStationServiceImpl implements RepairStationService {

    private final RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository;
    private final RepairStationRepository repairStationRepository;

    private final RepairStationTypeService repairStationTypeService;
    private final BaseService baseService;
    private final EquipmentTypeService equipmentTypeService;

    public RepairStationServiceImpl(
            RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository,
            RepairStationRepository repairStationRepository,
            RepairStationTypeService repairStationTypeService,
            BaseService baseService,
            EquipmentTypeService equipmentTypeService) {
        this.repairStationEquipmentCapabilitiesRepository = repairStationEquipmentCapabilitiesRepository;
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
                       repairStationEquipmentCapabilitiesRepository.findAllByRepairStationId(repairStationId));
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

    private void checkEquipmentStaffPreconditions(Long repairStationId,
                                                  Long equipmentSubTypeId,
                                                  int availableStaff,
                                                  int totalStaff) {
        getRepairStationOrThrow(repairStationId);
        equipmentTypeService.getSubType(equipmentSubTypeId); //Проверка на существование
        if (totalStaff < availableStaff) {
            throw new IncorrectParamException(
                    "Всего производственников < доступно производственников (" + totalStaff + " < " + availableStaff + ")");
        }
    }

    @Override
    public void setEquipmentStaff(UUID sessionId,
                                  Long repairStationId,
                                  Long equipmentSubTypeId,
                                  int availableStaff,
                                  int totalStaff) {
        checkEquipmentStaffPreconditions(repairStationId, equipmentSubTypeId, availableStaff, totalStaff);

        EquipmentSubTypePerRepairStation id =
                new EquipmentSubTypePerRepairStation(repairStationId, equipmentSubTypeId, sessionId);
        RepairStationEquipmentStaff repairStationEquipmentStaff =
                new RepairStationEquipmentStaff(id, totalStaff, availableStaff);

        repairStationEquipmentCapabilitiesRepository.save(repairStationEquipmentStaff);
    }

    @Override
    public void updateEquipmentStaff(UUID sessionId,
                                     Long repairStationId,
                                     Long equipmentSubTypeId,
                                     int availableStaff,
                                     int totalStaff) {
        checkEquipmentStaffPreconditions(repairStationId, equipmentSubTypeId, availableStaff, totalStaff);

        RepairStationEquipmentStaff stationEquipmentStaff =
                repairStationEquipmentCapabilitiesRepository
                        .findById(new EquipmentSubTypePerRepairStation(repairStationId, equipmentSubTypeId, sessionId))
                        .orElseThrow(() -> new NotFoundException("Прозиводственные возможности РВО с id = " + repairStationId + " по ВВСТ с id = " + equipmentSubTypeId + " не найдены!"));

        stationEquipmentStaff.setAvailableStaff(availableStaff);
        stationEquipmentStaff.setTotalStaff(totalStaff);

        repairStationEquipmentCapabilitiesRepository.save(stationEquipmentStaff);
    }

    @Override
    public Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> getRepairStationEquipmentStaff(UUID sessionId,
                                                                                                                 List<Long> repairStationIds,
                                                                                                                 List<Long> equipmentTypeIds,
                                                                                                                 List<Long> equipmentSubTypeIds) {
        List<RepairStationEquipmentStaff> equipmentStaffList =
                repairStationEquipmentCapabilitiesRepository.findFiltered(repairStationIds,
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

}
