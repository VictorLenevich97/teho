package va.rit.teho.controller.repairformation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.repairformation.RepairFormationDTO;
import va.rit.teho.service.repairformation.RepairFormationService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Ремонтные формирования")
public class RepairFormationController {

    private final RepairFormationService repairFormationService;

    public RepairFormationController(RepairFormationService repairFormationService) {
        this.repairFormationService = repairFormationService;
    }

    @GetMapping("/formation/{formationId}/repair-formation")
    public ResponseEntity<List<RepairFormationDTO>> listRepairFormations(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId) {
        return ResponseEntity.ok(repairFormationService
                                         .list(formationId)
                                         .stream()
                                         .map(rf -> RepairFormationDTO.from(rf, false))
                                         .collect(Collectors.toList()));
    }

    @PostMapping(path = "/formation/{formationId}/repair-formation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addRepairFormation(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId,
            @RequestBody RepairFormationDTO repairFormationDTO) {
        repairFormationService.add(repairFormationDTO.getName(),
                                   repairFormationDTO.getType().getId(),
                                   formationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(path = "/formation/{formationId}/repair-formation/{repairFormationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateRepairFormation(
            @ApiParam(value = "Ключ формирования", required = true) @PathVariable Long formationId,
            @ApiParam(value = "Ключ обновляемого ремонтного формирования", required = true) @PathVariable Long repairFormationId,
            @RequestBody RepairFormationDTO repairFormationDTO) {
        repairFormationService.update(repairFormationId,
                                      repairFormationDTO.getName(),
                                      repairFormationDTO.getType().getId(),
                                      formationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
