package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.repairformation.RepairFormationUnitDTO;
import va.rit.teho.dto.repairformation.RepairFormationUnitEquipmentStaffDTO;
import va.rit.teho.dto.repairformation.RepairFormationUnitEquipmentStaffRowData;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "РВО")
public class RepairFormationUnitController {

    private static final String KEY_TOTAL_STAFF = "total";
    private static final String KEY_AVAILABLE_STAFF = "available";

    private static final Map<String, String> STAFF_KEYS_AND_TEXT = new HashMap<>();

    static {
        STAFF_KEYS_AND_TEXT.put(KEY_TOTAL_STAFF, "По штату, чел.");
        STAFF_KEYS_AND_TEXT.put(KEY_AVAILABLE_STAFF, "В наличии, чел.");
    }

    private final RepairFormationUnitService repairFormationUnitService;
    private final EquipmentTypeService equipmentTypeService;

    public RepairFormationUnitController(RepairFormationUnitService repairFormationUnitService,
                                         EquipmentTypeService equipmentTypeService) {
        this.repairFormationUnitService = repairFormationUnitService;
        this.equipmentTypeService = equipmentTypeService;
    }

    @Resource
    private TehoSessionData tehoSession;

    @GetMapping("/formation/repair-formation/unit")
    @ResponseBody
    @ApiOperation(value = "Получить список РВО")
    public ResponseEntity<List<RepairFormationUnitDTO>> listRepairFormationUnits(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(value = "id", required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        List<RepairFormationUnitDTO> repairFormationUnitDTOList =
                repairFormationUnitService.list(Optional.ofNullable(ids).orElse(Collections.emptyList()),
                                                pageNum,
                                                pageSize)
                                          .stream()
                                          .map(RepairFormationUnitDTO::from)
                                          .collect(Collectors.toList());
        return ResponseEntity.ok(repairFormationUnitDTOList);
    }

    @GetMapping("/formation/repair-formation/unit/staff")
    @ApiOperation(value = "Получение состава, штатной численности и укомплектованности РВО (в табличном виде)")
    public ResponseEntity<TableDataDTO<Map<String, RepairFormationUnitEquipmentStaffDTO>>> getEquipmentStaff(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairFormationUnitId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @ApiParam(value = "Ключи подтипов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentSubTypeId,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        List<RepairFormationUnit> repairFormationUnitList = repairFormationUnitService.list(repairFormationUnitId,
                                                                                            pageNum,
                                                                                            pageSize);
        Map<EquipmentType, List<EquipmentSubType>> typesWithSubTypes =
                equipmentTypeService.listTypesWithSubTypes(equipmentTypeId,
                                                           equipmentSubTypeId);
        Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> repairFormationUnitEquipmentStaff =
                repairFormationUnitService.getWithEquipmentStaffGrouped(tehoSession.getSessionId(),
                                                                        repairFormationUnitId,
                                                                        equipmentTypeId,
                                                                        equipmentSubTypeId);
        TableDataDTO<Map<String, RepairFormationUnitEquipmentStaffDTO>> equipmentStaffDTO =
                buildEquipmentStaffDTO(repairFormationUnitList,
                                       typesWithSubTypes,
                                       repairFormationUnitEquipmentStaff);
        return ResponseEntity.ok(equipmentStaffDTO);
    }

    @GetMapping("/formation/{formationId}/repair-formation/{repairFormationId}/unit/{repairFormationUnitId}")
    @ResponseBody
    @ApiOperation(value = "Получить детальные данные по РВО")
    public ResponseEntity<RepairFormationUnitDTO> getRepairFormationUnit(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId,
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable Long repairFormationId,
            @ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairFormationUnitId) {
        Pair<RepairFormationUnit, List<RepairFormationUnitEquipmentStaff>> repairFormationUnitListPair =
                repairFormationUnitService.getWithStaff(repairFormationUnitId, tehoSession.getSessionId());
        RepairFormationUnitDTO repairFormationUnitDTO = RepairFormationUnitDTO
                .from(repairFormationUnitListPair.getFirst())
                .setStaff(
                        repairFormationUnitListPair
                                .getSecond()
                                .stream()
                                .map(RepairFormationUnitEquipmentStaffDTO::from)
                                .collect(Collectors.toList()));
        return ResponseEntity.ok(repairFormationUnitDTO);
    }

    @GetMapping("/formation/{formationId}/repair-formation/{repairFormationId}/unit")
    public ResponseEntity<List<RepairFormationUnitDTO>> listRepairFormationUnitsInFormation(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId,
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable Long repairFormationId,
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(value = "id", required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        return ResponseEntity.ok(repairFormationUnitService.list(repairFormationId, ids, pageNum, pageSize)
                                                           .stream()
                                                           .map(RepairFormationUnitDTO::from)
                                                           .collect(Collectors.toList()));
    }

    @PostMapping(path = "/formation/{formationId}/repair-formation/{repairFormationId}/unit", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавление РВО")
    public ResponseEntity<Object> addRepairFormationUnit(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId,
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable Long repairFormationId,
            @ApiParam(value = "Данные по РВО", required = true) @RequestBody RepairFormationUnitDTO repairFormationUnitDTO) {
        repairFormationUnitService.add(repairFormationUnitDTO.getName(),
                                       repairFormationUnitDTO.getRepairFormation().getId(),
                                       repairFormationUnitDTO.getStationType().getId(),
                                       repairFormationUnitDTO.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(path = "/formation/{formationId}/repair-formation/{repairFormationId}/unit/{repairFormationUnitId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление РВО")
    public ResponseEntity<Object> updateRepairFormationUnit(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId,
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable Long repairFormationId,
            @ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairFormationUnitId,
            @ApiParam(value = "Данные по РВО", required = true) @RequestBody RepairFormationUnitDTO repairFormationUnitDTO) {
        repairFormationUnitService.update(repairFormationUnitId,
                                          repairFormationUnitDTO.getName(),
                                          repairFormationUnitDTO.getRepairFormation().getId(),
                                          repairFormationUnitDTO.getStationType().getId(),
                                          repairFormationUnitDTO.getAmount());
        return ResponseEntity.accepted().build();
    }

    @PutMapping(path = "/formation/{formationId}/repair-formation/{repairFormationId}/unit/{repairFormationUnitId}/staff", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление информации по личному составу РВО")
    public ResponseEntity<Object> updateRepairFormationUnitEquipmentStaff(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId,
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable Long repairFormationId,
            @ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairFormationUnitId,
            @ApiParam(value = "Данные по численности л/с", required = true) @RequestBody Map<Long, RepairFormationUnitEquipmentStaffDTO> staff) {
        repairFormationUnitService.updateEquipmentStaff(
                staff
                        .entrySet()
                        .stream()
                        .map(staffDTOEntry -> staffDTOEntry.getValue().toEntity(tehoSession.getSessionId(),
                                                                                staffDTOEntry.getKey(),
                                                                                repairFormationUnitId))
                        .collect(Collectors.toList()));
        return ResponseEntity.accepted().build();
    }

    private TableDataDTO<Map<String, RepairFormationUnitEquipmentStaffDTO>> buildEquipmentStaffDTO(
            List<RepairFormationUnit> repairFormationUnitList,
            Map<EquipmentType, List<EquipmentSubType>> equipmentTypeListMap,
            Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> repairFormationUnitMap) {
        List<EquipmentSubType> columns =
                equipmentTypeListMap
                        .values()
                        .stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        List<NestedColumnsDTO> nestedColumnsTotal =
                equipmentTypeListMap
                        .entrySet()
                        .stream()
                        .flatMap(this::getEquipmentStaffNestedColumnsDTO)
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
        List<RowData<Map<String, RepairFormationUnitEquipmentStaffDTO>>> rows = repairFormationUnitList
                .stream()
                .map(rs -> getEquipmentStaffRow(repairFormationUnitMap, columns, rs))
                .collect(Collectors.toList());
        return new TableDataDTO<>(nestedColumnsTotal, rows);
    }

    private Stream<NestedColumnsDTO> getEquipmentStaffNestedColumnsDTO(
            Map.Entry<EquipmentType, List<EquipmentSubType>> equipmentTypeEntry) {
        EquipmentType equipmentType = equipmentTypeEntry.getKey();
        if (equipmentType == null) {
            return equipmentTypeEntry
                    .getValue()
                    .stream()
                    .map(this::getEquipmentSubTypeNestedColumnsDTOFunction);
        }
        return Stream.of(new NestedColumnsDTO(
                equipmentType.getShortName(),
                equipmentTypeEntry.getValue()
                                  .stream()
                                  .map(this::getEquipmentSubTypeNestedColumnsDTOFunction)
                                  .collect(Collectors.toList())));
    }

    private NestedColumnsDTO getEquipmentSubTypeNestedColumnsDTOFunction(EquipmentSubType est) {
        return new NestedColumnsDTO(est.getShortName(),
                                    STAFF_KEYS_AND_TEXT
                                            .entrySet()
                                            .stream()
                                            .map(e -> new NestedColumnsDTO(
                                                    Arrays.asList(est.getId().toString(), e.getKey()), e.getValue()))
                                            .collect(Collectors.toList()));
    }

    private RowData<Map<String, RepairFormationUnitEquipmentStaffDTO>> getEquipmentStaffRow(
            Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> repairFormationUnitMap,
            List<EquipmentSubType> columns,
            RepairFormationUnit rs) {
        Map<String, RepairFormationUnitEquipmentStaffDTO> dataMap = new HashMap<>();
        for (EquipmentSubType est : columns) {
            RepairFormationUnitEquipmentStaff repairFormationUnitEquipmentStaff =
                    repairFormationUnitMap.getOrDefault(rs, Collections.emptyMap()).get(est);
            boolean emptyStaff = repairFormationUnitEquipmentStaff == null;
            int totalStaff = emptyStaff || repairFormationUnitEquipmentStaff.getTotalStaff() == null ? 0 : repairFormationUnitEquipmentStaff
                    .getTotalStaff();
            int availableStaff = emptyStaff || repairFormationUnitEquipmentStaff.getAvailableStaff() == null ? 0 : repairFormationUnitEquipmentStaff
                    .getAvailableStaff();
            dataMap.put(est.getId().toString(), new RepairFormationUnitEquipmentStaffDTO(totalStaff, availableStaff));
        }
        return new RepairFormationUnitEquipmentStaffRowData(rs.getId(),
                                                            rs.getName(),
                                                            dataMap,
                                                            rs.getRepairStationType().getName(),
                                                            rs.getStationAmount());
    }


}
