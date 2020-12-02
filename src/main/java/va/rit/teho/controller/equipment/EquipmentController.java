package va.rit.teho.controller.equipment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.equipment.EquipmentDTO;
import va.rit.teho.dto.equipment.EquipmentLaborInputPerTypeRowData;
import va.rit.teho.dto.table.NestedColumnsDTO;
import va.rit.teho.dto.table.TableDataDTO;
import va.rit.teho.service.common.RepairTypeService;
import va.rit.teho.service.equipment.EquipmentService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "equipment", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "ВВСТ")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final RepairTypeService repairTypeService;

    public EquipmentController(EquipmentService equipmentService,
                               RepairTypeService repairTypeService) {
        this.equipmentService = equipmentService;
        this.repairTypeService = repairTypeService;
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
    public ResponseEntity<Object> addNewEquipment(@ApiParam(value = "Данные о ВВСТ", required = true) @RequestBody EquipmentDTO equipmentDTO) {
        equipmentService.add(equipmentDTO.getName(), equipmentDTO.getSubType().getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(path = "/{equipmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Обновить ВВСТ")
    public ResponseEntity<Object> updateEquipment(@ApiParam(value = "Ключ ВВСТ", required = true, example = "1") @PathVariable Long equipmentId,
                                                  @ApiParam(value = "Данные о ВВСТ", required = true) @RequestBody EquipmentDTO equipmentDTO) {
        equipmentService.update(equipmentId, equipmentDTO.getName(), equipmentDTO.getSubType().getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
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
                                             equipmentMapEntry.getKey().getName(),
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
}
