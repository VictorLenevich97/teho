package va.rit.teho.controller.repairdivision;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.repairdivision.RepairDivisionUnitDTO;
import va.rit.teho.dto.repairdivision.RepairDivisionUnitEquipmentStaffDTO;
import va.rit.teho.dto.repairdivision.RepairDivisionUnitEquipmentStaffRowData;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairdivision.RepairDivisionUnit;
import va.rit.teho.entity.repairdivision.RepairDivisionUnitEquipmentStaff;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.repairdivision.RepairDivisionService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "repair-division", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "РВО")
public class RepairDivisionController {

    private static final String KEY_TOTAL_STAFF = "total";
    private static final String KEY_AVAILABLE_STAFF = "available";

    private static final Map<String, String> STAFF_KEYS_AND_TEXT = new HashMap<>();

    static {
        STAFF_KEYS_AND_TEXT.put(KEY_TOTAL_STAFF, "По штату, чел.");
        STAFF_KEYS_AND_TEXT.put(KEY_AVAILABLE_STAFF, "В наличии, чел.");
    }

    private final RepairDivisionService repairDivisionService;
    private final EquipmentTypeService equipmentTypeService;

    public RepairDivisionController(RepairDivisionService repairDivisionService,
                                    EquipmentTypeService equipmentTypeService) {
        this.repairDivisionService = repairDivisionService;
        this.equipmentTypeService = equipmentTypeService;
    }

    @Resource
    private TehoSessionData tehoSession;

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить список РВО")
    public ResponseEntity<List<RepairDivisionUnitDTO>> listRepairDivisionUnits(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(value = "id", required = false) List<Long> ids) {
        List<RepairDivisionUnitDTO> repairDivisionUnitDTOList =
                repairDivisionService.listUnits(Optional.ofNullable(ids).orElse(Collections.emptyList()))
                                     .stream()
                                     .map(RepairDivisionUnitDTO::from)
                                     .collect(Collectors.toList());
        return ResponseEntity.ok(repairDivisionUnitDTOList);
    }

    @GetMapping("/{repairDivisionUnitId}")
    @ResponseBody
    @ApiOperation(value = "Получить детальные данные по РВО")
    public ResponseEntity<RepairDivisionUnitDTO> getRepairDivisionUnit(@ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairDivisionUnitId) {
        Pair<RepairDivisionUnit, List<RepairDivisionUnitEquipmentStaff>> repairDivisionUnitListPair =
                repairDivisionService.getUnitWithStaff(repairDivisionUnitId, tehoSession.getSessionId());
        RepairDivisionUnitDTO repairDivisionUnitDTO = RepairDivisionUnitDTO
                .from(repairDivisionUnitListPair.getFirst())
                .setStaff(
                        repairDivisionUnitListPair
                                .getSecond()
                                .stream()
                                .map(RepairDivisionUnitEquipmentStaffDTO::from)
                                .collect(Collectors.toList()));
        return ResponseEntity.ok(repairDivisionUnitDTO);
    }

