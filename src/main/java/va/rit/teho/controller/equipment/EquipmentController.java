package va.rit.teho.controller.equipment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.equipment.EquipmentDTO;
import va.rit.teho.dto.equipment.EquipmentSubTypeDTO;
import va.rit.teho.dto.equipment.EquipmentTypeDTO;
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
    public List<EquipmentDTO> getEquipmentList() {
        return equipmentService.list().stream().map(EquipmentDTO::from).collect(Collectors.toList());
    }

    @GetMapping("/{equipmentId}")
    @ResponseBody
    public EquipmentDTO getEquipment(@PathVariable Long equipmentId) {
        Equipment equipment = equipmentService.getEquipment(equipmentId);
        EquipmentDTO equipmentDTO = new EquipmentDTO();
        equipmentDTO.setKey(equipment.getId());
        equipmentDTO.setName(equipment.getName());
        equipmentDTO.setSubType(EquipmentSubTypeDTO.from(equipment.getEquipmentSubType()));
        equipmentDTO.setType(EquipmentTypeDTO.from(equipment.getEquipmentSubType().getEquipmentType()));
        return equipmentDTO;
    }


    @ResponseBody
    public void addNewEquipment(@RequestBody EquipmentDTO equipmentDTO) {
        equipmentService.add(equipmentDTO.getName(), equipmentDTO.getSubTypeKey());
    }
}
