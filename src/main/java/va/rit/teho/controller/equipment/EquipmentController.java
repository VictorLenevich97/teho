package va.rit.teho.controller.equipment;

import org.springframework.http.HttpStatus;
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
@RequestMapping("equipment")
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
    public ResponseEntity<List<EquipmentDTO>> getEquipmentList() {
        return ResponseEntity.ok(equipmentService.list()
                                                 .stream()
                                                 .map(EquipmentDTO::idAndNameFrom)
                                                 .collect(Collectors.toList()));
    }

    @GetMapping("/{equipmentId}")
    @ResponseBody
    public ResponseEntity<EquipmentDTO> getEquipment(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(EquipmentDTO.from(equipmentService.get(equipmentId)));
    }


    @PostMapping
    public ResponseEntity<Object> addNewEquipment(@RequestBody EquipmentDTO equipmentDTO) {
        equipmentService.add(equipmentDTO.getName(), equipmentDTO.getSubType().getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{equipmentId}")
    public ResponseEntity<Object> updateEquipment(@PathVariable Long equipmentId,
                                                  @RequestBody EquipmentDTO equipmentDTO) {
        equipmentService.update(equipmentId, equipmentDTO.getName(), equipmentDTO.getSubType().getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/labor-input")
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
