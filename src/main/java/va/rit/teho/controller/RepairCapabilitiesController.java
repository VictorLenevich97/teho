package va.rit.teho.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.NestedColumnsDTO;
import va.rit.teho.dto.TableDataDTO;
import va.rit.teho.entity.*;
import va.rit.teho.service.EquipmentService;
import va.rit.teho.service.EquipmentTypeService;
import va.rit.teho.service.RepairCapabilitiesService;
import va.rit.teho.service.RepairStationService;

import java.util.*;
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
    public ResponseEntity<TableDataDTO<Double>> calculateAndGet(@PathVariable("id") Long repairTypeId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilities(repairTypeId);
        return getCalculatedRepairCapabilities(repairTypeId,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
    }

    @PostMapping("/repair-type/{id}/repair-station/{repairStationId}")
    @ResponseBody
    public ResponseEntity<TableDataDTO<Double>> calculateAndGetPerStation(@PathVariable("id") Long repairTypeId,
                                                                          @PathVariable Long repairStationId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilitiesPerStation(repairStationId, repairTypeId);
        TableDataDTO<Double> repairCapabilitiesDTO =
                getCalculatedRepairCapabilities(repairTypeId,
                        Collections.singletonList(repairStationId),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()).getBody();
        return ResponseEntity.accepted().body(repairCapabilitiesDTO);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<TableDataDTO<Double>> getCalculatedRepairCapabilities(
            @RequestParam(required = false) List<Long> repairStationId,
            @RequestParam(required = false) List<Long> equipmentId,
            @RequestParam(required = false) List<Long> equipmentTypeId,
            @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        List<RepairStation> repairStationList = repairStationService.list(repairStationId);
        Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped =
                equipmentService.listGroupedByTypes(equipmentId, equipmentSubTypeId, equipmentTypeId);
        Map<RepairStation, Map<Equipment, Double>> calculatedRepairCapabilities =
                repairCapabilitiesService.getCalculatedRepairCapabilities(repairStationId,
                        equipmentId,
                        equipmentSubTypeId,
                        equipmentTypeId);
        TableDataDTO<Double> repairCapabilitiesFullDTO = buildRepairCapabilitiesDTO(repairStationList,
                grouped,
                calculatedRepairCapabilities);
        return ResponseEntity.ok(repairCapabilitiesFullDTO);
    }

    private NestedColumnsDTO getRepairCapabilitiesNestedColumnsDTO(Map.Entry<EquipmentType, Map<EquipmentSubType, List<Equipment>>> equipmentTypeEntry) {
        return new NestedColumnsDTO(
                equipmentTypeEntry.getKey().getShortName(),
                equipmentTypeEntry.getValue()
                        .entrySet()
                        .stream()
                        .map(subTypeListEntry ->
                                new NestedColumnsDTO(subTypeListEntry.getKey().getShortName(),
                                        subTypeListEntry.getValue()
                                                .stream()
                                                .map(e -> new NestedColumnsDTO(e.getId(), e.getName()))
                                                .collect(Collectors.toList())))
                        .collect(Collectors.toList()));
    }

    private TableDataDTO<Double> buildRepairCapabilitiesDTO(List<RepairStation> repairStationList,
                                                            Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>> grouped,
                                                            Map<RepairStation, Map<Equipment, Double>> calculatedRepairCapabilities) {
        List<Equipment> columns =
                grouped
                        .values()
                        .stream()
                        .flatMap(l -> l.values().stream())
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        List<NestedColumnsDTO> equipmentPerTypeDTOList =
                grouped.entrySet()
                        .stream()
                        .map(this::getRepairCapabilitiesNestedColumnsDTO)
                        .collect(Collectors.toList());
        List<TableDataDTO.RowData<Double>> data =
                repairStationList
                        .stream()
                        .map(rs -> getRepairCapabilitiesRow(calculatedRepairCapabilities, columns, rs))
                        .collect(Collectors.toList());
        return new TableDataDTO<>(equipmentPerTypeDTOList, data);
    }

    private TableDataDTO.RowData<Double> getRepairCapabilitiesRow(
            Map<RepairStation, Map<Equipment, Double>> calculatedRepairCapabilities,
            List<Equipment> columns,
            RepairStation rs) {
        return new TableDataDTO.RowData<>(
                rs.getName(),
                columns
                        .stream()
                        .collect(Collectors.toMap(
                                equipment -> equipment.getId().toString(),
                                equipment -> calculatedRepairCapabilities
                                        .getOrDefault(rs, Collections.emptyMap())
                                        .getOrDefault(equipment, 0.0))));
    }

    @GetMapping("/repair-type/{id}")
    @ResponseBody
    public ResponseEntity<TableDataDTO<Double>> getCalculatedRepairCapabilities(
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
        TableDataDTO<Double> repairCapabilitiesFullDTO = buildRepairCapabilitiesDTO(repairStationList,
                grouped,
                calculatedRepairCapabilities);
        return ResponseEntity.ok(repairCapabilitiesFullDTO);
    }

    private NestedColumnsDTO getEquipmentStaffNestedColumnsDTO(
            Map.Entry<EquipmentType, List<EquipmentSubType>> equipmentTypeEntry,
            String postfix) {
        return new NestedColumnsDTO(
                equipmentTypeEntry.getKey().getShortName(),
                equipmentTypeEntry.getValue()
                        .stream()
                        .map(est -> new NestedColumnsDTO(est.getId() + "." + postfix, est.getShortName()))
                        .collect(Collectors.toList()));
    }

    private TableDataDTO<Map<String, Integer>> buildEquipmentStaffDTO(List<RepairStation> repairStationList,
                                                         Map<EquipmentType, List<EquipmentSubType>> equipmentTypeListMap,
                                                         Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> repairStationMap) {
        List<EquipmentSubType> columns =
                equipmentTypeListMap
                        .values()
                        .stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        List<NestedColumnsDTO> nestedColumnsTotal =
                equipmentTypeListMap.entrySet()
                        .stream()
                        .map(entry -> this.getEquipmentStaffNestedColumnsDTO(entry, "total"))
                        .collect(Collectors.toList());
        nestedColumnsTotal.addAll(
                equipmentTypeListMap.entrySet()
                        .stream()
                        .map(entry -> this.getEquipmentStaffNestedColumnsDTO(entry, "available"))
                        .collect(Collectors.toList()));
        List<TableDataDTO.RowData<Map<String, Integer>>> rows = repairStationList
                .stream()
                .map(rs -> {
                    Map<String, Map<String, Integer>> dataMap = new HashMap<>();
                    columns.forEach(est -> {
                        RepairStationEquipmentStaff repairStationEquipmentStaff =
                                repairStationMap.getOrDefault(rs, Collections.emptyMap()).get(est);
                        Map<String, Integer> innerMap = new HashMap<>();
                        innerMap.put("total", repairStationEquipmentStaff == null ? 0 : repairStationEquipmentStaff.getTotalStaff());
                        innerMap.put("available", repairStationEquipmentStaff == null ? 0 : repairStationEquipmentStaff.getAvailableStaff());
                        dataMap.put(est.getId().toString(), innerMap);
                    });
                    return new TableDataDTO.RowData<>(rs.getName(), dataMap);
                })
                .collect(Collectors.toList());
        return new TableDataDTO<>(nestedColumnsTotal, rows);
    }

    @GetMapping("/staff")
    public ResponseEntity<TableDataDTO<Map<String, Integer>>> getEquipmentStaffData(
            @RequestParam(required = false) List<Long> repairStationId,
            @RequestParam(required = false) List<Long> equipmentTypeId,
            @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        List<RepairStation> repairStationList = repairStationService.list(repairStationId);
        Map<EquipmentType, List<EquipmentSubType>> typesWithSubTypes =
                equipmentTypeService.listTypesWithSubTypes(equipmentTypeId, equipmentSubTypeId);
        Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> repairStationEquipmentStaff =
                repairCapabilitiesService.getRepairStationEquipmentStaff(repairStationId,
                        equipmentTypeId,
                        equipmentSubTypeId);
        TableDataDTO<Map<String, Integer>> equipmentStaffDTO =
                buildEquipmentStaffDTO(repairStationList, typesWithSubTypes, repairStationEquipmentStaff);
        return ResponseEntity.ok(equipmentStaffDTO);
    }

}
