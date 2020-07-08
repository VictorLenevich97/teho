package by.varb.teho.controller;

import by.varb.teho.model.Equipment;
import by.varb.teho.model.EquipmentType;
import by.varb.teho.service.EquipmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping("/getEquipmentInfo")
    @ResponseBody
    public List<Equipment> getEquipmentInfo() {
        return equipmentService.getEquipmentInfo();
    }

    @GetMapping("/getEquipmentTypesList")
    @ResponseBody
    public List<EquipmentType> getEquipmentTypes() {
        return equipmentService.getEquipmentTypes();
    }

    @PostMapping("/addNewVehicle")
    @ResponseBody
    public void addNewVehicle(@RequestBody Map<String, Object> data) throws Exception {
        equipmentService.addNewVehicle(data);
    }
}
