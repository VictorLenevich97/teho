package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.util.Pair;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.repairformation.*;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitCombinedData;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.repairformation.RepairFormationUnitService;
import va.rit.teho.service.report.ReportService;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    private final ReportService<RepairFormationUnitCombinedData> reportService;

    @Resource
    private TehoSessionData tehoSession;

    public RepairFormationUnitController(RepairFormationUnitService repairFormationUnitService,
                                         EquipmentTypeService equipmentTypeService,
                                         ReportService<RepairFormationUnitCombinedData> reportService) {
        this.repairFormationUnitService = repairFormationUnitService;
        this.equipmentTypeService = equipmentTypeService;
        this.reportService = reportService;
    }

    @GetMapping("/formation/repair-formation/unit")
    @ResponseBody
    @ApiOperation(value = "Получить список РВО")
    public ResponseEntity<List<RepairFormationUnitDTO>> listRepairFormationUnits(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(value = "id", required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        List<RepairFormationUnitDTO> repairFormationUnitDTOList =
                repairFormationUnitService.list(ids, pageNum, pageSize)
                                          .stream()
                                          .map(RepairFormationUnitDTO::from)
                                          .collect(Collectors.toList());
        return ResponseEntity.ok(repairFormationUnitDTOList);
    }

    @GetMapping("/formation/repair-formation/unit/staff/report")
    @ApiOperation(value = "[ПЕЧАТЬ] Получение состава, штатной численности и укомплектованности РВО (в табличном виде)")
    public ResponseEntity<byte[]> getEquipmentStaffReport(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairFormationUnitId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @ApiParam(value = "Ключи подтипов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentSubTypeId,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) throws UnsupportedEncodingException {
        RepairFormationUnitCombinedData repairFormationUnitCombinedData = getRepairFormationUnitCombinedData(
                repairFormationUnitId,
                equipmentTypeId,
                equipmentSubTypeId,
                pageNum,
                pageSize);
        byte[] bytes = reportService.generateReport(repairFormationUnitCombinedData);
        String encode = URLEncoder.encode("Состав и штатная численность РВО.xls",
                                          "UTF-8");
        return ResponseEntity.ok().contentLength(bytes.length)
                             .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                             .cacheControl(CacheControl.noCache())
                             .header("Content-Disposition", "attachment; filename=" + encode)
                             .body(bytes);

    }

    private RepairFormationUnitCombinedData getRepairFormationUnitCombinedData(List<Long> repairFormationUnitId,
                                                                               List<Long> equipmentTypeId,
                                                                               List<Long> equipmentSubTypeId,
                                                                               int pageNum,
                                                                               int pageSize) {
        List<RepairFormationUnit> repairFormationUnitList = repairFormationUnitService.list(repairFormationUnitId,
                                                                                            pageNum,
                                                                                            pageSize);
        Map<EquipmentType, List<EquipmentSubType>> typesWithSubTypes =
                equipmentTypeService.listTypesWithSubTypes(equipmentTypeId,
                                                           equipmentSubTypeId);
        Map<RepairFormationUnit, Map<EquipmentSubType, RepairFormationUnitEquipmentStaff>> repairFormationUnitEquipmentStaff =
                repairFormationUnitService.listEquipmentStaffPerSubType(tehoSession.getSessionId(),
                                                                        repairFormationUnitId,
                                                                        equipmentTypeId,
                                                                        equipmentSubTypeId);
        return new RepairFormationUnitCombinedData(repairFormationUnitList,
                                                   typesWithSubTypes,
                                                   repairFormationUnitEquipmentStaff);
    }

    @GetMapping("/formation/repair-formation/unit/staff")
    @ApiOperation(value = "Получение состава, штатной численности и укомплектованности РВО (в табличном виде)")
    public ResponseEntity<TableDataDTO<Map<String, RepairFormationUnitEquipmentStaffDTO>>> getEquipmentStaff(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairFormationUnitId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @ApiParam(value = "Ключи подтипов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentSubTypeId,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        RepairFormationUnitCombinedData repairFormationUnitCombinedData = getRepairFormationUnitCombinedData(
                repairFormationUnitId,
                equipmentTypeId,
                equipmentSubTypeId,
                pageNum,
                pageSize);
        TableDataDTO<Map<String, RepairFormationUnitEquipmentStaffDTO>> equipmentStaffDTO =
                buildEquipmentStaffDTO(repairFormationUnitCombinedData);
        return ResponseEntity.ok(equipmentStaffDTO);
    }

    @GetMapping("/formation/repair-formation/unit/{repairFormationUnitId}/staff")
    @ApiOperation(value = "Получение состава, штатной численности и укомплектованности конкретного РВО (в виде списка)")
    public ResponseEntity<List<EquipmentTypeStaffData>> getEquipmentStaffPerType(
            @ApiParam(value = "Ключ РВО") @PathVariable Long repairFormationUnitId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @ApiParam(value = "Ключи подтипов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentSubTypeId) {
        Map<EquipmentType, List<EquipmentSubType>> typesWithSubTypes =
                equipmentTypeService.listTypesWithSubTypes(equipmentTypeId,
                                                           equipmentSubTypeId);
        Map<EquipmentSubType, RepairFormationUnitEquipmentStaff> equipmentStaff =
                repairFormationUnitService.getEquipmentStaffPerSubType(tehoSession.getSessionId(),
                                                                       repairFormationUnitId,
                                                                       equipmentTypeId,
                                                                       equipmentSubTypeId);

        List<EquipmentTypeStaffData> typeStaffData = typesWithSubTypes
                .entrySet()
                .stream()
                .map(equipmentTypeListEntry -> {
                    List<EquipmentStaffPerSubType> subTypes = equipmentTypeListEntry
                            .getValue()
                            .stream()
                            .map(est ->
                                         new EquipmentStaffPerSubType(
                                                 est.getId(),
                                                 est.getFullName(),
                                                 equipmentStaff
                                                         .getOrDefault(est, RepairFormationUnitEquipmentStaff.EMPTY)
                                                         .getTotalStaff(),
                                                 equipmentStaff
                                                         .getOrDefault(est, RepairFormationUnitEquipmentStaff.EMPTY)
                                                         .getAvailableStaff()))
                            .collect(Collectors.toList());
                    EquipmentType equipmentType = equipmentTypeListEntry.getKey();
                    if (equipmentType != null) {
                        return new EquipmentTypeStaffData(equipmentType.getId(), equipmentType.getFullName(), subTypes);
                    } else {
                        return new EquipmentTypeStaffData(-1L, subTypes);
                    }
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(typeStaffData);
    }


    @GetMapping("/formation/repair-formation/unit/{repairFormationUnitId}")
    @ResponseBody
    @ApiOperation(value = "Получить детальные данные по РВО")
    public ResponseEntity<RepairFormationUnitDTO> getRepairFormationUnit(
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

    @GetMapping("/formation/repair-formation/{repairFormationId}/unit")
    public ResponseEntity<List<RepairFormationUnitDTO>> listRepairFormationUnitsInFormation(
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable Long repairFormationId,
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(value = "id", required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        return ResponseEntity.ok(repairFormationUnitService.list(repairFormationId, ids, pageNum, pageSize)
                                                           .stream()
                                                           .map(RepairFormationUnitDTO::from)
                                                           .collect(Collectors.toList()));
    }

    @PostMapping(path = "/formation/repair-formation/{repairFormationId}/unit", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавление РВО")
    public ResponseEntity<RepairFormationUnitDTO> addRepairFormationUnit(
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable Long repairFormationId,
            @ApiParam(value = "Данные по РВО", required = true) @RequestBody RepairFormationUnitDTO repairFormationUnitDTO) {
        RepairFormationUnit repairFormationUnit = repairFormationUnitService.add(repairFormationUnitDTO.getName(),
                                                                                 repairFormationId,
                                                                                 repairFormationUnitDTO
                                                                                         .getStationType()
                                                                                         .getId(),
                                                                                 repairFormationUnitDTO.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(RepairFormationUnitDTO.from(repairFormationUnit));
    }

    @PutMapping(path = "/formation/repair-formation/{repairFormationId}/unit/{repairFormationUnitId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление РВО")
    public ResponseEntity<RepairFormationUnitDTO> updateRepairFormationUnit(
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable Long repairFormationId,
            @ApiParam(value = "Ключ РВО", required = true) @PathVariable Long repairFormationUnitId,
            @ApiParam(value = "Данные по РВО", required = true) @RequestBody RepairFormationUnitDTO repairFormationUnitDTO) {
        RepairFormationUnit repairFormationUnit = repairFormationUnitService.update(repairFormationUnitId,
                                                                                    repairFormationUnitDTO.getName(),
                                                                                    repairFormationId,
                                                                                    repairFormationUnitDTO
                                                                                            .getStationType()
                                                                                            .getId(),
                                                                                    repairFormationUnitDTO.getAmount());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(RepairFormationUnitDTO.from(repairFormationUnit));
    }

    @PutMapping(path = "/formation/repair-formation/unit/{repairFormationUnitId}/staff", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление информации по личному составу РВО")
    public ResponseEntity<Object> updateRepairFormationUnitEquipmentStaff(
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
            RepairFormationUnitCombinedData repairFormationUnitCombinedData) {
        List<EquipmentSubType> columns =
                repairFormationUnitCombinedData.getTypesWithSubTypes()
                                               .values()
                                               .stream()
                                               .flatMap(List::stream)
                                               .collect(Collectors.toList());
        List<NestedColumnsDTO> nestedColumnsTotal =
                repairFormationUnitCombinedData.getTypesWithSubTypes()
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
        List<RowData<Map<String, RepairFormationUnitEquipmentStaffDTO>>> rows = repairFormationUnitCombinedData
                .getRepairFormationUnitList()
                .stream()
                .map(rs -> getEquipmentStaffRow(repairFormationUnitCombinedData.getRepairFormationUnitEquipmentStaff(),
                                                columns,
                                                rs))
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
                    repairFormationUnitMap.getOrDefault(rs, Collections.emptyMap()).getOrDefault(est,
                                                                                                 RepairFormationUnitEquipmentStaff.EMPTY);
            dataMap.put(est.getId().toString(),
                        new RepairFormationUnitEquipmentStaffDTO(repairFormationUnitEquipmentStaff.getTotalStaff(),
                                                                 repairFormationUnitEquipmentStaff.getAvailableStaff()));
        }
        return new RepairFormationUnitEquipmentStaffRowData(rs.getId(),
                                                            rs.getName(),
                                                            dataMap,
                                                            rs.getRepairStationType().getName(),
                                                            rs.getStationAmount());
    }


}
