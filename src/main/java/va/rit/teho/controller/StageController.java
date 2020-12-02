package va.rit.teho.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import va.rit.teho.dto.common.StageDTO;
import va.rit.teho.service.common.StageService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
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
}
