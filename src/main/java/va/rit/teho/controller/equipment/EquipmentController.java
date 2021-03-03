package va.rit.teho.controller.equipment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.Paginator;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.equipment.EquipmentDTO;
import va.rit.teho.dto.equipment.EquipmentLaborInputPerTypeRowData;
import va.rit.teho.dto.table.GenericTableDataDTO;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.equipment.EquipmentTypeService;
import va.rit.teho.service.report.ReportService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static va.rit.teho.controller.helper.FilterConverter.nullIfEmpty;

@Controller
@RequestMapping(path = "equipment", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "ВВСТ")
@Validated
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EquipmentTypeService equipmentTypeService;
    private final RepairTypeService repairTypeService;
    private final ReportService<Collection<EquipmentType>> equipmentReportService;

    public EquipmentController(EquipmentService equipmentService,
                               EquipmentTypeService equipmentTypeService,
                               RepairTypeService repairTypeService,
                               ReportService<Collection<EquipmentType>> equipmentReportService) {
        this.equipmentService = equipmentService;
        this.equipmentTypeService = equipmentTypeService;
        this.repairTypeService = repairTypeService;
        this.equipmentReportService = equipmentReportService;
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить список ВВСТ")
    public ResponseEntity<List<EquipmentDTO>> getEquipmentList() {
        return ResponseEntity.ok(equipmentService.list()
                                                 .stream()
                                                 .map(EquipmentDTO::idAndNameFrom)
                                                 .collect(Collectors.toList()));
    }

    @GetMapping(path = "/{equipmentId}")
    @ResponseBody
    @ApiOperation(value = "Получить детали о ВВСТ")
    public ResponseEntity<EquipmentDTO> getEquipment(@ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable @Min(1L) Long equipmentId) {
        return ResponseEntity.ok(EquipmentDTO.from(equipmentService.get(equipmentId)));
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавить ВВСТ")
    @Transactional
    public ResponseEntity<EquipmentLaborInputPerTypeRowData> addNewEquipment(@ApiParam(value = "Данные о ВВСТ и трудоемкости ремонта", required = true)
                                                                             @Valid @RequestBody EquipmentLaborInputPerTypeRowData equipmentData) {
        Map<Long, Integer> repairTypeIdLaborInputMap = mapStringKeysToLong(equipmentData.getData());
        Equipment added = equipmentService.add(equipmentData.getName(),
                                               equipmentData.getTypeId(),
                                               repairTypeIdLaborInputMap);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EquipmentLaborInputPerTypeRowData(added,
                                                                                                    equipmentData.getData()));
    }

    private Map<Long, Integer> mapStringKeysToLong(Map<String, Integer> equipmentData) {
        Map<Long, Integer> repairTypeIdLaborInputMap = new HashMap<>();
        for (Map.Entry<String, Integer> typeIdLaborInputEntry : equipmentData.entrySet()) {
            repairTypeIdLaborInputMap.put(Long.valueOf(typeIdLaborInputEntry.getKey()),
                                          typeIdLaborInputEntry.getValue());
        }
        return repairTypeIdLaborInputMap;
    }

    @PutMapping(path = "/{equipmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить ВВСТ")
    public ResponseEntity<EquipmentLaborInputPerTypeRowData> updateEquipment(@ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable @Positive Long equipmentId,
                                                                             @ApiParam(value = "Данные о ВВСТ", required = true) @Valid @RequestBody EquipmentLaborInputPerTypeRowData equipmentData) {
        Equipment updatedEquipment = equipmentService.update(equipmentId,
                                                             equipmentData.getName(),
                                                             equipmentData.getTypeId(),
                                                             mapStringKeysToLong(equipmentData.getData()));
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new EquipmentLaborInputPerTypeRowData(updatedEquipment, equipmentData.getData()));
    }

    @DeleteMapping(path = "/{equipmentId}")
    @ApiOperation(value = "Удалить ВВСТ")
    @Transactional
    public ResponseEntity<Object> deleteEquipment(
            @ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable @Positive Long equipmentId) {
        //Проверка на существование
        equipmentService.get(equipmentId);

        equipmentService.delete(equipmentId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/labor-input")
    @ApiOperation(value = "Получить список ВВСТ с нормативной трудоемкостью (в табличном виде)")
    public ResponseEntity<GenericTableDataDTO<Map<String, Integer>, EquipmentLaborInputPerTypeRowData>> listEquipmentWithLaborInputData(
            @ApiParam(value = "Ключи ВВСТ, по которым осуществляется фильтр") @RequestParam(required = false) List<Long> ids,
            @ApiParam(value = "Ключи типов ВВСТ, по которым осуществляется фильтр") @RequestParam(required = false) List<Long> typeIds,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        List<Long> idsFilter = nullIfEmpty(ids);
        List<Long> typeIdsFilter = nullIfEmpty(typeIds);

        Long rowCount = equipmentService.count(idsFilter, typeIdsFilter);
        List<NestedColumnsDTO> columns =
                repairTypeService.list(true)
                                 .stream()
                                 .filter(RepairType::isRepairable)
                                 .map(rt -> new NestedColumnsDTO(rt.getId().toString(), rt.getFullName()))
                                 .collect(Collectors.toList());
        List<EquipmentLaborInputPerTypeRowData> data =
                equipmentService
                        .listWithLaborInputPerType(idsFilter, typeIdsFilter, pageNum, pageSize)
                        .entrySet()
                        .stream()
                        .map(equipmentMapEntry ->
                                     new EquipmentLaborInputPerTypeRowData(
                                             equipmentMapEntry.getKey(),
                                             equipmentMapEntry
                                                     .getValue()
                                                     .entrySet()
                                                     .stream()
                                                     .collect(Collectors.toMap(e -> e.getKey().getId().toString(),
                                                                               Map.Entry::getValue))))
                        .sorted(Comparator.comparing(EquipmentLaborInputPerTypeRowData::getId,
                                                     Comparator.reverseOrder()))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(new GenericTableDataDTO<>(columns, data, Paginator.getPageNum(pageSize, rowCount)));
    }

    @GetMapping(value = "/labor-input/report", produces = "application/vnd.ms-excel")
    @ResponseBody
    @Transactional
    public ResponseEntity<byte[]> equipmentLaborInputPerTypeReport(
            @ApiParam(value = "Ключи типов, по которым осуществляется фильтр") @RequestParam(required = false) List<Long> typeIds)
            throws UnsupportedEncodingException {
        byte[] bytes = equipmentReportService.generateReport(equipmentTypeService.listHighestLevelTypes(typeIds));

        return ReportResponseEntity.ok("Список ВВСТ (с трудоёмкостью)", bytes);
    }
}
