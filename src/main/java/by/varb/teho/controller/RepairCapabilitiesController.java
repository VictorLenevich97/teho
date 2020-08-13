package by.varb.teho.controller;

import by.varb.teho.entity.CalculatedRepairCapabilitesPerDay;
import by.varb.teho.entity.Equipment;
import by.varb.teho.entity.RepairStation;
import by.varb.teho.service.EquipmentService;
import by.varb.teho.service.RepairCapabilitiesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("repair-capabilities")
public class RepairCapabilitiesController {

    private final RepairCapabilitiesService repairCapabilitiesService;


    public RepairCapabilitiesController(
            RepairCapabilitiesService repairCapabilitiesService,
            EquipmentService equipmentService) {
        this.repairCapabilitiesService = repairCapabilitiesService;
    }

    /**
     * Расчет производственных возможностей РВО по ремонту (сразу для всех РВО по всем ВВСТ).
     */
    @PostMapping("/calculate")
    public Map<String, Map<String, Double>> calculateAndGet() {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilities();
        return getCalculatedRepairCapabilities();
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
