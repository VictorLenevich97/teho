package va.rit.teho.controller.intensity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.intensity.OperationDTO;
import va.rit.teho.entity.intensity.Operation;
import va.rit.teho.service.intensity.OperationService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(path = "/operation", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Операции")
public class OperationController {

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping
    @ApiOperation(value = "Получить список Операций")
    public ResponseEntity<List<OperationDTO>> listOperations() {
        return ResponseEntity.ok(operationService.list().stream().map(OperationDTO::from).collect(Collectors.toList()));
    }

    @PostMapping
    @ApiOperation(value = "Добавить Операцию")
    public ResponseEntity<OperationDTO> addOperation(@RequestBody @Validated OperationDTO operationDTO) {
        Operation added = operationService.add(operationDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(OperationDTO.from(added));
    }

    @PostMapping("/{id}/activate")
    @ApiOperation(value = "Сделать операцию активной")
    public ResponseEntity<OperationDTO> setOperationActive(@PathVariable Long id) {
        Operation operation = operationService.setActive(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(OperationDTO.from(operation));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Обновить Операцию")
    public ResponseEntity<OperationDTO> updateOperation(@PathVariable Long id, @RequestBody @Validated OperationDTO operationDTO) {
        Operation operation = operationService.update(id, operationDTO.getName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(OperationDTO.from(operation));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Удалить Операцию")
    public ResponseEntity<OperationDTO> deleteOperation(@PathVariable Long id) {
        Operation deleted = operationService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(OperationDTO.from(deleted));
    }
}
