package va.rit.teho.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.RepairCapabilitiesDTO;
import va.rit.teho.dto.RepairCapabilitiesFullDTO;
import va.rit.teho.dto.equipment.EquipmentSubTypeWithEquipmentPerTypeDTO;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.entity.RepairStation;
import va.rit.teho.exception.NotFoundException;
import va.rit.teho.service.EquipmentService;
import va.rit.teho.service.RepairCapabilitiesService;
import va.rit.teho.service.RepairStationService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("repair-capabilities")
public class RepairCapabilitiesController {

    private final RepairCapabilitiesService repairCapabilitiesService;
    private final EquipmentService equipmentService;
    private final RepairStationService repairStationService;

    public RepairCapabilitiesController(
            RepairCapabilitiesService repairCapabilitiesService,
            EquipmentService equipmentService,
            RepairStationService repairStationService) {
        this.repairCapabilitiesService = repairCapabilitiesService;
        this.equipmentService = equipmentService;
        this.repairStationService = repairStationService;
    }

    /**
     * Расчет производственных возможностей РВО по ремонту (сразу для всех РВО по всем ВВСТ).
     */
    @PostMapping("/repair-type/{id}")
    @ResponseBody
    public ResponseEntity<List<RepairCapabilitiesDTO>> calculateAndGet(@PathVariable("id") Long repairTypeId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilities(repairTypeId);
        return getCalculatedRepairCapabilities(repairTypeId,
                                               Collections.emptyList(),
                                               Collections.emptyList(),
                                               Collections.emptyList(),
                                               Collections.emptyList());
    }

    @PostMapping("/repair-type/{id}/repair-station/{repairStationId}")
    @ResponseBody
    public ResponseEntity<RepairCapabilitiesDTO> calculateAndGetPerStation(@PathVariable("id") Long repairTypeId,
                                                                           @PathVariable Long repairStationId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilitiesPerStation(repairStationId, repairTypeId);
        RepairCapabilitiesDTO repairCapabilitiesDTO =
                Optional.ofNullable(
                        getCalculatedRepairCapabilities(repairTypeId,
                                                        Collections.singletonList(repairStationId),
                                                        Collections.emptyList(),
                                                        Collections.emptyList(),
                                                        Collections.emptyList()).getBody())
                        .flatMap(v -> v.stream().findFirst())
                        .orElseThrow(() -> new NotFoundException("Производственные возможности РВО с id = " + repairStationId + " не найдены!"));
        return ResponseEntity.accepted().body(repairCapabilitiesDTO);
    }

    private RepairCapabilitiesDTO buildRepairCapabilitiesDTO(
            Map.Entry<RepairStation, Map<Equipment, Double>> repairStationCapabilitiesEntry) {
        RepairStation repairStation = repairStationCapabilitiesEntry.getKey();
        return new RepairCapabilitiesDTO(
                repairStation.getId(),
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
    public ResponseEntity<List<RepairCapabilitiesDTO>> getCalculatedRepairCapabilities(
            @RequestParam(required = false) List<Long> repairStationId,
            @RequestParam(required = false) List<Long> equipmentId,
            @RequestParam(required = false) List<Long> equipmentTypeId,
            @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        List<RepairCapabilitiesDTO> repairCapabilitiesDTOList = repairCapabilitiesService
                .getCalculatedRepairCapabilities(repairStationId,
                                                 equipmentId,
                                                 equipmentSubTypeId,
                                                 equipmentTypeId)
                .entrySet()
                .stream()
                .map(this::buildRepairCapabilitiesDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(repairCapabilitiesDTOList);
    }

    @GetMapping("/new")
    @ResponseBody
    public ResponseEntity<RepairCapabilitiesFullDTO> getCalculatedRepairCapabilitiesAsOne(
            @RequestParam(required = false) List<Long> repairStationId,
            @RequestParam(required = false) List<Long> equipmentId,
            @RequestParam(required = false) List<Long> equipmentTypeId,
            @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        List<RepairStation> repairStationList = repairStationService.list(repairStationId);
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped = equipmentService.listGroupedByTypesSorted(
                equipmentId,
                equipmentSubTypeId,
                equipmentTypeId);
        Map<RepairStation, Map<Equipment, Double>> calculatedRepairCapabilities = repairCapabilitiesService
                .getCalculatedRepairCapabilities(repairStationId,
                                                 equipmentId,
                                                 equipmentSubTypeId,
                                                 equipmentTypeId);
        List<Equipment> collected = grouped.values()
                                           .stream()
                                           .flatMap(e -> e.values().stream())
                                           .flatMap(List::stream)
                                           .collect(Collectors.toList());
        Double[][] data = new Double[repairStationList.size()][collected.size()];
        for (Double[] dataRow : data) {
            Arrays.fill(dataRow, 0.0);
        }
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = calculatedRepairCapabilities.getOrDefault(repairStationList.get(i),
                                                                       Collections.emptyMap())
                                                         .getOrDefault(collected.get(j), 0.0);
            }
        }
        List<EquipmentSubTypeWithEquipmentPerTypeDTO> equipmentPerTypeDTOList =
                grouped.entrySet()
                       .stream()
                       .map(equipmentTypeEntry ->
                                    EquipmentSubTypeWithEquipmentPerTypeDTO.from(equipmentTypeEntry.getKey(),
                                                                                 equipmentTypeEntry.getValue()))
                       .collect(Collectors.toList());
        return ResponseEntity.ok(new RepairCapabilitiesFullDTO(repairStationList.stream()
                                                                                .map(RepairStation::getName)
                                                                                .collect(Collectors.toList()),
                                                               equipmentPerTypeDTOList,
                                                               data));
    }

    @GetMapping("/repair-type/{id}")
    @ResponseBody
    public ResponseEntity<List<RepairCapabilitiesDTO>> getCalculatedRepairCapabilities(
            @PathVariable("id") Long repairTypeId,
            @RequestParam(required = false) List<Long> repairStationId,
            @RequestParam(required = false) List<Long> equipmentId,
            @RequestParam(required = false) List<Long> equipmentTypeId,
            @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        List<RepairCapabilitiesDTO> repairCapabilitiesDTOList = repairCapabilitiesService
                .getCalculatedRepairCapabilities(repairTypeId,
                                                 repairStationId,
                                                 equipmentId,
                                                 equipmentSubTypeId,
                                                 equipmentTypeId)
                .entrySet()
                .stream()
                .map(this::buildRepairCapabilitiesDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(repairCapabilitiesDTOList);
    }

}
