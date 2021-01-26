package va.rit.teho.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import va.rit.teho.dto.common.IdAndNameDTO;
import va.rit.teho.entity.labordistribution.RestorationType;
import va.rit.teho.service.labordistribution.RestorationTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(path = "/restoration-type", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestorationTypeController {

    private final RestorationTypeService restorationTypeService;

    public RestorationTypeController(RestorationTypeService restorationTypeService) {
        this.restorationTypeService = restorationTypeService;
    }

    @GetMapping
    @ApiOperation(value = "Получить список типов восстановления")
    @ResponseBody
    public ResponseEntity<List<IdAndNameDTO>> listRepairTypes() {
        List<RestorationType> restorationTypes = restorationTypeService.list();
        return ResponseEntity.ok(restorationTypes
                                         .stream()
                                         .map(rt -> new IdAndNameDTO(rt.getId(), rt.getName()))
                                         .collect(Collectors.toList()));
    }

}
