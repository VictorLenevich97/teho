package by.varb.teho.controller;

import by.varb.teho.entity.*;
import by.varb.teho.enums.RepairTypeEnum;
import by.varb.teho.exception.RepairTypeLaborInputNotFoundException;
import by.varb.teho.service.CalculationService;
import by.varb.teho.service.EquipmentService;
import by.varb.teho.service.RepairCapabilitiesService;
import by.varb.teho.service.RepairStationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("repair-capabilities")
//TODO: Перенести логику в сервис
public class RepairCapabilitiesController {

    private final RepairCapabilitiesService repairCapabilitiesService;
    private final EquipmentService equipmentService;
    private final RepairStationService repairStationService;
    private final CalculationService calculationService;


    public RepairCapabilitiesController(
            RepairCapabilitiesService repairCapabilitiesService,
            EquipmentService equipmentService,
            RepairStationService repairStationService,
            CalculationService calculationService) {
        this.repairCapabilitiesService = repairCapabilitiesService;
        this.equipmentService = equipmentService;
        this.repairStationService = repairStationService;
        this.calculationService = calculationService;
    }

    /**
     * Расчет производственных возможностей РВО по ремонту (сразу для всех РВО по всем ВВСТ).
     */
    @PostMapping("/calculate")
    public void calculate() {
        List<Equipment> equipmentList = this.equipmentService.list();
        List<RepairStation> repairStations = this.repairStationService.list();
        for (Equipment e : equipmentList) {
            for (RepairStation repairStation : repairStations) {
                EquipmentPerRepairStation equipmentPerRepairStation = new EquipmentPerRepairStation(repairStation.getId(),
                                                                                                    e.getId());
                Optional<RepairStationEquipmentStaff> repairStationEquipmentCapabilities =
                        repairCapabilitiesService.getRepairStationEquipmentStaff(e.getId(), repairStation.getId());
                repairStationEquipmentCapabilities.ifPresent(rsec -> {
                    CalculatedRepairCapabilitesPerDay calculatedRepairCapabilitesPerDay =
                            getCalculatedRepairCapabilitesPerDay(e, repairStation, equipmentPerRepairStation, rsec);
                    repairCapabilitiesService.saveCalculatedRepairCapabilities(calculatedRepairCapabilitesPerDay);
                });
            }
        }
    }

    private CalculatedRepairCapabilitesPerDay getCalculatedRepairCapabilitesPerDay(
            Equipment e,
            RepairStation repairStation,
            EquipmentPerRepairStation equipmentPerRepairStation,
            RepairStationEquipmentStaff rsec) {
        int laborInputAmount = rsec
                .getEquipment()
                .getLaborInputPerTypes()
                .stream()
                .filter(lipt -> lipt.getRepairType().getName().equals(RepairTypeEnum.AVG_REPAIR.getName()))
                .findFirst()
                .orElseThrow(() -> new RepairTypeLaborInputNotFoundException(RepairTypeEnum.AVG_REPAIR, e))
                .getAmount();
        double calculatedCapabilities = calculationService.calculateRepairCapabilities(
                rsec.getTotalStaff(),
                rsec.getRepairStation().getRepairStationType().getWorkingHoursMax(),
                laborInputAmount);
        return new CalculatedRepairCapabilitesPerDay(equipmentPerRepairStation,
                                                     repairStation,
                                                     e,
                                                     calculatedCapabilities);
    }

    @GetMapping
    @ResponseBody
    public Map<String, Map<String, Double>> getCalculatedRepairCapabilities() {
        Map<RepairStation, Map<Equipment, CalculatedRepairCapabilitesPerDay>> totalCalculatedRepairCapabilities =
                this.repairCapabilitiesService.getTotalCalculatedRepairCapabilities();
        Map<String, Map<String, Double>> result = new HashMap<>();
        for (Map.Entry<RepairStation, Map<Equipment, CalculatedRepairCapabilitesPerDay>> entry : totalCalculatedRepairCapabilities
                .entrySet()) {
            Map<String, Double> equipmentCapabilities = new HashMap<>();
            result.put(entry.getKey().getName(), equipmentCapabilities);
            for (Map.Entry<Equipment, CalculatedRepairCapabilitesPerDay> capabilitesPerDayEntry : entry
                    .getValue()
                    .entrySet()) {
                equipmentCapabilities.put(capabilitesPerDayEntry.getKey().getName(),
                                          capabilitesPerDayEntry.getValue().getCapability());
            }
        }
        return result;
    }
}
