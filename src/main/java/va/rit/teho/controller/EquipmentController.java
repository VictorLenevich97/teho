package va.rit.teho.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.EquipmentDTO;
import va.rit.teho.dto.EquipmentSubTypePerTypeDTO;
import va.rit.teho.dto.EquipmentSubTypeWithEquipmentPerTypeDTO;
import va.rit.teho.dto.EquipmentTypeDTO;
import va.rit.teho.entity.Equipment;
import va.rit.teho.service.EquipmentService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    @ResponseBody
    public List<Equipment> getEquipmentInfo() {
        return equipmentService.list();
    }

    @GetMapping("/grouped")
    @ResponseBody
    public List<EquipmentSubTypeWithEquipmentPerTypeDTO> getEquipmentPerType() {
        return equipmentService
                .listGroupedByTypes()
                .entrySet()
                .stream()
                .map(equipmentTypeEntry -> EquipmentSubTypeWithEquipmentPerTypeDTO.from(equipmentTypeEntry.getKey(),
                                                                                        equipmentTypeEntry.getValue()))
                .collect(Collectors.toList());
    }

    @GetMapping("/type")
    @ResponseBody
    public List<EquipmentSubTypePerTypeDTO> getEquipmentTypes() {
        return equipmentService
                .listSubTypesPerTypes()
                .entrySet()
                .stream()
                .map(typeEntry -> EquipmentSubTypePerTypeDTO.from(typeEntry.getKey(), typeEntry.getValue()))
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseBody
    public void addNewEquipment(@RequestBody EquipmentDTO equipmentDTO) {
        equipmentService.add(equipmentDTO.getName(), equipmentDTO.getTypeId());
    }

    @PostMapping("/type")
    public void addEquipmentType(@RequestBody EquipmentTypeDTO equipmentTypeModel) {
        equipmentService.addType(equipmentTypeModel.getShortName(), equipmentTypeModel.getFullName());
    }


}
