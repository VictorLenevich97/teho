package va.rit.teho.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import va.rit.teho.dto.common.StageDTO;
import va.rit.teho.service.common.StageService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Validated
@RequestMapping(path = "stage", produces = MediaType.APPLICATION_JSON_VALUE)
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @GetMapping
    public ResponseEntity<List<StageDTO>> listStages() {
        return ResponseEntity.ok(stageService.list().stream().map(StageDTO::from).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<StageDTO> addStage(@Valid @RequestBody StageDTO stageDTO) {
        return ResponseEntity.accepted().body(StageDTO.from(stageService.add(stageDTO.getNum())));
    }

    @DeleteMapping("/{stageId}")
    @Transactional
    public ResponseEntity<Object> deleteStage(@PathVariable @Positive Long stageId) {
        //Проверка на существование
        stageService.get(stageId);

        stageService.delete(stageId);
        return ResponseEntity.noContent().build();
    }
}
