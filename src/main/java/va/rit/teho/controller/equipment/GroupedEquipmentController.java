package va.rit.teho.controller.equipment;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import va.rit.teho.dto.equipment.EquipmentSubTypeWithEquipmentPerTypeDTO;
import va.rit.teho.service.EquipmentService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("grouped-equipment")
public class GroupedEquipmentController {

    private final EquipmentService equipmentService;

    public GroupedEquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<EquipmentSubTypeWithEquipmentPerTypeDTO>> getEquipmentPerType() {
        List<EquipmentSubTypeWithEquipmentPerTypeDTO> equipmentPerTypeDTOList = equipmentService
                .listGroupedByTypes()
                .entrySet()
                .stream()
                .map(equipmentTypeEntry -> EquipmentSubTypeWithEquipmentPerTypeDTO.from(equipmentTypeEntry.getKey(),
                                                                                        equipmentTypeEntry.getValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(equipmentPerTypeDTOList);
    }

}
