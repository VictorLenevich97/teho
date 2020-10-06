package va.rit.teho.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.DistributionIntervalDTO;
import va.rit.teho.dto.LaborInputDistributionDTO;
import va.rit.teho.dto.equipment.EquipmentSubTypeDTO;
import va.rit.teho.dto.equipment.EquipmentTypeDTO;
import va.rit.teho.entity.EquipmentLaborInputDistribution;
import va.rit.teho.entity.EquipmentSubType;
import va.rit.teho.entity.EquipmentType;
import va.rit.teho.server.TehoSessionData;
import va.rit.teho.service.LaborInputDistributionService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("labor-distribution")
public class LaborInputDistributionController {

    private final LaborInputDistributionService laborInputDistributionService;

    public LaborInputDistributionController(LaborInputDistributionService laborInputDistributionService) {
        this.laborInputDistributionService = laborInputDistributionService;
    }

    @Resource
    private TehoSessionData tehoSession;

    @GetMapping("/intervals")
    public ResponseEntity<List<DistributionIntervalDTO>> getDistributionIntervals() {
        return ResponseEntity.ok(
                laborInputDistributionService
                        .getDistributionIntervals()
                        .stream()
                        .map(DistributionIntervalDTO::from)
                        .collect(Collectors.toList()));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<LaborInputDistributionDTO>> getDistributionData(@RequestParam(required = false) List<Long> typeId) {
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> laborInputDistribution =
                laborInputDistributionService.getLaborInputDistribution(tehoSession.getSessionId(), typeId);

        List<LaborInputDistributionDTO> laborInputDistributionDTOList = laborInputDistribution
                .entrySet()
                .stream()
                .map(this::mapLaborInputDistributionDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(laborInputDistributionDTOList);
    }

    private LaborInputDistributionDTO mapLaborInputDistributionDTO(
            Map.Entry<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> typeEntry) {
        List<LaborInputDistributionDTO.SubTypeDistributionDTO> subTypeDistribution =
                typeEntry
                        .getValue()
                        .entrySet()
                        .stream()
                        .map(subTypeEntry -> new LaborInputDistributionDTO.SubTypeDistributionDTO(
                                EquipmentSubTypeDTO.from(subTypeEntry.getKey()),
                                subTypeEntry
                                        .getValue()
                                        .stream()
                                        .map(LaborInputDistributionDTO.EquipmentLaborInputDistributionDTO::from)
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toList());
        return new LaborInputDistributionDTO(EquipmentTypeDTO.from(typeEntry.getKey()), subTypeDistribution);
    }

    @PostMapping("/{coefficient}")
    public ResponseEntity<Object> updateDistributionData(@PathVariable Double coefficient) {
        laborInputDistributionService.updateLaborInputDistribution(tehoSession.getSessionId(), coefficient);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/type/{typeId}/{coefficient}")
    public ResponseEntity<Object> updateDistributionDataPerEquipmentType(@PathVariable Long typeId,
                                                                         @PathVariable Double coefficient) {
        laborInputDistributionService.updateLaborInputDistributionPerEquipmentType(tehoSession.getSessionId(),
                                                                                   coefficient,
                                                                                   typeId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/subtype/{subTypeId}/{coefficient}")
    public ResponseEntity<Object> updateDistributionDataPerEquipmentSubType(@PathVariable Long subTypeId,
                                                                            @PathVariable Double coefficient) {
        laborInputDistributionService.updateLaborInputDistributionPerEquipmentSubType(tehoSession.getSessionId(),
                                                                                      coefficient,
                                                                                      subTypeId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/base/{baseId}/{coefficient}")
    public ResponseEntity<Object> updateDistributionDataPerBase(@PathVariable Long baseId,
                                                                @PathVariable Double coefficient) {
        laborInputDistributionService.updateLaborInputDistributionPerBase(tehoSession.getSessionId(),
                                                                          coefficient,
                                                                          baseId);
        return ResponseEntity.accepted().build();
    }

}
