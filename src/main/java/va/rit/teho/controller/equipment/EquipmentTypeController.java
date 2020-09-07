package va.rit.teho.controller.equipment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.equipment.EquipmentSubTypeDTO;
import va.rit.teho.dto.equipment.EquipmentSubTypePerTypeDTO;
import va.rit.teho.dto.equipment.EquipmentTypeDTO;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.model.Pair;
import va.rit.teho.service.EquipmentService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("equipment-type")
public class EquipmentTypeController {

    private final EquipmentService equipmentService;

    public EquipmentTypeController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<EquipmentSubTypePerTypeDTO>> getEquipmentTypes() {
        List<EquipmentSubTypePerTypeDTO> equipmentTypes = equipmentService
                .listTypesWithSubTypes()
                .entrySet()
                .stream()
                .map(typeEntry -> EquipmentSubTypePerTypeDTO.from(typeEntry.getKey(), typeEntry.getValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(equipmentTypes);
    }

    @GetMapping("/{typeId}")
    @ResponseBody
    public ResponseEntity<EquipmentSubTypePerTypeDTO> getEquipmentTypeById(@PathVariable Long typeId) {
        Pair<EquipmentType, List<EquipmentSubType>> typeWithSubTypes = equipmentService.getTypeWithSubTypes(typeId);
        return ResponseEntity.ok(EquipmentSubTypePerTypeDTO.from(typeWithSubTypes.getLeft(),
                                                                 typeWithSubTypes.getRight()));
    }

    @PostMapping
    public ResponseEntity<Object> addEquipmentType(@RequestBody EquipmentTypeDTO equipmentTypeDTO) {
        equipmentService.addType(equipmentTypeDTO.getShortName(), equipmentTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{typeId}")
    public ResponseEntity<Object> updateEquipmentType(@PathVariable Long typeId,
                                                   @RequestBody EquipmentTypeDTO equipmentTypeDTO) {
        equipmentService.updateType(typeId, equipmentTypeDTO.getShortName(), equipmentTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/{typeId}/subtype")
    public ResponseEntity<Object> addEquipmentSubType(@PathVariable Long typeId,
                                                      @RequestBody EquipmentSubTypeDTO equipmentSubTypeDTO) {
        equipmentService.addSubType(typeId, equipmentSubTypeDTO.getShortName(), equipmentSubTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{typeId}/subtype/{subTypeId}")
    public ResponseEntity<Object> updateEquipmentSubType(@PathVariable Long typeId,
                                                      @PathVariable Long subTypeId,
                                                      @RequestBody EquipmentSubTypeDTO equipmentSubTypeDTO) {
        equipmentService.updateSubType(subTypeId,
                                       typeId,
                                       equipmentSubTypeDTO.getShortName(),
                                       equipmentSubTypeDTO.getFullName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
