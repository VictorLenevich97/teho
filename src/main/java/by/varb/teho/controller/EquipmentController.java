package by.varb.teho.controller;

import by.varb.teho.entity.Equipment;
import by.varb.teho.entity.EquipmentType;
import by.varb.teho.model.EquipmentModel;
import by.varb.teho.model.EquipmentTypeModel;
import by.varb.teho.service.EquipmentService;
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
        return equipmentService.getAll();
    }

    @GetMapping("/type")
    @ResponseBody
    public List<EquipmentType> getEquipmentTypes() {
        return equipmentService.getAllTypes();
    }

    @PostMapping
    @ResponseBody
    public void addNewEquipment(@RequestBody EquipmentModel equipmentModel) {
        equipmentService.add(equipmentModel.getName(), equipmentModel.getTypeId());
    }

    @PostMapping("/type")
    public void addEquipmentType(@RequestBody EquipmentTypeModel equipmentTypeModel) {
        equipmentService.addType(equipmentTypeModel.getShortName(), equipmentTypeModel.getFullName());
    }


}
