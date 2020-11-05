package va.rit.teho.controller.repairstation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.repairstation.RepairStationDTO;
import va.rit.teho.dto.repairstation.RepairStationStaffDTO;
import va.rit.teho.dto.repairstation.RepairStationStaffRowData;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairstation.RepairStation;
import va.rit.teho.entity.repairstation.RepairStationEquipmentStaff;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.repairstation.RepairStationService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "repair-station", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "РВО")
public class RepairStationController {

    private static final String KEY_TOTAL_STAFF = "total";
    private static final String KEY_AVAILABLE_STAFF = "available";

    private static final Map<String, String> STAFF_KEYS_AND_TEXT = new HashMap<>();

    static {
        STAFF_KEYS_AND_TEXT.put(KEY_TOTAL_STAFF, "По штату, чел.");
        STAFF_KEYS_AND_TEXT.put(KEY_AVAILABLE_STAFF, "В наличии, чел.");
    }

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
    @ApiOperation(value = "Получить список РВО")
    public ResponseEntity<List<RepairStationDTO>> listRepairStations(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(value = "id", required = false) List<Long> ids) {
        List<RepairStationDTO> repairStationDTOList =
                repairStationService.list(Optional.ofNullable(ids).orElse(Collections.emptyList()))
                                    .stream()
                                    .map(RepairStationDTO::from)
                                    .collect(Collectors.toList());
        return ResponseEntity.ok(repairStationDTOList);
    }

    @GetMapping("/{repairStationId}")
    @ResponseBody
    @ApiOperation(value = "Получить детальные данные по РВО")
    public ResponseEntity<RepairStationDTO> getRepairStation(@ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairStationId) {
        Pair<RepairStation, List<RepairStationEquipmentStaff>> repairStationListPair =
                repairStationService.get(repairStationId);
        RepairStationDTO repairStationDTO = RepairStationDTO
                .from(repairStationListPair.getFirst())
                .setStaff(
                        repairStationListPair
                                .getSecond()
                                .stream()
                                .map(RepairStationStaffDTO::from)
                                .collect(Collectors.toList()));
        return ResponseEntity.ok(repairStationDTO);
    }

