package va.rit.teho.controller.equipment;

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
    public List<EquipmentSubTypePerTypeDTO> getEquipmentTypes() {
        return equipmentService
                .listTypesWithSubTypes()
                .entrySet()
                .stream()
                .map(typeEntry -> EquipmentSubTypePerTypeDTO.from(typeEntry.getKey(), typeEntry.getValue()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{typeId}")
    @ResponseBody
    public EquipmentSubTypePerTypeDTO getEquipmentTypeById(@PathVariable Long typeId) {
        Pair<EquipmentType, List<EquipmentSubType>> typeWithSubTypes = equipmentService.getTypeWithSubTypes(typeId);
        return EquipmentSubTypePerTypeDTO.from(typeWithSubTypes.getLeft(), typeWithSubTypes.getRight());
    }

    @PostMapping
    public void addEquipmentType(@RequestBody EquipmentTypeDTO equipmentTypeDTO) {
        equipmentService.addType(equipmentTypeDTO.getShortName(), equipmentTypeDTO.getFullName());
    }

    @PostMapping("/{typeId}/subtype")
    public void addEquipmentSubType(@PathVariable Long typeId, @RequestBody EquipmentSubTypeDTO equipmentSubTypeDTO) {
        equipmentService.addSubType(typeId, equipmentSubTypeDTO.getShortName(), equipmentSubTypeDTO.getFullName());
    }
}
