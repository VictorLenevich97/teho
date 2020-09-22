package va.rit.teho.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.NestedColumnsDTO;
import va.rit.teho.dto.TableDataDTO;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.entity.RepairStation;
import va.rit.teho.service.EquipmentService;
import va.rit.teho.service.EquipmentTypeService;
import va.rit.teho.service.RepairCapabilitiesService;
import va.rit.teho.service.RepairStationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("repair-capabilities")
public class RepairCapabilitiesController {

    private final RepairCapabilitiesService repairCapabilitiesService;
    private final EquipmentService equipmentService;
    private final EquipmentTypeService equipmentTypeService;
    private final RepairStationService repairStationService;

    public RepairCapabilitiesController(
            RepairCapabilitiesService repairCapabilitiesService,
            EquipmentService equipmentService,
            EquipmentTypeService equipmentTypeService,
            RepairStationService repairStationService) {
        this.repairCapabilitiesService = repairCapabilitiesService;
        this.equipmentService = equipmentService;
        this.equipmentTypeService = equipmentTypeService;
        this.repairStationService = repairStationService;
    }

    /**
     * Расчет производственных возможностей РВО по ремонту (сразу для всех РВО по всем ВВСТ).
     */
    @PostMapping("/repair-type/{id}")
    @ResponseBody
    public ResponseEntity<TableDataDTO> calculateAndGet(@PathVariable("id") Long repairTypeId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilities(repairTypeId);
        return getCalculatedRepairCapabilities(repairTypeId,
                                               Collections.emptyList(),
                                               Collections.emptyList(),
                                               Collections.emptyList(),
                                               Collections.emptyList());
    }

    @PostMapping("/repair-type/{id}/repair-station/{repairStationId}")
    @ResponseBody
    public ResponseEntity<TableDataDTO> calculateAndGetPerStation(@PathVariable("id") Long repairTypeId,
                                                                  @PathVariable Long repairStationId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilitiesPerStation(repairStationId, repairTypeId);
        TableDataDTO repairCapabilitiesDTO =
                getCalculatedRepairCapabilities(repairTypeId,
                                                Collections.singletonList(repairStationId),
                                                Collections.emptyList(),
                                                Collections.emptyList(),
                                                Collections.emptyList()).getBody();
        return ResponseEntity.accepted().body(repairCapabilitiesDTO);
    }

    @GetMapping("/repair-type/{id}")
    @ResponseBody
    public ResponseEntity<TableDataDTO> getCalculatedRepairCapabilities(
            @PathVariable("id") Long repairTypeId,
            @RequestParam(required = false) List<Long> repairStationId,
            @RequestParam(required = false) List<Long> equipmentId,
            @RequestParam(required = false) List<Long> equipmentTypeId,
            @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        List<RepairStation> repairStationList = repairStationService.list(repairStationId);
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped =
                equipmentService.listGroupedByTypes(equipmentId, equipmentSubTypeId, equipmentTypeId);
        Map<RepairStation, Map<Equipment, Double>> calculatedRepairCapabilities =
                repairCapabilitiesService.getCalculatedRepairCapabilities(repairTypeId,
                                                                          repairStationId,
                                                                          equipmentId,
                                                                          equipmentSubTypeId,
                                                                          equipmentTypeId);
        TableDataDTO repairCapabilitiesFullDTO = buildRepairCapabilitiesDTO(repairStationList,
                                                                            grouped,
                                                                            calculatedRepairCapabilities);
        return ResponseEntity.ok(repairCapabilitiesFullDTO);
    }

    private TableDataDTO buildRepairCapabilitiesDTO(List<RepairStation> repairStationList,
                                                    Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped,
                                                    Map<RepairStation, Map<Equipment, Double>> calculatedRepairCapabilities) {
        List<Equipment> flattenedColumns =
                grouped.values()
                       .stream()
                       .flatMap(e -> e.values().stream())
                       .flatMap(List::stream)
                       .collect(Collectors.toList());
        Double[][] data = mapTableData(repairStationList, calculatedRepairCapabilities, flattenedColumns);
        List<NestedColumnsDTO> equipmentPerTypeDTOList =
                grouped.entrySet()
                       .stream()
                       .map(this::getNestedColumnsDTO)
                       .collect(Collectors.toList());
        List<String> rsNames = repairStationList.stream().map(RepairStation::getName).collect(Collectors.toList());
        return new TableDataDTO(rsNames, equipmentPerTypeDTOList, data);
    }

    private NestedColumnsDTO getNestedColumnsDTO(Map.Entry<EquipmentType, Map<EquipmentSubType, List<Equipment>>> equipmentTypeEntry) {
        return new NestedColumnsDTO(
                equipmentTypeEntry.getKey().getShortName(),
                equipmentTypeEntry.getValue()
                                  .entrySet()
                                  .stream()
                                  .map(subTypeListEntry ->
                                               new NestedColumnsDTO(subTypeListEntry.getKey().getShortName(),
                                                                    subTypeListEntry.getValue()
                                                                                    .stream()
                                                                                    .map(e -> new NestedColumnsDTO(e.getName(),
                                                                                                                   null))
                                                                                    .collect(Collectors.toList())))
                                  .collect(Collectors.toList()));
    }

    private Double[][] mapTableData(List<RepairStation> repairStationList,
                                    Map<RepairStation, Map<Equipment, Double>> calculatedRepairCapabilities,
                                    List<Equipment> equipmentList) {
        Double[][] data = new Double[repairStationList.size()][equipmentList.size()];
        Arrays.stream(data).forEach(dataRow -> Arrays.fill(dataRow, 0.0));
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = calculatedRepairCapabilities.getOrDefault(repairStationList.get(i),
                                                                       Collections.emptyMap())
                                                         .getOrDefault(equipmentList.get(j), 0.0);
            }
        }
        return data;
    }

    public ResponseEntity<TableDataDTO> getEquipmentStaffData(
            @RequestParam(required = false) List<Long> repairStationId) {
        List<RepairStation> repairStationList = repairStationService.list(repairStationId);
        Map<EquipmentType, List<EquipmentSubType>> typesWithSubTypes = this.equipmentTypeService.listTypesWithSubTypes(
                null,
                null);
        //repairStationList.get(0)

        return null;
    }

}
