package va.rit.teho.controller;

import va.rit.teho.dto.EquipmentDTO;
import va.rit.teho.dto.EquipmentTypeDTO;
import va.rit.teho.entity.Equipment;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.service.EquipmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/type")
    @ResponseBody
    public List<EquipmentType> getEquipmentTypes() {
        return equipmentService.listTypes();
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
