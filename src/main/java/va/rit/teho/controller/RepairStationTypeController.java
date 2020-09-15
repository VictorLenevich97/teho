package va.rit.teho.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.RepairStationTypeDTO;
import va.rit.teho.service.RepairStationTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("repair-station/type")
public class RepairStationTypeController {

    private final RepairStationTypeService repairStationTypeService;

    public RepairStationTypeController(RepairStationTypeService repairStationTypeService) {
        this.repairStationTypeService = repairStationTypeService;
    }

    @GetMapping
    public ResponseEntity<List<RepairStationTypeDTO>> listRepairStationTypes() {
        List<RepairStationTypeDTO> repairStationTypeDTOList = repairStationTypeService.listTypes()
                                                                                      .stream()
                                                                                      .map(RepairStationTypeDTO::from)
                                                                                      .collect(Collectors.toList());
        return ResponseEntity.ok(repairStationTypeDTOList);
    }

    @PostMapping
    public ResponseEntity<Object> addRepairStationType(@RequestBody RepairStationTypeDTO repairStationTypeDTO) {
        repairStationTypeService.addType(repairStationTypeDTO.getName(),
                                         repairStationTypeDTO.getWorkingHoursMin(),
                                         repairStationTypeDTO.getWorkingHoursMax());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{typeId}")
    public ResponseEntity<Object> updateRepairStationType(@PathVariable Long typeId,
                                                          @RequestBody RepairStationTypeDTO repairStationTypeDTO) {
        repairStationTypeService.updateType(typeId,
                                            repairStationTypeDTO.getName(),
                                            repairStationTypeDTO.getWorkingHoursMin(),
                                            repairStationTypeDTO.getWorkingHoursMax());
        return ResponseEntity.accepted().build();
    }

}
