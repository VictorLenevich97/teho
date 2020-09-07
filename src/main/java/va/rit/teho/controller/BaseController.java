package va.rit.teho.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.BaseDTO;
import va.rit.teho.dto.equipment.IntensityAndAmountDTO;
import va.rit.teho.service.BaseService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("base")
public class BaseController {

    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    @PostMapping
    public ResponseEntity<Object> addBase(@RequestBody BaseDTO baseModel) {
        baseService.add(baseModel.getShortName(), baseModel.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{baseId}")
    public ResponseEntity<Object> updateBase(@PathVariable Long baseId, @RequestBody BaseDTO baseModel) {
        baseService.update(baseId, baseModel.getShortName(), baseModel.getFullName());
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<BaseDTO>> listBases() {
        return ResponseEntity.ok(baseService.list().stream().map(BaseDTO::from).collect(Collectors.toList()));
    }

    @GetMapping("/{baseId}")
    @ResponseBody
    public ResponseEntity<BaseDTO> getBase(@PathVariable Long baseId) {
        return ResponseEntity.ok(BaseDTO.from(baseService.get(baseId)));
    }

    @PostMapping("/{baseId}/equipment/{equipmentId}")
    public ResponseEntity<Object> addEquipmentToBase(@PathVariable Long baseId,
                                                     @PathVariable Long equipmentId,
                                                     @RequestBody IntensityAndAmountDTO intensityAndAmount) {
        baseService.addEquipmentToBase(baseId,
                                       equipmentId,
                                       intensityAndAmount.getIntensity(),
                                       intensityAndAmount.getAmount());
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{baseId}/equipment/{equipmentId}")
    public ResponseEntity<Object> updateEquipmentInBase(@PathVariable Long baseId,
                                                        @PathVariable Long equipmentId,
                                                        @RequestBody IntensityAndAmountDTO intensityAndAmount) {
        baseService.updateEquipmentInBase(baseId,
                                          equipmentId,
                                          intensityAndAmount.getIntensity(),
                                          intensityAndAmount.getAmount());
        return ResponseEntity.accepted().build();
    }
}
