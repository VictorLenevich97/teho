package va.rit.teho.service.implementation;

import org.springframework.stereotype.Service;
import va.rit.teho.entity.*;
import va.rit.teho.exception.AlreadyExistsException;
import va.rit.teho.exception.IncorrectParamException;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.model.Pair;
import va.rit.teho.repository.RepairStationEquipmentCapabilitiesRepository;
import va.rit.teho.repository.RepairStationRepository;
import va.rit.teho.repository.RepairStationTypeRepository;
import va.rit.teho.service.BaseService;
import va.rit.teho.service.EquipmentService;
import va.rit.teho.service.RepairStationService;

import java.util.List;

@Service
public class RepairStationServiceImpl implements RepairStationService {

    private final RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository;
    private final RepairStationRepository repairStationRepository;
    private final RepairStationTypeRepository repairStationTypeRepository;
    private final BaseService baseService;
    private final EquipmentService equipmentService;

    public RepairStationServiceImpl(
            RepairStationEquipmentCapabilitiesRepository repairStationEquipmentCapabilitiesRepository,
            RepairStationRepository repairStationRepository,
            RepairStationTypeRepository repairStationTypeRepository,
            BaseService baseService,
            EquipmentService equipmentService) {
        this.repairStationEquipmentCapabilitiesRepository = repairStationEquipmentCapabilitiesRepository;
        this.repairStationRepository = repairStationRepository;
        this.repairStationTypeRepository = repairStationTypeRepository;
        this.baseService = baseService;
        this.equipmentService = equipmentService;
    }

    @Override
    public List<RepairStation> list(List<Long> filterIds) {
        return (List<RepairStation>) (
                filterIds.isEmpty() ? repairStationRepository.findAll() : repairStationRepository.findAllById(filterIds));
    }

    @Override
    public Pair<RepairStation, List<RepairStationEquipmentStaff>> get(Long repairStationId) {
        return Pair.of(getRepairStationOrThrow(repairStationId),
                       repairStationEquipmentCapabilitiesRepository.findAllByRepairStationId(repairStationId));
    }

    @Override
    public Long add(String name, Long baseId, Long typeId, int amount) {
        Base base = baseService.get(baseId);
        RepairStationType repairStationType =
                repairStationTypeRepository
                        .findById(typeId)
                        .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + typeId + " не найден"));
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
        RepairStationType repairStationType =
                repairStationTypeRepository
                        .findById(typeId)
                        .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + typeId + " не найден"));
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
    public void setEquipmentStaff(Long repairStationId, Long equipmentId, int availableStaff, int totalStaff) {
        getRepairStationOrThrow(repairStationId);
        equipmentService.getEquipment(equipmentId); //Проверка на существование
        if (totalStaff < availableStaff) {
            throw new IncorrectParamException(
                    "Всего производственников < доступно производственников (" + totalStaff + " < " + availableStaff + ")");
        }
        RepairStationEquipmentStaff repairStationEquipmentStaff = new RepairStationEquipmentStaff(
                new EquipmentPerRepairStation(repairStationId, equipmentId), totalStaff, availableStaff);
        repairStationEquipmentCapabilitiesRepository.save(repairStationEquipmentStaff);
    }

    @Override
    public void updateEquipmentStaff(Long repairStationId, Long equipmentId, int availableStaff, int totalStaff) {
        getRepairStationOrThrow(repairStationId);
        equipmentService.getEquipment(equipmentId); //Проверка на существование
        if (totalStaff < availableStaff) {
            throw new IncorrectParamException(
                    "Всего производственников < доступно производственников (" + totalStaff + " < " + availableStaff + ")");
        }
        RepairStationEquipmentStaff stationEquipmentStaff =
                repairStationEquipmentCapabilitiesRepository
                        .findById(new EquipmentPerRepairStation(repairStationId, equipmentId))
                        .orElseThrow(() -> new NotFoundException("Прозиводственные возможности РВО с id = " + repairStationId + " по ВВСТ с id = " + equipmentId + " не найдены!"));
        stationEquipmentStaff.setAvailableStaff(availableStaff);
        stationEquipmentStaff.setTotalStaff(totalStaff);
        repairStationEquipmentCapabilitiesRepository.save(stationEquipmentStaff);
    }

    @Override
    public Long addType(String name, int workingHoursMin, int workingHoursMax) {
        repairStationTypeRepository.findByName(name).ifPresent((rst) -> {
            throw new AlreadyExistsException("Тип РВО", "название", name);
        });
        if (workingHoursMax < workingHoursMin) {
            throw new IncorrectParamException("Верхний предел рабочего времени производственником меньше нижнего!");
        }
        RepairStationType repairStationType = new RepairStationType(name, workingHoursMin, workingHoursMax);
        return repairStationTypeRepository.save(repairStationType).getId();
    }

    @Override
    public void updateType(Long id, String name, int workingHoursMin, int workingHoursMax) {
        RepairStationType repairStationType =
                repairStationTypeRepository.findById(id)
                                           .orElseThrow(() -> new NotFoundException("Тип РВО с id = " + id + " не найден!"));
        if (workingHoursMax < workingHoursMin) {
            throw new IncorrectParamException("Верхний предел рабочего времени производственником меньше нижнего!");
        }
        repairStationType.setName(name);
        repairStationType.setWorkingHoursMax(workingHoursMax);
        repairStationType.setWorkingHoursMin(workingHoursMin);
        repairStationTypeRepository.save(repairStationType);
    }

    @Override
    public List<RepairStationType> listTypes() {
        return (List<RepairStationType>) this.repairStationTypeRepository.findAll();
    }
}
