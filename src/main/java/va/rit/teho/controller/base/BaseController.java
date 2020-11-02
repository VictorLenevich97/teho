package va.rit.teho.controller.base;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.base.BaseDTO;
import va.rit.teho.service.base.BaseService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "base", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Часть/Подразделение")
public class BaseController {

    private final BaseService baseService;

    public BaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    @PostMapping
    @ApiOperation(value = "Добавить Часть/Подразделение")
    public ResponseEntity<Object> addBase(@RequestBody BaseDTO baseModel) {
        baseService.add(baseModel.getShortName(), baseModel.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{baseId}")
    @ApiOperation(value = "Обновить Часть/Подразделение")
    public ResponseEntity<Object> updateBase(@PathVariable Long baseId, @RequestBody BaseDTO baseModel) {
        baseService.update(baseId, baseModel.getShortName(), baseModel.getFullName());
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    @ResponseBody
    @ApiOperation(value = "Получить список Часть/Подразделение")
    public ResponseEntity<List<BaseDTO>> listBases() {
        return ResponseEntity.ok(baseService.list().stream().map(BaseDTO::from).collect(Collectors.toList()));
    }

    @GetMapping("/{baseId}")
    @ResponseBody
    @ApiOperation(value = "Получить подробности о Часть/Подразделение")
    public ResponseEntity<BaseDTO> getBase(@PathVariable Long baseId) {
        return ResponseEntity.ok(BaseDTO.from(baseService.get(baseId)));
    }

}
