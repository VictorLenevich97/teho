package by.varb.teho.controller;

import by.varb.teho.dto.BaseDTO;
import by.varb.teho.service.BaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/base")
public class BaseController {

    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    @PostMapping("/base")
    public void addBase(@RequestBody BaseDTO baseModel) {
        baseService.add(baseModel.getShortName(), baseModel.getFullName());
    }

    @PostMapping("/base/{baseId}/equipment/{equipmentId}/{amount}")
    public void addEquipmentPerBase(@PathVariable Long equipmentId, @PathVariable Long baseId, @PathVariable int intensity, @PathVariable int amount) {
        baseService.addEquipmentToBase(baseId, equipmentId, intensity, amount);
    }
}
