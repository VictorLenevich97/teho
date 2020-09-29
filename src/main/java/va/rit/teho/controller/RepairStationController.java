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
import va.rit.teho.server.TehoSessionData;
import va.rit.teho.service.RepairStationService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("repair-station")
public class RepairStationController {

    private final RepairStationService repairStationService;

    public RepairStationController(RepairStationService repairStationService) {
        this.repairStationService = repairStationService;
    }

    @Resource
    private TehoSessionData tehoSession;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<RepairStationDTO>> listRepairStations(
            @RequestParam(value = "id", required = false) List<Long> ids) {
        List<RepairStationDTO> repairStationDTOList =
                repairStationService.list(Optional.ofNullable(ids).orElse(Collections.emptyList()))
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
                                 repairStationDTO.getBase().getId(),
                                 repairStationDTO.getType().getId(),
                                 repairStationDTO.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{repairStationId}")
    public ResponseEntity<Object> updateRepairStation(@PathVariable Long repairStationId,
                                                      @RequestBody RepairStationDTO repairStationDTO) {
        repairStationService.update(repairStationId,
                                    repairStationDTO.getName(),
                                    repairStationDTO.getBase().getId(),
                                    repairStationDTO.getType().getId(),
                                    repairStationDTO.getAmount());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{repairStationId}/subtype/{subTypeId}")
    public ResponseEntity<Object> setRepairStationEquipmentStaff(@PathVariable Long repairStationId,
                                                                 @PathVariable Long subTypeId,
                                                                 @RequestBody EquipmentStaffDTO equipmentStaffDTO) {
        repairStationService.setEquipmentStaff(
                tehoSession.getSessionId(),
                repairStationId,
                subTypeId,
                equipmentStaffDTO.getAvailableStaff(),
                equipmentStaffDTO.getTotalStaff());
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{repairStationId}/subtype/{subTypeId}")
    public ResponseEntity<Object> updateRepairStationEquipmentStaff(@PathVariable Long repairStationId,
                                                                    @PathVariable Long subTypeId,
                                                                    @RequestBody EquipmentStaffDTO equipmentStaffDTO) {
        repairStationService.updateEquipmentStaff(
                tehoSession.getSessionId(),
                repairStationId,
                subTypeId,
                equipmentStaffDTO.getAvailableStaff(),
                equipmentStaffDTO.getTotalStaff());
        return ResponseEntity.accepted().build();
    }

}