    @PostMapping
    @ApiOperation(value = "Добавление РВО")
    public ResponseEntity<Object> addRepairStation(@ApiParam(value = "Данные по РВО", required = true) @RequestBody RepairStationDTO repairStationDTO) {
        repairStationService.add(repairStationDTO.getName(),
                                 repairStationDTO.getBase().getId(),
                                 repairStationDTO.getType().getId(),
                                 repairStationDTO.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{repairStationId}")
    @ApiOperation(value = "Обновление РВО")
    public ResponseEntity<Object> updateRepairStation(@ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairStationId,
                                                      @ApiParam(value = "Данные по РВО", required = true) @RequestBody RepairStationDTO repairStationDTO) {
        repairStationService.update(repairStationId,
                                    repairStationDTO.getName(),
                                    repairStationDTO.getBase().getId(),
                                    repairStationDTO.getType().getId(),
                                    repairStationDTO.getAmount());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/staff")
    @ApiOperation(value = "Получение состава, штатной численности и укомплектованности РВО (в табличном виде)")
    public ResponseEntity<TableDataDTO<Map<String, RepairStationStaffDTO>>> getEquipmentStaff(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairStationId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @ApiParam(value = "Ключи подтипов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        List<RepairStation> repairStationList = repairStationService.list(repairStationId);
        Map<EquipmentType, List<EquipmentSubType>> typesWithSubTypes =
                equipmentTypeService.listTypesWithSubTypes(equipmentTypeId,
                                                           equipmentSubTypeId);
        Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> repairStationEquipmentStaff =
                repairStationService.getRepairStationEquipmentStaffGrouped(tehoSession.getSessionId(),
                                                                           repairStationId,
                                                                           equipmentTypeId,
                                                                           equipmentSubTypeId);
        TableDataDTO<Map<String, RepairStationStaffDTO>> equipmentStaffDTO =
                buildEquipmentStaffDTO(repairStationList,
                                       typesWithSubTypes,
                                       repairStationEquipmentStaff);
        return ResponseEntity.ok(equipmentStaffDTO);
    }

    @PutMapping("/{repairStationId}/staff")
    @ApiOperation(value = "Обновление информации по личному составу РВО")
    public ResponseEntity<Object> updateRepairStationEquipmentStaff(@ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairStationId,
                                                                    @ApiParam(value = "Данные по численности л/с", required = true) @RequestBody Map<Long, RepairStationStaffDTO> staff) {
        repairStationService.updateEquipmentStaff(
                staff
                        .entrySet()
                        .stream()
                        .map(staffDTOEntry -> staffDTOEntry.getValue().toEntity(tehoSession.getSessionId(),
                                                                                staffDTOEntry.getKey(),
                                                                                repairStationId))
                        .collect(Collectors.toList()));
        return ResponseEntity.accepted().build();
    }

    private TableDataDTO<Map<String, RepairStationStaffDTO>> buildEquipmentStaffDTO(
            List<RepairStation> repairStationList,
            Map<EquipmentType, List<EquipmentSubType>> equipmentTypeListMap,
            Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> repairStationMap) {
        List<EquipmentSubType> columns =
                equipmentTypeListMap
                        .values()
                        .stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        List<NestedColumnsDTO> nestedColumnsTotal = equipmentTypeListMap
                .entrySet()
                .stream()
                .map(this::getEquipmentStaffNestedColumnsDTO)
                .collect(Collectors.toList());
        //TODO: вернуть старую иерархию столбцов при необходимости
//        List<NestedColumnsDTO> nestedColumnsTotal =
//                STAFF_KEYS_AND_TEXT
//                        .entrySet()
//                        .stream()
//                        .map(staffKeyEntry ->
//                                     new NestedColumnsDTO(staffKeyEntry.getValue(), equipmentTypeListMap
//                                             .entrySet()
//                                             .stream()
//                                             .map(entry -> this.getEquipmentStaffNestedColumnsDTO(entry,
//                                                                                                  staffKeyEntry.getKey()))
//                                             .collect(Collectors.toList())))
//                        .collect(Collectors.toList());
        List<RowData<Map<String, RepairStationStaffDTO>>> rows = repairStationList
                .stream()
                .map(rs -> getEquipmentStaffRow(repairStationMap, columns, rs))
                .collect(Collectors.toList());
        return new TableDataDTO<>(nestedColumnsTotal, rows);
    }

    private NestedColumnsDTO getEquipmentStaffNestedColumnsDTO(
            Map.Entry<EquipmentType, List<EquipmentSubType>> equipmentTypeEntry) {
        return new NestedColumnsDTO(
                equipmentTypeEntry.getKey().getShortName(),
                equipmentTypeEntry.getValue()
                                  .stream()
                                  .map(est -> new NestedColumnsDTO(est.getShortName(),
                                                                   STAFF_KEYS_AND_TEXT
                                                                           .entrySet()
                                                                           .stream()
                                                                           .map(e -> new NestedColumnsDTO(Arrays.asList(
                                                                                   est.getId().toString(),
                                                                                   e.getKey()), e.getValue()))
                                                                           .collect(
                                                                                   Collectors.toList())))
                                  .collect(Collectors.toList()));
    }

    private RowData<Map<String, RepairStationStaffDTO>> getEquipmentStaffRow(
            Map<RepairStation, Map<EquipmentSubType, RepairStationEquipmentStaff>> repairStationMap,
            List<EquipmentSubType> columns,
            RepairStation rs) {
        Map<String, RepairStationStaffDTO> dataMap = new HashMap<>();
        for (EquipmentSubType est : columns) {
            RepairStationEquipmentStaff repairStationEquipmentStaff =
                    repairStationMap.getOrDefault(rs, Collections.emptyMap()).get(est);
            boolean emptyStaff = repairStationEquipmentStaff == null;
            int totalStaff = emptyStaff || repairStationEquipmentStaff.getTotalStaff() == null ? 0 : repairStationEquipmentStaff
                    .getTotalStaff();
            int availableStaff = emptyStaff || repairStationEquipmentStaff.getAvailableStaff() == null ? 0 : repairStationEquipmentStaff
                    .getAvailableStaff();
            dataMap.put(est.getId().toString(), new RepairStationStaffDTO(totalStaff, availableStaff));
        }
        return new RepairStationStaffRowData(rs.getId(),
                                             rs.getName(),
                                             dataMap,
                                             rs.getRepairStationType().getName(),
                                             rs.getStationAmount());
    }


}
