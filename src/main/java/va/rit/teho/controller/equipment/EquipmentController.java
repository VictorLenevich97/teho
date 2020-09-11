package va.rit.teho.controller.equipment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.equipment.EquipmentDTO;
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
    public ResponseEntity<List<EquipmentDTO>> getEquipmentList() {
        return ResponseEntity.ok(equipmentService.list()
                                                 .stream()
                                                 .map(EquipmentDTO::idAndNameFrom)
                                                 .collect(Collectors.toList()));
    }

    @GetMapping("/{equipmentId}")
    @ResponseBody
    public ResponseEntity<EquipmentDTO> getEquipment(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(EquipmentDTO.from(equipmentService.getEquipment(equipmentId)));
    }


    @PostMapping
    public ResponseEntity<Object> addNewEquipment(@RequestBody EquipmentDTO equipmentDTO) {
        equipmentService.add(equipmentDTO.getName(), equipmentDTO.getSubType().getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{equipmentId}")
    public ResponseEntity<Object> updateEquipment(@PathVariable Long equipmentId, @RequestBody EquipmentDTO equipmentDTO) {
        equipmentService.update(equipmentId, equipmentDTO.getName(), equipmentDTO.getSubType().getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
