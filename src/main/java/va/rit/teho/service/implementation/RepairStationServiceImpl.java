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
import va.rit.teho.service.EquipmentService;
import va.rit.teho.service.RepairStationService;
import va.rit.teho.service.RepairStationTypeService;

import java.util.List;
import java.util.UUID;

@Service
public class RepairStationServiceImpl implements RepairStationService {

    private final RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository;
    private final RepairStationRepository repairStationRepository;

    private final RepairStationTypeService repairStationTypeService;
    private final BaseService baseService;
    private final EquipmentService equipmentService;

    public RepairStationServiceImpl(
            RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository,
            RepairStationRepository repairStationRepository,
            RepairStationTypeService repairStationTypeService,
            BaseService baseService,
            EquipmentService equipmentService) {
        this.repairStationEquipmentCapabilitiesRepository = repairStationEquipmentCapabilitiesRepository;
        this.repairStationRepository = repairStationRepository;
        this.repairStationTypeService = repairStationTypeService;
        this.baseService = baseService;
        this.equipmentService = equipmentService;
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

    @Override
    public void setEquipmentStaff(UUID sessionId,
                                  Long repairStationId,
                                  Long equipmentId,
                                  int availableStaff,
                                  int totalStaff) {
        getRepairStationOrThrow(repairStationId);
        equipmentService.getEquipment(equipmentId); //Проверка на существование
        if (totalStaff < availableStaff) {
            throw new IncorrectParamException(
                    "Всего производственников < доступно производственников (" + totalStaff + " < " + availableStaff + ")");
        }
        RepairStationEquipmentStaff repairStationEquipmentStaff = new RepairStationEquipmentStaff(
                new EquipmentSubTypePerRepairStation(repairStationId, equipmentId, sessionId),
                totalStaff,
                availableStaff);
        repairStationEquipmentCapabilitiesRepository.save(repairStationEquipmentStaff);
    }

    @Override
    public void updateEquipmentStaff(UUID sessionId,
                                     Long repairStationId,
                                     Long equipmentId,
                                     int availableStaff,
                                     int totalStaff) {
        getRepairStationOrThrow(repairStationId);
        equipmentService.getEquipment(equipmentId); //Проверка на существование
        if (totalStaff < availableStaff) {
            throw new IncorrectParamException(
                    "Всего производственников < доступно производственников (" + totalStaff + " < " + availableStaff + ")");
        }
        RepairStationEquipmentStaff stationEquipmentStaff =
                repairStationEquipmentCapabilitiesRepository
                        .findById(new EquipmentSubTypePerRepairStation(repairStationId, equipmentId, sessionId))
                        .orElseThrow(() -> new NotFoundException("Прозиводственные возможности РВО с id = " + repairStationId + " по ВВСТ с id = " + equipmentId + " не найдены!"));
        stationEquipmentStaff.setAvailableStaff(availableStaff);
        stationEquipmentStaff.setTotalStaff(totalStaff);
        repairStationEquipmentCapabilitiesRepository.save(stationEquipmentStaff);
    }

    @Override
    public List<RepairStationEquipmentStaff> listEquipmentStaff(UUID sessionId,
                                                                List<Long> repairStationIds,
                                                                List<Long> equipmentTypeIds,
                                                                List<Long> equipmentSubTypeIds) {
        return null;
    }

}
