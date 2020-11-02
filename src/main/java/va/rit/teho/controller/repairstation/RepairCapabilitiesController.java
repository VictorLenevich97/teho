package va.rit.teho.controller.repairstation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairstation.RepairStation;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.repairstation.RepairCapabilitiesService;
import va.rit.teho.service.repairstation.RepairStationService;

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
    public ResponseEntity<TableDataDTO<Map<String, Double>>> calculateAndGet(@PathVariable("id") Long repairTypeId) {
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
    public ResponseEntity<TableDataDTO<Map<String, Double>>> calculateAndGetPerStation(@PathVariable("id") Long repairTypeId,
                                                                                       @PathVariable Long repairStationId) {
        this.repairCapabilitiesService.calculateAndUpdateRepairCapabilitiesPerStation(tehoSession.getSessionId(),
                                                                                      repairStationId,
                                                                                      repairTypeId);
        TableDataDTO<Map<String, Double>> repairCapabilitiesDTO =
                getCalculatedRepairCapabilities(repairTypeId,
                                                Collections.singletonList(repairStationId),
                                                Collections.emptyList(),
                                                Collections.emptyList(),
                                                Collections.emptyList()).getBody();
        return ResponseEntity.accepted().body(repairCapabilitiesDTO);
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

    private TableDataDTO<Map<String, Double>> buildRepairCapabilitiesDTO(List<RepairStation> repairStationList,
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
        List<RowData<Map<String, Double>>> data =
                repairStationList
                        .stream()
                        .map(rs -> getRepairCapabilitiesRow(calculatedRepairCapabilities, columns, rs))
                        .collect(Collectors.toList());
        return new TableDataDTO<>(equipmentPerTypeDTOList, data);
    }

    private RowData<Map<String, Double>> getRepairCapabilitiesRow(
            Map<RepairStation, Map<Equipment, Double>> calculatedRepairCapabilities,
            List<Equipment> columns,
            RepairStation rs) {
        return new RowData<>(
                rs.getId(),
                rs.getName(),
                columns.stream().collect(Collectors.toMap(equipment -> equipment.getId().toString(),
                                                          equipment -> calculatedRepairCapabilities
                                                                  .getOrDefault(rs, Collections.emptyMap())
                                                                  .getOrDefault(equipment, 0.0))));
    }

    @GetMapping("/repair-type/{id}")
    @ResponseBody
    public ResponseEntity<TableDataDTO<Map<String, Double>>> getCalculatedRepairCapabilities(
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
        TableDataDTO<Map<String, Double>> repairCapabilitiesFullDTO = buildRepairCapabilitiesDTO(repairStationList,
                                                                                                 grouped,
                                                                                                 calculatedRepairCapabilities);
        return ResponseEntity.ok(repairCapabilitiesFullDTO);
    }

}
