package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.Paginator;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.repairformation.*;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.RowData;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.entity.repairformation.RepairFormationUnit;
import va.rit.teho.entity.repairformation.RepairFormationUnitEquipmentStaff;
import va.rit.teho.entity.repairformation.combined.RepairFormationUnitCombinedData;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.repairformation.RepairFormationUnitServiceFacade;
import va.rit.teho.service.report.ReportService;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static va.rit.teho.controller.helper.FilterConverter.nullIfEmpty;

@Controller
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "РВО")
@Transactional
public class RepairFormationUnitController {

    private static final String KEY_TOTAL_STAFF = "total";
    private static final String KEY_AVAILABLE_STAFF = "available";

    private static final Map<String, String> STAFF_KEYS_AND_TEXT = new HashMap<>();

    static {
        STAFF_KEYS_AND_TEXT.put(KEY_TOTAL_STAFF, "По штату, чел.");
        STAFF_KEYS_AND_TEXT.put(KEY_AVAILABLE_STAFF, "В наличии, чел.");
    }

    private final RepairFormationUnitServiceFacade repairFormationUnitServiceFacade;
    private final EquipmentTypeService equipmentTypeService;
    private final ReportService<RepairFormationUnitCombinedData> reportService;

    @Resource
    private TehoSessionData tehoSession;

    public RepairFormationUnitController(RepairFormationUnitServiceFacade repairFormationUnitServiceFacade,
                                         EquipmentTypeService equipmentTypeService,
                                         ReportService<RepairFormationUnitCombinedData> reportService) {
        this.repairFormationUnitServiceFacade = repairFormationUnitServiceFacade;
        this.equipmentTypeService = equipmentTypeService;
        this.reportService = reportService;
    }

    @GetMapping("/formation/repair-formation/unit")
    @ResponseBody
    @ApiOperation(value = "Получить список РВО")
    public ResponseEntity<List<RepairFormationUnitDTO>> listRepairFormationUnits(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        List<RepairFormationUnitDTO> repairFormationUnitDTOList =
                repairFormationUnitServiceFacade.list(tehoSession.getSessionId(), nullIfEmpty(ids), pageNum, pageSize)
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
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) throws UnsupportedEncodingException {
        RepairFormationUnitCombinedData repairFormationUnitCombinedData = getRepairFormationUnitCombinedData(
                repairFormationUnitId,
                equipmentTypeId,
                pageNum,
                pageSize);

        return ReportResponseEntity.ok("Состав и штатная численность РВО",
                reportService.generateReport(repairFormationUnitCombinedData));
    }

    private RepairFormationUnitCombinedData getRepairFormationUnitCombinedData(List<Long> repairFormationUnitId,
                                                                               List<Long> equipmentTypeId,
                                                                               int pageNum,
                                                                               int pageSize) {
        List<RepairFormationUnit> repairFormationUnitList = repairFormationUnitServiceFacade.list(
                tehoSession.getSessionId(),
                nullIfEmpty(repairFormationUnitId),
                pageNum,
                pageSize);
        List<EquipmentType> equipmentTypes = equipmentTypeService.listHighestLevelTypes(nullIfEmpty(equipmentTypeId));
        Map<RepairFormationUnit, Map<EquipmentType, RepairFormationUnitEquipmentStaff>> repairFormationUnitEquipmentStaff =
                repairFormationUnitServiceFacade.listEquipmentStaffPerType(tehoSession.getSessionId(),
                        nullIfEmpty(repairFormationUnitId),
                        nullIfEmpty(equipmentTypeId));
        return new RepairFormationUnitCombinedData(repairFormationUnitList,
                equipmentTypes,
                repairFormationUnitEquipmentStaff);
    }

    @GetMapping("/formation/repair-formation/unit/staff")
    @ApiOperation(value = "Получение состава, штатной численности и укомплектованности РВО (в табличном виде)")
    public ResponseEntity<TableDataDTO<Map<String, RepairFormationUnitEquipmentStaffDTO>>> getEquipmentStaff(
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> repairFormationUnitId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        RepairFormationUnitCombinedData repairFormationUnitCombinedData = getRepairFormationUnitCombinedData(
                repairFormationUnitId,
                equipmentTypeId,
                pageNum,
                pageSize);

        Long rowCount = repairFormationUnitServiceFacade.count(repairFormationUnitId);

        TableDataDTO<Map<String, RepairFormationUnitEquipmentStaffDTO>> equipmentStaffDTO =
                buildEquipmentStaffDTO(repairFormationUnitCombinedData, rowCount, pageSize);
        return ResponseEntity.ok(equipmentStaffDTO);
    }

