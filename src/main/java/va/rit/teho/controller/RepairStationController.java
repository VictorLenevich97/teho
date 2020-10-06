package va.rit.teho.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.EquipmentStaffDTO;
import va.rit.teho.dto.NestedColumnsDTO;
import va.rit.teho.dto.RepairStationDTO;
import va.rit.teho.dto.TableDataDTO;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.entity.RepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;
import va.rit.teho.model.Pair;
import va.rit.teho.server.TehoSessionData;
import va.rit.teho.service.EquipmentTypeService;
import va.rit.teho.service.RepairStationService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("repair-station")
public class RepairStationController {

    private final RepairStationService repairStationService;
    private final EquipmentTypeService equipmentTypeService;

    public RepairStationController(RepairStationService repairStationService,
                                   EquipmentTypeService equipmentTypeService) {
        this.repairStationService = repairStationService;
        this.equipmentTypeService = equipmentTypeService;
    }

    @Resource
    private TehoSessionData tehoSession;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<RepairStationDTO>> listRepairStations(
            @RequestParam(value = "id", required = false) List<Long> ids) {
        List<RepairStationDTO> repairStationDTOList =
                repairStationService.list(Optional.ofNullable(ids).orElse(Collections.emptyList()))
                                    .stream()
                                    .map(RepairStationDTO::from)
                                    .collect(Collectors.toList());
        return ResponseEntity.ok(repairStationDTOList);
    }

    @GetMapping("/{repairStationId}")
    @ResponseBody
    public ResponseEntity<RepairStationDTO> getRepairStation(@PathVariable Long repairStationId) {
        Pair<RepairStation, List<RepairStationEquipmentStaff>> repairStationListPair =
                repairStationService.get(repairStationId);
        RepairStationDTO repairStationDTO = RepairStationDTO
                .from(repairStationListPair.getLeft())
                .setEquipmentStaff(
                        repairStationListPair
                                .getRight()
                                .stream()
                                .map(EquipmentStaffDTO::from)
                                .collect(Collectors.toList()));
        return ResponseEntity.ok(repairStationDTO);
    }

    @PostMapping
    public ResponseEntity<Object> addRepairStation(@RequestBody RepairStationDTO repairStationDTO) {
        repairStationService.add(repairStationDTO.getName(),
                                 repairStationDTO.getBase().getId(),
                                 repairStationDTO.getType().getId(),
                                 repairStationDTO.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{repairStationId}")
    public ResponseEntity<Object> updateRepairStation(@PathVariable Long repairStationId,
                                                      @RequestBody RepairStationDTO repairStationDTO) {
        repairStationService.update(repairStationId,
                                    repairStationDTO.getName(),
                                    repairStationDTO.getBase().getId(),
                                    repairStationDTO.getType().getId(),
                                    repairStationDTO.getAmount());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/staff")
    public ResponseEntity<TableDataDTO<Map<String, Integer>>> getEquipmentStaff(
            @RequestParam(required = false) List<Long> repairStationId,
            @RequestParam(required = false) List<Long> equipmentTypeId,
            @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        List<RepairStation> repairStationList = repairStationService.list(repairStationId);
        Map<EquipmentType, List<EquipmentSubType>> typesWithSubTypes =
                equipmentTypeService.listTypesWithSubTypes(equipmentTypeId,
                                                           equipmentSubTypeId);
        Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> repairStationEquipmentStaff =
                repairStationService.getRepairStationEquipmentStaff(tehoSession.getSessionId(),
                                                                    repairStationId,
                                                                    equipmentTypeId,
                                                                    equipmentSubTypeId);
        TableDataDTO<Map<String, Integer>> equipmentStaffDTO =
                buildEquipmentStaffDTO(repairStationList,
                                       typesWithSubTypes,
                                       repairStationEquipmentStaff);
        return ResponseEntity.ok(equipmentStaffDTO);
    }

    @PostMapping("/{repairStationId}/staff")
    public ResponseEntity<Object> setRepairStationEquipmentStaff(@PathVariable Long repairStationId,
                                                                 @RequestBody List<EquipmentStaffDTO> equipmentStaffDTOList) {
        repairStationService.setEquipmentStaff(
                equipmentStaffDTOList
                        .stream()
                        .map(equipmentStaffDTO -> equipmentStaffDTO.toEntity(tehoSession.getSessionId(),
                                                                             repairStationId))
                        .collect(Collectors.toList()));
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{repairStationId}/staff")
    public ResponseEntity<Object> updateRepairStationEquipmentStaff(@PathVariable Long repairStationId,
                                                                    @RequestBody List<EquipmentStaffDTO> equipmentStaffDTOList) {
        repairStationService.updateEquipmentStaff(
                equipmentStaffDTOList
                        .stream()
                        .map(equipmentStaffDTO -> equipmentStaffDTO.toEntity(tehoSession.getSessionId(),
                                                                             repairStationId))
                        .collect(Collectors.toList()));
        return ResponseEntity.accepted().build();
    }

    private TableDataDTO<Map<String, Integer>> buildEquipmentStaffDTO(
            List<RepairStation> repairStationList,
            Map<EquipmentType, List<EquipmentSubType>> equipmentTypeListMap,
            Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> repairStationMap) {
        List<EquipmentSubType> columns =
                equipmentTypeListMap
                        .values()
                        .stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        List<NestedColumnsDTO> nestedColumnsTotal =
                Stream
                        .of("total", "available")
                        .flatMap(postfix -> equipmentTypeListMap
                                .entrySet()
                                .stream()
                                .map(entry -> this.getEquipmentStaffNestedColumnsDTO(entry, postfix)))
                        .collect(Collectors.toList());
        List<TableDataDTO.RowData<Map<String, Integer>>> rows =
                repairStationList
                        .stream()
                        .map(rs -> getEquipmentStaffRow(repairStationMap, columns, rs))
                        .collect(Collectors.toList());
        return new TableDataDTO<>(nestedColumnsTotal, rows);
    }

    private NestedColumnsDTO getEquipmentStaffNestedColumnsDTO(
            Map.Entry<EquipmentType, List<EquipmentSubType>> equipmentTypeEntry,
            String postfix) {
        return new NestedColumnsDTO(
                equipmentTypeEntry.getKey().getShortName(),
                equipmentTypeEntry.getValue()
                                  .stream()
                                  .map(est -> new NestedColumnsDTO(Arrays.asList(est.getId().toString(), postfix),
                                                                   est.getShortName()))
                                  .collect(Collectors.toList()));
    }

    private TableDataDTO.RowData<Map<String, Integer>> getEquipmentStaffRow(
            Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> repairStationMap,
            List<EquipmentSubType> columns,
            RepairStation rs) {
        Map<String, Map<String, Integer>> dataMap = new HashMap<>();
        for (EquipmentSubType est : columns) {
            RepairStationEquipmentStaff repairStationEquipmentStaff =
                    repairStationMap.getOrDefault(rs, Collections.emptyMap()).get(est);
            Map<String, Integer> innerMap = new HashMap<>();
            boolean emptyStaff = repairStationEquipmentStaff == null;
            innerMap.put("total", emptyStaff ? 0 : repairStationEquipmentStaff.getTotalStaff());
            innerMap.put("available", emptyStaff ? 0 : repairStationEquipmentStaff.getAvailableStaff());
            dataMap.put(est.getId().toString(), innerMap);
        }
        return new TableDataDTO.RowData<>(rs.getName(), dataMap);
    }


}
