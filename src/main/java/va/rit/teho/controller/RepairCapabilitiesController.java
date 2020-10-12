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
import va.rit.teho.server.TehoSessionData;
import va.rit.teho.service.EquipmentService;
import va.rit.teho.service.RepairCapabilitiesService;
import va.rit.teho.service.RepairStationService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Resource
    private TehoSessionData tehoSession;

    /**
     * Расчет производственных возможностей РВО по ремонту (сразу для всех РВО по всем ВВСТ).
     */
    @PostMapping("/repair-type/{id}")
    @ResponseBody
    public ResponseEntity<TableDataDTO<Double>> calculateAndGet(@PathVariable("id") Long repairTypeId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilities(tehoSession.getSessionId(),
                                                                            repairTypeId);
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
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilitiesPerStation(tehoSession.getSessionId(),
                                                                                      repairStationId,
                                                                                      repairTypeId);
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
                equipmentService.listGroupedByTypes(equipmentId,
                                                    equipmentSubTypeId,
                                                    equipmentTypeId);
        Map<RepairStation, Map<Equipment, Double>> calculatedRepairCapabilities =
                repairCapabilitiesService.getCalculatedRepairCapabilities(tehoSession.getSessionId(),
                                                                          repairStationId,
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
                                                .map(e -> new NestedColumnsDTO(e.getId().toString(), e.getName()))
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
                rs.getId(),
                rs.getName(),
                columns.stream().collect(Collectors.toMap(equipment -> equipment.getId().toString(),
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
                equipmentService.listGroupedByTypes(equipmentId,
                                                    equipmentSubTypeId,
                                                    equipmentTypeId);
        Map<RepairStation, Map<Equipment, Double>> calculatedRepairCapabilities =
                repairCapabilitiesService.getCalculatedRepairCapabilities(tehoSession.getSessionId(),
                                                                          repairTypeId,
                                                                          repairStationId,
                                                                          equipmentId,
                                                                          equipmentSubTypeId,
                                                                          equipmentTypeId);
        TableDataDTO<Double> repairCapabilitiesFullDTO = buildRepairCapabilitiesDTO(repairStationList,
                                                                                    grouped,
                                                                                    calculatedRepairCapabilities);
        return ResponseEntity.ok(repairCapabilitiesFullDTO);
    }

}
