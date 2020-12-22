package va.rit.teho.controller.labordistribution;

import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import va.rit.teho.dto.labordistribution.EquipmentRFUDistributionDTO;
import va.rit.teho.server.config.TehoSessionData;
import va.rit.teho.service.labordistribution.EquipmentRFUDistributionService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/formation/repair-formation/unit", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Распределение ремонтного фонда")
public class EquipmentRFUDistributionController {

    private final EquipmentRFUDistributionService equipmentRFUDistributionService;

    @Resource
    private TehoSessionData tehoSession;

    public EquipmentRFUDistributionController(EquipmentRFUDistributionService equipmentRFUDistributionService) {
        this.equipmentRFUDistributionService = equipmentRFUDistributionService;
    }

    @PostMapping("/equipment")
    public ResponseEntity<Object> distributeEquipmentPerRFU() {
        equipmentRFUDistributionService.distribute(tehoSession.getSessionId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{repairFormationUnitId}/equipment")
    public ResponseEntity<List<EquipmentRFUDistributionDTO>> getDistributedEquipmentForRFU(@PathVariable Long repairFormationUnitId) {
        List<EquipmentRFUDistributionDTO> equipmentRFUDistributionDTOList = equipmentRFUDistributionService
                .listRFUDistributedEquipment(repairFormationUnitId, tehoSession.getSessionId())
                .stream()
                .map(EquipmentRFUDistributionDTO::from)
                .collect(
                        Collectors.toList());
        return ResponseEntity.ok(equipmentRFUDistributionDTOList);
    }


}