    private Stream<EquipmentTypeStaffData> getEquipmentTypeStaffData(Collection<EquipmentType> equipmentTypes,
                                                                     Map<EquipmentType, RepairFormationUnitEquipmentStaff> equipmentStaff) {
        return equipmentTypes
                .stream()
                .map(equipmentType -> {
                    RepairFormationUnitEquipmentStaff staff = equipmentStaff.getOrDefault(equipmentType,
                            RepairFormationUnitEquipmentStaff.EMPTY);
                    return new EquipmentTypeStaffData(
                            equipmentType.getId(),
                            equipmentType.getShortName(),
                            staff.getTotalStaff(),
                            staff.getAvailableStaff(),
                            getEquipmentTypeStaffData(equipmentType.getEquipmentTypes(), equipmentStaff)
                                    .collect(Collectors.toList()),
                            null);
                });
    }

    @GetMapping("/formation/repair-formation/unit/{repairFormationUnitId}/staff")
    @ApiOperation(value = "Получение состава, штатной численности и укомплектованности конкретного РВО (в виде списка)")
    public ResponseEntity<List<EquipmentTypeStaffData>> getEquipmentStaffPerType(
            @ApiParam(value = "Ключ РВО") @PathVariable @Positive Long repairFormationUnitId,
            @ApiParam(value = "Ключи типов ВВСТ (для фильтрации)") @RequestParam(required = false) List<Long> equipmentTypeId) {
        List<EquipmentType> equipmentTypes =
                Optional
                        .ofNullable(nullIfEmpty(equipmentTypeId))
                        .map(equipmentTypeService::listTypes)
                        .orElse(equipmentTypeService.listHighestLevelTypes(null));
        Map<EquipmentType, RepairFormationUnitEquipmentStaff> equipmentStaff =
                repairFormationUnitServiceFacade.getEquipmentStaffPerType(tehoSession.getSessionId(),
                        repairFormationUnitId,
                        nullIfEmpty(equipmentTypeId));

        List<EquipmentTypeStaffData> typeStaffData =
                getEquipmentTypeStaffData(equipmentTypes, equipmentStaff).collect(Collectors.toList());

        return ResponseEntity.ok(typeStaffData);
    }


    @GetMapping("/formation/repair-formation/unit/{repairFormationUnitId}")
    @ResponseBody
    @ApiOperation(value = "Получить детальные данные по РВО")
    public ResponseEntity<RepairFormationUnitDTO> getRepairFormationUnit(
            @ApiParam(value = "Ключ РВО", required = true) @PathVariable @Positive Long repairFormationUnitId) {
        Pair<RepairFormationUnit, List<RepairFormationUnitEquipmentStaff>> repairFormationUnitListPair =
                repairFormationUnitServiceFacade.getWithStaff(repairFormationUnitId);
        RepairFormationUnitDTO repairFormationUnitDTO = RepairFormationUnitDTO
                .from(repairFormationUnitListPair.getFirst())
                .setStaff(repairFormationUnitListPair
                        .getSecond()
                        .stream()
                        .map(RepairFormationUnitEquipmentStaffDTO::from)
                        .collect(Collectors.toList()));
        return ResponseEntity.ok(repairFormationUnitDTO);
    }

    @GetMapping("/formation/repair-formation/{repairFormationId}/unit")
    @ApiOperation(value = "Получить список РВО, принадлежащих данному Ремонтному Формированию")
    public ResponseEntity<List<RepairFormationUnitDTO>> listRepairFormationUnitsInFormation(
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable @Positive Long repairFormationId,
            @ApiParam(value = "Ключи РВО (для фильтрации)") @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        return ResponseEntity.ok(repairFormationUnitServiceFacade.list(tehoSession.getSessionId(), repairFormationId, ids, pageNum, pageSize)
                .stream()
                .map(RepairFormationUnitDTO::from)
                .collect(Collectors.toList()));
    }

