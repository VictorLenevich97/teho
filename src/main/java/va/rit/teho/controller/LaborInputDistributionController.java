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
import va.rit.teho.service.LaborInputDistributionService;

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
    public List<LaborInputDistributionDTO> getDistributionData(@RequestParam(required = false) List<Long> typeIds) {
        Map<EquipmentType, Map<EquipmentSubType, List<EquipmentLaborInputDistribution>>> laborInputDistribution =
                laborInputDistributionService.getLaborInputDistribution(typeIds);

        return laborInputDistribution
                .entrySet()
                .stream()
                .map(this::mapLaborInputDistributionDTO)
                .collect(Collectors.toList());
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

    @PostMapping
    public ResponseEntity<Object> updateDistributionData() {
        laborInputDistributionService.updateLaborInputDistribution();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/type/{typeId}")
    public ResponseEntity<Object> updateDistributionDataPerEquipmentType(@PathVariable Long typeId) {
        laborInputDistributionService.updateLaborInputDistributionPerEquipmentType(typeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subtype/{subTypeId}")
    public ResponseEntity<Object> updateDistributionDataPerEquipmentSubType(@PathVariable Long subTypeId) {
        laborInputDistributionService.updateLaborInputDistributionPerEquipmentSubType(subTypeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/base/{baseId}")
    public ResponseEntity<Object> updateDistributionDataPerBase(@PathVariable Long baseId) {
        laborInputDistributionService.updateLaborInputDistributionPerBase(baseId);
        return ResponseEntity.ok().build();
    }

}
