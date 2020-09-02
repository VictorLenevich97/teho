package va.rit.teho.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.EquipmentStaffDTO;
import va.rit.teho.dto.RepairStationDTO;
import va.rit.teho.entity.RepairStation;
import va.rit.teho.entity.RepairStationEquipmentStaff;
import va.rit.teho.model.Pair;
import va.rit.teho.service.RepairStationService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("repair-station")
public class RepairStationController {

    private final RepairStationService repairStationService;

    public RepairStationController(RepairStationService repairStationService) {
        this.repairStationService = repairStationService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<RepairStationDTO>> listRepairStations() {
        List<RepairStationDTO> repairStationDTOList = repairStationService.list()
                                                                          .stream()
                                                                          .map(RepairStationDTO::from)
                                                                          .collect(Collectors.toList());
        return ResponseEntity.ok(repairStationDTOList);
    }

    @GetMapping("/{repairStationId}")
    @ResponseBody
    public ResponseEntity<RepairStationDTO> getRepairStation(@PathVariable Long repairStationId) {
        Pair<RepairStation, List<RepairStationEquipmentStaff>> repairStationListPair =
                repairStationService.get(repairStationId);
        RepairStationDTO repairStationDTO = RepairStationDTO
                .from(repairStationListPair.getLeft())
                .setEquipmentStaff(
                        repairStationListPair
                                .getRight()
                                .stream()
                                .map(EquipmentStaffDTO::from)
                                .collect(Collectors.toList()));
        return ResponseEntity.ok(repairStationDTO);
    }

    @PostMapping
    public ResponseEntity<Object> addRepairStation(@RequestBody RepairStationDTO repairStationDTO) {
        repairStationService.add(repairStationDTO.getName(),
                                 repairStationDTO.getBaseKey(),
                                 repairStationDTO.getType().getKey(),
                                 repairStationDTO.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{repairStationId}/equipment/{equipmentId}")
    public ResponseEntity<Object> setRepairStationEquipmentStaff(@PathVariable Long repairStationId,
                                                                 @PathVariable Long equipmentId,
                                                                 @RequestBody EquipmentStaffDTO equipmentStaffDTO) {
        repairStationService.setEquipmentStaff(repairStationId,
                                               equipmentId,
                                               equipmentStaffDTO.getAvailableStaff(),
                                               equipmentStaffDTO.getTotalStaff());
        return ResponseEntity.accepted().build();
    }
}