    @PostMapping(path = "/formation/repair-formation/{repairFormationId}/unit", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавление РВО")
    public ResponseEntity<RepairFormationUnitDTO> addRepairFormationUnit(
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable @Positive Long repairFormationId,
            @ApiParam(value = "Данные по РВО", required = true) @Valid @RequestBody RepairFormationUnitDTO repairFormationUnitDTO) {
        RepairFormationUnit repairFormationUnit = repairFormationUnitServiceFacade.add(repairFormationUnitDTO.getName(),
                repairFormationId,
                repairFormationUnitDTO.getDistributionInterval().getId(),
                repairFormationUnitDTO.getRepairType().getId(),
                repairFormationUnitDTO
                        .getStationType()
                        .getId(),
                repairFormationUnitDTO.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(RepairFormationUnitDTO.from(repairFormationUnit));
    }

    @PutMapping(path = "/formation/repair-formation/{repairFormationId}/unit/{repairFormationUnitId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление РВО")
    public ResponseEntity<RepairFormationUnitDTO> updateRepairFormationUnit(
            @ApiParam(value = "Ключ ремонтного формирования", required = true) @PathVariable @Positive Long repairFormationId,
            @ApiParam(value = "Ключ РВО", required = true) @PathVariable @Positive Long repairFormationUnitId,
            @ApiParam(value = "Данные по РВО", required = true) @Valid @RequestBody RepairFormationUnitDTO repairFormationUnitDTO) {
        RepairFormationUnit repairFormationUnit = repairFormationUnitServiceFacade.update(repairFormationUnitId,
                repairFormationUnitDTO.getName(),
                repairFormationId,
                repairFormationUnitDTO.getDistributionInterval().getId(),
                repairFormationUnitDTO.getRepairType().getId(),
                repairFormationUnitDTO
                        .getStationType()
                        .getId(),
                repairFormationUnitDTO.getAmount());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(RepairFormationUnitDTO.from(repairFormationUnit));
    }

    @PutMapping(path = "/formation/repair-formation/unit/{repairFormationUnitId}/staff", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновление информации по личному составу РВО")
    public ResponseEntity<List<EquipmentStaffPerType>> updateRepairFormationUnitEquipmentStaff(
            @ApiParam(value = "Ключ РВО", required = true) @PathVariable @Positive Long repairFormationUnitId,
            @ApiParam(value = "Данные по численности л/с", required = true) @Valid @RequestBody List<EquipmentStaffPerType> staffData) {
        List<RepairFormationUnitEquipmentStaff> list =
                staffData.stream()
                        .map(sd -> sd.toEntity(tehoSession.getSessionId(), repairFormationUnitId))
                        .collect(Collectors.toList());

        return ResponseEntity.accepted().body(
                repairFormationUnitServiceFacade
                        .updateEquipmentStaff(list)
                        .stream()
                        .map(EquipmentStaffPerType::from)
                        .collect(Collectors.toList()));
    }

    private TableDataDTO<Map<String, RepairFormationUnitEquipmentStaffDTO>> buildEquipmentStaffDTO(
            RepairFormationUnitCombinedData repairFormationUnitCombinedData,
            long rowCount,
            int pageSize) {
        List<EquipmentType> columns =
                repairFormationUnitCombinedData.getEquipmentTypes()
                        .stream()
                        .flatMap(EquipmentType::collectLowestLevelTypes)
                        .collect(Collectors.toList());
        List<NestedColumnsDTO> nestedColumnsTotal =
                repairFormationUnitCombinedData.getEquipmentTypes()
                        .stream()
                        .flatMap(this::getEquipmentStaffNestedColumnsDTO)
                        .collect(Collectors.toList());
        List<RowData<Map<String, RepairFormationUnitEquipmentStaffDTO>>> rows = repairFormationUnitCombinedData
                .getRepairFormationUnitList()
                .stream()
                .map(rs -> getEquipmentStaffRow(repairFormationUnitCombinedData.getRepairFormationUnitEquipmentStaff(),
                        columns,
                        rs))
                .collect(Collectors.toList());
        return new TableDataDTO<>(nestedColumnsTotal, rows, Paginator.getPageNum(pageSize, rowCount));
    }

    private Stream<NestedColumnsDTO> getEquipmentStaffNestedColumnsDTO(EquipmentType equipmentType) {
        Stream<NestedColumnsDTO> subColumns;
        if (equipmentType.getEquipmentTypes().isEmpty()) {
            subColumns =
                    STAFF_KEYS_AND_TEXT
                            .entrySet()
                            .stream()
                            .map(e -> new NestedColumnsDTO(Arrays.asList(equipmentType.getId().toString(), e.getKey()),
                                    e.getValue()));
        } else {
            subColumns = equipmentType.getEquipmentTypes().stream().flatMap(this::getEquipmentStaffNestedColumnsDTO);
        }
        return Stream.of(new NestedColumnsDTO(
                equipmentType.getShortName(),
                subColumns.collect(Collectors.toList())));
    }

    private RowData<Map<String, RepairFormationUnitEquipmentStaffDTO>> getEquipmentStaffRow(
            Map<RepairFormationUnit, Map<EquipmentType, RepairFormationUnitEquipmentStaff>> repairFormationUnitMap,
            List<EquipmentType> columns,
            RepairFormationUnit rs) {
        Map<String, RepairFormationUnitEquipmentStaffDTO> dataMap = new HashMap<>();
        for (EquipmentType est : columns) {
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

    @DeleteMapping(path = "/formation/repair-formation/unit/{repairFormationUnitId}")
    @Transactional
    @ApiOperation(value = "Удалить РВО и все связанные сущности/рассчитанные данные")
    public ResponseEntity<Object> deleteRepairFormationUnit(@ApiParam(value = "Ключ РВО", required = true) @PathVariable @Positive Long repairFormationUnitId) {
        repairFormationUnitServiceFacade.delete(repairFormationUnitId);
        return ResponseEntity.noContent().build();
    }
}
