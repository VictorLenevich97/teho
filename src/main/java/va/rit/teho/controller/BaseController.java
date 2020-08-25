package va.rit.teho.controller;

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
    public void addBase(@RequestBody BaseDTO baseModel) {
        baseService.add(baseModel.getShortName(), baseModel.getFullName());
    }

    @GetMapping
    @ResponseBody
    public List<BaseDTO> listBases() {
        return baseService.list().stream().map(BaseDTO::from).collect(Collectors.toList());
    }

    @GetMapping("/{baseId}")
    @ResponseBody
    public BaseDTO getBase(@PathVariable Long baseId) {
        return BaseDTO.from(baseService.get(baseId));
    }

    @PostMapping("/{baseId}/equipment/{equipmentId}")
    public void addEquipmentPerBase(@PathVariable Long baseId,
                                    @PathVariable Long equipmentId,
                                    @RequestBody IntensityAndAmountDTO intensityAndAmount) {
        baseService.addEquipmentToBase(baseId,
                                       equipmentId,
                                       intensityAndAmount.getIntensity(),
                                       intensityAndAmount.getAmount());
    }
}
