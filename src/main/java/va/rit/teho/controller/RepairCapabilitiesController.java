package va.rit.teho.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.RepairCapabilitiesDTO;
import va.rit.teho.dto.RepairStationDTO;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.RepairStation;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.service.RepairCapabilitiesService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("repair-capabilities")
public class RepairCapabilitiesController {

    private final RepairCapabilitiesService repairCapabilitiesService;

    public RepairCapabilitiesController(
            RepairCapabilitiesService repairCapabilitiesService) {
        this.repairCapabilitiesService = repairCapabilitiesService;
    }

    /**
     * Расчет производственных возможностей РВО по ремонту (сразу для всех РВО по всем ВВСТ).
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<List<RepairCapabilitiesDTO>> calculateAndGet() {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilities();
        return getCalculatedRepairCapabilities(Collections.emptyList());
    }

    @PostMapping("/repair-station/{repairStationId}")
    @ResponseBody
    public ResponseEntity<RepairCapabilitiesDTO> calculateAndGetPerStation(@PathVariable Long repairStationId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilitiesPerStation(repairStationId);
        RepairCapabilitiesDTO repairCapabilitiesDTO =
                Optional.ofNullable(
                        getCalculatedRepairCapabilities(Collections.singletonList(repairStationId)).getBody())
                        .flatMap(v -> v.stream().findFirst())
                        .orElseThrow(() -> new NotFoundException("Производственные возможности РВО с id = " + repairStationId + " не найдены!"));
        return ResponseEntity.accepted().body(repairCapabilitiesDTO);
    }

    private RepairCapabilitiesDTO buildRepairCapabilitiesDTO(
            Map.Entry<RepairStation, Map<Equipment, Double>> repairStationCapabilitiesEntry) {
        RepairStation repairStation = repairStationCapabilitiesEntry.getKey();
        return new RepairCapabilitiesDTO(
                new RepairStationDTO(repairStation.getId(), repairStation.getName()),
                repairStationCapabilitiesEntry
                        .getValue()
                        .entrySet()
                        .stream()
                        .map(equipmentCapabilityEntry ->
                                     new RepairCapabilitiesDTO.EquipmentRepairCapabilityDTO(
                                             equipmentCapabilityEntry.getKey().getId(),
                                             equipmentCapabilityEntry.getValue()))
                        .collect(Collectors.toList()));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<RepairCapabilitiesDTO>> getCalculatedRepairCapabilities(@RequestParam(required = false) List<Long> repairStationIds) {
        List<RepairCapabilitiesDTO> repairCapabilitiesDTOList = repairCapabilitiesService
                .getCalculatedRepairCapabilities(repairStationIds)
                .entrySet()
                .stream()
                .map(this::buildRepairCapabilitiesDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(repairCapabilitiesDTOList);
    }

}
