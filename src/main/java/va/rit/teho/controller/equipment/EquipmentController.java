package va.rit.teho.controller.equipment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.controller.helper.ReportResponseEntity;
import va.rit.teho.dto.equipment.EquipmentDTO;
import va.rit.teho.dto.equipment.EquipmentLaborInputPerTypeRowData;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.entity.equipment.Equipment;
import va.rit.teho.entity.equipment.EquipmentSubType;
import va.rit.teho.entity.equipment.EquipmentType;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentService;
import va.rit.teho.service.report.ReportService;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "equipment", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "ВВСТ")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final RepairTypeService repairTypeService;
    private final ReportService<Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>>> equipmentReportService;

    public EquipmentController(EquipmentService equipmentService,
                               RepairTypeService repairTypeService,
                               ReportService<Map<EquipmentType, Map<EquipmentSubType, List<Equipment>>>> equipmentReportService) {
        this.equipmentService = equipmentService;
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
    public ResponseEntity<EquipmentDTO> getEquipment(@ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable Long equipmentId) {
        return ResponseEntity.ok(EquipmentDTO.from(equipmentService.get(equipmentId)));
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Добавить ВВСТ")
    @Transactional
    public ResponseEntity<EquipmentLaborInputPerTypeRowData> addNewEquipment(@ApiParam(value = "Данные о ВВСТ и трудоемкости ремонта", required = true)
                                                                             @RequestBody EquipmentLaborInputPerTypeRowData equipmentData) {
        Map<Long, Integer> repairTypeIdLaborInputMap = mapStringKeysToLong(equipmentData.getData());
        Equipment added = equipmentService.add(equipmentData.getName(),
                                               equipmentData.getSubTypeId(),
                                               repairTypeIdLaborInputMap);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new EquipmentLaborInputPerTypeRowData(added.getId(),
                                                            added.getName(),
                                                            added.getEquipmentSubType().getId(),
                                                            added.getEquipmentSubType().getFullName(),
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
    public ResponseEntity<EquipmentLaborInputPerTypeRowData> updateEquipment(@ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable Long equipmentId,
                                                                             @ApiParam(value = "Данные о ВВСТ", required = true) @RequestBody EquipmentLaborInputPerTypeRowData equipmentData) {
        Equipment updatedEquipment = equipmentService.update(equipmentId,
                                                             equipmentData.getName(),
                                                             equipmentData.getSubTypeId(),
                                                             mapStringKeysToLong(equipmentData.getData()));
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new EquipmentLaborInputPerTypeRowData(updatedEquipment.getId(),
                                                            updatedEquipment.getName(),
                                                            updatedEquipment.getEquipmentSubType().getId(),
                                                            updatedEquipment.getEquipmentSubType().getFullName(),
                                                            equipmentData.getData()));
    }

    @GetMapping("/labor-input")
    @ApiOperation(value = "Получить список ВВСТ с нормативной трудоемкостью (в табличном виде)")
    public ResponseEntity<TableDataDTO<Map<String, Integer>>> listEquipmentWithLaborInputData() {
        List<NestedColumnsDTO> columns =
                repairTypeService.list(true).stream()
                                 .map(rt -> new NestedColumnsDTO(rt.getId().toString(), rt.getFullName()))
                                 .collect(Collectors.toList());
        List<EquipmentLaborInputPerTypeRowData> data =
                equipmentService
                        .listWithLaborInputPerType()
                        .entrySet()
                        .stream()
                        .map(equipmentMapEntry ->
                                     new EquipmentLaborInputPerTypeRowData(
                                             equipmentMapEntry.getKey().getId(),
                                             equipmentMapEntry.getKey().getName(),
                                             equipmentMapEntry.getKey().getEquipmentSubType().getId(),
                                             equipmentMapEntry.getKey().getEquipmentSubType().getShortName(),
                                             equipmentMapEntry
                                                     .getValue()
                                                     .entrySet()
                                                     .stream()
                                                     .collect(Collectors.toMap(e -> e.getKey().getId().toString(),
                                                                               Map.Entry::getValue))))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(new TableDataDTO<>(columns, data));
    }

    @GetMapping(value = "/labor-input/report", produces = "application/vnd.ms-excel")
    @ResponseBody
    public ResponseEntity<byte[]> equipmentLaborInputPerType() throws UnsupportedEncodingException {
        byte[] bytes = equipmentReportService.generateReport(equipmentService.listGroupedByTypes(null, null, null));

        return ReportResponseEntity.ok("Список ВВСТ (с трудоёмкостью)", bytes);
    }
}
