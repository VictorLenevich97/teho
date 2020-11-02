package va.rit.teho.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import va.rit.teho.dto.common.RepairTypeDTO;
import va.rit.teho.entity.common.RepairType;
import va.rit.teho.service.common.RepairTypeService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "repair-type", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Типы ремонта")
public class RepairTypeController {

    private final RepairTypeService repairTypeService;

    public RepairTypeController(RepairTypeService repairTypeService) {
        this.repairTypeService = repairTypeService;
    }

    @GetMapping
    @ApiOperation(value = "Получить список типов ремонта")
    public ResponseEntity<List<RepairTypeDTO>> listRepairTypes(
            @ApiParam(value = "Фильтр по индикатору, определяющему используется ли тип ремонта в расчетах. В случае, когда параметр не указан, возвращаются все типы ремонта.",
                    example = "true") @RequestParam(required = false) Boolean repairable) {
        List<RepairType> types;
        if (repairable == null) {
            types = repairTypeService.list();
        } else {
            types = repairTypeService.list(repairable);
        }
        return ResponseEntity.ok(types
                                         .stream()
                                         .map(RepairTypeDTO::from)
                                         .sorted(Comparator.comparing(RepairTypeDTO::getId))
                                         .collect(Collectors.toList()));
    }
}