    @PostMapping
    @ApiOperation(value = "Добавление РВО")
    public ResponseEntity<Object> addRepairDivisionUnit(@ApiParam(value = "Данные по РВО", required = true) @RequestBody RepairDivisionUnitDTO repairDivisionUnitDTO) {
        repairDivisionService.addUnit(repairDivisionUnitDTO.getName(),
                                      repairDivisionUnitDTO.getType().getId(),
                                      repairDivisionUnitDTO.getStationType().getId(),
                                      repairDivisionUnitDTO.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{repairDivisionUnitId}")
    @ApiOperation(value = "Обновление РВО")
    public ResponseEntity<Object> updateRepairDivisionUnit(@ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairDivisionUnitId,
                                                           @ApiParam(value = "Данные по РВО", required = true) @RequestBody RepairDivisionUnitDTO repairDivisionUnitDTO) {
        repairDivisionService.updateUnit(repairDivisionUnitId,
                                         repairDivisionUnitDTO.getName(),
                                         repairDivisionUnitDTO.getType().getId(),
                                         repairDivisionUnitDTO.getStationType().getId(),
                                         repairDivisionUnitDTO.getAmount());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/staff")
    @ApiOperation(value = "Получение состава, штатной численности и укомплектованности РВО (в табличном виде)")
    public ResponseEntity<TableDataDTO<Map<String, RepairDivisionUnitEquipmentStaffDTO>>> getEquipmentStaff(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairDivisionUnitId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @ApiParam(value = "Ключи подтипов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        List<RepairDivisionUnit> repairDivisionUnitList = repairDivisionService.listUnits(repairDivisionUnitId);
        Map<EquipmentType, List<EquipmentSubType>> typesWithSubTypes =
                equipmentTypeService.listTypesWithSubTypes(equipmentTypeId,
                                                           equipmentSubTypeId);
        Map<RepairDivisionUnit, Map<EquipmentSubType, RepairDivisionUnitEquipmentStaff>> repairDivisionUnitEquipmentStaff =
                repairDivisionService.getRepairDivisionUnitEquipmentStaffGrouped(tehoSession.getSessionId(),
                                                                                 repairDivisionUnitId,
                                                                                 equipmentTypeId,
                                                                                 equipmentSubTypeId);
        TableDataDTO<Map<String, RepairDivisionUnitEquipmentStaffDTO>> equipmentStaffDTO =
                buildEquipmentStaffDTO(repairDivisionUnitList,
                                       typesWithSubTypes,
                                       repairDivisionUnitEquipmentStaff);
        return ResponseEntity.ok(equipmentStaffDTO);
    }

    @PutMapping("/{repairDivisionUnitId}/staff")
    @ApiOperation(value = "Обновление информации по личному составу РВО")
    public ResponseEntity<Object> updateRepairDivisionUnitEquipmentStaff(@ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairDivisionUnitId,
                                                                         @ApiParam(value = "Данные по численности л/с", required = true) @RequestBody Map<Long, RepairDivisionUnitEquipmentStaffDTO> staff) {
        repairDivisionService.updateUnitEquipmentStaff(
                staff
                        .entrySet()
                        .stream()
                        .map(staffDTOEntry -> staffDTOEntry.getValue().toEntity(tehoSession.getSessionId(),
                                                                                staffDTOEntry.getKey(),
                                                                                repairDivisionUnitId))
                        .collect(Collectors.toList()));
        return ResponseEntity.accepted().build();
    }

    private TableDataDTO<Map<String, RepairDivisionUnitEquipmentStaffDTO>> buildEquipmentStaffDTO(
            List<RepairDivisionUnit> repairDivisionUnitList,
            Map<EquipmentType, List<EquipmentSubType>> equipmentTypeListMap,
            Map<RepairDivisionUnit, Map<EquipmentSubType, RepairDivisionUnitEquipmentStaff>> repairDivisionUnitMap) {
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
        List<RowData<Map<String, RepairDivisionUnitEquipmentStaffDTO>>> rows = repairDivisionUnitList
                .stream()
                .map(rs -> getEquipmentStaffRow(repairDivisionUnitMap, columns, rs))
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

    private RowData<Map<String, RepairDivisionUnitEquipmentStaffDTO>> getEquipmentStaffRow(
            Map<RepairDivisionUnit, Map<EquipmentSubType, RepairDivisionUnitEquipmentStaff>> repairDivisionUnitMap,
            List<EquipmentSubType> columns,
            RepairDivisionUnit rs) {
        Map<String, RepairDivisionUnitEquipmentStaffDTO> dataMap = new HashMap<>();
        for (EquipmentSubType est : columns) {
            RepairDivisionUnitEquipmentStaff repairDivisionUnitEquipmentStaff =
                    repairDivisionUnitMap.getOrDefault(rs, Collections.emptyMap()).get(est);
            boolean emptyStaff = repairDivisionUnitEquipmentStaff == null;
            int totalStaff = emptyStaff || repairDivisionUnitEquipmentStaff.getTotalStaff() == null ? 0 : repairDivisionUnitEquipmentStaff
                    .getTotalStaff();
            int availableStaff = emptyStaff || repairDivisionUnitEquipmentStaff.getAvailableStaff() == null ? 0 : repairDivisionUnitEquipmentStaff
                    .getAvailableStaff();
            dataMap.put(est.getId().toString(), new RepairDivisionUnitEquipmentStaffDTO(totalStaff, availableStaff));
        }
        return new RepairDivisionUnitEquipmentStaffRowData(rs.getId(),
                                                           rs.getName(),
                                                           dataMap,
                                                           rs.getRepairStationType().getName(),
                                                           rs.getStationAmount());
    }


}
